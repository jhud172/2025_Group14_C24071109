# Phase 4 staging and recovery runbook

## Status

This is a design and execution checklist. `render-staging.yaml` has not been
applied. No Render resource, provider credential or webhook was changed while
preparing it.

A read-only Render account check on 28 July 2026 found one existing workspace
(`My Workspace`) and no dedicated staging workspace. The staging workspace
therefore has to be created as a new boundary after approval; the existing
workspace will not be selected or modified for this work.

## Isolation boundary

Create a dedicated **Hobby workspace** named `one-to-one-staging`, separate
from the workspace that contains production. Apply `render-staging.yaml` only
inside that workspace.

The staging workspace must have:

- no production environment group;
- no production database, disk, secret, domain or webhook;
- one staging web service and one fresh staging PostgreSQL database;
- PostgreSQL external access blocked with `ipAllowList: []`;
- automatic deploys disabled;
- a 1 GB persistent disk mounted at `/var/data/uploads`;
- email, SMS, AI and OAuth disabled at the first boot;
- a generated, staging-only card-encryption key.

The four upload routes map to subdirectories of the same durable mount:

| Public route | Staging storage directory |
| --- | --- |
| `/uploads/profile/**` | `/var/data/uploads/profile` |
| `/uploads/chat/**` | `/var/data/uploads/chat` |
| `/uploads/merch/**` | `/var/data/uploads/merch` |
| `/uploads/workout-videos/**` | `/var/data/uploads/workout-videos` |

## Expected minimum Render cost

Pricing inspected on 28 July 2026:

| Resource | Selected size | Expected monthly cost |
| --- | --- | ---: |
| Web service | Starter, 512 MB | US$7.00 |
| PostgreSQL compute | Basic-256mb | approximately US$6.00 |
| PostgreSQL storage | 1 GB at US$0.30/GB | US$0.30 |
| Persistent upload disk | 1 GB at US$0.25/GB | US$0.25 |
| **Expected minimum** | | **approximately US$13.55/month** |

Usage is prorated by the second. Bandwidth above the Hobby allowance and any
provider usage are extra. A Render point-in-time recovery creates a second
billable database temporarily and therefore needs a separate cost approval
before that drill.

## Approval boundary

Do not create the workspace, service, database, disk, recovery database or
provider endpoints until James explicitly approves the cost and creation.

Approval of the base staging cost does not authorise:

- a production-data copy;
- production credentials;
- a real payment, refund, email or SMS;
- a production Stripe webhook change;
- a billable point-in-time recovery database.

## First deployment

1. Confirm the Git revision to deploy has passed the complete Gradle and web
   release-gate suites.
2. Create or select the dedicated `one-to-one-staging` Hobby workspace.
3. Review `render-staging.yaml` and confirm its service/database names do not
   match any production resource.
4. Apply the Blueprint to that staging workspace only.
5. Confirm PostgreSQL reports zero application users after Flyway V1.
6. Confirm `flyway_schema_history` contains V1 and no SQL initializer ran.
7. Confirm the four upload directories survive a controlled staging redeploy.

## Provider activation order

Activate one provider at a time and return it to its safe state before moving
to the next.

### SMTP

Use an SMTP test inbox that cannot deliver to real recipients. Set only the
staging service's `SPRING_MAIL_*`, `APP_EMAIL_FROM` and
`APP_EMAIL_PROVIDER=smtp` values.

Prove:

1. verification email accepted by the test inbox;
2. password-reset email accepted by the test inbox;
3. link host is the staging origin;
4. invalid SMTP credentials produce a visible failure and no false success;
5. no production recipient or sender appears in the evidence.

### Twilio

Use Twilio test credentials and documented magic test numbers. Do not use a
live account token or a real recipient. Set only the staging service's
`TWILIO_*` values and `APP_SMS_PROVIDER=twilio`.

Prove successful acceptance, invalid-recipient failure, credential failure,
OTP expiry, maximum attempts and single-use behaviour. Redact tokens and OTPs
from retained logs.

### Stripe

Use an `sk_test_...` key and a new test-mode webhook endpoint whose sole
destination is the staging service. Never modify the production endpoint.

Prove:

1. successful test checkout and return;
2. failed and cancelled test checkout;
3. valid signed webhook processing;
4. invalid signature and stale timestamp rejection;
5. duplicate event idempotency;
6. subscription activation and cancellation;
7. merchandise order payment lifecycle;
8. no live-mode object or real charge was created.

## Backup, restore and rollback drill

Use only the staging database and staging upload disk.

1. Create labelled staging fixture records and one upload in every boundary.
2. Export a logical PostgreSQL backup.
3. Restore it into an empty local PostgreSQL 17 database or a separately
   approved temporary staging recovery database.
4. Compare Flyway version, table counts, labelled fixture records and
   referential-integrity checks.
5. Download the four staging uploads and verify hashes.
6. Deploy the previous known-good application revision with no database
   downgrade.
7. Verify login, client, trainer, gym and admin smoke journeys.
8. Redeploy the candidate revision and verify Flyway remains at the expected
   version.

Flyway Community migrations are forward-only. A failed schema change is
repaired with a new forward migration. Database rollback means restoring an
approved backup into a new empty database and switching only the staging
service after validation.

## Evidence to retain

- exact Git commit and Render deploy IDs;
- redacted environment-variable names, never values;
- Flyway history and empty-user baseline;
- provider request IDs in test mode;
- webhook event IDs and duplicate-handling result;
- database export/restore checks;
- upload hashes before and after redeploy;
- complete Gradle and release-gate summaries;
- final launch blocker and go/no-go decision.
