# Email setup (real SMTP delivery)

This project now supports two email providers:

- `log` (default): logs email content to the application logs (no real delivery)
- `smtp`: sends real emails via SMTP

## 1) Set environment variables

Use these environment variables before starting the app:

- `APP_EMAIL_PROVIDER=smtp`
- `APP_EMAIL_FROM=<your-from-address>`
- `SPRING_MAIL_HOST=<smtp-host>`
- `SPRING_MAIL_PORT=<smtp-port>` (usually `587` for STARTTLS, `465` for SSL)
- `SPRING_MAIL_USERNAME=<smtp-username>`
- `SPRING_MAIL_PASSWORD=<smtp-password-or-app-password>`

Optional:

- `SPRING_MAIL_SMTP_AUTH=true`
- `SPRING_MAIL_SMTP_STARTTLS_ENABLE=true`
- `APP_EMAIL_FAIL_ON_ERROR=true`

## 2) Restart and verify

Start the app and trigger one of these flows:

- password reset
- email verification
- trainer verification update
- membership price change notification

With `APP_EMAIL_PROVIDER=smtp`, delivery errors now fail fast (instead of only logging), so issues are visible immediately.

## 3) Common provider examples

### Gmail

- `SPRING_MAIL_HOST=smtp.gmail.com`
- `SPRING_MAIL_PORT=587`
- Use a Google App Password (not your normal password)

### Outlook / Microsoft 365

- `SPRING_MAIL_HOST=smtp.office365.com`
- `SPRING_MAIL_PORT=587`

## 4) Security note

Do not commit credentials to source control. Keep values in environment variables or a local secrets manager.
