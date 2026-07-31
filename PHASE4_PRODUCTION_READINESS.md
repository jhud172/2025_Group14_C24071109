# One To One — Phase 4 production-readiness inventory

**Assessment date:** 29 July 2026
**Decision:** **NO-GO for production launch**
**Safe test environment:** isolated local H2 plus `one-to-one-staging-jhuds` using only PostgreSQL schema `one_to_one_staging`
**External effects:** staging-only sandbox requests; no delivered message, Stripe object, charge, production webhook change or production-data mutation

## Executive result

The code-level Phase 4 transactional checks completed without using production data or making a billable provider call. Client, trainer, gym-admin and platform-admin journeys were exercised in disposable H2. Five reproduced defects were repaired and covered:

1. Stripe webhooks were blocked by CSRF before signature handling.
2. Correctly signed Stripe webhook payloads had no timestamp tolerance and could be replayed indefinitely.
3. Trainer client-list Message actions used `GET` against a `POST` endpoint.
4. The seeded `PLATFORM_ADMIN` could see but could not enter the trainer-verification queue.
5. An empty SMTP host still selected `SmtpEmailService` instead of the documented no-op provider.

The current operational-readiness candidate additionally closes health,
shared-session/throttle, scheduled-job ownership, privileged-audit,
saved-card encryption, configuration ownership, provider wording and required
CI gaps. The final post-repair automated result is:

- CSS production build: passed.
- Gradle: **568 tests passed, 0 failed, 0 skipped** across 146 suites.
- Responsive release matrix: **88/88 passed**.
- Axe: **22/22 passed** with no serious or critical finding.
- Slow 4G/4× CPU journeys: **6/6 passed**.
- Lighthouse journeys: **6/6 passed** with no threshold finding.

The shared-password production seed is removed. The isolated Render staging
service, Flyway V1–V4, durable upload redeploy/rollback proof, seven-day Render
logical export and isolated recovery-database validation now pass. The
temporary recovery instance was deleted after validation. This does not make
the application production-ready: the configured SMTP host, Twilio test
credentials and Stripe test key are invalid, so their required live sandbox
lifecycles remain unproved.

## Safety boundary used

The local transactional run used separate JAR processes. The Render staging run
used only a dedicated schema and synthetic `.example.invalid` identity. The
production service and provider integrations were left untouched. The safety
boundary was:

- in-memory H2;
- Stripe’s application simulation sentinel for local journeys and a
  staging-only test configuration for the live probe;
- a staging-only synthetic identity and Twilio magic test numbers;
- a non-real `.example.invalid` email recipient;
- AI disabled;
- test-only upload directories under ignored `Web_App/output/`;
- reserved `.example.test` email addresses.

No charge, refund, production subscription mutation, real email, real SMS,
OAuth account, production upload or production database row was created. Only
aggregate baseline counts were rechecked; no production row content was
retrieved.

## Direct software dependencies

| Boundary | Direct dependencies |
| --- | --- |
| Runtime | Java 21; Spring Boot 3.5.7 |
| Spring | Web, Validation, Security, OAuth2 Client, Data JPA, Thymeleaf, Mail and Actuator starters |
| Server-side libraries | Thymeleaf Spring Security Extras 3.1.5.RELEASE; OpenPDF 1.3.30; jsoup 1.22.2; OkHttp 4.12.0; Lombok 1.18.46 |
| Databases | PostgreSQL JDBC 42.7.11 in production; H2 for local/test |
| Build | Gradle wrapper 8.14.3; Spring dependency-management plugin 1.1.7; Gradle Versions plugin 0.54.0; JaCoCo |
| Front-end build | Node 22.22.x; npm 11.11.x; PostCSS 8.5.12; Tailwind CSS 3.4.19; Autoprefixer 10.5.0; cssnano 7.1.9; postcss-cli 11.0.1; postcss-import 16.1.0; tailwind-scrollbar 3.1.0 |
| Front-end runtime | Self-hosted SlimSelect asset plus application CSS/JavaScript |
| Hosting | Render Docker web service; Temurin 21 JRE image; Gradle 8.14.3 JDK 21 build image |

Transitive Java and npm packages resolve through Gradle/Maven Central and `package-lock.json`. There is no committed Gradle dependency lockfile.

## External services and network destinations

| Service | Purpose | Integration | Current safe result |
| --- | --- | --- | --- |
| Stripe | Platform subscriptions and merch checkout | Direct OkHttp calls to Stripe `/v1`; inbound HMAC webhook | Live staging request reached Stripe but returned HTTP 401 for an invalid configured key; no object or charge created |
| SMTP | Verification, password reset, trainer status, price changes and admin outreach | Spring `JavaMailSender` | SMTP selected after redeploy, but the configured host is invalid; no test-inbox delivery |
| Twilio | Phone verification SMS | Direct OkHttp Messages API | Missing sender was repaired with the documented test sender; live test request then returned Twilio 20003 for invalid test credentials; no SMS sent |
| OpenAI | Charlie assistant responses | Direct OkHttp Chat Completions call | Disabled for this assessment |
| Google | OAuth/OIDC login | Spring Security OAuth client | Disabled/unconfigured in sandbox |
| Microsoft Entra/Graph | OAuth/OIDC login and userinfo | Issuer discovery, token endpoints and Graph OIDC userinfo | Disabled/unconfigured in sandbox |
| Apple | OAuth/OIDC login | Apple authorisation, token and key endpoints | Disabled/unconfigured in sandbox |
| Open-Meteo | Client-dashboard weather | Browser-side `fetch` | Non-transactional; covered by prior UI gate |
| BigDataCloud | Browser reverse geocoding | Browser-side `fetch` | Non-transactional; covered by prior UI gate |
| YouTube/Vimeo | Approved workout-video embeds | Browser iframe URL generation | No upload or provider mutation |
| Unsplash | Demo merch fallback images | Browser image request | Demo fallback only |

No Redis, external cache, message broker, job queue, object store, CDN upload
service or separate session infrastructure is configured. HTTP sessions use the
isolated PostgreSQL schema through Spring Session JDBC.

## Environment and property inventory

Values were not copied into this document.

### Runtime and database

- `SPRING_PROFILES_ACTIVE`
- `PORT`
- `RENDER`
- `DEV_MODE`
- `APP_BASE_URL`
- `DATABASE_URL`
- `DATABASE_USER`
- `DATABASE_PASSWORD`
- `DATABASE_DRIVER`
- `SQL_INIT_MODE`
- `PGSSLMODE`
- `PG_CONNECT_TIMEOUT`

`bootRun` also recognises PostgreSQL-style `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER` and `PGPASSWORD` keys when deciding which local values not to forward.

### Payment and encrypted card-token storage

- `STRIPE_SECRET_KEY`
- `STRIPE_WEBHOOK_SECRET`
- `APP_PAYMENTS_CURRENCY`
- `APP_ENCRYPTION_CARD_KEY` for the `app.encryption.card-key` property

Without a persistent 32-byte Base64 card key, the app generates a random in-memory AES key and previously saved encrypted card tokens become unreadable after a restart.

### Email

- `APP_EMAIL_PROVIDER` (`none` or `smtp`)
- `APP_EMAIL_FROM`
- `APP_EMAIL_FAIL_ON_ERROR`
- `SPRING_MAIL_HOST`
- `SPRING_MAIL_PORT`
- `SPRING_MAIL_USERNAME`
- `SPRING_MAIL_PASSWORD`
- `SPRING_MAIL_SMTP_AUTH`
- `SPRING_MAIL_SMTP_STARTTLS_ENABLE`
- `SPRING_MAIL_SMTP_STARTTLS_REQUIRED`
- `SPRING_MAIL_SMTP_SSL_PROTOCOLS`
- `SPRING_MAIL_SMTP_SSL_TRUST`
- `SPRING_MAIL_SMTP_CONNECTION_TIMEOUT`
- `SPRING_MAIL_SMTP_TIMEOUT`
- `SPRING_MAIL_SMTP_WRITE_TIMEOUT`

### SMS

- `APP_SMS_PROVIDER` (`console` or `twilio`)
- `APP_SMS_FAIL_ON_ERROR`
- `TWILIO_ACCOUNT_SID`
- `TWILIO_AUTH_TOKEN`
- `TWILIO_FROM_NUMBER`
- `TWILIO_MESSAGING_SERVICE_SID`

### AI

- `OPENAI_API_KEY`
- `APP_AI_ENABLED`
- `APP_AI_MODEL`
- `APP_AI_TIMEOUT_MS`
- `APP_AI_DISCLOSURE`

### OAuth

- `APP_OAUTH_GOOGLE_LOGIN_ACTIVE`
- `APP_OAUTH_GOOGLE_CLIENT_ID`
- `APP_OAUTH_GOOGLE_CLIENT_SECRET`
- `APP_OAUTH_MICROSOFT_LOGIN_ACTIVE`
- `APP_OAUTH_MICROSOFT_CLIENT_ID`
- `APP_OAUTH_MICROSOFT_CLIENT_SECRET`
- `APP_OAUTH_MICROSOFT_ISSUER_URI`
- `APP_OAUTH_APPLE_LOGIN_ACTIVE`
- `APP_OAUTH_APPLE_CLIENT_ID`
- `APP_OAUTH_APPLE_CLIENT_SECRET`
- `APP_OAUTH_APPLE_ISSUER_URI`

### Upload locations

- `APP_STORAGE_PROFILE_DIR`
- `APP_STORAGE_CHAT_DIR`
- `APP_STORAGE_MERCH_DIR`
- `APP_STORAGE_WORKOUT_VIDEO_DIR`

### Scheduled-property override

- `app.notifications.proteinNudgeCron`, default `0 0 18 * * *`

The committed `render.yaml` currently declares only the profile, base URL, database, Stripe and OpenAI keys. Email, SMS, OAuth, storage, card encryption and operational scheduling configuration are absent from the deployment manifest.

## Webhooks and callback boundaries

### Inbound

The only application webhook is:

- `POST /pricing/webhook/stripe`

It accepts Stripe subscription checkout/update/deletion events. The route now bypasses browser CSRF so the controller can apply HMAC verification. Signatures now require a timestamp within five minutes of the server clock. A live disabled-secret probe reached the controller and returned its controlled 503 response.

Remaining provider evidence is still required for:

- a Stripe test-mode endpoint registration;
- checkout completion;
- subscription renewal/update/cancellation;
- failed payment;
- duplicate event delivery;
- delayed event delivery;
- provider retry behaviour;
- event observability and reconciliation.

Flyway V5 adds a persistent Stripe event-ID ledger. Unit/integration coverage
now proves duplicate suppression, invoice payment failure/recovery transitions
and provider-first subscription cancellation. Live provider confirmation
remains blocked by the invalid Stripe test key and webhook secret.

### Outbound callbacks

- Stripe checkout success/cancel URLs use `APP_BASE_URL`.
- OAuth redirect URIs use `{baseUrl}/login/oauth2/code/{registrationId}`.
- Email verification and password-reset links use `APP_BASE_URL`.

## Scheduled jobs

All jobs run inside each web application process and acquire a PostgreSQL lease
from `scheduled_job_locks` before doing work. The lease acquisition is atomic,
has an expiry selected for the job's maximum expected duration and fails closed
when database ownership cannot be established. This removes duplicate
multi-instance execution; job-level business idempotency remains desirable.

| Job | Schedule | Boundary |
| --- | --- | --- |
| Workout form-feedback processor | Fixed delay, 30 seconds | Reads uploaded workout video records and writes generated feedback |
| Upcoming task notifications | Fixed delay, 60 seconds | Reads calendar tasks and writes in-app notifications |
| Protein nudges | Daily at 18:00 server-local time by default | Reads completed workouts/nutrition and writes notifications |
| Timed health-condition follow-up | Daily at 02:15 server-local time | Reads sensitive health-condition data and writes notifications |
| Pending merch-order expiry | Fixed delay, 15 minutes; two-hour TTL | Cancels abandoned pending orders and restores stock |

The five business jobs and the operational-retention job are covered by an
annotation ownership contract. Time zone and production alert thresholds still
need an explicit launch decision.

## Upload and storage boundaries

| Upload | Application checks | Persisted to |
| --- | --- | --- |
| Profile image | 2 MiB limit; decoded and sanitised image | Local filesystem |
| Chat image | Up to five files, 4 MiB each; decoded and re-encoded | Local filesystem |
| Merch image | 5 MiB limit; sanitised image | Local filesystem |
| Workout form video | 8 MiB limit plus MP4/WebM signature check | Local filesystem |

Profile and merchandise images use public `/uploads/**` resource routes. Chat
images and workout videos are served through authenticated controller routes:
owners receive the file with `Cache-Control: no-store`, anonymous requests
receive 401 and unrelated authenticated owners receive 404.

The live staging service has a 1 GB persistent disk at `/var/data/uploads` and
points all four directories under it. A profile image remained HTTP 200 with
the same 139,305-byte size and SHA-256
`054f1b7337602ac967057876fe0f166b9ce9a7d9ba8d80498f22a391605a738f`
after a controlled redeploy and application rollback. Synthetic chat and
merchandise images remained 23,044 bytes with SHA-256
`d08fc3b55a4a7d1c50c77f8929cd7ac0ca69656652f9bab9fc19f11510fa613a`;
the synthetic workout video remained 24 bytes with SHA-256
`c8c5af84ac765d911a9ab05bc9a19d15d0b1bc5cf0654eff4469ce536410654e`
after deploy `dep-d9kst0rl550s73f6kdmg`. Each application deletion removed its
file and the labelled staging rows were cleaned afterwards.

Spring's multipart transport limits are explicitly 8 MB per file and 25 MB per
request. Tomcat's bounded 32 MB swallow limit permits a structured 413 JSON
response. Embedded-server coverage proves exact 8 MiB acceptance, 8 MiB + 1
byte rejection and aggregate multipart rejection above 25 MiB.

## Data stores, sessions and recovery

| Data | Current implementation |
| --- | --- |
| Production relational store | PostgreSQL via Render environment variables |
| Production schema | Flyway `V1__baseline_schema.sql`; SQL initialisation disabled |
| Production seed | None; `render-data.sql` removed |
| Local/test store | In-memory H2 |
| HTTP sessions | Spring Session JDBC in `spring_session` and `spring_session_attributes`; 30-minute expiry |
| Login attempt counters | PostgreSQL `login_attempts`; hashed username/network key, atomic update and two-day stale-record retention |
| Uploads | Configurable filesystem directories on the live 1 GB staging disk; all four boundaries passed create/read/delete and redeploy persistence, with owner checks on chat/video |
| Queue/cache | None |
| Backup/restore | Logical export and isolated temporary recovery-database validation passed; the temporary database was deleted after evidence was retained |
| Schema migration | Flyway V1 clean provisioning and forward upgrades through V7 proved on PostgreSQL 18; V5 adds the provider event ledger, V6 adds operational state/audit tables and V7 adds card-key continuity state |

The production demo seed and its shared credentials have been removed. Local and
test H2 fixtures remain available only to the local/test profiles.

## Representative transactional journeys

| Role/boundary | Safe journey | Result |
| --- | --- | --- |
| Client | Merch order through the explicit simulated checkout using a provider test-card number | Order reached PAID in disposable H2; page stated no charge or delivery |
| Trainer | Open/create the seeded client conversation and send a local coaching message | Conversation persisted and message showed Sent |
| Gym admin | Invite a new trainer for verification | Trainer and request persisted as Pending |
| Platform admin | Open the verification queue and approve the gym-submitted trainer | Pending moved to Approved; trainer marked verified |
| Stripe webhook | POST without browser CSRF while provider is disabled | Controller reached; controlled 503 returned |
| Email selection | Start contexts with `none` and `smtp` providers | Correct implementation selected in both cases |
| SMTP live staging | Trigger password recovery for a synthetic `.example.invalid` identity | SMTP selected; invalid host reproduced; no delivery |
| Twilio live staging | Request an OTP with documented test numbers | Missing sender reproduced and repaired; Twilio then rejected invalid test credentials with 20003; no SMS |
| Stripe live staging | Start subscription checkout with staging-only configuration | Stripe returned HTTP 401 for invalid key; no Checkout Session, subscription or charge |
| Database recovery | Restore to temporary Basic-256mb/1 GB recovery database | Available; schema creation and Flyway V1–V4 successful, 122 tables and labelled staging fixtures validated; instance deleted |

Provider delivery is intentionally not claimed. The current implementation
attempts email synchronously, catches provider failures and has no durable
delivery-status record. Admin success wording now says delivery was attempted
immediately and is not tracked; it does not claim queued or confirmed delivery.

## Repaired defects and coverage

| Confirmed defect | Repair | Coverage |
| --- | --- | --- |
| Stripe webhook returned an authentication response before HMAC handling | Exact webhook path excluded from CSRF only | `StripeWebhookSecurityIntegrationTest` |
| Old/future correctly signed webhook payloads were accepted | Injected `Clock`; enforced five-minute tolerance | `StripeWebhookServiceTest` |
| Trainer Message action used the wrong HTTP method | Replaced links with CSRF-protected POST forms | `TemplateRouteContractTest` plus live browser journey |
| Platform admin could not enter verification queue | Allowed both `PLATFORM_ADMIN` and `SUPER_ADMIN` at request and method layers | `RoleDashboardAccessTest` plus live browser approval |
| Empty mail host selected SMTP | Added explicit `app.email.provider` contract, default `none` | `EmailProviderConfigurationTest` |
| Render boot loaded shared-password demo users | Removed `render-data.sql`; disabled Render SQL initialisation; added Flyway V1 | `ProductionReadinessContractTest` |
| Configured upload roots were not served | Added one resource mapping per configured upload boundary | `ProductionReadinessContractTest` |
| Flyway activated inside H2 test slices | Explicitly disabled Flyway in local/test configuration | Full 513-test suite |
| Reused database would target populated `public` schema | Added validated JDBC, Flyway and Hibernate staging-schema boundary | `OneToOneApplicationDatabaseSchemaTest` and `ProductionReadinessContractTest` |
| Legacy and V2 chat entities both owned `chat_messages` with incompatible mappings | Removed the unused legacy persistence path and asserted unique explicit table ownership | `ProductionReadinessContractTest` |
| Real PostgreSQL validation required missing chat-thread fields | Added Flyway V2 for `chat_type` and `peer_user_id` | Migration contract coverage plus live boot |
| Seven health-record columns used PostgreSQL types incompatible with Hibernate | Added Flyway V3 with the required integer/double-precision types | Migration contract coverage plus live boot |
| `saved_payment_methods.last_four` used fixed-width `CHAR(4)` | Added Flyway V4 converting it to `VARCHAR(4)` | Migration contract coverage plus live boot |
| Duplicate Stripe event IDs were processed more than once | Added a persistent event-ID ledger and record-after-success handling | `StripeWebhookServiceTest` and `StripeWebhookEventStoreIntegrationTest` |
| Stripe invoice failure/recovery events were ignored | Added `PAST_DUE` transition and recovery to the intended active/expiring state | `StripeWebhookServiceTest` and `PlatformSubscriptionServiceTest` |
| Subscription cancellation changed only local state | Send cancellation to Stripe first and commit local state only after provider success | `PlatformSubscriptionControllerTest` |
| Chat images and workout videos were publicly readable | Replaced their static mappings with authenticated owner-scoped reads | `PrivateUploadAccessIntegrationTest` plus staging owner/peer/anonymous acceptance |
| Chat batches over five files were silently truncated and partial failures left files | Reject the complete batch and remove earlier writes on failure | `ChatImageStorageServiceTest` |
| Merchandise deletion/replacement orphaned durable images | Remove only unreferenced files, retain order snapshots and clean failed saves | `MerchProductServiceImplTest` |
| Workout video size/deletion lifecycle was unbounded | Enforce 8 MiB, clean failed persistence and add owner-scoped delete | `WorkoutFormFeedbackServiceTest` plus staging acceptance |
| Multipart transport rejection returned no useful response | Set 8 MB/25 MB limits and bounded swallowing; return safe JSON 413 | `MultipartUploadRejectionIntegrationTest` and `MultipartUploadLimitsContractTest` |
| Actuator probes were protected and Render checked `/login` | Added status-only liveness/readiness endpoints, retained aggregate-health authentication and configured Render readiness | `HealthEndpointSecurityIntegrationTest` plus live HTTP proof |
| Sessions and login throttles were lost on restart | Added Spring Session JDBC and a hashed PostgreSQL login-attempt store | `SessionPersistenceIntegrationTest`, `LoginAttemptServiceTest` plus two staging restart proofs |
| Scheduled jobs had no cross-instance ownership | Added database leases and fail-closed method interception to every scheduled job | `ScheduledJobLeaseServiceTest` and `ScheduledJobOwnershipContractTest` |
| Spring could not bind the lease annotation when a real scheduled proxy fired | Resolve the annotation from the concrete target method and fail closed if it is absent | `ScheduledJobLeaseAspectTest` real-proxy coverage |
| Privileged mutations had no retained security evidence | Added privacy-bounded audit events, 180-day retention and operational ownership | `PrivilegedAuditFilterTest`, `PrivilegedAuditServiceTest` and live staging probe |
| An access-denied redirect was classified as audit success | Treat `/login` and `/access-denied` security redirects as failed privileged outcomes | `PrivilegedAuditFilterTest` plus live denial replay |

## Production topology comparison

The 29 July 2026 read-only Render inspection confirms that the isolated
reference is one Oregon Starter instance with a 1 GB disk. Render prohibits
multiple instances while that disk is attached and disables zero-downtime
deploys for disk-backed services.

| Decision | Technical contract | Exact current Render increment | Decision status |
| --- | --- | ---: | --- |
| Single-instance maintenance handover | Retain one Starter and the 1 GB disk; use maintenance mode, readiness, a recorded rollback artifact and named primary/backup sign-off for every production deploy | **US$0.00/month** (current web/disk baseline remains US$7.25/month) | Recommended for initial launch; awaiting James's sign-off |
| Two-instance zero-downtime topology | First replace all four local upload boundaries with shared object storage, detach the disk, then run two manually scaled Starter instances | **US$6.75/month net Render increase** (US$14.00 rather than US$7.25), plus unquoted external object-storage usage | Awaiting provider selection, exact external quote and separate billable approval |

The second Starter instance itself adds US$7.00/month, billed by the second;
removing the 1 GB Render disk after a successful storage migration subtracts
US$0.25/month. Render does not provide S3-style object storage, so the total
two-instance cost cannot be represented as exact until a shared provider,
region and usage envelope are selected. No scale, disk, storage-provider or
production configuration change was made.

The complete operational procedure and approval boundary are recorded in
`Web_App/docs/phase4-staging-runbook.md`. Production monitoring/security
assignments remain deliberately unrecorded until James supplies a named primary
and backup; the manifest does not authorise treating the interim staging owner
as the production assignment.

## Launch blockers

| Priority | Blocker | Exit evidence | Suggested owner |
| --- | --- | --- | --- |
| Closed | Production demo seed removal and clean PostgreSQL provisioning | Flyway V1 created the staging schema with zero users; no demo seed ran | Backend/security |
| Closed | Upload persistence, ownership, deletion and multipart limits | All four boundaries passed; commit `7c4fce55`, live deploy `dep-d9kst0rl550s73f6kdmg`, 31 focused storage tests | Platform/backend |
| Closed | Isolated staging service and disk | Live Starter service, dedicated schema and 1 GB persistent disk with providers disabled | Platform |
| Deferred P0 | Stripe test key/webhook secret are intentional invalid placeholders; live lifecycle is unproved | At the final pre-launch gate, install a valid `sk_test_...` key and staging-only endpoint secret; prove checkout, renewal, cancellation, failure, retry, duplicate and replay | Payments/backend |
| Deferred P0 | SMTP host and Twilio test credentials are intentional invalid placeholders | At the final pre-launch gate, install a non-delivering SMTP inbox and valid Twilio test credentials; prove delivery/failure/OTP behaviour | Platform/backend |
| Closed | Isolated restore and restored Flyway validation | Temporary recovery database validated V1–V4, fixtures and table count, then deleted | Platform/database |
| Closed | Stripe repairs and Flyway V5 deployment | Commit `f99024cf`; live deploy `dep-d9kgqgh42hec73doqmkg`; six migration records and V5 ledger table validated | Backend/payments |
| Closed | Authentication-safe health and Render health path | Status-only liveness/readiness return 200; aggregate health stays 401; Render checks readiness | Platform |
| Closed | Scheduled-job multi-instance ownership | All scheduled jobs use PostgreSQL leases and fail closed without ownership | Platform/backend |
| Closed | Process-local sessions and login throttles | JDBC session and hashed throttle state both survived controlled staging restarts | Platform/security |
| Closed | Privileged-action audit and retention | Mutations retained without body/query content; security denials fail; 180-day purge and interim owner documented | Security/product |
| Closed | Environment and secret ownership/rotation manifest | Versioned variable inventory, interim ownership, rotation/revocation and redaction controls | Platform/security |
| Closed | Persistent saved-card encryption and restart continuity | Render fails closed without the key; V7 marker and saved token decrypted after controlled restart | Security/backend |
| Closed | Provider UI claimed a queue that does not exist | Admin wording now states synchronous attempt and untracked delivery | Backend/product |
| Closed | Release suite was not a required CI check | GitHub Actions release gate passed and strict `Release gate` is required on `main` | Platform |
| P1 | Production topology is not signed off | Sign off the documented US$0 incremental maintenance handover, or select/quote shared object storage and separately approve the US$6.75/month net Render increase before a two-instance change | Platform/product |
| P1 | Production monitoring/security owners are not named | Assign primary and backup owners; James is interim staging owner only | Product/platform |
| P2 | External weather/geocoding, image and video dependencies need privacy/availability review | CSP/privacy/failure-mode review and documented fallback | Front-end/legal |

## Go/no-go checklist

### Code and automated gate

- [x] Full Gradle suite passes.
- [x] Production CSS build passes.
- [x] Responsive, Axe, throttled-performance and Lighthouse gates pass.
- [x] Webhook route reaches signature validation without browser CSRF.
- [x] Webhook signature timestamp tolerance is covered.
- [x] Representative role transactions pass in disposable H2.
- [x] Email provider selection is explicit.

### Required before GO

- [x] Remove production demo accounts/shared credentials.
- [x] Provision staging with a dedicated database schema and prove `public` is unchanged.
- [ ] Configure and prove Stripe test mode, including webhook retries and duplicates.
- [ ] Configure and prove SMTP test delivery and failure handling.
- [ ] Configure and prove Twilio test delivery and failure handling.
- [ ] Prove Google/Microsoft/Apple OAuth only for providers intended at launch.
- [x] Persist profile uploads and prove redeploy/rollback recovery.
- [x] Prove chat-image create/read/delete, owner isolation and redeploy persistence.
- [x] Prove merchandise-image create/read/delete and redeploy persistence.
- [x] Prove workout-video create/read/delete, owner isolation and redeploy persistence.
- [x] Set explicit multipart file/request limits and prove rejection at the real server boundary.
- [x] Configure a persistent card-encryption key and prove restart compatibility.
- [x] Adopt versioned production database migrations.
- [x] Prove Flyway clean provisioning and forward upgrade on staging PostgreSQL.
- [x] Complete the isolated database restore; logical export and application rollback pass.
- [x] Configure readiness/liveness monitoring and staging alert ownership.
- [x] Resolve multi-instance scheduler, session and login-throttle ownership.
- [x] Confirm audit logging, retention, privacy and staging incident-response expectations.
- [ ] Assign named primary and backup production monitoring/security owners.
- [ ] Decide maintenance-handover versus two-instance production deployment.
- [x] Re-run the full local gate against the current staging code revision.

The decision remains **NO-GO** until every P0 item and any launch-applicable P1 item has an owner, evidence and sign-off.

## Next safe work package

Provider variables were loaded by a new live staging deployment. Safe requests
then reproduced configuration failures: the SMTP host is invalid, Twilio
returned authentication error 20003, and Stripe returned HTTP 401 for an
invalid API key. The application provider paths are therefore reached, but no
delivery or Stripe lifecycle pass is claimed.

James directed these real sandbox credentials to remain deferred until the
final pre-launch gate. A deterministic focused run with the relevant SMTP,
Twilio and Stripe environment values set to the intentional placeholder
`"2bd"` passed **19 tests across 6 suites** with zero failures. Together with
the live probes, the accepted simulation result is fail-closed: no email or
SMS delivery, no Stripe object or charge, no subscription activation and no
local cancellation success after provider rejection.

James separately approved one temporary Basic-256mb Render recovery database
with 1 GB storage at **US$6.30/month**, prorated by the second. The isolated
restore passed and the exact recovery database was deleted immediately after
validation. Only the original source database remains available.

The approved approximately **US$7.25/month incremental** staging web service
and disk are live. The schema boundary, migrations, all four upload boundaries,
logical export, application rollback, operational state and current-code
automated gates pass.
Execute the remaining work in order:

1. decide the one- versus two-instance launch topology; any scale increase is
   billable and requires explicit approval;
2. assign named primary and backup production monitoring/security owners;
3. replace the invalid provider placeholders only at the final pre-launch gate
   and prove the complete live sandbox lifecycles;
4. rerun the full gate against that provider-enabled release candidate.

## Persistent saved-card encryption and required CI evidence

Commit `fcf33145` encrypts saved provider tokens with versioned AES-256-GCM
ciphertext before persistence and enforces ownership before decryption. Render
profile startup now fails closed if `APP_ENCRYPTION_CARD_KEY` is absent,
invalid or cannot decrypt the V7 continuity marker or any saved token. Existing
unversioned encrypted values remain readable for compatibility; plaintext is
never accepted by the startup verifier.

The already-present masked staging key was retained rather than rotated
blindly. `APP_ENCRYPTION_REQUIRE_PERSISTENT_KEY=true` was added without changing
SMTP, Twilio or Stripe values. Initial deploy `dep-d9l00lid0e5s73ei0j8g`
applied Flyway V7 and created one versioned 80-character continuity marker. A
synthetic client then saved one synthetic provider token. Read-only staging
evidence showed one 72-character `v1` ciphertext, no plaintext match and
fingerprint `039e0ddfc22b67e4efb2cc4a475c6cb1`.

Controlled same-commit deploy `dep-d9l03oj7uimc7389hllg` reached live. Startup
logged successful continuity verification for one saved payment method,
readiness returned HTTP 200 and the ciphertext fingerprint remained unchanged.
The synthetic account was deleted through its authenticated account lifecycle;
subsequent user and card counts were both zero. The encrypted continuity marker
remains as operational state.

The complete local candidate passed **568/568 tests across 146 suites** and
`bootJar`. The first full browser run had one non-repeatable client CLS sample;
an isolated rerun of all six throttled journeys passed with zero findings, so
no performance edit was made. GitHub Actions run `30456277694` then passed the
production CSS build, Java tests/artefact and full browser release gate using
Java 21 and Node 22.22. The `main` branch now strictly requires the
`Release gate` status check.

The versioned environment/secret register is
`Web_App/docs/phase4-environment-secret-manifest.md`. Production remains
**NO-GO** because primary/backup production owners and deployment topology are
unresolved and the intentional `2bd` provider placeholders have not completed
their final sandbox lifecycles.

Any real provider charge, refund, production data read/write, production webhook change or destructive database operation still requires James’s explicit approval.
