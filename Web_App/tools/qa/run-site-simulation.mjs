import fs from "node:fs";
import fsp from "node:fs/promises";
import path from "node:path";
import process from "node:process";
import { spawn, spawnSync } from "node:child_process";

const args = process.argv.slice(2);

function option(name, fallback) {
  const inline = args.find((value) => value.startsWith(`${name}=`));
  if (inline) return inline.slice(name.length + 1);
  const index = args.indexOf(name);
  return index >= 0 && args[index + 1] && !args[index + 1].startsWith("--") ? args[index + 1] : fallback;
}

const hasFlag = (name) => args.includes(name);
const root = process.cwd();
const baseUrl = option("--base-url", process.env.AUDIT_BASE_URL || "http://localhost:8081").replace(/\/$/, "");
const outputDir = path.resolve(option("--output", path.join("output", "playwright", "site-simulation")));
const targetUrl = new URL(baseUrl);
const isLocal = ["localhost", "127.0.0.1", "::1"].includes(targetUrl.hostname);
const targetPort = targetUrl.port || (targetUrl.protocol === "https:" ? "443" : "80");
let serverProcess = null;
let serverLog = null;

async function isReachable() {
  try {
    const response = await fetch(`${baseUrl}/`, { signal: AbortSignal.timeout(3_000), redirect: "manual" });
    return response.status > 0;
  } catch {
    return false;
  }
}

async function startServer() {
  if (await isReachable()) {
    console.log(`Using the application already running at ${baseUrl}.`);
    return;
  }
  if (!isLocal || hasFlag("--no-start")) {
    throw new Error(`The application is not reachable at ${baseUrl}. Start it first or remove --no-start for a local target.`);
  }

  await fsp.mkdir(outputDir, { recursive: true });
  const logPath = path.join(outputDir, "server.log");
  serverLog = fs.createWriteStream(logPath, { flags: "w" });
  const command = process.platform === "win32" ? "cmd.exe" : "./gradlew";
  const commandArgs = process.platform === "win32"
    ? ["/d", "/s", "/c", "gradlew.bat bootRun --console=plain"]
    : ["bootRun", "--console=plain"];
  serverProcess = spawn(command, commandArgs, {
    cwd: root,
    env: {
      ...process.env,
      SPRING_PROFILES_ACTIVE: process.env.SPRING_PROFILES_ACTIVE || "local",
      APP_AI_ENABLED: process.env.APP_AI_ENABLED || "false",
      SERVER_PORT: targetPort,
    },
    detached: process.platform !== "win32",
    stdio: ["ignore", "pipe", "pipe"],
  });
  serverProcess.stdout.pipe(serverLog);
  serverProcess.stderr.pipe(serverLog);
  console.log(`Starting the local application; startup output is being written to ${logPath}.`);

  const deadline = Date.now() + 180_000;
  while (Date.now() < deadline) {
    if (serverProcess.exitCode !== null) throw new Error(`The application stopped during startup with exit code ${serverProcess.exitCode}. See ${logPath}.`);
    if (await isReachable()) {
      console.log(`Application ready at ${baseUrl}.`);
      return;
    }
    await new Promise((resolve) => setTimeout(resolve, 2_000));
  }
  throw new Error(`The application did not become ready within 180 seconds. See ${logPath}.`);
}

async function ensurePlaywright() {
  const playwrightCli = path.join(root, "tools", "playwright-local", "node_modules", "playwright", "cli.js");
  if (!fs.existsSync(playwrightCli)) {
    console.log("Installing the isolated Playwright audit dependencies.");
    const npm = process.platform === "win32" ? "npm.cmd" : "npm";
    const install = spawnSync(npm, ["ci"], { cwd: path.join(root, "tools", "playwright-local"), stdio: "inherit" });
    if (install.status) throw new Error(`Playwright dependency installation failed with exit code ${install.status}.`);
  }

  console.log("Checking the Chromium runtime used by the simulation.");
  const browserInstall = spawnSync(process.execPath, [playwrightCli, "install", "chromium"], { cwd: root, stdio: "inherit" });
  if (browserInstall.status) throw new Error(`Playwright browser installation failed with exit code ${browserInstall.status}.`);
}

function stopServer() {
  if (!serverProcess) return;
  if (process.platform === "win32") {
    spawnSync("taskkill", ["/pid", String(serverProcess.pid), "/T", "/F"], { stdio: "ignore" });
  } else {
    try {
      process.kill(-serverProcess.pid, "SIGTERM");
    } catch {
      serverProcess.kill("SIGTERM");
    }
  }
  serverLog?.end();
}

async function runNode(script, scriptArgs = [], extraEnv = {}) {
  return new Promise((resolve) => {
    const child = spawn(process.execPath, [script, ...scriptArgs], {
      cwd: root,
      env: { ...process.env, AUDIT_BASE_URL: baseUrl, ...extraEnv },
      stdio: "inherit",
    });
    child.on("exit", (code) => resolve(code ?? 1));
  });
}

async function run() {
  let workflowExit = 0;
  let standardsExit = 0;
  let workflowStatus = "skipped";
  try {
    await ensurePlaywright();
    await startServer();

    const workflowsAllowed = isLocal || hasFlag("--allow-remote-mutations");
    if (!hasFlag("--skip-workflows") && workflowsAllowed) {
      console.log("Running profile logins, access checks, interactions and full feature workflows.");
      const loginRolesOnly = hasFlag("--login-only") ? option("--roles", "trainer,gym") : "";
      workflowExit = await runNode(
        path.join("tools", "qa", "playwright-local-view-audit.mjs"),
        [],
        {
          AUDIT_FAIL_ON_WORKFLOW_FINDING: option("--fail-on", "none") === "none" ? "0" : "1",
          AUDIT_LOGIN_ROLES_ONLY: loginRolesOnly,
        },
      );
      workflowStatus = [0, 2].includes(workflowExit) ? "completed" : "failed";
    } else if (!hasFlag("--skip-workflows")) {
      console.log("Skipping data-changing feature workflows for a remote target. Use --allow-remote-mutations only on a disposable test environment.");
    }

    if (!hasFlag("--login-only")) {
      console.log("Running page, element, responsive design, text and accessibility standards.");
      const forwarded = ["--base-url", baseUrl, "--output", outputDir];
      for (const name of ["--profiles", "--viewports", "--fail-on", "--screenshots"]) {
        const value = option(name, null);
        if (value) forwarded.push(name, value);
      }
      standardsExit = await runNode(path.join("tools", "qa", "playwright-site-simulation.mjs"), forwarded, { AUDIT_WORKFLOW_STATUS: workflowStatus });
    }
  } finally {
    stopServer();
  }

  if (workflowExit || standardsExit) process.exitCode = 1;
}

run().catch((error) => {
  console.error(error);
  stopServer();
  process.exitCode = 1;
});
