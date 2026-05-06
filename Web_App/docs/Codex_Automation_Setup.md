# Codex Automation Setup

## Purpose

This document explains how Codex should be used against this repository.

It is not a second system overview. It is a repo-specific working guide for safe automation, repeated audits, and scoped implementation help.

## Current Repository Rules

When touching frontend files in this project:

- keep JavaScript in external JS files
- keep CSS in external CSS files
- do not add inline `<script>` blocks to templates
- do not add inline `<style>` blocks to templates
- prefer shared fragments, shared JS, and shared CSS over page-specific duplication

These rules match the current frontend audit direction in [audits/frontend-template-structure-audit-2026-03-29.md](./audits/frontend-template-structure-audit-2026-03-29.md).

## Safe Default Scope

Codex is safe to use across the whole repository, but the default scope should match the task.

### Frontend-Focused Tasks

Prefer limiting work to:

- `src/main/resources/templates/**`
- `src/main/resources/static/css/**`
- `src/main/resources/static/js/**`

### Full-Stack Tasks

Expand into Java, SQL, and security code only when the task actually requires it, for example:

- authentication fixes
- model binding issues
- route access issues
- profile data wiring
- payment or integration wiring

## Recommended Validation Commands

For most UI or template work:

```bash
npm run build:css
./gradlew test --tests uk.ac.cf._5.group14.One_To_One.ProfileTests.ProfileRouteAccessTest
./gradlew test --tests uk.ac.cf._5.group14.One_To_One.SecurityTests.LoginIntegrationTest
```

For broader changes:

```bash
./gradlew test
```

## Good Automation Use Cases

Codex is a good fit for recurring or repeated work such as:

- frontend structure audits
- navbar/profile consistency checks
- responsive cleanup passes
- route-to-template consistency checks
- documentation refresh passes
- targeted regression checks after UI changes

## Bad Automation Use Cases

Do not let recurring automations make uncontrolled product decisions such as:

- redesigning the entire information architecture
- rewriting security rules without an explicit task
- changing payment behavior without verification
- moving or deleting large parts of the backend based on guesswork
- auto-merging broad speculative changes

## Suggested Automation Pattern

If automations are used, keep them narrow and reviewable.

### 1. Frontend Audit

Suggested goal:

- inspect templates for inline CSS/JS violations
- spot repeated fragments or duplicated interaction code
- flag large templates that should be split next

Suggested output:

- summary
- files inspected
- findings grouped by severity
- minimal safe edits only

### 2. Docs Accuracy Pass

Suggested goal:

- compare docs against the current repo
- keep [README.md](../README.md), [docs/README.md](./README.md), and the two overview files aligned
- remove stale references to deleted files or superseded audits

### 3. UI Regression Pass

Suggested goal:

- verify navbar, auth, dashboard, and profile flows
- confirm logged-in previews use real data
- report layout breakage before making style changes

## Worktree Recommendation

If Codex automations are run outside the current shared local checkout, use isolated Git worktrees so automation output does not trample ongoing manual work.

## Human Review Rule

Every recurring automation should still be reviewed by a person before changes are accepted.

Preferred review flow:

1. read the summary
2. inspect the diff
3. verify the changed routes or pages
4. run the relevant tests
5. merge only small, coherent changes

## Related Docs

- [README.md](../README.md)
- [docs/README.md](./README.md)
- [System-Overview.md](./System-Overview.md)
- [System-Overview-Dev-Mode.md](./System-Overview-Dev-Mode.md)
- [audits/frontend-template-structure-audit-2026-03-29.md](./audits/frontend-template-structure-audit-2026-03-29.md)
