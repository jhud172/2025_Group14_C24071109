import fs from "node:fs/promises";
import path from "node:path";
import process from "node:process";
import { Client } from "file:///C:/Users/jhuds/AppData/Local/npm-cache/_npx/5a9d879542beca3a/node_modules/@modelcontextprotocol/sdk/dist/esm/client/index.js";
import { StdioClientTransport } from "file:///C:/Users/jhuds/AppData/Local/npm-cache/_npx/5a9d879542beca3a/node_modules/@modelcontextprotocol/sdk/dist/esm/client/stdio.js";

const root = process.cwd();
const outputDir = path.join(root, "output", "playwright");
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

const client = new Client({ name: "codex-ui-audit", version: "1.0.0" });
const transport = new StdioClientTransport({
  command,
  args,
  cwd: root,
  stderr: "pipe",
});

const callTool = async (name, toolArgs = {}) => {
  const result = await client.callTool({ name, arguments: toolArgs });
  return result;
};

const ensureBrowser = async () => {
  await callTool("browser_install", {});
};

const saveJson = async (fileName, data) => {
  const target = path.join(outputDir, fileName);
  await fs.writeFile(target, JSON.stringify(data, null, 2), "utf8");
};

const capturePage = async ({ label, url, width, height, waitSeconds = 2 }) => {
  await callTool("browser_resize", { width, height });
  const nav = await callTool("browser_navigate", { url });
  await callTool("browser_wait_for", { time: waitSeconds });
  const screenshot = `${label}-${width}.png`;
  const snapshot = `${label}-${width}.md`;
  const consoleLog = `${label}-${width}-console.json`;
  const absoluteScreenshotPath = path.join(outputDir, screenshot);
  await callTool("browser_run_code", {
    code: `async (page) => {
      await page.screenshot({
        path: ${JSON.stringify(absoluteScreenshotPath)},
        fullPage: true
      });
      return { url: page.url() };
    }`,
  });
  await callTool("browser_snapshot", { filename: snapshot });
  const consoleMessages = await callTool("browser_console_messages", {
    level: "info",
    filename: consoleLog,
  });
  return {
    label,
    url,
    width,
    height,
    nav,
    consoleMessages,
    screenshot,
    snapshot,
    consoleLog,
  };
};

const captureViewport = async ({ label, url, width, height, waitSeconds = 2 }) => {
  await callTool("browser_resize", { width, height });
  await callTool("browser_navigate", { url });
  await callTool("browser_wait_for", { time: waitSeconds });
  const screenshot = `${label}-${width}-viewport.png`;
  const absoluteScreenshotPath = path.join(outputDir, screenshot);
  const diagnostics = await callTool("browser_run_code", {
    code: `async (page) => {
      await page.screenshot({
        path: ${JSON.stringify(absoluteScreenshotPath)},
        fullPage: false
      });
      const qs = (selector) => document.querySelector(selector);
      const visible = (element) => {
        if (!element) return false;
        const style = window.getComputedStyle(element);
        const rect = element.getBoundingClientRect();
        return style.display !== 'none' && style.visibility !== 'hidden' && rect.width > 0 && rect.height > 0;
      };
      const navButton = qs('#siteNavButton');
      const navSlot = qs('.navheader__nav-slot');
      const authContainer = qs('.nav-auth-container');
      const scrollWidth = Math.max(
        document.documentElement.scrollWidth,
        document.body ? document.body.scrollWidth : 0
      );
      return {
        url: page.url(),
        innerWidth: window.innerWidth,
        clientWidth: document.documentElement.clientWidth,
        scrollWidth,
        hasHorizontalOverflow: scrollWidth > window.innerWidth,
        navButtonVisible: visible(navButton),
        navSlotVisible: visible(navSlot),
        authContainerWidth: authContainer ? authContainer.getBoundingClientRect().width : null
      };
    }`,
  });

  return {
    label,
    url,
    width,
    height,
    screenshot,
    diagnostics,
  };
};

const main = async () => {
  await fs.mkdir(outputDir, { recursive: true });
  await client.connect(transport);
  await ensureBrowser();

  const widths = [
    { key: "desktop", width: 1440, height: 1200 },
    { key: "tablet", width: 1024, height: 1200 },
    { key: "mobile", width: 390, height: 844 },
  ];

  const baseUrl = "https://two025-group14-c24071109-1.onrender.com";
  const results = [];

  for (const viewport of widths) {
    results.push(
      await capturePage({
        label: `home-${viewport.key}`,
        url: baseUrl,
        width: viewport.width,
        height: viewport.height,
      }),
    );
    results.push(
      await capturePage({
        label: `login-${viewport.key}`,
        url: `${baseUrl}/login`,
        width: viewport.width,
        height: viewport.height,
      }),
    );
  }

  results.push(
    await captureViewport({
      label: "home-mobile-top",
      url: baseUrl,
      width: 390,
      height: 844,
      waitSeconds: 2,
    }),
  );

  await callTool("browser_resize", { width: 1440, height: 1200 });
  await callTool("browser_navigate", { url: `${baseUrl}/login?devLogin=1` });
  await callTool("browser_wait_for", { time: 2 });
  const loginFlow = await callTool("browser_run_code", {
    code: `async (page) => {
      await page.goto("${baseUrl}/login?devLogin=1", { waitUntil: "domcontentloaded" });
      await page.locator("#username").fill("demo_client");
      await page.locator("#password").fill("Demo123!");
      await Promise.all([
        page.waitForLoadState("networkidle"),
        page.locator("#loginForm button[type='submit']").click()
      ]);
      await page.waitForTimeout(2000);
      return {
        url: page.url(),
        title: await page.title()
      };
    }`,
  });
  results.push({ label: "login-flow", loginFlow });

  results.push(
    await captureViewport({
      label: "dashboard-mobile-top",
      url: `${baseUrl}/dashboard`,
      width: 390,
      height: 844,
      waitSeconds: 2,
    }),
  );

  await callTool("browser_run_code", {
    code: `async (page) => {
      await page.screenshot({
        path: ${JSON.stringify(path.join(outputDir, "dashboard-desktop.png"))},
        fullPage: true
      });
      return { url: page.url() };
    }`,
  });
  await callTool("browser_snapshot", { filename: "dashboard-desktop.md" });

  const profileMenu = await callTool("browser_run_code", {
    code: `async (page) => {
      const profileTrigger = page.getByRole("button", { name: /profile/i }).first();
      await profileTrigger.click();
      await page.waitForTimeout(1200);
      return {
        url: page.url(),
        profileButtons: await page.getByRole("button").allTextContents()
      };
    }`,
  });
  results.push({ label: "profile-menu-open", profileMenu });

  await callTool("browser_run_code", {
    code: `async (page) => {
      await page.screenshot({
        path: ${JSON.stringify(path.join(outputDir, "dashboard-profile-menu-desktop.png"))},
        fullPage: true
      });
      return { url: page.url() };
    }`,
  });

  results.push(
    await capturePage({
      label: "profile-desktop",
      url: `${baseUrl}/profile`,
      width: 1440,
      height: 1200,
      waitSeconds: 3,
    }),
  );

  results.push(
    await captureViewport({
      label: "profile-mobile-top",
      url: `${baseUrl}/profile`,
      width: 390,
      height: 844,
      waitSeconds: 2,
    }),
  );

  results.push(
    await capturePage({
      label: "profile-tablet",
      url: `${baseUrl}/profile`,
      width: 1024,
      height: 1200,
      waitSeconds: 3,
    }),
  );

  results.push(
    await capturePage({
      label: "profile-mobile",
      url: `${baseUrl}/profile`,
      width: 390,
      height: 844,
      waitSeconds: 3,
    }),
  );

  await saveJson("audit-results.json", results);
  await client.close();
};

main().catch(async (error) => {
  console.error(error);
  try {
    await client.close();
  } catch {
    // ignore shutdown errors in failure path
  }
  process.exitCode = 1;
});
