# Cloudflare Playwright MCP

This worker hosts a real Playwright MCP server on Cloudflare Workers using Browser Rendering and a Durable Object-backed MCP agent.

## Worker URL

- Base URL: `https://one-to-one-playwright-mcp.jhud172.workers.dev`

## Endpoints

- `GET /sse`
  Stable SSE transport. This is the verified endpoint for Inspector and for local MCP bridges such as `mcp-remote`.
- `POST /sse/message`
  SSE message endpoint used after the initial `/sse` session is opened.
- `POST /mcp`
  Streamable HTTP MCP endpoint exposed by the worker. It initializes correctly, but this setup is currently not the validated Codex path.
- `GET /health`
  Lightweight worker health response with exposed routes.
- `GET /health/browser`
  Browser Rendering limits and session state.
- `GET /health/browser/acquire`
  Browser session acquisition diagnostic.
- `GET /health/browser/connect`
  Browser connect diagnostic.
- `GET /health/browser/launch`
  Full browser launch diagnostic.

## Install

```bash
npm install
npx wrangler whoami
```

## Run Locally

Browser Rendering requires remote mode during local development.

```bash
npx wrangler dev --remote
```

## Deploy

```bash
npx wrangler deploy
```

## Inspector

Verified SSE Inspector command:

```bash
npx @modelcontextprotocol/inspector --cli --method tools/list https://one-to-one-playwright-mcp.jhud172.workers.dev/sse
```

## Codex

Use a local bridge against the verified SSE endpoint:

```toml
[mcp_servers.cloudflarePlaywright]
enabled = true
required = false
command = "npx"
args = ["mcp-remote", "https://one-to-one-playwright-mcp.jhud172.workers.dev/sse"]
startup_timeout_sec = 30.0
tool_timeout_sec = 300.0
```

Restart Codex after changing config.

## Browser Validation Notes

- MCP connection over `/sse` is verified.
- `tools/list` is verified.
- Browser Rendering limits are reachable.
- Actual browser launch currently still needs validation against rate limits and the current Cloudflare session state before claiming end-to-end page inspection as working.
