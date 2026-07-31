import fs from "node:fs/promises";
import path from "node:path";
import process from "node:process";
import { spawnSync } from "node:child_process";
import AxeBuilder from "../playwright-local/node_modules/@axe-core/playwright/dist/index.mjs";
import { chromium } from "../playwright-local/node_modules/playwright/index.mjs";

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
const outputDir = path.resolve(option("--output", path.join("output", "playwright", "release-gate")));
const selectedChecks = new Set(option("--checks", "responsive,axe,performance,lighthouse").split(",").map((value) => value.trim()));
const password = process.env.AUDIT_PASSWORD || "Demo123!";
const lighthouseVersion = "13.4.1";

const widths = [390, 768, 1024, 1280, 1366, 1440, 1536, 1920];
const axeWidths = [390, 1440];
const performanceSamples = 3;

const journeys = [
  {
    id: "public",
    primaryPath: "/",
    routes: [
      { label: "home", path: "/" },
      { label: "pricing", path: "/pricing" },
    ],
  },
  {
    id: "login",
    primaryPath: "/login",
    routes: [{ label: "login", path: "/login" }],
  },
  {
    id: "client",
    role: "client",
    username: "demo_client",
    primaryPath: "/dashboard",
    routes: [
      { label: "dashboard", path: "/dashboard" },
      { label: "calendar", path: "/calendar?view=month" },
    ],
  },
  {
    id: "trainer",
    role: "trainer",
    username: "demo_trainer",
    primaryPath: "/trainer/dashboard",
    routes: [
      { label: "dashboard", path: "/trainer/dashboard" },
      { label: "schedules", path: "/schedules" },
    ],
  },
  {
    id: "gym",
    role: "gym",
    username: "demo_gym",
    primaryPath: "/gym/dashboard",
    routes: [
      { label: "dashboard", path: "/gym/dashboard" },
      { label: "trainers", path: "/gym/admin/trainers" },
    ],
  },
  {
    id: "admin",
    role: "admin",
    username: "demo_admin",
    primaryPath: "/admin/dashboard",
    routes: [
      { label: "dashboard", path: "/admin/dashboard" },
      { label: "feedback", path: "/admin/feedback" },
    ],
  },
];

const report = {
  generatedAt: new Date().toISOString(),
  baseUrl,
  widths,
  axeWidths,
  throttle: {
    profile: "Slow 4G with 4× CPU slowdown",
    latencyMs: 150,
    downloadKbps: 1600,
    uploadKbps: 750,
    samples: performanceSamples,
  },
  responsive: [],
  axe: [],
  performance: [],
  lighthouse: [],
  findings: [],
};

function slug(value) {
  return value.replace(/[^a-z0-9-]+/gi, "-").replace(/^-|-$/g, "").toLowerCase();
}

function median(values) {
  const sorted = values.filter(Number.isFinite).sort((a, b) => a - b);
  if (!sorted.length) return null;
  const middle = Math.floor(sorted.length / 2);
  return sorted.length % 2 ? sorted[middle] : (sorted[middle - 1] + sorted[middle]) / 2;
}

function round(value, digits = 0) {
  if (!Number.isFinite(value)) return null;
  const factor = 10 ** digits;
  return Math.round(value * factor) / factor;
}

function addFinding(area, journey, message, evidence = {}) {
  report.findings.push({ area, journey: journey.id, message, evidence });
}

async function openPage(page, url, timeout = 60_000) {
  const response = await page.goto(url, { waitUntil: "domcontentloaded", timeout });
  await page.waitForLoadState("networkidle", { timeout: 5_000 }).catch(() => {});
  await page.waitForTimeout(250);
  return response;
}

async function completeTourIfVisible(page) {
  const skip = page.locator("#siteTourSkip");
  if (!(await skip.count()) || !(await skip.isVisible().catch(() => false))) return;
  await Promise.all([
    page.waitForNavigation({ waitUntil: "domcontentloaded", timeout: 10_000 }).catch(() => {}),
    skip.click(),
  ]);
  await page.waitForLoadState("networkidle", { timeout: 5_000 }).catch(() => {});
}

async function login(page, journey) {
  await openPage(page, `${baseUrl}/login`);
  if (["trainer", "gym"].includes(journey.role)) {
    await page.locator(`[data-role="${journey.role}"]`).click();
  }

  const usernameSelector = journey.role === "gym" ? "#gymUsername" : "#username";
  const passwordSelector = journey.role === "gym" ? "#gymPassword" : "#password";
  await page.locator(usernameSelector).fill(journey.username);

  if (journey.role === "trainer") {
    await page.locator("#trainerCode1").fill("2407");
    await page.locator("#trainerCode2").fill("8190");
    await page.locator("#trainerCode3").fill("3465");
  }
  if (journey.role === "gym") {
    await page.locator("#gymSecretCode1").fill("4827");
    await page.locator("#gymSecretCode2").fill("0019");
    await page.locator("#gymSecretCode3").fill("3845");
    await page.locator("#gymSecretCode4").fill("6203");
  }

  await page.locator(passwordSelector).fill(password);
  await Promise.all([
    page.waitForNavigation({ waitUntil: "domcontentloaded", timeout: 30_000 }).catch(() => {}),
    page.locator("#loginForm button[type='submit']").click(),
  ]);
  await page.waitForLoadState("networkidle", { timeout: 5_000 }).catch(() => {});
  await completeTourIfVisible(page);

  if (new URL(page.url()).pathname.startsWith("/login")) {
    throw new Error(`${journey.id} demo login did not leave the login page.`);
  }
}

async function createSession(browser, journey) {
  const context = await browser.newContext({
    viewport: { width: 1440, height: 1000 },
    reducedMotion: "reduce",
  });
  const page = await context.newPage();
  if (journey.role) await login(page, journey);
  return { context, page };
}

function trackRuntime(page) {
  const consoleErrors = [];
  const pageErrors = [];
  const failedRequests = [];
  const onConsole = (message) => {
    if (message.type() === "error") consoleErrors.push(message.text());
  };
  const onPageError = (error) => pageErrors.push(String(error));
  const onRequestFailed = (request) => {
    if (request.url().startsWith(baseUrl)) {
      failedRequests.push(`${request.method()} ${request.url()} — ${request.failure()?.errorText || "failed"}`);
    }
  };
  page.on("console", onConsole);
  page.on("pageerror", onPageError);
  page.on("requestfailed", onRequestFailed);
  return {
    consoleErrors,
    pageErrors,
    failedRequests,
    stop() {
      page.off("console", onConsole);
      page.off("pageerror", onPageError);
      page.off("requestfailed", onRequestFailed);
    },
  };
}

async function inspectResponsive(page) {
  return page.evaluate(() => {
    const visible = (element) => {
      if (element.closest("[hidden], [aria-hidden='true'], [inert]")) return false;
      const style = getComputedStyle(element);
      const rect = element.getBoundingClientRect();
      return style.display !== "none"
        && style.visibility !== "hidden"
        && Number(style.opacity) !== 0
        && rect.width > 0
        && rect.height > 0;
    };
    const documentWidth = Math.max(document.documentElement.scrollWidth, document.body?.scrollWidth || 0);
    const overflowElements = Array.from(document.querySelectorAll("body *"))
      .filter(visible)
      .map((element) => {
        const rect = element.getBoundingClientRect();
        return { element, rect };
      })
      .filter(({ rect }) => rect.left < -3 || rect.right > window.innerWidth + 3)
      .slice(0, 8)
      .map(({ element, rect }) => ({
        selector: `${element.tagName.toLowerCase()}${element.id ? `#${element.id}` : ""}${element.classList.length ? `.${Array.from(element.classList).slice(0, 2).join(".")}` : ""}`,
        left: Math.round(rect.left),
        right: Math.round(rect.right),
        width: Math.round(rect.width),
      }));
    return {
      title: document.title,
      pathname: location.pathname,
      viewportWidth: window.innerWidth,
      documentWidth,
      mainCount: Array.from(document.querySelectorAll("main")).filter(visible).length,
      h1Count: Array.from(document.querySelectorAll("h1")).filter(visible).length,
      overflowElements,
    };
  });
}

async function runResponsive(browser) {
  for (const journey of journeys) {
    const { context, page } = await createSession(browser, journey);
    try {
      for (const route of journey.routes) {
        for (const width of widths) {
          const height = width === 390 ? 844 : width < 1024 ? 900 : 1000;
          await page.setViewportSize({ width, height });
          const runtime = trackRuntime(page);
          let status = null;
          let error = null;
          try {
            status = (await openPage(page, `${baseUrl}${route.path}`))?.status() ?? null;
          } catch (navigationError) {
            error = String(navigationError);
          }
          const diagnostics = error ? null : await inspectResponsive(page);
          runtime.stop();

          const result = {
            journey: journey.id,
            route: route.label,
            path: route.path,
            width,
            status,
            finalUrl: page.url(),
            error,
            diagnostics,
            consoleErrors: runtime.consoleErrors,
            pageErrors: runtime.pageErrors,
            failedRequests: runtime.failedRequests,
          };
          report.responsive.push(result);

          const expectedPath = new URL(`${baseUrl}${route.path}`).pathname;
          const failed = Boolean(error)
            || status !== 200
            || diagnostics?.pathname !== expectedPath
            || diagnostics?.documentWidth > width + 3
            || diagnostics?.mainCount !== 1
            || diagnostics?.h1Count !== 1
            || runtime.consoleErrors.length
            || runtime.pageErrors.length
            || runtime.failedRequests.length;
          if (failed) {
            addFinding("responsive", journey, `${route.label} failed the ${width}px responsive contract.`, result);
            const screenshotPath = path.join(outputDir, "screenshots", `responsive-${journey.id}-${route.label}-${width}.png`);
            await page.screenshot({ path: screenshotPath, fullPage: true }).catch(() => {});
          }
        }
      }
    } finally {
      await context.close();
    }
  }
}

async function runAxe(browser) {
  for (const journey of journeys) {
    const { context, page } = await createSession(browser, journey);
    try {
      for (const route of journey.routes) {
        for (const width of axeWidths) {
          await page.setViewportSize({ width, height: width === 390 ? 844 : 1000 });
          await openPage(page, `${baseUrl}${route.path}`);
          const results = await new AxeBuilder({ page })
            .withTags(["wcag2a", "wcag2aa", "wcag21a", "wcag21aa", "wcag22aa"])
            .analyze();
          const violations = results.violations.map((violation) => ({
            id: violation.id,
            impact: violation.impact,
            help: violation.help,
            helpUrl: violation.helpUrl,
            nodes: violation.nodes.slice(0, 5).map((node) => ({
              target: node.target,
              html: node.html.slice(0, 500),
              summary: node.failureSummary,
            })),
            nodeCount: violation.nodes.length,
          }));
          const serious = violations.filter(({ impact }) => ["critical", "serious"].includes(impact));
          const contrast = violations.filter(({ id }) => id === "color-contrast");
          const result = {
            journey: journey.id,
            route: route.label,
            path: route.path,
            width,
            violations,
            seriousCount: serious.length,
            contrastNodeCount: contrast.reduce((sum, violation) => sum + violation.nodeCount, 0),
          };
          report.axe.push(result);
          if (serious.length) {
            addFinding("axe", journey, `${route.label} has ${serious.length} serious/critical axe violation(s) at ${width}px.`, result);
          }
        }
      }
    } finally {
      await context.close();
    }
  }
}

async function collectPerformanceSample(context, journey) {
  const page = await context.newPage();
  await page.addInitScript(() => {
    window.__releaseGatePerformance = { lcp: 0, cls: 0, longTasks: 0 };
    try {
      new PerformanceObserver((list) => {
        const entries = list.getEntries();
        const latest = entries[entries.length - 1];
        if (latest) window.__releaseGatePerformance.lcp = latest.startTime;
      }).observe({ type: "largest-contentful-paint", buffered: true });
      new PerformanceObserver((list) => {
        for (const entry of list.getEntries()) {
          if (!entry.hadRecentInput) window.__releaseGatePerformance.cls += entry.value;
        }
      }).observe({ type: "layout-shift", buffered: true });
      new PerformanceObserver((list) => {
        window.__releaseGatePerformance.longTasks += list.getEntries().length;
      }).observe({ type: "longtask", buffered: true });
    } catch {
      // Older engines may not expose every observer type.
    }
  });

  const cdp = await context.newCDPSession(page);
  await cdp.send("Network.enable");
  await cdp.send("Network.clearBrowserCache");
  await cdp.send("Network.emulateNetworkConditions", {
    offline: false,
    latency: 150,
    downloadThroughput: (1600 * 1024) / 8,
    uploadThroughput: (750 * 1024) / 8,
    connectionType: "cellular3g",
  });
  await cdp.send("Emulation.setCPUThrottlingRate", { rate: 4 });

  let response = null;
  let error = null;
  try {
    response = await page.goto(`${baseUrl}${journey.primaryPath}`, { waitUntil: "load", timeout: 90_000 });
    await page.waitForTimeout(2_500);
  } catch (navigationError) {
    error = String(navigationError);
  }

  const metrics = error ? null : await page.evaluate(() => {
    const navigation = performance.getEntriesByType("navigation")[0];
    const paints = Object.fromEntries(performance.getEntriesByType("paint").map((entry) => [entry.name, entry.startTime]));
    const state = window.__releaseGatePerformance || {};
    return {
      ttfb: navigation?.responseStart || null,
      domContentLoaded: navigation?.domContentLoadedEventEnd || null,
      load: navigation?.loadEventEnd || null,
      fcp: paints["first-contentful-paint"] || null,
      lcp: state.lcp || null,
      cls: state.cls || 0,
      longTasks: state.longTasks || 0,
      transferBytes: performance.getEntriesByType("resource")
        .reduce((sum, entry) => sum + (entry.transferSize || 0), navigation?.transferSize || 0),
    };
  });

  await cdp.send("Emulation.setCPUThrottlingRate", { rate: 1 }).catch(() => {});
  await cdp.send("Network.emulateNetworkConditions", {
    offline: false,
    latency: 0,
    downloadThroughput: -1,
    uploadThroughput: -1,
  }).catch(() => {});
  const finalUrl = page.url();
  await page.close();
  return { status: response?.status() ?? null, finalUrl, error, metrics };
}

async function runPerformance(browser) {
  for (const journey of journeys) {
    const { context, page } = await createSession(browser, journey);
    await page.close();
    try {
      const samples = [];
      for (let sample = 0; sample < performanceSamples; sample += 1) {
        samples.push(await collectPerformanceSample(context, journey));
      }
      const metrics = {
        ttfb: round(median(samples.map((sample) => sample.metrics?.ttfb))),
        domContentLoaded: round(median(samples.map((sample) => sample.metrics?.domContentLoaded))),
        load: round(median(samples.map((sample) => sample.metrics?.load))),
        fcp: round(median(samples.map((sample) => sample.metrics?.fcp))),
        lcp: round(median(samples.map((sample) => sample.metrics?.lcp))),
        cls: round(median(samples.map((sample) => sample.metrics?.cls)), 3),
        longTasks: round(median(samples.map((sample) => sample.metrics?.longTasks))),
        transferBytes: round(median(samples.map((sample) => sample.metrics?.transferBytes))),
      };
      const result = { journey: journey.id, path: journey.primaryPath, samples, median: metrics };
      report.performance.push(result);
      const failed = samples.some(({ error, status }) => error || status !== 200)
        || metrics.fcp === null
        || metrics.fcp > 3_000
        || (metrics.lcp !== null && metrics.lcp > 4_000)
        || metrics.load === null
        || metrics.load > 7_000
        || metrics.cls > 0.1;
      if (failed) {
        addFinding("performance", journey, `${journey.primaryPath} exceeded the Slow-4G/4×-CPU performance contract.`, result);
      }
    } finally {
      await context.close();
    }
  }
}

async function runLighthouse(browser) {
  const npxCommand = process.platform === "win32"
    ? process.execPath
    : "npx";
  const npxPrefixArgs = process.platform === "win32"
    ? [path.join(path.dirname(process.execPath), "node_modules", "npm", "bin", "npx-cli.js")]
    : [];
  for (const journey of journeys) {
    const { context, page } = await createSession(browser, journey);
    try {
      const cookies = journey.role ? await context.cookies(baseUrl) : [];
      const headersPath = path.join(outputDir, `.lighthouse-${journey.id}-headers.json`);
      const lighthousePath = path.join(outputDir, "lighthouse", `${journey.id}.json`);
      await fs.rm(lighthousePath, { force: true });
      const cookieHeader = cookies.map(({ name, value }) => `${name}=${value}`).join("; ");
      if (cookieHeader) {
        await fs.writeFile(headersPath, JSON.stringify({ Cookie: cookieHeader }), { mode: 0o600 });
      }

      const categories = journey.id === "public" || journey.id === "login"
        ? "performance,accessibility,best-practices,seo"
        : "performance,accessibility,best-practices";
      const lighthouseArgs = [
        "--yes",
        `lighthouse@${lighthouseVersion}`,
        `${baseUrl}${journey.primaryPath}`,
        "--quiet",
        "--output=json",
        `--output-path=${lighthousePath}`,
        `--only-categories=${categories}`,
        "--disable-full-page-screenshot",
        "--chrome-flags=--headless --no-sandbox",
      ];
      if (cookieHeader) lighthouseArgs.push(`--extra-headers=${headersPath}`);

      const run = spawnSync(npxCommand, [...npxPrefixArgs, ...lighthouseArgs], {
        cwd: process.cwd(),
        env: { ...process.env, CHROME_PATH: chromium.executablePath() },
        encoding: "utf8",
        timeout: 150_000,
      });
      if (cookieHeader) await fs.rm(headersPath, { force: true });

      let raw;
      try {
        raw = JSON.parse(await fs.readFile(lighthousePath, "utf8"));
      } catch {
        raw = null;
      }

      if (!raw) {
        const result = {
          journey: journey.id,
          path: journey.primaryPath,
          exitCode: run.status,
          error: String(run.error || run.stderr || run.stdout || "Lighthouse failed.").slice(0, 2_000),
        };
        report.lighthouse.push(result);
        addFinding("lighthouse", journey, "Lighthouse could not complete.", result);
        continue;
      }

      const scores = Object.fromEntries(Object.entries(raw.categories).map(([id, category]) => [id, round(category.score * 100)]));
      const metrics = {
        fcp: raw.audits["first-contentful-paint"]?.numericValue ?? null,
        lcp: raw.audits["largest-contentful-paint"]?.numericValue ?? null,
        speedIndex: raw.audits["speed-index"]?.numericValue ?? null,
        totalBlockingTime: raw.audits["total-blocking-time"]?.numericValue ?? null,
        cls: raw.audits["cumulative-layout-shift"]?.numericValue ?? null,
      };
      const weakAudits = Object.values(raw.audits)
        .filter((audit) => typeof audit.score === "number" && audit.score < 0.5 && !["manual", "notApplicable"].includes(audit.scoreDisplayMode))
        .slice(0, 20)
        .map((audit) => ({ id: audit.id, title: audit.title, score: audit.score, displayValue: audit.displayValue || null }));
      const result = {
        journey: journey.id,
        path: journey.primaryPath,
        requestedUrl: raw.requestedUrl,
        finalUrl: raw.finalUrl,
        scores,
        metrics,
        weakAudits,
        report: path.relative(outputDir, lighthousePath).replaceAll("\\", "/"),
        runnerWarning: run.status === 0
          ? null
          : String(run.stderr || run.stdout || `Lighthouse exited with code ${run.status}.`).slice(0, 2_000),
      };
      report.lighthouse.push(result);

      const expectedPath = new URL(`${baseUrl}${journey.primaryPath}`).pathname;
      const finalPath = new URL(raw.finalUrl).pathname;
      const failed = finalPath !== expectedPath
        || scores.performance < 75
        || scores.accessibility < 95
        || scores["best-practices"] < 90
        || ((journey.id === "public" || journey.id === "login") && scores.seo < 85);
      if (failed) {
        addFinding("lighthouse", journey, `${journey.primaryPath} missed a Lighthouse release threshold.`, result);
      }
    } finally {
      await context.close();
    }
  }
}

function markdownTable(headers, rows) {
  const safe = (value) => String(value ?? "—").replaceAll("|", "\\|").replaceAll("\n", " ");
  return [
    `| ${headers.join(" | ")} |`,
    `| ${headers.map(() => "---").join(" | ")} |`,
    ...rows.map((row) => `| ${row.map(safe).join(" | ")} |`),
  ].join("\n");
}

async function writeReport() {
  const responsiveFailures = report.findings.filter(({ area }) => area === "responsive").length;
  const axeFailures = report.findings.filter(({ area }) => area === "axe").length;
  const performanceFailures = report.findings.filter(({ area }) => area === "performance").length;
  const lighthouseFailures = report.findings.filter(({ area }) => area === "lighthouse").length;

  const markdown = [
    "# One To One release gate",
    "",
    `Generated: ${report.generatedAt}`,
    "",
    "## Summary",
    "",
    `- Responsive cases: ${report.responsive.length}; findings: ${responsiveFailures}`,
    `- Axe cases: ${report.axe.length}; serious/critical findings: ${axeFailures}`,
    `- Throttled performance journeys: ${report.performance.length}; findings: ${performanceFailures}`,
    `- Lighthouse journeys: ${report.lighthouse.length}; findings: ${lighthouseFailures}`,
    "",
    "## Throttled performance",
    "",
    markdownTable(
      ["Journey", "FCP ms", "LCP ms", "Load ms", "CLS", "Transfer bytes"],
      report.performance.map(({ journey, median: metrics }) => [
        journey,
        metrics.fcp,
        metrics.lcp,
        metrics.load,
        metrics.cls,
        metrics.transferBytes,
      ]),
    ),
    "",
    "## Lighthouse",
    "",
    markdownTable(
      ["Journey", "Performance", "Accessibility", "Best practices", "SEO", "Final URL"],
      report.lighthouse.map(({ journey, scores = {}, finalUrl, error }) => [
        journey,
        scores.performance,
        scores.accessibility,
        scores["best-practices"],
        scores.seo,
        finalUrl || error,
      ]),
    ),
    "",
    "## Findings",
    "",
    report.findings.length
      ? report.findings.map((finding) => `- **${finding.area} / ${finding.journey}:** ${finding.message}`).join("\n")
      : "No release-threshold findings.",
    "",
  ].join("\n");

  await fs.writeFile(path.join(outputDir, "report.json"), JSON.stringify(report, null, 2));
  await fs.writeFile(path.join(outputDir, "report.md"), markdown);
}

async function run() {
  await fs.mkdir(path.join(outputDir, "screenshots"), { recursive: true });
  await fs.mkdir(path.join(outputDir, "lighthouse"), { recursive: true });

  const health = await fetch(`${baseUrl}/login`, { redirect: "manual", signal: AbortSignal.timeout(5_000) });
  if (health.status !== 200) throw new Error(`Expected ${baseUrl}/login to return 200; received ${health.status}.`);

  const browser = await chromium.launch({ headless: true });
  try {
    if (selectedChecks.has("responsive")) await runResponsive(browser);
    if (selectedChecks.has("axe")) await runAxe(browser);
    if (selectedChecks.has("performance")) await runPerformance(browser);
    if (selectedChecks.has("lighthouse")) await runLighthouse(browser);
  } finally {
    await browser.close();
  }
  await writeReport();

  const summary = {
    outputDir,
    responsiveCases: report.responsive.length,
    axeCases: report.axe.length,
    performanceJourneys: report.performance.length,
    lighthouseJourneys: report.lighthouse.length,
    findings: report.findings.length,
  };
  console.log(JSON.stringify(summary, null, 2));
  if (report.findings.length > 0) {
    process.exitCode = 1;
  }
}

run().catch(async (error) => {
  report.fatalError = String(error?.stack || error);
  await fs.mkdir(outputDir, { recursive: true }).catch(() => {});
  await writeReport().catch(() => {});
  console.error(error);
  process.exitCode = 1;
});
