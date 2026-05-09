# One To One Android Assignment Underview

## Application Summary

The One To One Android app is a native mobile companion for the One To One premium coaching platform. It has been developed from an initial local trainer demo into a role-aware Android client connected to the wider One To One product.

The app supports four user states:

- Public users who can explore, log in, sign up, or use demo mode.
- Client users who can view their coaching home, calendar, day plan, training logs, chat, and account state.
- Trainer users who can view client-related records, calendar information, chat, and account tools.
- Gym users who can view trainer and request information through a separate gym-focused interface.

## Relationship To One To One

The app follows the same product rules as the One To One website:

- The platform is built around focused one-to-one coaching.
- Clients should have only one active trainer relationship at a time.
- Trainers are treated as verified coaching accounts.
- Trainers can operate independently or through gyms.
- Payments and sensitive account operations remain server-managed.
- Features must support client training, trainer management, gym oversight, messaging, or account access.

The Android app uses One To One's visual identity, including the logo and a website-inspired colour palette, but adapts the experience for mobile rather than copying the website layout directly.

## Main Features

### Public Experience

The public experience includes a premium welcome screen, an explore page, login, signup, and assessment-safe demo mode.

### Authentication

Login and signup are designed to call the hosted Spring Boot mobile API. The app validates required fields locally before sending requests. Remembered login uses an encrypted opaque mobile token stored through Android Keystore.

### Role-Aware Navigation

Navigation changes depending on the signed-in role:

- Client: Home, Calendar, Train, Chat.
- Trainer: Home, Clients, Calendar, Chat, More.
- Gym: Home, Trainers, Requests, Calendar, More.

The day view is reached from calendar days instead of being a permanent client navigation item.

### Calendar And Day Planning

Calendar days with assigned work are clickable. Opening a day shows the matching day plan and completion state. Completion actions write back through the mobile API.

### Training Log Flow

Clients can write training notes. The form validates that notes are present before attempting to save the log through the API.

### Coach Chat

The chat screen supports coach-style messages and validates that the user has written a message before sending it. Chat is designed to use only necessary coaching context and not expose payment or server secrets.

### Demo Mode

Demo mode is retained so the project can be assessed even if the hosted backend is not reachable. This keeps the submission reliable while still demonstrating the intended integrated architecture.

## Technical Approach

The Android application uses:

- Kotlin.
- Jetpack Compose.
- Material 3.
- Coroutines and StateFlow.
- Android Keystore.
- Room for demo/fallback data.
- WorkManager for reminder support.
- A remote API package for JSON calls and session management.

The Spring Boot website provides the mobile API layer under `/api/mobile/**`. Android never connects directly to the Render PostgreSQL database. The server remains responsible for database access, role checks, validation, payment handling, and secret management.

## Assessment Fit

The app demonstrates:

- Use of Android APIs and Jetpack libraries.
- A clear mobile user interface.
- Local and remote data handling.
- Secure session storage.
- Form validation and error feedback.
- A role-aware navigation model.
- Integration planning with an existing backend.
- A fallback path for reliable assessment.

## Grade Estimate

Based on the current implementation, the realistic grade estimate is:

```text
72-80%
```

This assumes the final submission is packaged correctly, the video demonstrates the app clearly, and the examiner can see both the live integration intent and the assessment-safe demo mode.

The main remaining risk is deployment alignment. The Android app is configured for the live Render web service, but the hosted backend must be redeployed with the `/api/mobile/**` endpoints before real production login, signup, and data features work against the live database.

## Submission Guidance

Before final submission, ensure the required assessment structure is satisfied. If the pro-forma expects numbered files, these documents may need to be copied or renamed into the expected format, for example:

```text
1-overview.md
2-requirements.md
3-api.md
4-application
5-retrospective.md
6-video.mp4
```

The current documentation has been written to map cleanly onto that structure.
