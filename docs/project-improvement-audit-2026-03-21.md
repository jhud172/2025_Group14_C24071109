# Historical Project Improvement Audit

Date of original audit: `2026-03-21`

## Status Of This File

This file is intentionally kept as a historical audit summary, not as the current source of truth for the repository.

Use these files for the live/current state instead:

- [README.md](../README.md)
- [docs/README.md](./README.md)
- [System-Overview.md](./System-Overview.md)
- [System-Overview-Dev-Mode.md](./System-Overview-Dev-Mode.md)
- [audits/frontend-template-structure-audit-2026-03-29.md](./audits/frontend-template-structure-audit-2026-03-29.md)

## Why This File Still Exists

The original 2026-03-21 audit captured a useful repository-wide improvement plan. A large part of the detailed issue list inside that original version is now outdated, partially resolved, or better described by newer docs.

This file remains to preserve:

- the original strategic direction
- the main categories of improvement that shaped later work
- a short record of what has changed since then

## Major Areas Identified By The Original Audit

The original audit correctly highlighted these broad themes:

- identity and security consistency
- consolidation of overlapping user journeys
- frontend extraction and cleanup
- payment and billing hardening
- test stability and confidence

Those themes are still useful as planning categories, even though many specific findings have moved on.

## Major Changes Since The Original Audit

Since the original audit, the repository has already moved forward in several meaningful ways:

- method security is enabled in the security configuration
- `/super-admin/**` is explicitly protected
- identity resolution has been pushed closer to Spring Security as the primary source of truth
- destructive schedule actions were converted away from `GET`
- canonical messaging and coach routes were tightened up
- hosted payment-provider flows were added for pricing and merch paths
- Stripe-backed billing and webhook code now exists in the repo
- AI traffic is now routed through a shared gateway abstraction
- upload validation has been hardened in more than one feature area
- dev-mode routing and page restriction behavior are now more explicit and documented

## What Still Matters From The Original Audit

The highest-value remaining themes are still:

### Test Reliability

The repository is large enough that test confidence matters. Continue treating test recovery and regression prevention as first-class work.

### Journey Consolidation

The product is broad. It still benefits from clearer role journeys, cleaner navigation ownership, and less overlap between related surfaces.

### Frontend Cleanup

The latest frontend template audit is the current source of truth for template-structure cleanup. Continue to reduce duplicated interaction code and oversized templates in a planned way.

### Billing And Integration Hardening

Payment and billing flows now have real foundations, but integration hardening is still a sensible long-term theme.

## How To Use This File

Use this document as a historical planning note only.

Do not use it to describe:

- the current route map
- the current security model
- the current docs structure
- the current frontend audit status

For those, use the newer core docs listed at the top of this file.
