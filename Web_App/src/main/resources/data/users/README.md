# Demo User Data

This folder is the human-readable map of the runtime demo estate.

## Structure

- `clients/`
- `trainers/`
- `gym_accounts/`
- `admins/`
- `super_admins/`

Each role folder contains a `README.md` plus one folder per seeded account.

Each account folder contains:
- `info.md` for identity, purpose, and linked relationships
- `account_setup.md` for login, subscription, verification, and access details
- `calendar_data.md` for seeded timeline coverage and current activity rules

## Runtime source of truth

The application still loads from SQL, not from these markdown files.

- `src/main/resources/data/00-auth-demo.sql`
- `src/main/resources/data/01-demo-foundation.sql`
- `src/main/resources/data/02-demo-role-data.sql`
- `src/main/resources/data/03-preferences-seed.sql`

Timeline data now uses `CURRENT_DATE`-relative seeding so past, present, and upcoming activity stays current when the demo dataset is loaded.

## Overview reference

- `docs/user_data_overview.md`
