import { env as workerEnv } from "cloudflare:workers";
import { acquire, connect, limits, launch } from "@cloudflare/playwright";
import { createMcpAgent } from "@cloudflare/playwright-mcp";

interface Env {
  MCP_OBJECT: DurableObjectNamespace;
  BROWSER: unknown;
}

export const PlaywrightMCP = createMcpAgent(workerEnv.BROWSER);

export default {
  async fetch(request: Request, env: Env, ctx: ExecutionContext) {
    const { pathname } = new URL(request.url);

    switch (pathname) {
      case "/health":
        return Response.json({
          ok: true,
          endpoints: [
            "/sse",
            "/sse/message",
            "/mcp",
            "/health/browser",
            "/health/browser/acquire",
            "/health/browser/connect",
            "/health/browser/launch",
          ],
        });
      case "/health/browser":
        try {
          const currentLimits = await limits(env.BROWSER);
          return Response.json({ ok: true, limits: currentLimits });
        } catch (error) {
          return Response.json(
            {
              ok: false,
              error: error instanceof Error ? error.message : String(error),
            },
            { status: 500 },
          );
        }
      case "/health/browser/acquire":
        try {
          const session = await acquire(env.BROWSER);
          return Response.json({ ok: true, session });
        } catch (error) {
          return Response.json(
            {
              ok: false,
              error: error instanceof Error ? error.message : String(error),
            },
            { status: 500 },
          );
        }
      case "/health/browser/connect":
        try {
          const sessionId =
            new URL(request.url).searchParams.get("sessionId") ??
            (await acquire(env.BROWSER)).sessionId;
          const session = { sessionId };
          const browser = await connect(env.BROWSER, session.sessionId);
          await browser.close();
          return Response.json({ ok: true, session });
        } catch (error) {
          return Response.json(
            {
              ok: false,
              error: error instanceof Error ? error.message : String(error),
            },
            { status: 500 },
          );
        }
      case "/health/browser/launch":
        try {
          const browser = await launch(env.BROWSER);
          await browser.close();
          return Response.json({ ok: true });
        } catch (error) {
          return Response.json(
            {
              ok: false,
              error: error instanceof Error ? error.message : String(error),
            },
            { status: 500 },
          );
        }
      case "/sse":
      case "/sse/message":
        return PlaywrightMCP.serveSSE("/sse").fetch(request, env, ctx);
      case "/mcp":
        return PlaywrightMCP.serve("/mcp").fetch(request, env, ctx);
      default:
        return new Response("Not Found", { status: 404 });
    }
  },
};
