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
- Playwright general audits belong under [`../output/playwright/general`](../output/playwright/general).
- Playwright cross-role audits belong under [`../output/playwright/roles`](../output/playwright/roles).
- The complete profile simulation is run with `npm run qa:simulate` from `Web_App`.
- Its executable criteria live in [`qa/site-simulation.config.mjs`](./qa/site-simulation.config.mjs), with the acceptance baseline documented in [`../docs/qa/SITE_SIMULATION_STANDARDS.md`](../docs/qa/SITE_SIMULATION_STANDARDS.md).
- Generated simulation reports belong under [`../output/playwright/site-simulation`](../output/playwright/site-simulation).
