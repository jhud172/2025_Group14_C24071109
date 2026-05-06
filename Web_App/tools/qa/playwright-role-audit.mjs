import fs from "node:fs/promises";
import path from "node:path";
import process from "node:process";
import { Client } from "file:///C:/Users/jhuds/AppData/Local/npm-cache/_npx/5a9d879542beca3a/node_modules/@modelcontextprotocol/sdk/dist/esm/client/index.js";
import { StdioClientTransport } from "file:///C:/Users/jhuds/AppData/Local/npm-cache/_npx/5a9d879542beca3a/node_modules/@modelcontextprotocol/sdk/dist/esm/client/stdio.js";

const root = process.cwd();
const outputDir = path.join(root, "output", "playwright", "roles");
const command = process.platform === "win32" ? "npx.cmd" : "npx";
const args = [
  "@playwright/mcp@latest",
  "--browser",
  "firefox",
  "--isolated",
  "--headless",
  "--output-dir",
  outputDir,
];

const baseUrl = "https://two025-group14-c24071109-1.onrender.com";
const today = new Date().toISOString().slice(0, 10);

const roleConfigs = [
  {
    role: "client",
    username: "demo_client",
    pages: [
      "/dashboard",
      "/profile",
      "/calendar?view=month",
      "/calendar?view=week",
      `/calendar/day/${today}`,
      "/select-preferences",
      "/client/trainers",
    ],
  },
  {
    role: "trainer",
    username: "demo_trainer",
    pages: [
      "/trainer/dashboard",
      "/profile",
      "/calendar?view=month",
      "/calendar?view=week",
      `/calendar/day/${today}`,
      "/select-preferences",
      "/trainer/clients",
      "/trainer/library/workouts",
      "/trainer/library/programmes",
      "/trainer/templates",
    ],
  },
  {
    role: "gym",
    username: "demo_gym",
    pages: [
      "/gym/dashboard",
      "/profile",
      "/calendar?view=month",
      "/calendar?view=week",
      `/calendar/day/${today}`,
      "/select-preferences",
      "/gym/admin/trainers",
      "/gym/admin/memberships",
    ],
  },
];

function createClient(role) {
  const client = new Client({ name: `codex-role-audit-${role}`, version: "1.0.0" });
  const transport = new StdioClientTransport({
    command,
    args,
    cwd: root,
    stderr: "pipe",
  });
  const callTool = async (name, toolArgs = {}) => client.callTool({ name, arguments: toolArgs });
  return { client, transport, callTool };
}

async function ensureBrowser(callTool) {
  await callTool("browser_install", {});
}

async function saveJson(fileName, data) {
  const target = path.join(outputDir, fileName);
  await fs.writeFile(target, JSON.stringify(data, null, 2), "utf8");
}

async function setViewport(callTool, width, height) {
  await callTool("browser_resize", { width, height });
}

async function login(callTool, username) {
  await setViewport(callTool, 1440, 1200);
  await callTool("browser_navigate", { url: `${baseUrl}/login?devLogin=1` });
  await callTool("browser_wait_for", { time: 1 });
  return callTool("browser_run_code", {
    code: `async (page) => {
      await page.goto("${baseUrl}/login?devLogin=1", { waitUntil: "domcontentloaded" });
      await page.locator("#username").fill(${JSON.stringify(username)});
      await page.locator("#password").fill("Demo123!");
      await Promise.all([
        page.waitForLoadState("networkidle"),
        page.locator("#loginForm button[type='submit']").click()
      ]);
      await page.waitForTimeout(1500);
      return { url: page.url(), title: await page.title() };
    }`,
  });
}

async function setClientWeatherGraphMode(callTool) {
  await callTool("browser_navigate", { url: `${baseUrl}/select-preferences` });
  await callTool("browser_wait_for", { time: 1 });
  return callTool("browser_run_code", {
    code: `async (page) => {
      await page.locator("#weatherDisplayMode").selectOption("GRAPH");
      await Promise.all([
        page.waitForURL((url) => url.toString().includes("saved=1"), { timeout: 15000 }),
        page.locator("#preferences-editor-form").evaluate((form) => form.requestSubmit())
      ]);
      return {
        url: page.url(),
        weatherDisplayMode: await page.locator("#weatherDisplayMode").inputValue()
      };
    }`,
  });
}

async function grantGeolocation(callTool) {
  return callTool("browser_run_code", {
    code: `async (page) => {
      await page.context().grantPermissions(["geolocation"], { origin: "${baseUrl}" });
      await page.context().setGeolocation({ latitude: 51.4816, longitude: -3.1791 });
      return { granted: true };
    }`,
  });
}

async function capturePage(callTool, { role, label, url, width, height, waitSeconds = 2 }) {
  await setViewport(callTool, width, height);
  const nav = await callTool("browser_navigate", { url });
  await callTool("browser_wait_for", { time: waitSeconds });
  const screenshotName = `${role}-${label}-${width}.png`;
  const snapshotName = `${role}-${label}-${width}.md`;
  const consoleName = `${role}-${label}-${width}-console.json`;
  const absoluteSnapshotPath = path.join(outputDir, snapshotName);
  const absoluteConsolePath = path.join(outputDir, consoleName);
  const diagnostics = await callTool("browser_run_code", {
    code: `async (page) => {
      await page.screenshot({ path: ${JSON.stringify(path.join(outputDir, screenshotName))}, fullPage: true });
      const weirdNodes = Array.from(document.querySelectorAll("body *"))
        .map((node) => node.textContent || "")
        .filter((text) => /Ã|â|ðŸ|Â/.test(text))
        .slice(0, 10);
      const scrollWidth = Math.max(
        document.documentElement.scrollWidth,
        document.body ? document.body.scrollWidth : 0
      );
      return {
        url: page.url(),
        title: await page.title(),
        innerWidth: window.innerWidth,
        scrollWidth,
        hasHorizontalOverflow: scrollWidth > window.innerWidth,
        weirdText: weirdNodes
      };
    }`,
  });
  await callTool("browser_snapshot", { filename: absoluteSnapshotPath });
  const consoleMessages = await callTool("browser_console_messages", {
    level: "info",
    filename: absoluteConsolePath,
  });
  return {
    role,
    label,
    url,
    width,
    height,
    nav,
    diagnostics,
    consoleMessages,
    screenshot: screenshotName,
    snapshot: snapshotName,
    consoleLog: consoleName,
  };
}

async function auditRole(roleConfig) {
  const { client, transport, callTool } = createClient(roleConfig.role);
  const results = [];

  await client.connect(transport);
  try {
    await ensureBrowser(callTool);
    results.push({
      role: roleConfig.role,
      label: "login",
      result: await login(callTool, roleConfig.username),
    });

    if (roleConfig.role === "client") {
      results.push({
        role: roleConfig.role,
        label: "set-weather-graph-mode",
        result: await setClientWeatherGraphMode(callTool),
      });
      results.push({
        role: roleConfig.role,
        label: "grant-geolocation",
        result: await grantGeolocation(callTool),
      });
    }

    for (const pagePath of roleConfig.pages) {
      const slug = pagePath
        .replace(/^\//, "")
        .replace(/[/?=&]+/g, "-")
        .replace(/-+/g, "-")
        .replace(/^-|-$/g, "") || "home";

      results.push(
        await capturePage(callTool, {
          role: roleConfig.role,
          label: `${slug}-desktop`,
          url: `${baseUrl}${pagePath}`,
          width: 1440,
          height: 1200,
          waitSeconds: 2,
        }),
      );
    }

    if (roleConfig.role === "client") {
      results.push(
        await capturePage(callTool, {
          role: roleConfig.role,
          label: "dashboard-mobile",
          url: `${baseUrl}/dashboard`,
          width: 390,
          height: 844,
          waitSeconds: 2,
        }),
      );
      results.push(
        await capturePage(callTool, {
          role: roleConfig.role,
          label: "calendar-month-mobile",
          url: `${baseUrl}/calendar?view=month`,
          width: 390,
          height: 844,
          waitSeconds: 2,
        }),
      );
    }

    return results;
  } finally {
    await client.close();
  }
}

async function main() {
  await fs.mkdir(outputDir, { recursive: true });
  const allResults = [];
  for (const roleConfig of roleConfigs) {
    allResults.push(...(await auditRole(roleConfig)));
  }
  await saveJson("role-audit-results.json", allResults);
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
