import fs from "node:fs/promises";
import path from "node:path";
import process from "node:process";
import { chromium } from "../playwright-local/node_modules/playwright/index.mjs";

const baseUrl = process.env.AUDIT_BASE_URL || "http://localhost:8081";
const password = process.env.AUDIT_PASSWORD || "Demo123!";
const outputDir = path.join(process.cwd(), "output", "playwright", "local-view-audit");

const viewports = {
  desktop: { width: 1440, height: 1200 },
  tablet: { width: 1024, height: 1100 },
  mobile: { width: 390, height: 844 },
};

const publicPages = [
  { label: "public-home", path: "/", viewports: ["desktop", "mobile"] },
  { label: "login", path: "/login", viewports: ["desktop", "mobile"] },
  { label: "signup-choice", path: "/signup", viewports: ["desktop"] },
  { label: "pricing", path: "/pricing", viewports: ["desktop", "mobile"] },
  { label: "faq", path: "/faq", viewports: ["desktop"] },
  { label: "about", path: "/about", viewports: ["desktop"] },
  { label: "verify-email-code", path: "/verify/email/code", viewports: ["desktop"] },
];

const protectedRouteChecks = [
  { label: "dashboard", path: "/dashboard" },
  { label: "profile", path: "/profile" },
  { label: "calendar", path: "/calendar?view=month" },
  { label: "trainer-dashboard", path: "/trainer/dashboard" },
  { label: "gym-dashboard", path: "/gym/dashboard" },
  { label: "admin-dashboard", path: "/admin/dashboard" },
  { label: "super-admin-verification", path: "/super-admin/verification/queue" },
];

const roleLockChecks = [
  { actor: "client", path: "/trainer/dashboard", expected: "blocked" },
  { actor: "client", path: "/gym/dashboard", expected: "blocked" },
  { actor: "trainer", path: "/client/trainers", expected: "blocked" },
  { actor: "trainer", path: "/gym/dashboard", expected: "blocked" },
  { actor: "gym", path: "/trainer/dashboard", expected: "blocked" },
  { actor: "gym", path: "/super-admin/verification/queue", expected: "blocked" },
  { actor: "admin", path: "/super-admin/verification/queue", expected: "blocked" },
];

const roleAudits = [
  {
    role: "client",
    username: "demo_client",
    pages: [
      { label: "dashboard", path: "/dashboard", viewports: ["desktop", "mobile"] },
      { label: "profile", path: "/profile", viewports: ["desktop", "mobile"] },
      { label: "calendar-month", path: "/calendar?view=month", viewports: ["desktop", "mobile"] },
      { label: "client-trainers", path: "/client/trainers", viewports: ["desktop"] },
      { label: "goals", path: "/goals", viewports: ["desktop"] },
      { label: "workout-management", path: "/workout-management", viewports: ["desktop"] },
      { label: "merch-shop", path: "/merch", viewports: ["desktop"] },
    ],
  },
  {
    role: "trainer",
    username: "demo_trainer",
    pages: [
      { label: "trainer-dashboard", path: "/trainer/dashboard", viewports: ["desktop", "mobile"] },
      { label: "trainer-clients", path: "/trainer/clients", viewports: ["desktop"] },
      { label: "trainer-library", path: "/trainer/library", viewports: ["desktop"] },
      { label: "trainer-exercises", path: "/trainer/library/exercises", viewports: ["desktop"] },
      { label: "trainer-workout-templates", path: "/workout-templates", viewports: ["desktop"] },
      { label: "trainer-schedules", path: "/schedules", viewports: ["desktop", "mobile"] },
      { label: "trainer-workouts", path: "/workouts", viewports: ["desktop"] },
    ],
  },
  {
    role: "gym",
    username: "demo_gym",
    pages: [
      { label: "gym-dashboard", path: "/gym/dashboard", viewports: ["desktop", "mobile"] },
      { label: "gym-trainers", path: "/gym/admin/trainers", viewports: ["desktop"] },
      { label: "gym-memberships", path: "/gym/admin/memberships", viewports: ["desktop"] },
    ],
  },
  {
    role: "admin",
    username: "demo_admin",
    pages: [
      { label: "admin-dashboard", path: "/admin/dashboard", viewports: ["desktop"] },
      { label: "admin-feedback", path: "/admin/feedback", viewports: ["desktop"] },
      { label: "admin-gym-applications", path: "/admin/gym-applications", viewports: ["desktop"] },
      { label: "admin-merch", path: "/admin/merch", viewports: ["desktop"] },
    ],
  },
  {
    role: "super-admin",
    username: "superadmin_demo",
    pages: [
      { label: "super-admin-verification", path: "/super-admin/verification/queue", viewports: ["desktop"] },
    ],
  },
];

function slug(input) {
  return input.replace(/[^a-z0-9-]+/gi, "-").replace(/^-|-$/g, "").toLowerCase();
}

async function ensureDir(dir) {
  await fs.mkdir(dir, { recursive: true });
}

async function getDiagnostics(page) {
  return page.evaluate(() => {
    const text = document.body?.innerText || "";
    const scrollWidth = Math.max(document.documentElement.scrollWidth, document.body?.scrollWidth || 0);
    const interactive = {
      links: document.querySelectorAll("a[href]").length,
      buttons: document.querySelectorAll("button").length,
      forms: document.querySelectorAll("form").length,
      inputs: document.querySelectorAll("input, select, textarea").length,
    };
    const isVisible = (element) => {
      const style = window.getComputedStyle(element);
      const rect = element.getBoundingClientRect();
      return style.display !== "none" && style.visibility !== "hidden" && rect.width > 0 && rect.height > 0;
    };
    const missingButtonNames = Array.from(document.querySelectorAll("button"))
      .filter((button) => isVisible(button) && !button.innerText.trim() && !button.getAttribute("aria-label"))
      .slice(0, 10)
      .map((button) => button.outerHTML.slice(0, 160));
    const deadLinks = Array.from(document.querySelectorAll("a[href]"))
      .map((a) => a.getAttribute("href") || "")
      .filter((href) => href === "#" || href.toLowerCase().startsWith("javascript:"))
      .slice(0, 20);
    const suspiciousText = text.match(/Ã|Â|â€|ðŸ/g) ? text.split("\n").filter((line) => /Ã|Â|â€|ðŸ/.test(line)).slice(0, 8) : [];
    return {
      title: document.title,
      url: location.href,
      statusText: document.querySelector("main")?.innerText.slice(0, 180) || text.slice(0, 180),
      hasHorizontalOverflow: scrollWidth > window.innerWidth + 8,
      scrollWidth,
      innerWidth: window.innerWidth,
      interactive,
      missingButtonNames,
      deadLinks,
      suspiciousText,
    };
  });
}

async function capture(page, entry, viewportName, role = "public") {
  const viewport = viewports[viewportName];
  await page.setViewportSize(viewport);
  const consoleMessages = [];
  const consoleListener = (message) => {
    if (["error", "warning"].includes(message.type())) {
      consoleMessages.push({ type: message.type(), text: message.text() });
    }
  };
  page.on("console", consoleListener);

  const response = await page.goto(`${baseUrl}${entry.path}`, { waitUntil: "networkidle", timeout: 30000 }).catch((error) => ({ error }));
  await page.waitForTimeout(350);
  const label = `${role}-${entry.label}-${viewportName}`;
  const screenshot = `${slug(label)}.png`;
  await page.screenshot({ path: path.join(outputDir, screenshot), fullPage: true });
  const diagnostics = await getDiagnostics(page);
  page.off("console", consoleListener);

  return {
    role,
    label: entry.label,
    path: entry.path,
    viewport: viewportName,
    status: typeof response?.status === "function" ? response.status() : null,
    responseError: response?.error ? String(response.error) : null,
    finalUrl: page.url(),
    screenshot,
    consoleMessages,
    diagnostics,
  };
}

async function login(page, username, role) {
  await page.setViewportSize(viewports.desktop);
  await page.goto(`${baseUrl}/login`, { waitUntil: "networkidle", timeout: 30000 });
  if (role === "trainer" || role === "gym") {
    await page.locator(`[data-role="${role}"]`).click();
  }
  const usernameSelector = role === "gym" ? "#gymUsername" : "#username";
  const passwordSelector = role === "gym" ? "#gymPassword" : "#password";
  await page.locator(usernameSelector).fill(username);
  if (role === "trainer") {
    await page.locator("#trainerCode1").fill("2407");
    await page.locator("#trainerCode2").fill("8190");
    await page.locator("#trainerCode3").fill("3465");
  }
  if (role === "gym") {
    await page.locator("#gymSecretCode1").fill("4827");
    await page.locator("#gymSecretCode2").fill("0019");
    await page.locator("#gymSecretCode3").fill("3845");
    await page.locator("#gymSecretCode4").fill("6203");
  }
  await page.locator(passwordSelector).fill(password);
  await Promise.all([
    page.waitForLoadState("networkidle"),
    page.locator("#loginForm button[type='submit']").click(),
  ]);
  await page.waitForTimeout(500);
  return { username, finalUrl: page.url(), title: await page.title() };
}

async function auditInvalidLogin(page) {
  await page.setViewportSize(viewports.desktop);
  await page.goto(`${baseUrl}/login`, { waitUntil: "networkidle" });
  await page.locator("#username").fill("not_a_real_user");
  await page.locator("#password").fill("wrong_password");
  await Promise.all([
    page.waitForLoadState("networkidle"),
    page.locator("#loginForm button[type='submit']").click(),
  ]);
  const text = await page.locator("body").innerText();
  const alertText = await page.locator("[role='alert']").last().innerText().catch(() => "");
  return {
    finalUrl: page.url(),
    stayedOnLogin: page.url().includes("/login"),
    hasErrorText: /invalid|error|incorrect|failed|annilys|password|cyfrinair/i.test(`${text}\n${alertText}`),
  };
}

async function auditLoginInteractions(page) {
  await page.setViewportSize(viewports.desktop);
  await page.goto(`${baseUrl}/login`, { waitUntil: "networkidle" });
  const result = { roleTabs: [], passwordToggleWorked: false };
  const password = page.locator("#password");
  const toggle = page.locator("#togglePassword");
  if ((await password.count()) && (await toggle.count())) {
    const before = await password.getAttribute("type");
    await toggle.click();
    const after = await password.getAttribute("type");
    result.passwordToggleWorked = before !== after;
  }
  for (const role of ["client", "trainer", "gym"]) {
    const button = page.locator(`[data-role="${role}"]`).first();
    if (await button.count()) {
      await button.click();
      result.roleTabs.push({
        role,
        loginType: await page.locator("#loginType").inputValue().catch(() => null),
        selected: await button.getAttribute("aria-selected"),
      });
    }
  }
  return result;
}

async function auditProtectedRoutes(page) {
  const checks = [];
  for (const route of protectedRouteChecks) {
    const response = await page.goto(`${baseUrl}${route.path}`, { waitUntil: "networkidle", timeout: 30000 });
    checks.push({
      label: route.label,
      path: route.path,
      status: response?.status() ?? null,
      finalUrl: page.url(),
      redirectedToLogin: page.url().includes("/login"),
    });
  }
  return checks;
}

async function auditRoleLocks(page, actor) {
  const checks = [];
  for (const route of roleLockChecks.filter((check) => check.actor === actor)) {
    const response = await page.goto(`${baseUrl}${route.path}`, { waitUntil: "networkidle", timeout: 30000 });
    const bodyText = await page.locator("body").innerText().catch(() => "");
    checks.push({
      actor,
      path: route.path,
      status: response?.status() ?? null,
      finalUrl: page.url(),
      blocked: [401, 403].includes(response?.status() ?? 0)
        || page.url().includes("/access-denied")
        || page.url().includes("/dev-mode/restricted")
        || /access denied|unauthori[sz]ed|restricted|forbidden/i.test(bodyText),
    });
  }
  return checks;
}

async function auditScheduleInteractions(page) {
  await page.setViewportSize(viewports.desktop);
  await page.goto(`${baseUrl}/schedules`, { waitUntil: "networkidle", timeout: 30000 });

  const result = {
    searchEmptyStateWorked: null,
    createModalOpened: false,
    createModalClosed: false,
    previewOpened: null,
    duplicateCancelPreservedPage: null,
    duplicateRequestHadCsrf: null,
    duplicateRequestMethod: null,
  };

  const search = page.locator("#schedule-search");
  if (await search.count()) {
    await search.fill("__no_matching_schedule__");
    await page.waitForTimeout(150);
    result.searchEmptyStateWorked = await page.locator("#schedule-search-empty:not(.tcc-hidden)").isVisible().catch(() => false);
    await search.fill("");
  }

  const createButton = page.locator("[data-open-create-modal]").first();
  if (await createButton.count()) {
    await createButton.click();
    await page.waitForTimeout(150);
    result.createModalOpened = await page.locator("#create-modal:not(.tcc-hidden)").isVisible().catch(() => false);
    await page.keyboard.press("Escape");
    await page.waitForTimeout(150);
    result.createModalClosed = await page.locator("#create-modal").evaluate((el) => el.classList.contains("tcc-hidden")).catch(() => true);
  }

  const previewButton = page.locator("[data-open-schedule-preview]").first();
  if (await previewButton.count()) {
    await previewButton.click();
    await page.waitForTimeout(500);
    result.previewOpened = await page.locator("#preview-modal:not(.tcc-hidden)").isVisible().catch(() => false);
    await page.keyboard.press("Escape");
  }

  const duplicateButton = page.locator("[data-duplicate-schedule]").first();
  if (await duplicateButton.count()) {
    page.once("dialog", async (dialog) => {
      await dialog.dismiss();
    });
    const beforeUrl = page.url();
    await duplicateButton.click();
    await page.waitForTimeout(250);
    result.duplicateCancelPreservedPage = page.url() === beforeUrl;

    await page.route("**/api/schedules/*/duplicate", async (route) => {
      const request = route.request();
      result.duplicateRequestMethod = request.method();
      result.duplicateRequestHadCsrf = request.headers()["x-csrf-token"] != null;
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify({ id: 999999, name: "Intercepted Audit Copy" }),
      });
    });
    page.once("dialog", async (dialog) => {
      await dialog.accept();
    });
    await duplicateButton.click();
    await page.waitForTimeout(500);
    await page.unroute("**/api/schedules/*/duplicate").catch(() => undefined);
  }

  return result;
}

async function auditChatWidget(page) {
  const result = { opened: null, sentEmptyWithoutError: null };
  const launcher = page.locator("#chatToggle, [data-chat-toggle], [aria-controls='chatWidgetPanel']").first();
  if (!(await launcher.count())) return result;
  await launcher.click();
  await page.waitForTimeout(250);
  result.opened = await page.locator("#chatPanel, #chatWidgetPanel, [data-chat-panel]").first().isVisible().catch(() => false);
  const send = page.locator("#chatSend").first();
  if (await send.count()) {
    await send.click();
    await page.waitForTimeout(250);
    result.sentEmptyWithoutError = true;
  }
  return result;
}

async function auditGoalDataFlow(page) {
  const unique = `Audit Goal ${Date.now()}`;
  const result = {
    created: false,
    detailUrl: null,
    checkInSaved: false,
  };

  await page.setViewportSize(viewports.desktop);
  await page.goto(`${baseUrl}/goals/create`, { waitUntil: "networkidle", timeout: 30000 });
  await page.locator("#title").fill(unique);
  await page.locator("#description").fill("Created by the local Web_App workflow audit.");
  await page.locator("#goalType").selectOption({ index: 0 }).catch(() => undefined);
  await page.locator("#status").selectOption({ index: 0 }).catch(() => undefined);
  await page.locator("#targetMetricName").fill("Sessions");
  await page.locator("#targetMetricValue").fill("3");
  await page.locator("#targetMetricUnit").fill("per week");
  await page.locator("#startDate").fill("2026-05-08");
  await page.locator("#targetDate").fill("2026-06-08");
  await page.locator("#priority").fill("1");

  await Promise.all([
    page.waitForURL(/\/goals\/\d+$/, { timeout: 15000 }),
    page.getByRole("button", { name: "Save goal" }).click(),
  ]);
  await page.waitForLoadState("networkidle");

  result.detailUrl = page.url();
  result.created = page.url().includes("/goals/") && (await page.locator("body").innerText()).includes(unique);

  const idMatch = page.url().match(/\/goals\/(\d+)/);
  if (idMatch) {
    await page.goto(`${baseUrl}/goals/${idMatch[1]}/checkins`, { waitUntil: "networkidle", timeout: 30000 });
    await page.locator("#reflection").fill(`Audit check-in ${Date.now()}`);
    await page.locator("#confidenceRating").fill("8");
    await page.locator("#trainerComment").fill("Audit note");
    await page.getByRole("button", { name: "Save check-in" }).click();
    await page.waitForLoadState("networkidle");
    const text = await page.locator("body").innerText();
    result.checkInSaved = /Audit check-in/.test(text) && /Confidence: 8/.test(text);
  }

  return result;
}

async function auditNotesDataFlow(page) {
  const unique = `Audit Note ${Date.now()}`;
  const result = {
    appLoaded: false,
    created: false,
    autosaved: false,
    searchFound: false,
    deleted: false,
  };

  await page.setViewportSize(viewports.desktop);
  await page.goto(`${baseUrl}/notes`, { waitUntil: "networkidle", timeout: 30000 });
  result.appLoaded = await page.locator("#notes-app").isVisible().catch(() => false);
  await page.waitForFunction(() => window.Quill && document.querySelector("#addNoteBtn"), null, { timeout: 15000 });

  await page.locator("#addNoteBtn").click();
  await page.waitForFunction(() => document.querySelector("#noteTitle")?.value === "Untitled", null, { timeout: 10000 });
  result.created = true;

  await page.locator("#noteTitle").fill(unique);
  await page.locator(".ql-editor").fill("Created, autosaved, searched, and deleted by the local audit.");
  await page.waitForFunction(() => document.querySelector("#saveStatus")?.textContent?.trim() === "Saved", null, { timeout: 12000 });
  result.autosaved = true;

  await page.locator("#noteSearch").fill(unique);
  await page.waitForTimeout(600);
  result.searchFound = await page.locator("#noteList").innerText().then((text) => text.includes(unique)).catch(() => false);

  await page.locator("#deleteNoteBtn").click();
  await page.waitForTimeout(900);
  const titleAfterDelete = await page.locator("#noteTitle").inputValue().catch(() => "");
  const listAfterDelete = await page.locator("#noteList").innerText().catch(() => "");
  result.deleted = !titleAfterDelete.includes(unique) && !listAfterDelete.includes(unique);

  return result;
}

async function auditHealthRecordDataFlow(page) {
  const result = {
    formLoaded: false,
    submitted: false,
    successUrl: null,
  };

  await page.setViewportSize(viewports.desktop);
  await page.goto(`${baseUrl}/health-record`, { waitUntil: "networkidle", timeout: 30000 });
  result.formLoaded = await page.locator("form[action='/health-record'], form").first().isVisible().catch(() => false);
  await page.locator("#baselineDate").fill("2026-05-08T09:30");
  await page.locator("#systolicBloodPressure").fill("118");
  await page.locator("#diastolicBloodPressure").fill("76");
  await page.locator("#cholesterol").fill("4.2");
  await page.locator("#weightKg").fill("72");
  await page.locator("#heightCm").fill("178");
  await page.locator("#waistCm").fill("82");
  await page.locator("#activityLevel").selectOption("Moderately Active");
  await Promise.all([
    page.waitForLoadState("networkidle"),
    page.locator("form[action='/health-record'], form[action$='/health-record']").first().locator("button[type='submit']").click(),
  ]);
  result.successUrl = page.url();
  result.submitted = page.url().includes("/health-record/list") && page.url().includes("success");
  return result;
}

async function auditProfileUpdateFlow(page) {
  const unique = `Profile audit ${Date.now()}`;
  const result = {
    editOpened: false,
    saved: false,
    finalUrl: null,
  };

  await page.setViewportSize(viewports.desktop);
  await page.goto(`${baseUrl}/profile`, { waitUntil: "networkidle", timeout: 30000 });
  await page.locator("#toggle-profile-edit").click();
  await page.waitForTimeout(300);
  result.editOpened = await page.locator("#profile-bio-input").isVisible().catch(() => false);
  await page.locator("#profile-bio-input").fill(unique);
  await Promise.all([
    page.waitForLoadState("networkidle"),
    page.locator("#save-profile-edit").click(),
  ]);
  result.finalUrl = page.url();
  const bodyText = await page.locator("body").innerText();
  result.saved = page.url().includes("/profile") && bodyText.includes(unique);
  return result;
}

async function auditBloodPressureFlow(page) {
  const unique = `Audit BP ${Date.now()}`;
  const result = {
    created: false,
    edited: false,
    deleted: false,
    finalUrl: null,
  };

  await page.setViewportSize(viewports.desktop);
  await page.goto(`${baseUrl}/health/blood-pressure`, { waitUntil: "networkidle", timeout: 30000 });
  await page.locator("#readingDate").fill("2026-05-08");
  await page.locator("#readingTime").fill("10:15");
  await page.locator("#systolic").fill("123");
  await page.locator("#diastolic").fill("79");
  await page.locator("#toggleOptional").click();
  await page.locator("#pulse").fill("68");
  await page.locator("#notes").fill(unique);
  await Promise.all([
      page.waitForURL(/\/health\/blood-pressure\?range=\d+$/, { timeout: 15000, waitUntil: "domcontentloaded" }),
    page.getByRole("button", { name: "Save Reading" }).click(),
  ]);
  await page.waitForLoadState("domcontentloaded");
  let bodyText = await page.locator("body").innerText();
  result.created = bodyText.includes("123/79") || bodyText.includes("Reading saved");

  const editLink = page.getByRole("link", { name: "Edit" }).first();
  if (await editLink.count()) {
    await editLink.click();
    await page.waitForFunction(() => /\/health\/blood-pressure\/edit\/\d+$/.test(location.pathname), null, { timeout: 15000 });
    await page.locator("#systolic").fill("124");
    await page.locator("#notes").fill(`${unique} edited`);
    await Promise.all([
      page.waitForResponse((response) => response.request().method() === "POST" && response.url().includes("/health/blood-pressure/edit/"), { timeout: 15000 }),
      page.getByRole("button", { name: "Save Changes" }).click(),
    ]);
    await page.waitForFunction(() => document.body?.innerText?.includes("124/79") || document.body?.innerText?.includes("Reading updated"), null, { timeout: 15000 });
    bodyText = await page.locator("body").innerText();
    result.edited = bodyText.includes("124/79") || bodyText.includes("Reading updated");
  }

  const deleteButton = page.getByRole("button", { name: "Delete" }).first();
  if (await deleteButton.count()) {
    const deleteResponse = page.waitForResponse((response) => response.request().method() === "POST" && response.url().includes("/health/blood-pressure/delete/"), { timeout: 15000 });
    page.once("dialog", async (dialog) => dialog.accept());
    await deleteButton.click();
    await deleteResponse;
    await page.waitForFunction(() => document.body?.innerText?.includes("Reading deleted") || !document.body?.innerText?.includes("124/79"), null, { timeout: 15000 });
    bodyText = await page.locator("body").innerText();
    result.deleted = bodyText.includes("Reading deleted") || !bodyText.includes("124/79");
  }

  result.finalUrl = page.url();
  return result;
}

async function auditVaultFlow(page) {
  const unique = `Audit Vault ${Date.now()}`;
  const result = {
    created: false,
    edited: false,
    pinned: false,
    deleted: false,
    finalUrl: null,
  };

  await page.setViewportSize(viewports.desktop);
  await page.goto(`${baseUrl}/vault/new`, { waitUntil: "networkidle", timeout: 30000 });
  await page.locator("input[name='title']").fill(unique);
  await page.locator("textarea[name='content']").fill("Created by the local Web_App workflow audit.");
  await page.locator("input[name='linkedDate']").fill("2026-05-08");
  await page.locator("input[name='tags']").fill("audit, workflow");
  await page.locator("select[name='mood']").selectOption("GOOD").catch(() => undefined);
  await Promise.all([
    page.waitForURL(/\/vault\/\d+$/, { timeout: 15000 }),
    page.getByRole("button", { name: "Save Note" }).click(),
  ]);
  await page.waitForLoadState("networkidle");
  let bodyText = await page.locator("body").innerText();
  result.created = page.url().includes("/vault/") && bodyText.includes(unique);
  const idMatch = page.url().match(/\/vault\/(\d+)/);

  if (idMatch) {
    await page.goto(`${baseUrl}/vault/${idMatch[1]}/edit`, { waitUntil: "networkidle", timeout: 30000 });
    await page.locator("textarea[name='content']").fill("Updated by the local Web_App workflow audit.");
    await Promise.all([
      page.waitForURL(new RegExp(`/vault/${idMatch[1]}$`), { timeout: 15000 }),
      page.getByRole("button", { name: "Save Note" }).click(),
    ]);
    await page.waitForLoadState("networkidle");
    bodyText = await page.locator("body").innerText();
    result.edited = bodyText.includes("Updated by the local Web_App workflow audit.");

    await page.getByRole("button", { name: "Pin" }).click();
    await page.waitForLoadState("networkidle");
    bodyText = await page.locator("body").innerText();
    result.pinned = bodyText.includes("Pinned") || (await page.getByRole("button", { name: "Unpin" }).count()) > 0;

    await page.getByRole("button", { name: "Delete" }).first().click();
    const confirmOk = page.locator("#confirmOk");
    if (await confirmOk.isVisible().catch(() => false)) {
      await Promise.all([
        page.waitForLoadState("networkidle"),
        confirmOk.click(),
      ]);
    } else {
      page.once("dialog", async (dialog) => dialog.accept());
      await page.waitForLoadState("networkidle");
    }
    bodyText = await page.locator("body").innerText();
    result.deleted = page.url().includes("/vault") && !bodyText.includes(unique);
  }

  result.finalUrl = page.url();
  return result;
}

async function auditInboxFlow(page) {
  const unique = `Audit inbox ${Date.now()}`;
  const result = {
    threadAvailable: false,
    sent: null,
    finalUrl: null,
  };

  await page.setViewportSize(viewports.desktop);
  await page.goto(`${baseUrl}/inbox`, { waitUntil: "networkidle", timeout: 30000 });
  const threadLink = page.locator("#inboxThreadList a[href^='/inbox/']").first();
  if (!(await threadLink.count())) {
    result.finalUrl = page.url();
    return result;
  }

  result.threadAvailable = true;
  await Promise.all([
    page.waitForURL(/\/inbox\/\d+$/, { timeout: 15000 }),
    threadLink.click(),
  ]);
  await page.waitForLoadState("domcontentloaded");
  await page.locator("#inboxBody").fill(unique);
  await Promise.all([
    page.waitForURL(/\/inbox\/\d+$/, { timeout: 15000 }),
    page.getByRole("button", { name: "Send" }).click(),
  ]);
  await page.waitForLoadState("domcontentloaded");
  await page.waitForFunction((message) => document.body?.innerText?.includes(message), unique, { timeout: 15000 }).catch(() => undefined);
  let bodyText = await page.locator("body").innerText();
  if (!bodyText.includes(unique)) {
    await page.goto(`${baseUrl}/inbox`, { waitUntil: "networkidle", timeout: 30000 });
    bodyText = await page.locator("body").innerText();
  }
  result.sent = bodyText.includes(unique);
  result.finalUrl = page.url();
  return result;
}

async function auditMerchCheckoutFlow(page) {
  const result = {
    checkoutAvailable: false,
    newCardSectionVisible: null,
    cardTokenized: null,
    completed: null,
    finalUrl: null,
  };

  await page.setViewportSize(viewports.desktop);
  await page.goto(`${baseUrl}/merch`, { waitUntil: "networkidle", timeout: 30000 });
  const buyLink = page.locator("a[href*='/buy']").first();
  if (!(await buyLink.count())) {
    result.finalUrl = page.url();
    return result;
  }

  await Promise.all([
    page.waitForLoadState("networkidle"),
    buyLink.click(),
  ]);
  result.checkoutAvailable = page.url().includes("/buy");

  const form = page.locator("form[data-simulated-payment-form='true']").first();
  if (!(await form.count())) {
    result.completed = await page.getByRole("button", { name: /continue to secure checkout|complete demo purchase/i }).isDisabled().then((disabled) => disabled === true).catch(() => null);
    result.finalUrl = page.url();
    return result;
  }

  const differentCard = form.locator("input[data-select-new-card='true']");
  if (await differentCard.count()) {
    await differentCard.check();
  }
  result.newCardSectionVisible = await form.locator("[data-new-card-section='true']").isVisible().catch(() => false);
  await form.locator("input[name='newCardHolderName']").fill("Audit Client");
  await form.locator("[data-card-number-display='true']").fill("4242 4242 4242 4242");
  await form.locator("input[name='newBrand']").fill("Visa");
  await form.locator("input[name='newExpiryMonth']").fill("12");
  await form.locator("input[name='newExpiryYear']").fill("2030");
  await Promise.all([
    page.waitForLoadState("networkidle"),
    form.getByRole("button", { name: "Complete demo purchase" }).click(),
  ]);
  result.finalUrl = page.url();
  const bodyText = await page.locator("body").innerText();
  result.cardTokenized = true;
  result.completed = page.url().includes("/orders") && /Demo order|Order/i.test(bodyText);
  return result;
}

async function auditMobileNav(page) {
  await page.setViewportSize(viewports.mobile);
  await page.goto(`${baseUrl}/`, { waitUntil: "networkidle" });
  const before = await page.locator("[data-mobile-links]").isVisible().catch(() => false);
  const trigger = page.locator("#siteNavButton");
  if (await trigger.count()) {
    await trigger.click();
    await page.waitForTimeout(250);
  }
  const after = await page.locator("[data-mobile-links]").isVisible().catch(() => false);
  return { before, after, url: page.url() };
}

async function run() {
  await ensureDir(outputDir);
  const browser = await chromium.launch({ headless: true });
  const results = [];

  try {
    const publicContext = await browser.newContext();
    const publicPage = await publicContext.newPage();
    results.push({ type: "interaction", label: "invalid-login", result: await auditInvalidLogin(publicPage) });
    results.push({ type: "interaction", label: "login-controls", result: await auditLoginInteractions(publicPage) });
    results.push({ type: "interaction", label: "mobile-nav", result: await auditMobileNav(publicPage) });
    results.push({ type: "access", label: "unauthenticated-protected-routes", result: await auditProtectedRoutes(publicPage) });
    for (const entry of publicPages) {
      for (const viewportName of entry.viewports) {
        results.push(await capture(publicPage, entry, viewportName));
      }
    }
    await publicContext.close();

    for (const roleAudit of roleAudits) {
      const context = await browser.newContext();
      const page = await context.newPage();
      results.push({ type: "login", role: roleAudit.role, result: await login(page, roleAudit.username, roleAudit.role) });
      results.push({ type: "access", role: roleAudit.role, label: "role-locks", result: await auditRoleLocks(page, roleAudit.role) });
      if (roleAudit.role === "trainer") {
        results.push({ type: "interaction", role: roleAudit.role, label: "schedule-controls", result: await auditScheduleInteractions(page) });
      }
      if (roleAudit.role === "client") {
        results.push({ type: "interaction", role: roleAudit.role, label: "chat-widget", result: await auditChatWidget(page) });
        results.push({ type: "workflow", role: roleAudit.role, label: "goal-data-flow", result: await auditGoalDataFlow(page) });
        results.push({ type: "workflow", role: roleAudit.role, label: "notes-data-flow", result: await auditNotesDataFlow(page) });
        results.push({ type: "workflow", role: roleAudit.role, label: "health-record-data-flow", result: await auditHealthRecordDataFlow(page) });
        results.push({ type: "workflow", role: roleAudit.role, label: "profile-update-flow", result: await auditProfileUpdateFlow(page) });
        results.push({ type: "workflow", role: roleAudit.role, label: "blood-pressure-flow", result: await auditBloodPressureFlow(page) });
        results.push({ type: "workflow", role: roleAudit.role, label: "vault-flow", result: await auditVaultFlow(page) });
        results.push({ type: "workflow", role: roleAudit.role, label: "inbox-flow", result: await auditInboxFlow(page) });
        results.push({ type: "workflow", role: roleAudit.role, label: "merch-checkout-flow", result: await auditMerchCheckoutFlow(page) });
      }
      for (const entry of roleAudit.pages) {
        for (const viewportName of entry.viewports) {
          results.push(await capture(page, entry, viewportName, roleAudit.role));
        }
      }
      await context.close();
    }
  } finally {
    await browser.close();
  }

  const summary = {
    generatedAt: new Date().toISOString(),
    baseUrl,
    resultCount: results.length,
    findings: results
      .filter((item) => item.result?.stayedOnLogin === false
        || item.result?.hasErrorText === false
        || item.result?.passwordToggleWorked === false
        || item.result?.after === false
        || item.result?.some?.((check) => check.redirectedToLogin === false || check.blocked === false)
        || item.result?.searchEmptyStateWorked === false
        || item.result?.createModalOpened === false
        || item.result?.createModalClosed === false
        || item.result?.previewOpened === false
        || item.result?.duplicateCancelPreservedPage === false
        || item.result?.duplicateRequestHadCsrf === false
        || item.result?.created === false
        || item.result?.checkInSaved === false
        || item.result?.appLoaded === false
        || item.result?.autosaved === false
        || item.result?.searchFound === false
        || item.result?.deleted === false
        || item.result?.formLoaded === false
        || item.result?.submitted === false
        || item.result?.editOpened === false
        || item.result?.saved === false
        || item.result?.edited === false
        || item.result?.pinned === false
        || item.result?.sent === false
        || item.result?.checkoutAvailable === false
        || item.result?.newCardSectionVisible === false
        || item.result?.cardTokenized === false
        || item.result?.completed === false
        || item.diagnostics?.hasHorizontalOverflow
        || item.consoleMessages?.length
        || item.diagnostics?.deadLinks?.length
        || item.diagnostics?.missingButtonNames?.length
        || item.diagnostics?.suspiciousText?.length
        || item.status >= 400
        || item.responseError)
      .map((item) => ({
        role: item.role,
        label: item.label,
        path: item.path,
        viewport: item.viewport,
        status: item.status,
        finalUrl: item.finalUrl,
        responseError: item.responseError,
        hasHorizontalOverflow: item.diagnostics?.hasHorizontalOverflow,
        consoleMessages: item.consoleMessages,
        deadLinks: item.diagnostics?.deadLinks,
        missingButtonNames: item.diagnostics?.missingButtonNames,
        suspiciousText: item.diagnostics?.suspiciousText,
        screenshot: item.screenshot,
      })),
  };

  await fs.writeFile(path.join(outputDir, "results.json"), JSON.stringify(results, null, 2), "utf8");
  await fs.writeFile(path.join(outputDir, "summary.json"), JSON.stringify(summary, null, 2), "utf8");
  console.log(JSON.stringify(summary, null, 2));
}

run().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
