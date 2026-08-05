import fs from "node:fs/promises";
import path from "node:path";
import process from "node:process";
import AxeBuilder from "../playwright-local/node_modules/@axe-core/playwright/dist/index.mjs";
import { chromium } from "../playwright-local/node_modules/playwright/index.mjs";
import { britishEnglish, profiles, standards, viewports } from "./site-simulation.config.mjs";

const args = process.argv.slice(2);

function option(name, fallback) {
  const inline = args.find((value) => value.startsWith(`${name}=`));
  if (inline) return inline.slice(name.length + 1);
  const index = args.indexOf(name);
  return index >= 0 && args[index + 1] && !args[index + 1].startsWith("--")
    ? args[index + 1]
    : fallback;
}

const baseUrl = option("--base-url", process.env.AUDIT_BASE_URL || "http://localhost:8081").replace(/\/$/, "");
const selectedProfileIds = new Set(option("--profiles", process.env.AUDIT_PROFILES || profiles.map(({ id }) => id).join(",")).split(",").map((value) => value.trim()));
const selectedViewportIds = new Set(option("--viewports", process.env.AUDIT_VIEWPORTS || "desktop,mobile").split(",").map((value) => value.trim()));
const failOn = option("--fail-on", process.env.AUDIT_FAIL_ON || "none");
const screenshotMode = option("--screenshots", process.env.AUDIT_SCREENSHOTS || "failures");
const password = process.env.AUDIT_PASSWORD || "Demo123!";
const outputDir = path.resolve(option("--output", process.env.AUDIT_OUTPUT || path.join("output", "playwright", "site-simulation")));
const workflowDir = path.resolve(process.env.AUDIT_WORKFLOW_OUTPUT || path.join("output", "playwright", "local-view-audit"));

const severityRank = { critical: 3, major: 2, minor: 1, info: 0, none: 99 };
const findings = [];
const pageResults = [];
let checksRun = 0;
let checksPassed = 0;

function slug(value) {
  return value.replace(/[^a-z0-9-]+/gi, "-").replace(/^-|-$/g, "").toLowerCase();
}

function clip(value, length = 300) {
  const text = typeof value === "string" ? value : JSON.stringify(value);
  return text.length > length ? `${text.slice(0, length)}…` : text;
}

function recordCheck(id, passed, context, evidence = []) {
  checksRun += 1;
  if (passed) {
    checksPassed += 1;
    return;
  }

  const standard = standards[id];
  const entries = Array.isArray(evidence) && evidence.length ? evidence : ["No additional evidence was returned."];
  for (const entry of entries.slice(0, 20)) {
    findings.push({
      id,
      category: standard.category,
      severity: standard.severity,
      standard: standard.title,
      profile: context.profile,
      page: context.page,
      path: context.path,
      viewport: context.viewport,
      expected: standard.expected,
      actual: clip(entry, 700),
      fix: standard.fix,
      screenshot: null,
    });
  }
}

async function login(page, profile) {
  await page.setViewportSize(viewports.desktop);
  await page.goto(`${baseUrl}/login`, { waitUntil: "domcontentloaded", timeout: 45_000 });
  await page.waitForLoadState("networkidle", { timeout: 10_000 }).catch(() => {});

  if (["trainer", "gym"].includes(profile.role)) {
    const roleTab = page.locator(`[data-role="${profile.role}"]`);
    if (await roleTab.count()) await roleTab.click();
  }

  const usernameSelector = profile.role === "gym" ? "#gymUsername" : "#username";
  const passwordSelector = profile.role === "gym" ? "#gymPassword" : "#password";
  await page.locator(usernameSelector).fill(profile.username);

  if (profile.role === "trainer") {
    await page.locator("#trainerCode1").fill("2407");
    await page.locator("#trainerCode2").fill("8190");
    await page.locator("#trainerCode3").fill("3465");
  }
  if (profile.role === "gym") {
    await page.locator("#gymSecretCode1").fill("4827");
    await page.locator("#gymSecretCode2").fill("0019");
    await page.locator("#gymSecretCode3").fill("3845");
    await page.locator("#gymSecretCode4").fill("6203");
  }

  await page.locator(passwordSelector).fill(password);
  await Promise.all([
    page.waitForLoadState("domcontentloaded", { timeout: 30_000 }).catch(() => {}),
    page.locator("#loginForm button[type='submit']").click(),
  ]);
  await page.waitForLoadState("networkidle", { timeout: 10_000 }).catch(() => {});
  return { finalUrl: page.url(), succeeded: !new URL(page.url()).pathname.startsWith("/login") };
}

async function inspectDom(page) {
  return page.evaluate((spellings) => {
    const visible = (element) => {
      const style = getComputedStyle(element);
      const rect = element.getBoundingClientRect();
      const visibilityOptions = { checkOpacity: true, checkVisibilityCSS: true };
      const visibleThroughAncestors = typeof element.checkVisibility !== "function" || element.checkVisibility(visibilityOptions);
      return visibleThroughAncestors
        && style.display !== "none"
        && style.visibility !== "hidden"
        && Number(style.opacity) !== 0
        && rect.width > 0
        && rect.height > 0;
    };
    const describe = (element) => {
      const selector = [
        element.tagName.toLowerCase(),
        element.id ? `#${element.id}` : "",
        element.classList?.length ? `.${Array.from(element.classList).slice(0, 2).join(".")}` : "",
      ].join("");
      const text = (element.innerText || element.getAttribute("aria-label") || element.getAttribute("name") || "").trim().replace(/\s+/g, " ");
      return `${selector}${text ? ` “${text.slice(0, 80)}”` : ""}`;
    };
    const accessibleName = (element) => {
      const labelledBy = (element.getAttribute("aria-labelledby") || "")
        .split(/\s+/)
        .filter(Boolean)
        .map((id) => document.getElementById(id)?.textContent || "")
        .join(" ");
      const labels = "labels" in element ? Array.from(element.labels || []).map((label) => label.textContent || "").join(" ") : "";
      const imageAlt = element.querySelector?.("img[alt]")?.getAttribute("alt") || "";
      const buttonValue = element.matches?.("input[type='button'], input[type='submit'], input[type='reset']") ? element.value : "";
      return [element.getAttribute("aria-label"), labelledBy, labels, element.innerText, buttonValue, element.title, imageAlt]
        .filter(Boolean)
        .join(" ")
        .trim();
    };

    const bodyText = document.body?.innerText || "";
    const documentWidth = Math.max(document.documentElement.scrollWidth, document.body?.scrollWidth || 0);
    const ids = Array.from(document.querySelectorAll("[id]")).map((element) => element.id).filter(Boolean);
    const duplicateIds = [...new Set(ids.filter((id, index) => ids.indexOf(id) !== index))];
    const controls = Array.from(document.querySelectorAll("a[href], button, input:not([type='hidden']), select, textarea, [role='button'], [role='link']"))
      .filter(visible);
    const unnamedControls = controls.filter((element) => !accessibleName(element)).map(describe);
    const unlabelledFields = Array.from(document.querySelectorAll("input:not([type='hidden']):not([type='submit']):not([type='button']), select, textarea"))
      .filter(visible)
      .filter((element) => !accessibleName(element))
      .map(describe);
    const missingAlt = Array.from(document.images).filter((image) => !image.hasAttribute("alt")).map(describe);
    const brokenImages = Array.from(document.images).filter((image) => visible(image) && image.complete && image.naturalWidth === 0).map((image) => `${describe(image)} -> ${image.currentSrc || image.src}`);
    const invalidLinks = Array.from(document.querySelectorAll("a[href]"))
      .filter(visible)
      .filter((link) => {
        const href = (link.getAttribute("href") || "").trim();
        if (!href || href.toLowerCase().startsWith("javascript:")) return true;
        if (href === "#") return true;
        if (href.startsWith("#")) return !document.getElementById(href.slice(1));
        return false;
      })
      .map((link) => `${describe(link)} -> ${link.getAttribute("href")}`);
    const headings = Array.from(document.querySelectorAll("h1, h2, h3, h4, h5, h6")).filter(visible);
    const emptyHeadings = headings.filter((heading) => !(heading.innerText || "").trim()).map(describe);
    const headingSkips = [];
    headings.reduce((previous, heading) => {
      const level = Number(heading.tagName.slice(1));
      if (previous && level > previous + 1) headingSkips.push(`${describe(heading)} follows H${previous}`);
      return level;
    }, 0);
    const overflowElements = Array.from(document.querySelectorAll("body *"))
      .filter(visible)
      .filter((element) => {
        const rect = element.getBoundingClientRect();
        return rect.right > innerWidth + 3 || rect.left < -3;
      })
      .slice(0, 20)
      .map((element) => {
        const rect = element.getBoundingClientRect();
        return `${describe(element)} spans ${Math.round(rect.left)}px to ${Math.round(rect.right)}px in a ${innerWidth}px viewport`;
      });
    const smallTargets = controls
      .filter((element) => element.tagName !== "A" || getComputedStyle(element).display !== "inline")
      .filter((element) => {
        const rect = element.getBoundingClientRect();
        return rect.width < 24 || rect.height < 24;
      })
      .slice(0, 20)
      .map((element) => {
        const rect = element.getBoundingClientRect();
        return `${describe(element)} is ${Math.round(rect.width)}×${Math.round(rect.height)}px`;
      });
    const smallText = Array.from(document.querySelectorAll("body *"))
      .filter(visible)
      .filter((element) => !element.closest('[aria-hidden="true"]'))
      .filter((element) => element.children.length === 0 && (element.textContent || "").trim() && parseFloat(getComputedStyle(element).fontSize) < 12)
      .slice(0, 20)
      .map((element) => `${describe(element)} uses ${getComputedStyle(element).fontSize}`);
    const placeholderPattern = /\b(lorem ipsum|todo|fixme|tbd|placeholder text|coming soon)\b/i;
    const placeholderText = bodyText.split("\n").map((line) => line.trim()).filter((line) => placeholderPattern.test(line)).slice(0, 20);
    const encodingText = bodyText.split("\n").map((line) => line.trim()).filter((line) => /Ã|Â|â€|ðŸ|�/.test(line)).slice(0, 20);
    const americanSpellings = [];
    for (const [american, british] of spellings) {
      const match = bodyText.match(new RegExp(`\\b${american}\\b`, "i"));
      if (match) americanSpellings.push(`“${match[0]}” should normally be “${british}”`);
    }

    return {
      title: document.title.trim(),
      metaDescription: document.querySelector("meta[name='description']")?.content?.trim() || "",
      mainCount: Array.from(document.querySelectorAll("main")).filter(visible).length,
      h1Count: Array.from(document.querySelectorAll("h1")).filter(visible).length,
      h1Text: Array.from(document.querySelectorAll("h1")).filter(visible).map((heading) => heading.innerText.trim()),
      documentWidth,
      viewportWidth: innerWidth,
      duplicateIds,
      unnamedControls,
      unlabelledFields,
      missingAlt,
      brokenImages,
      invalidLinks,
      emptyHeadings,
      headingSkips,
      overflowElements,
      smallTargets,
      smallText,
      placeholderText,
      encodingText,
      americanSpellings,
    };
  }, [...britishEnglish.entries()]);
}

async function findMissingRequiredSelectors(page, selectors, timeoutMs = 1_500) {
  const pending = new Set(selectors || []);
  const deadline = Date.now() + timeoutMs;

  while (pending.size) {
    for (const selector of [...pending]) {
      const locator = page.locator(selector);
      const count = await locator.count().catch(() => 0);
      let visibleMatch = false;
      for (let index = 0; index < count; index += 1) {
        if (await locator.nth(index).isVisible().catch(() => false)) {
          visibleMatch = true;
          break;
        }
      }
      if (visibleMatch) pending.delete(selector);
    }

    if (!pending.size || Date.now() >= deadline) break;
    await page.waitForTimeout(50);
  }

  return [...pending].map((selector) => `Required selector ${selector} is missing or hidden.`);
}

async function auditPage(page, profile, pageConfig, viewportId) {
  const viewport = viewports[viewportId];
  const context = { profile: profile.id, page: pageConfig.label, path: pageConfig.path, viewport: viewportId };
  const startFindingIndex = findings.length;
  const consoleErrors = [];
  const failedRequests = [];
  const responseErrors = [];
  const onConsole = (message) => {
    if (message.type() === "error") consoleErrors.push(message.text());
  };
  const onRequestFailed = (request) => failedRequests.push(`${request.method()} ${request.url()} — ${request.failure()?.errorText || "request failed"}`);
  const onResponse = (response) => {
    const request = response.request();
    if (response.status() >= 400 && new URL(response.url()).origin === new URL(baseUrl).origin && ["document", "script", "stylesheet", "image", "fetch", "xhr"].includes(request.resourceType())) {
      responseErrors.push(`${response.status()} ${request.method()} ${response.url()}`);
    }
  };
  page.on("console", onConsole);
  page.on("requestfailed", onRequestFailed);
  page.on("response", onResponse);

  await page.setViewportSize(viewport);
  let response = null;
  let navigationError = null;
  try {
    response = await page.goto(`${baseUrl}${pageConfig.path}`, { waitUntil: "domcontentloaded", timeout: 45_000 });
    await page.waitForLoadState("networkidle", { timeout: 10_000 }).catch(() => {});
    await page.waitForTimeout(200);
  } catch (error) {
    navigationError = String(error);
  }

  const status = response?.status() ?? null;
  const acceptedStatuses = pageConfig.allowedStatuses || [200];
  recordCheck("PAGE-001", !navigationError && acceptedStatuses.includes(status), context, [navigationError || `Expected ${acceptedStatuses.join(" or ")}; received HTTP ${status}.`]);

  const finalPath = new URL(page.url()).pathname;
  if (profile.id !== "public") {
    const expectedPath = new URL(`${baseUrl}${pageConfig.path}`).pathname;
    const allowedPaths = [expectedPath, ...(pageConfig.allowedFinalPaths || [])];
    const stayedAuthenticated = !finalPath.startsWith("/login") && allowedPaths.includes(finalPath);
    recordCheck("PAGE-006", stayedAuthenticated, context, [`Requested ${pageConfig.path}; browser finished at ${page.url()}.`]);
  }

  let diagnostics = null;
  if (!navigationError) {
    diagnostics = await inspectDom(page);
    recordCheck("PAGE-002", diagnostics.title.length >= 8 && diagnostics.title.length <= 70, context, [`Title is “${diagnostics.title || "(empty)"}” (${diagnostics.title.length} characters).`]);
    recordCheck("PAGE-003", diagnostics.mainCount === 1, context, [`Found ${diagnostics.mainCount} visible main landmarks.`]);
    recordCheck("PAGE-004", diagnostics.h1Count === 1 && diagnostics.h1Text[0], context, [`Found ${diagnostics.h1Count} visible H1 elements: ${diagnostics.h1Text.join(" | ") || "none"}.`]);
    if (pageConfig.metadata) {
      recordCheck("PAGE-005", diagnostics.metaDescription.length >= 50 && diagnostics.metaDescription.length <= 170, context, [`Meta description is ${diagnostics.metaDescription.length} characters: “${diagnostics.metaDescription || "(empty)"}”.`]);
    }

    const missingRequired = await findMissingRequiredSelectors(page, pageConfig.required);
    recordCheck("PAGE-007", missingRequired.length === 0, context, missingRequired);
    recordCheck("ELEMENT-001", diagnostics.unnamedControls.length === 0, context, diagnostics.unnamedControls);
    recordCheck("ELEMENT-002", diagnostics.unlabelledFields.length === 0, context, diagnostics.unlabelledFields);
    recordCheck("ELEMENT-003", diagnostics.missingAlt.length === 0, context, diagnostics.missingAlt);
    recordCheck("ELEMENT-004", diagnostics.duplicateIds.length === 0, context, diagnostics.duplicateIds.map((id) => `Duplicate id: #${id}`));
    recordCheck("ELEMENT-005", diagnostics.invalidLinks.length === 0, context, diagnostics.invalidLinks);
    recordCheck("ELEMENT-006", diagnostics.brokenImages.length === 0, context, diagnostics.brokenImages);
    recordCheck("ELEMENT-007", diagnostics.headingSkips.length === 0, context, diagnostics.headingSkips);
    // Decorative orbit/grid elements deliberately extend beyond clipped scene
    // boundaries. The page fails this contract only when that geometry creates
    // real document-level horizontal overflow for the user.
    recordCheck("DESIGN-001", diagnostics.documentWidth <= diagnostics.viewportWidth + 3, context, [`Document width ${diagnostics.documentWidth}px exceeds viewport width ${diagnostics.viewportWidth}px.`]);
    if (viewportId === "mobile") recordCheck("DESIGN-002", diagnostics.smallTargets.length === 0, context, diagnostics.smallTargets);
    recordCheck("DESIGN-003", diagnostics.smallText.length === 0, context, diagnostics.smallText);
    recordCheck("TEXT-001", diagnostics.encodingText.length === 0, context, diagnostics.encodingText);
    recordCheck("TEXT-002", diagnostics.placeholderText.length === 0, context, diagnostics.placeholderText);
    recordCheck("TEXT-003", diagnostics.emptyHeadings.length === 0, context, diagnostics.emptyHeadings);
    if (profile.id === "public") recordCheck("TEXT-004", diagnostics.americanSpellings.length === 0, context, diagnostics.americanSpellings);

    try {
      const axeResults = await new AxeBuilder({ page })
        .withTags(["wcag2a", "wcag2aa", "wcag21a", "wcag21aa"])
        .analyze();
      const serious = axeResults.violations.filter(({ impact }) => ["critical", "serious"].includes(impact));
      recordCheck("A11Y-001", serious.length === 0, context, serious.map((violation) => `${violation.id}: ${violation.help}. ${violation.nodes.length} affected node(s). Example: ${violation.nodes[0]?.target?.join(" ") || "unknown"}`));
    } catch (error) {
      recordCheck("A11Y-001", false, context, [`Axe could not analyse the page: ${String(error)}`]);
    }
  }

  recordCheck("RUNTIME-001", consoleErrors.length === 0, context, consoleErrors);
  recordCheck("RUNTIME-002", failedRequests.length === 0 && responseErrors.length === 0, context, [...failedRequests, ...responseErrors]);

  page.off("console", onConsole);
  page.off("requestfailed", onRequestFailed);
  page.off("response", onResponse);

  const pageFindings = findings.slice(startFindingIndex);
  let screenshot = null;
  if (screenshotMode === "all" || (screenshotMode === "failures" && pageFindings.length)) {
    screenshot = `${slug(`${profile.id}-${pageConfig.label}-${viewportId}`)}.png`;
    await page.screenshot({ path: path.join(outputDir, "screenshots", screenshot), fullPage: true }).catch(() => { screenshot = null; });
    for (const finding of pageFindings) finding.screenshot = screenshot ? `screenshots/${screenshot}` : null;
  }

  pageResults.push({
    profile: profile.id,
    page: pageConfig.label,
    path: pageConfig.path,
    viewport: viewportId,
    status,
    finalUrl: page.url(),
    findings: pageFindings.length,
    screenshot: screenshot ? `screenshots/${screenshot}` : null,
  });
}

function escapeXml(value) {
  return String(value).replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll('"', "&quot;").replaceAll("'", "&apos;");
}

function escapeCell(value) {
  return String(value ?? "").replaceAll("|", "\\|").replaceAll("\n", " ");
}

async function loadWorkflowResults() {
  const status = process.env.AUDIT_WORKFLOW_STATUS;
  if (status === "skipped") return { ran: false, completed: false, resultCount: 0, workflowCount: 0, findingCount: 0, findings: [] };
  if (status === "failed") return { ran: true, completed: false, resultCount: 0, workflowCount: 0, findingCount: 1, findings: [{ label: "workflow-runner", error: "The feature workflow process exited before producing a complete current result set." }] };
  try {
    const rawResults = JSON.parse(await fs.readFile(path.join(workflowDir, "results.json"), "utf8"));
    const rawSummary = JSON.parse(await fs.readFile(path.join(workflowDir, "summary.json"), "utf8"));
    return {
      ran: true,
      completed: true,
      resultCount: rawResults.length,
      workflowCount: rawResults.filter(({ type }) => ["workflow", "interaction", "access", "login"].includes(type)).length,
      findingCount: rawSummary.findings?.length || 0,
      findings: rawSummary.findings || [],
    };
  } catch {
    return { ran: false, completed: false, resultCount: 0, workflowCount: 0, findingCount: 0, findings: [] };
  }
}

async function writeReports(workflows, startedAt) {
  const counts = findings.reduce((result, finding) => ({ ...result, [finding.severity]: (result[finding.severity] || 0) + 1 }), { critical: 0, major: 0, minor: 0 });
  const report = {
    generatedAt: new Date().toISOString(),
    durationSeconds: Math.round((Date.now() - startedAt) / 100) / 10,
    baseUrl,
    profiles: [...selectedProfileIds],
    viewports: [...selectedViewportIds],
    checks: { run: checksRun, passed: checksPassed, failed: checksRun - checksPassed },
    pages: { run: pageResults.length, withFindings: pageResults.filter(({ findings: count }) => count > 0).length },
    counts,
    workflowSimulation: workflows,
    standards,
    pageResults,
    findings,
  };
  await fs.writeFile(path.join(outputDir, "report.json"), JSON.stringify(report, null, 2), "utf8");

  const lines = [
    "# One To One automated site simulation",
    "",
    `Generated: ${report.generatedAt}`,
    "",
    `Target: ${baseUrl}`,
    "",
    "## Outcome",
    "",
    `- ${report.pages.run} page/viewport combinations tested across ${report.profiles.length} profiles`,
    `- ${report.checks.passed}/${report.checks.run} standards checks passed`,
    `- ${counts.critical} critical, ${counts.major} major and ${counts.minor} minor findings`,
    `- ${workflows.completed ? `${workflows.workflowCount} login, access, interaction and feature-workflow simulations executed; ${workflows.findingCount} workflow findings` : workflows.ran ? "Feature workflow simulation started but did not complete; see its finding below" : "Feature workflow simulation was not run in this invocation"}`,
    "",
    "## Findings",
    "",
  ];
  if (!findings.length) {
    lines.push("No automated standards failures were found.", "");
  } else {
    lines.push("| Severity | Standard | Profile / page | Viewport | What is wrong | Recommended fix |", "|---|---|---|---|---|---|");
    for (const finding of findings) {
      lines.push(`| ${escapeCell(finding.severity.toUpperCase())} | ${escapeCell(`${finding.id} ${finding.standard}`)} | ${escapeCell(`${finding.profile} / ${finding.page}`)} | ${escapeCell(finding.viewport)} | ${escapeCell(finding.actual)} | ${escapeCell(finding.fix)} |`);
    }
    lines.push("");
  }

  if (workflows.findings.length) {
    lines.push("## Feature workflow findings", "", "These come from the existing full CRUD, access, messaging, payment and role journey simulation.", "", "| Profile | Journey | Evidence |", "|---|---|---|");
    for (const finding of workflows.findings) {
      lines.push(`| ${escapeCell(finding.role || "public")} | ${escapeCell(finding.label || finding.path || "workflow")} | ${escapeCell(clip(finding.result || finding.responseError || finding, 500))} |`);
    }
    lines.push("");
  }

  lines.push("## Page-by-page result", "", "| Profile | Page | Viewport | HTTP | Findings | Screenshot |", "|---|---|---|---:|---:|---|");
  for (const result of pageResults) {
    lines.push(`| ${escapeCell(result.profile)} | ${escapeCell(result.path)} | ${escapeCell(result.viewport)} | ${escapeCell(result.status ?? "-")} | ${result.findings} | ${result.screenshot ? `[open](${result.screenshot})` : "-"} |`);
  }
  lines.push("", "The complete criteria catalogue and machine-readable evidence are in `report.json`. CI-compatible failures are in `junit.xml`.", "");
  await fs.writeFile(path.join(outputDir, "report.md"), lines.join("\n"), "utf8");

  const testCases = pageResults.map((result) => {
    const relevant = findings.filter((finding) => finding.profile === result.profile && finding.page === result.page && finding.viewport === result.viewport);
    const body = relevant.length ? `<failure message="${escapeXml(`${relevant.length} standards finding(s)`)}">${escapeXml(relevant.map((finding) => `${finding.severity.toUpperCase()} ${finding.id}: ${finding.actual}\nFix: ${finding.fix}`).join("\n\n"))}</failure>` : "";
    return `  <testcase classname="site.${escapeXml(result.profile)}.${escapeXml(result.viewport)}" name="${escapeXml(result.page)}">${body}</testcase>`;
  });
  const junit = [`<?xml version="1.0" encoding="UTF-8"?>`, `<testsuite name="One To One site simulation" tests="${pageResults.length}" failures="${pageResults.filter(({ findings: count }) => count > 0).length}" time="${report.durationSeconds}">`, ...testCases, "</testsuite>", ""].join("\n");
  await fs.writeFile(path.join(outputDir, "junit.xml"), junit, "utf8");
  return report;
}

async function run() {
  const startedAt = Date.now();
  await fs.mkdir(path.join(outputDir, "screenshots"), { recursive: true });
  const unknownProfiles = [...selectedProfileIds].filter((id) => !profiles.some((profile) => profile.id === id));
  const unknownViewports = [...selectedViewportIds].filter((id) => !viewports[id]);
  if (unknownProfiles.length || unknownViewports.length) {
    throw new Error(`Unknown selection. Profiles: ${unknownProfiles.join(", ") || "valid"}; viewports: ${unknownViewports.join(", ") || "valid"}.`);
  }

  const browser = await chromium.launch({ headless: process.env.AUDIT_HEADED !== "1" });
  try {
    for (const profile of profiles.filter(({ id }) => selectedProfileIds.has(id))) {
      const browserContext = await browser.newContext({ reducedMotion: "reduce" });
      const page = await browserContext.newPage();
      if (profile.id !== "public") {
        const result = await login(page, profile);
        const loginContext = { profile: profile.id, page: "login journey", path: "/login", viewport: "desktop" };
        recordCheck("PAGE-006", result.succeeded, loginContext, [`Login finished at ${result.finalUrl}.`]);
      }
      for (const pageConfig of profile.pages) {
        for (const viewportId of Object.keys(viewports).filter((id) => selectedViewportIds.has(id))) {
          await auditPage(page, profile, pageConfig, viewportId);
        }
      }
      await browserContext.close();
    }
  } finally {
    await browser.close();
  }

  const workflows = await loadWorkflowResults();
  const report = await writeReports(workflows, startedAt);
  console.log(`Site simulation complete: ${report.checks.passed}/${report.checks.run} checks passed; ${report.counts.critical} critical, ${report.counts.major} major, ${report.counts.minor} minor findings.`);
  console.log(`Report: ${path.join(outputDir, "report.md")}`);

  if (failOn !== "none" && findings.some((finding) => severityRank[finding.severity] >= severityRank[failOn])) process.exitCode = 1;
}

run().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
