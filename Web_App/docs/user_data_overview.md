# User Data Overview

This file lists every current demo login in display order.

## Password standard

All current demo accounts use:
- `Demo123!`

## Seed standard

- Runtime source of truth is SQL under `src/main/resources/data/`
- Timeline data is seeded relative to `CURRENT_DATE`
- Minimum of two fully prepared accounts now exists for each role: `CLIENT`, `TRAINER`, `GYM_ADMIN`, `PLATFORM_ADMIN`, `SUPER_ADMIN`

## Clients

| Username | Email | Password | State | Linked trainer | Code | Folder |
| --- | --- | --- | --- | --- | --- | --- |
| `demo_client` | `demo_client@example.com` | `Demo123!` | Premium active | `demo_trainer` | None | `src/main/resources/data/users/clients/demo_client/` |
| `demo` | `demo@example.com` | `Demo123!` | Lapsed premium | `trainer_demo` | None | `src/main/resources/data/users/clients/demo/` |
| `demo2` | `demo2@example.com` | `Demo123!` | Starter / non-premium | None | None | `src/main/resources/data/users/clients/demo2/` |

## Trainers

| Username | Email | Password | State | Linked client | Code | Folder |
| --- | --- | --- | --- | --- | --- | --- |
| `demo_trainer` | `demo_trainer@example.com` | `Demo123!` | Premium active | `demo_client` | `240781903465` | `src/main/resources/data/users/trainers/demo_trainer/` |
| `trainer_demo` | `trainer_demo@example.com` | `Demo123!` | Premium active | `demo` | `120340056789` | `src/main/resources/data/users/trainers/trainer_demo/` |

## Gym Accounts

| Username | Email | Password | State | Linked gym profile | Code | Folder |
| --- | --- | --- | --- | --- | --- | --- |
| `demo_gym` | `demo_gym@example.com` | `Demo123!` | Premium active | `Harbour Strength Club` | `4827001938456203` | `src/main/resources/data/users/gym_accounts/demo_gym/` |
| `gymadmin_demo` | `gymadmin_demo@example.com` | `Demo123!` | Premium active | `FitZone Gym Cardiff` | `4827001938456202` | `src/main/resources/data/users/gym_accounts/gymadmin_demo/` |

## Admins

| Username | Email | Password | State | Access | Code | Folder |
| --- | --- | --- | --- | --- | --- | --- |
| `demo_admin` | `demo_admin@example.com` | `Demo123!` | Premium active | `PLATFORM_ADMIN` | None | `src/main/resources/data/users/admins/demo_admin/` |
| `admin_demo` | `admin_demo@example.com` | `Demo123!` | Premium active | `PLATFORM_ADMIN` | None | `src/main/resources/data/users/admins/admin_demo/` |

## Super Admins

| Username | Email | Password | State | Access | Code | Folder |
| --- | --- | --- | --- | --- | --- | --- |
| `superadmin_demo` | `superadmin_demo@example.com` | `Demo123!` | Premium active | `SUPER_ADMIN` | None | `src/main/resources/data/users/super_admins/superadmin_demo/` |
| `superadmin_ops` | `superadmin_ops@example.com` | `Demo123!` | Premium active | `SUPER_ADMIN` | None | `src/main/resources/data/users/super_admins/superadmin_ops/` |

## Role indexes

- `src/main/resources/data/users/clients/README.md`
- `src/main/resources/data/users/trainers/README.md`
- `src/main/resources/data/users/gym_accounts/README.md`
- `src/main/resources/data/users/admins/README.md`
- `src/main/resources/data/users/super_admins/README.md`
