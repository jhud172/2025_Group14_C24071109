# Email setup (real SMTP delivery)

This project now uses SMTP for real email delivery.

## 1) Set environment variables

Use these environment variables before starting the app:

- `APP_EMAIL_FROM=<your-from-address>`
- `SPRING_MAIL_HOST=<smtp-host>`
- `SPRING_MAIL_PORT=<smtp-port>` (usually `587` for STARTTLS, `465` for SSL)
- `SPRING_MAIL_USERNAME=<smtp-username>`
- `SPRING_MAIL_PASSWORD=<smtp-password-or-app-password>`

Optional:

- `SPRING_MAIL_SMTP_AUTH=true`
- `SPRING_MAIL_SMTP_STARTTLS_ENABLE=true`
- `SPRING_MAIL_SMTP_STARTTLS_REQUIRED=true`
- `SPRING_MAIL_SMTP_SSL_PROTOCOLS=TLSv1.2`
- `SPRING_MAIL_SMTP_SSL_TRUST=<smtp-host>` (example: `smtp.mail.me.com`)
- `APP_EMAIL_FAIL_ON_ERROR=true`

## 2) Restart and verify

Start the app and trigger one of these flows:

- password reset
- email verification
- trainer verification update
- membership price change notification

Delivery errors now fail fast, so issues are visible immediately.

## 3) Common provider examples

### Gmail

- `SPRING_MAIL_HOST=smtp.gmail.com`
- `SPRING_MAIL_PORT=587`
- Use a Google App Password (not your normal password)

### Outlook / Microsoft 365

- `SPRING_MAIL_HOST=smtp.office365.com`
- `SPRING_MAIL_PORT=587`

### iCloud Mail

- `SPRING_MAIL_HOST=smtp.mail.me.com`
- `SPRING_MAIL_PORT=587`
- `SPRING_MAIL_SMTP_SSL_TRUST=smtp.mail.me.com`

## 4) Security note

Do not commit credentials to source control. Keep values in environment variables or a local secrets manager.
