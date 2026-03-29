# Tools

Supporting tooling lives here so the application root stays focused on the Spring Boot app.

## Structure

- `mcp/`
  - external MCP workers and browser tooling
- `qa/`
  - local audit or inspection scripts that are not part of the runtime application

## Notes

- Tool-specific `node_modules`, `.wrangler`, and local editor files are ignored.
- Generated outputs belong under [`../output`](../output), not in the repository root.
