# One To One — Phase 4 production-readiness inventory

**Assessment date:** 28 July 2026
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

The final post-repair automated result is:

- CSS production build: passed.
- Gradle: **520 tests passed, 0 failed, 0 skipped** across 131 suites.
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

No Redis, external cache, message broker, job queue, object store, CDN upload service or dedicated session store is configured.

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

All jobs run inside each web application process. There is no distributed scheduler lock.

| Job | Schedule | Boundary |
| --- | --- | --- |
| Workout form-feedback processor | Fixed delay, 30 seconds | Reads uploaded workout video records and writes generated feedback |
| Upcoming task notifications | Fixed delay, 60 seconds | Reads calendar tasks and writes in-app notifications |
| Protein nudges | Daily at 18:00 server-local time by default | Reads completed workouts/nutrition and writes notifications |
| Timed health-condition follow-up | Daily at 02:15 server-local time | Reads sensitive health-condition data and writes notifications |
| Pending merch-order expiry | Fixed delay, 15 minutes; two-hour TTL | Cancels abandoned pending orders and restores stock |

Scaling beyond one process can run the same job concurrently. Time zone, idempotency and multi-instance ownership need explicit production decisions.

## Upload and storage boundaries

| Upload | Application checks | Persisted to |
| --- | --- | --- |
| Profile image | 2 MiB limit; decoded and sanitised image | Local filesystem |
| Chat image | Up to five files, 4 MiB each; decoded and re-encoded | Local filesystem |
| Merch image | 5 MiB limit; sanitised image | Local filesystem |
| Workout form video | MP4/WebM signature check; no explicit service-level byte limit | Local filesystem |

Generated URLs use public `/uploads/**` routes. `WebConfig` now maps profile,
chat, merchandise and workout-video routes to their matching configured
directories.

The live staging service has a 1 GB persistent disk at `/var/data/uploads` and
points all four directories under it. A profile image remained HTTP 200 with
the same 139,305-byte size and SHA-256
`054f1b7337602ac967057876fe0f166b9ce9a7d9ba8d80498f22a391605a738f`
after a controlled redeploy and application rollback. Chat, merchandise and
workout-video create/delete boundaries still need representative acceptance.

Spring’s global multipart limits are not explicitly configured, so they must be aligned with the larger application-specific limits before upload acceptance can be considered proved.

## Data stores, sessions and recovery

| Data | Current implementation |
| --- | --- |
| Production relational store | PostgreSQL via Render environment variables |
| Production schema | Flyway `V1__baseline_schema.sql`; SQL initialisation disabled |
| Production seed | None; `render-data.sql` removed |
| Local/test store | In-memory H2 |
| HTTP sessions | In-process/default servlet sessions |
| Login attempt counters | In-process map |
| Uploads | Configurable filesystem directories on the live 1 GB staging disk; profile redeploy and rollback persistence proved |
| Queue/cache | None |
| Backup/restore | Logical export and isolated temporary recovery-database validation passed; the temporary database was deleted after evidence was retained |
| Schema migration | Flyway V1 clean provisioning and forward upgrades through V4 proved on PostgreSQL 18; V5 event-ledger migration passes integration coverage and awaits staging deployment |

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

Provider delivery was intentionally not claimed. The approval UI says “Notification queued”, but the current implementation has no durable notification queue or delivery-status record.

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

## Launch blockers

| Priority | Blocker | Exit evidence | Suggested owner |
| --- | --- | --- | --- |
| Closed | Production demo seed removal and clean PostgreSQL provisioning | Flyway V1 created the staging schema with zero users; no demo seed ran | Backend/security |
| P1 | Profile upload persistence passes; three upload types and deletion remain | Chat/merch/video upload-read-redeploy-delete proof and multipart-limit decision | Platform/backend |
| Closed | Isolated staging service and disk | Live Starter service, dedicated schema and 1 GB persistent disk with providers disabled | Platform |
| P0 | Stripe test key/webhook secret are invalid; live lifecycle is unproved | Valid `sk_test_...` key and staging-only endpoint secret; checkout, renewal, cancellation, failure, retry, duplicate and replay evidence | Payments/backend |
| P0 | SMTP host and Twilio test credentials are invalid | Non-delivering SMTP inbox and valid Twilio test credentials; delivery/failure/OTP evidence | Platform/backend |
| Closed | Isolated restore and restored Flyway validation | Temporary recovery database validated V1–V4, fixtures and table count, then deleted | Platform/database |
| P0 | Stripe repairs and Flyway V5 are not yet live on staging | Deploy current branch, verify V5 and rerun provider lifecycle against the repaired build | Backend/payments |
| P1 | Render manifest omits email, SMS, OAuth, storage and card-encryption configuration | Complete secret/config manifest with owner and rotation process | Platform/security |
| P1 | Saved-card encryption key is ephemeral when unconfigured | Persistent rotated secret configured and restart-decryption test | Security/backend |
| P1 | Provider actions have no durable queue/delivery state but UI says “queued” | Delivery-state model or accurate synchronous wording plus retry policy | Backend/product |
| P1 | Scheduled jobs have no multi-instance lock | Single-instance guarantee or distributed-lock/idempotency proof | Platform/backend |
| P1 | HTTP sessions and login throttles are process-local | Sticky/single-instance decision or shared session/rate-limit store | Platform/security |
| P1 | Actuator health returned 401 and Render has no configured health-check path | Auth-safe readiness endpoint and hosting health-check configuration | Platform |
| P1 | Docker build explicitly skips tests and deploys automatically | CI-required green gate before deploy; reviewed rollback path | Platform |
| P1 | Admin/provider auditing is partial | Audit requirements, retention and privileged-action evidence | Security/product |
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
- [ ] Configure a persistent card-encryption key and prove restart compatibility.
- [x] Adopt versioned production database migrations.
- [x] Prove Flyway clean provisioning and forward upgrade on staging PostgreSQL.
- [x] Complete the isolated database restore; logical export and application rollback pass.
- [ ] Configure readiness/liveness monitoring and alert ownership.
- [ ] Resolve multi-instance scheduler, session and login-throttle ownership.
- [ ] Confirm audit logging, retention, privacy and incident-response expectations.
- [x] Re-run the full local gate against the current staging code revision.

The decision remains **NO-GO** until every P0 item and any launch-applicable P1 item has an owner, evidence and sign-off.

## Next safe work package

Provider variables were loaded by a new live staging deployment. Safe requests
then reproduced configuration failures: the SMTP host is invalid, Twilio
returned authentication error 20003, and Stripe returned HTTP 401 for an
invalid API key. The application provider paths are therefore reached, but no
delivery or Stripe lifecycle pass is claimed.

James separately approved one temporary Basic-256mb Render recovery database
with 1 GB storage at **US$6.30/month**, prorated by the second. The isolated
restore passed and the exact recovery database was deleted immediately after
validation. Only the original source database remains available.

The approved approximately **US$7.25/month incremental** staging web service
and disk are live. The schema boundary, migrations, profile-upload persistence,
logical export, application rollback and current-code automated gates pass.
Execute the remaining work in order:

1. replace the invalid SMTP host/credentials and prove test-inbox verification and password recovery;
2. replace the invalid Twilio test SID/token and prove magic-number OTP behaviour;
3. replace the invalid Stripe key/webhook secret and prove the complete test-mode lifecycle;
4. finish the remaining upload-boundary, monitoring and operational-ownership gates;
5. rerun the full gate against the provider-enabled staging release candidate.

Any real provider charge, refund, production data read/write, production webhook change or destructive database operation still requires James’s explicit approval.
