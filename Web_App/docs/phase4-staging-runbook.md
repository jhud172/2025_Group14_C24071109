# Phase 4 staging and recovery runbook

## Status

The staging execution is active. The reference `render-staging.yaml` was not
applied as a Blueprint; the service was configured manually in the existing
`one-to-one` Render environment after James directed reuse of the current
database. Only staging sandbox variables were changed; no production provider
credential, webhook or service was changed.

A read-only inspection of the existing `1to-one` PostgreSQL 18 instance found
that it is available in Oregon on Basic-256mb with 15 GB storage and contains
existing application records. James then directed staging to reuse that
instance. Staging must therefore use a new `one_to_one_staging` schema; the
existing `public` schema is out of scope.

The exact pre-deployment `public` fingerprint retained on 28 July 2026 is:
6 users, 12 user-role links, 42 support requests, 2 trainer profiles, 1 gym
profile, 4 platform subscriptions, 7 mobile authentication tokens and 1
waitlist email. Recheck these counts immediately after schema creation and
after every migration drill.

Current resources:

- web service `one-to-one-staging-jhuds` (`srv-d9kct35aeets73ant7k0`);
- Starter compute with automatic deploys disabled;
- 1 GB disk `dsk-d9kct35aeets73ant8ag` mounted at `/var/data/uploads`;
- PostgreSQL schema `one_to_one_staging` on `1to-one`;
- live application commit `f99024cf`, deploy
  `dep-d9kgqgh42hec73doqmkg`.

## Isolation boundary

Create the staging web service in `My Workspace`, in Oregon, so it can use the
existing database's private connection. Set
`APP_DATABASE_SCHEMA=one_to_one_staging`. The application appends that schema
as PostgreSQL's `currentSchema` and configures both Flyway and Hibernate to use
it. Never set staging to `public`.

The staging service must have:

- no production environment group;
- no production secret, domain or webhook;
- no reads or writes to the existing `public` schema;
- one staging web service using only the `one_to_one_staging` schema;
- automatic deploys disabled;
- a 1 GB persistent disk mounted at `/var/data/uploads`;
- email, SMS, AI and OAuth disabled at the first boot;
- a generated, staging-only card-encryption key.

The four upload routes map to subdirectories of the same durable mount:

| Application route | Staging storage directory | Read policy |
| --- | --- | --- |
| `/uploads/profile/**` | `/var/data/uploads/profile` | Public |
| `/uploads/chat/**` | `/var/data/uploads/chat` | Authenticated owner only |
| `/uploads/merch/**` | `/var/data/uploads/merch` | Public |
| `/uploads/workout-videos/**` | `/var/data/uploads/workout-videos` | Authenticated owner only |

## Expected minimum Render cost

Pricing inspected on 28 July 2026:

| Resource | Selected size | Expected monthly cost |
| --- | --- | ---: |
| Web service | Starter, 512 MB | US$7.00 |
| Existing PostgreSQL | Reused; no new instance | US$0.00 incremental |
| Persistent upload disk | 1 GB at US$0.25/GB | US$0.25 |
| **Expected incremental minimum** | | **approximately US$7.25/month** |

Usage is prorated by the second. Bandwidth above the Hobby allowance and any
provider usage are extra. James separately approved one Basic-256mb/1 GB
recovery database at US$6.30/month, prorated by the second. It was deleted
after validation.

## Approval boundary

James approved the original US$13.55/month ceiling and later directed reuse of
the existing database. Do not create the service, disk, recovery database or
provider endpoints outside that approved ceiling.

Approval of the base staging cost does not authorise:

- a production-data copy;
- production credentials;
- a real payment, refund, email or SMS;
- a production Stripe webhook change;
- another billable point-in-time recovery database.

## First deployment

1. Confirm the Git revision to deploy has passed the complete Gradle and web
   release-gate suites.
2. Select `My Workspace` without editing the production `One_To_One` service.
3. Review `render-staging.yaml`, confirm `APP_DATABASE_SCHEMA` is
   `one_to_one_staging`, and securely link the existing `1to-one` database.
4. Apply the staging service and 1 GB disk without changing the database's
   `public` schema or production service.
5. Confirm the staging schema reports zero application users after Flyway V1.
6. Confirm `one_to_one_staging.flyway_schema_history` contains V1 and no SQL
   initializer ran.
7. Confirm the four upload directories survive a controlled staging redeploy.

Execution result on 28 July 2026:

- V1 created the isolated schema with zero users and no production demo seed;
- V2–V4 repaired only incompatibilities reproduced by real PostgreSQL
  validation;
- `flyway_schema_history` has five successful rows: schema creation and V1–V4;
- the synthetic staging client is the only staging user;
- the `public` aggregate fingerprint remained unchanged;
- the profile upload survived redeploy and rollback with identical size and
  SHA-256;
- deployment rollback reached live without a schema downgrade;
- AI and OAuth remain disabled/unconfigured.

A follow-up deployment loaded the SMTP, Twilio and Stripe variable names into
the live staging process. Safe provider probes produced these results:

- SMTP selected correctly, but the configured host is invalid; verification
  and password-reset delivery did not reach a test inbox;
- the missing Twilio sender was repaired with the documented magic sender,
  after which Twilio returned authentication error 20003 for the configured
  test SID/token; no SMS was sent;
- Stripe checkout reached Stripe but returned HTTP 401 for an invalid test
  key; no Checkout Session, subscription or charge was created.

Do not claim a provider pass until those invalid staging-only values are
replaced and a new deployment completes the lifecycles below.

James has deferred that replacement until the final pre-launch gate. Until
then, `"2bd"` is treated as an intentional invalid placeholder, never as a
credential. A focused 19-test provider/payment safety run passed with those
placeholder environment values and confirmed fail-closed application
behaviour. Do not repeatedly call the external providers while this deliberate
placeholder state remains active.

Storage acceptance completed on 29 July 2026 with labelled, synthetic
`one_to_one_staging` fixtures only:

- current live code before the repair returned 200 for owner, peer and
  anonymous reads of both a chat image and workout video, reproducing their
  public-read defect;
- commit `7c4fce55` and deploy `dep-d9kst0rl550s73f6kdmg` replaced those static
  reads with owner-scoped access;
- the chat and merchandise files retained their 23,044-byte size and SHA-256
  `d08fc3b55a4a7d1c50c77f8929cd7ac0ca69656652f9bab9fc19f11510fa613a`
  after redeploy;
- the workout video retained its 24-byte size and SHA-256
  `c8c5af84ac765d911a9ab05bc9a19d15d0b1bc5cf0654eff4469ce536410654e`;
- after deploy, owner chat/video reads returned 200, peer reads returned 404,
  anonymous reads returned 401, and public merchandise read returned 200;
- application deletion removed every fixture file and subsequent reads
  returned 404; the labelled users, workout and merchandise rows were then
  deleted, leaving zero labelled rows;
- multipart limits are 8 MB per file, 25 MB per request and 32 MB bounded
  Tomcat swallow; embedded-server tests cover exact-limit acceptance and both
  file/request rejection;
- no `public` row, provider variable, production webhook or production service
  was accessed or changed.

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

Use only the `one_to_one_staging` schema and staging upload disk. A
schema-scoped logical dump is mandatory; a whole-instance restore would include
the existing `public` data and is not authorised.

1. Create labelled staging fixture records and one upload in every boundary.
2. Export a logical PostgreSQL backup restricted to `one_to_one_staging`.
3. Restore it into an empty local PostgreSQL 18 database or a separately
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

Current evidence:

- Render created the complete logical export at 17:28 BST on 28 July 2026 and
  retains it for seven days.
- The archive was not downloaded or restored because it contains the existing
  out-of-scope `public` schema.
- James explicitly approved a temporary Basic-256mb/1 GB recovery instance at
  US$6.30/month, prorated by the second.
- Recovery database `one-to-one-phase4-recovery-20260728`
  (`dpg-d9kgb35aeets73aupr70-a`) reached available. Validation was restricted
  to the isolated `one_to_one_staging` boundary and confirmed successful schema
  creation plus Flyway V1–V4, 122 base tables, one expected synthetic user and
  one profile-upload reference. No `public` row query was made.
- The exact recovery database was deleted immediately after validation. Only
  source database `dpg-d73tqedactks7385ism0-a` remains available, stopping
  further recovery-instance charges.
- The application rollback portion passed by rolling back to the previous
  successful `50a2be4` artifact. Login returned HTTP 200, Flyway remained at
  V4, the staging client remained present and the persisted upload hash was
  unchanged.
- The rollback is an application proof only; it is not a database restore
  proof.
- The approved recovery resource existed only for the drill. Render bills the
  **US$6.30/month** configuration by the second; the final invoiced fraction is
  determined by Render.

## Current candidate validation

The current source candidate retains the live Flyway V5 provider lifecycle
work and completes chat, merchandise and workout-video storage acceptance.
Before deployment it passed:

- `npm ci` with zero reported vulnerabilities;
- `npm run build:css`;
- `bootJar`;
- **31/31 focused storage tests**;
- **544/544 Gradle tests** across 135 suites;
- **88/88 responsive**, **22/22 Axe**, **6/6 throttled** and **6/6
  Lighthouse** release-gate cases with zero findings.

Render deploy `dep-d9kst0rl550s73f6kdmg` reached live at commit `7c4fce55`.
All three retained fixture hashes matched after the redeploy, private ownership
responses matched the required 200/401/404 policy, and all application deletion
journeys removed their durable files. The labelled fixtures were cleaned.
Repeat provider tests only after the invalid staging sandbox values are
replaced at the final pre-launch gate.

## Evidence to retain

- exact Git commit and Render deploy IDs;
- redacted environment-variable names, never values;
- staging-schema Flyway history and empty-user baseline;
- provider request IDs in test mode;
- webhook event IDs and duplicate-handling result;
- database export/restore checks;
- upload hashes before and after redeploy;
- complete Gradle and release-gate summaries;
- final launch blocker and go/no-go decision.
