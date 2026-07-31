# Phase 4 environment and secret ownership manifest

Last updated: 29 July 2026

This is the versioned control register for application configuration. It records
variable names and handling requirements only. Secret values must remain in
Render or the relevant provider vault and must never be copied into source,
tickets, chat, screenshots or logs.

## Ownership and approval

| Scope | Primary owner | Backup owner | Current release status |
|---|---|---|---|
| Isolated staging configuration | James (interim) | Unassigned | Operational for Phase 4 |
| Production configuration | Unassigned | Unassigned | **NO-GO until both are named** |
| Database backup and recovery | James (interim staging) | Unassigned | Production owner required |
| Provider credentials and webhooks | James (interim staging) | Unassigned | Real credentials deferred |
| Incident response and emergency rotation | James (interim staging) | Unassigned | Production on-call owner required |

Only workspace members with an operational need may read or change environment
variables. Every production change requires a second-person review and an audit
record. Provider dashboard access must use individual accounts with MFA; shared
credentials are not permitted.

## Variable inventory

“Secret” means the value must be masked and rotated. “Sensitive” means it is not
normally a credential but must not be casually disclosed because it describes
infrastructure or security behaviour.

| Boundary | Variables | Classification | Requirement and current staging state |
|---|---|---|---|
| Runtime identity | `SPRING_PROFILES_ACTIVE`, `PORT`, `RENDER`, `DEV_MODE`, `APP_ENVIRONMENT`, `APP_BASE_URL`, `RENDER_INSTANCE_ID`, `HOSTNAME` | Public/sensitive mix | Render profile and staging base URL are required. Instance names are diagnostic metadata. |
| Primary database | `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD`, `DATABASE_DRIVER`, `APP_DATABASE_SCHEMA`, `SQL_INIT_MODE`, `PGSSLMODE`, `PG_CONNECT_TIMEOUT` | Secret/sensitive | Required in Render. Staging must remain on `one_to_one_staging`; production/public schema access is out of scope. |
| Local PostgreSQL aliases | `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD` | Secret/sensitive | Local tooling only; do not set these from production exports. |
| Card protection | `APP_ENCRYPTION_CARD_KEY`, `APP_ENCRYPTION_REQUIRE_PERSISTENT_KEY` | Secret/control | A masked persistent 32-byte Base64 key is present in isolated staging. Persistent-key enforcement is required in the Render profile. |
| Stripe | `STRIPE_SECRET_KEY`, `STRIPE_WEBHOOK_SECRET`, `APP_PAYMENTS_CURRENCY` | Secret/public mix | Staging credential values intentionally remain `2bd`; real test-mode credentials are deferred to the final gate. Never use live-mode values in staging. |
| Email provider | `APP_EMAIL_PROVIDER`, `APP_EMAIL_FROM`, `APP_EMAIL_FAIL_ON_ERROR` | Public/control | Staging provider values intentionally remain `2bd`; delivery is not currently proven. |
| SMTP transport | `SPRING_MAIL_HOST`, `SPRING_MAIL_PORT`, `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD`, `SPRING_MAIL_SMTP_AUTH`, `SPRING_MAIL_SMTP_STARTTLS_ENABLE`, `SPRING_MAIL_SMTP_STARTTLS_REQUIRED`, `SPRING_MAIL_SMTP_SSL_PROTOCOLS`, `SPRING_MAIL_SMTP_SSL_TRUST`, `SPRING_MAIL_SMTP_CONNECTION_TIMEOUT`, `SPRING_MAIL_SMTP_TIMEOUT`, `SPRING_MAIL_SMTP_WRITE_TIMEOUT` | Secret/control | Credentials intentionally remain `2bd`; timeouts and TLS controls must remain explicit. |
| SMS provider | `APP_SMS_PROVIDER`, `APP_SMS_FAIL_ON_ERROR`, `TWILIO_ACCOUNT_SID`, `TWILIO_AUTH_TOKEN`, `TWILIO_FROM_NUMBER`, `TWILIO_MESSAGING_SERVICE_SID` | Secret/control | Staging values intentionally remain `2bd`; real recipients are forbidden during validation. |
| OAuth | `APP_OAUTH_GOOGLE_LOGIN_ACTIVE`, `APP_OAUTH_GOOGLE_CLIENT_ID`, `APP_OAUTH_GOOGLE_CLIENT_SECRET`, `APP_OAUTH_MICROSOFT_LOGIN_ACTIVE`, `APP_OAUTH_MICROSOFT_CLIENT_ID`, `APP_OAUTH_MICROSOFT_CLIENT_SECRET`, `APP_OAUTH_MICROSOFT_ISSUER_URI`, `APP_OAUTH_APPLE_LOGIN_ACTIVE`, `APP_OAUTH_APPLE_CLIENT_ID`, `APP_OAUTH_APPLE_CLIENT_SECRET`, `APP_OAUTH_APPLE_ISSUER_URI` | Secret/control | OAuth remains disabled in staging until a launch decision and provider-specific callback review. |
| AI provider | `OPENAI_API_KEY`, `APP_AI_ENABLED`, `APP_AI_MODEL`, `APP_AI_TIMEOUT_MS`, `APP_AI_DISCLOSURE` | Secret/control | AI remains disabled in staging. A production data-use decision is required before enablement. |
| Durable uploads | `APP_STORAGE_PROFILE_DIR`, `APP_STORAGE_CHAT_DIR`, `APP_STORAGE_MERCH_DIR`, `APP_STORAGE_WORKOUT_VIDEO_DIR` | Sensitive | Required paths must remain under the staging persistent disk mount. |
| Operations | `APP_AUDIT_RETENTION_DAYS` | Control | Retention must be approved by the production data owner before launch. |

The scheduled protein-nudge expression is the Spring property
`app.notifications.proteinNudgeCron`; it is configuration, even though it is not
currently exposed as an upper-case environment variable.

## Rotation procedures

### Routine and emergency cadence

- Review access, owners and variable inventory quarterly and before every launch.
- Rotate immediately after suspected disclosure, provider compromise, staff
  departure, accidental logging or unauthorised environment access.
- Record who approved the change, the variable names changed, provider request
  identifiers, deployment ID, validation result and rollback decision. Never
  record the old or new values.

### Saved-card encryption key

The card key must not be replaced as an ordinary environment-variable update.
A rotation requires a tested, versioned dual-key migration:

1. take and verify a staging backup;
2. deploy code that can decrypt with the old key and encrypt with the new key;
3. re-encrypt the continuity marker and every saved provider token in one
   controlled migration;
4. prove reads and rollback from a separate process;
5. remove old-key support only after validation and an approved recovery point.

Changing the key without this sequence intentionally prevents startup. Never log
the key or decrypted provider tokens.

### Database, provider and OAuth secrets

1. Create the replacement in the provider's sandbox or database control plane.
2. Add it to isolated staging while the old credential remains recoverable.
3. deploy, run readiness and the relevant contract/lifecycle checks;
4. roll back the environment change if validation fails;
5. revoke the old credential only after a successful audit record.

Database changes must preserve a tested recovery connection until the new
connection and migrations are verified. Webhook rotations must support provider
overlap where available and must not alter production endpoints during staging
work.

## Redaction and evidence

Logs and test evidence may contain variable names, configured/not-configured
booleans, non-sensitive deployment IDs and provider request IDs. They must not
contain values, decrypted payment tokens, authorisation headers, passwords,
session identifiers, signed webhook bodies, recipient details or database URLs.
Screenshots must keep masked inputs masked.

The release gate must scan changes through review, run the complete Java and
browser suites and retain only non-secret evidence. A release remains **NO-GO**
if the required CI check is missing or failing, production owners are unassigned,
provider credentials are placeholders, or rollback evidence is incomplete.
