# System Overview

## Purpose

This file is the stable product and platform overview for the One To One repository.

Use this document when you need the broad answer to:

- what the platform is
- which user roles exist
- which feature areas are active in the codebase
- how the application is structured at a technical level

If you need the current development-only behavior, use [System-Overview-Dev-Mode.md](./System-Overview-Dev-Mode.md).

## Platform Summary

One To One is a Spring Boot coaching platform built around trainer-client collaboration. The codebase supports public marketing pages, signup and verification, role-based dashboards, planning and calendar flows, workouts, health and nutrition tracking, goals and check-ins, messaging, profile customization, trainer and gym operations, payments, merch, and admin oversight.

At the time of this documentation refresh, the repository contains:

- `69` controller classes
- `172` HTML/Thymeleaf templates
- `78` JavaScript files under `src/main/resources/static/js`
- `71` CSS files under `src/main/resources/static/css`
- `116` Java test files under `src/test/java`

## Roles

### Guest

Guests can access the public landing and information pages, browse public discovery surfaces, view pricing, and start signup flows.

### Client

Clients are the main end users of the product. Their core surfaces include:

- dashboard
- calendar and scheduling
- goals and progress tracking
- workouts and workout sessions
- health, nutrition, and daily logging
- inbox and chat
- profile, preferences, milestones, and level progress

### Trainer

Trainers can manage their coaching presence and work with clients through:

- trainer dashboard
- trainer profile
- trainer library and templates
- client assignments and plan sharing
- check-in review flows
- messaging and client relationship pages

### Gym Admin

Gym admins handle operational and business-facing areas such as:

- gym dashboard
- trainer onboarding and verification support
- membership management
- gym profile and operational settings

### Platform Admin and Super Admin

Admin-facing areas cover broader platform oversight, including:

- admin dashboard
- feedback and moderation-related surfaces
- development page controls
- super-admin-only routes

## Main Feature Areas

### Public and Marketing

The public site includes the landing experience and supporting pages such as:

- `/`
- `/about`
- `/faq`
- `/pricing`
- `/explore`
- policy pages under `/policies/**`

### Authentication and Verification

The application includes:

- login
- role-based signup
- forgot/reset password
- email verification
- phone verification

Security is handled through Spring Security with method security enabled.

### Dashboards and Role Home Surfaces

The codebase includes dedicated dashboards for:

- client
- trainer
- gym admin
- platform/admin

The root route redirects signed-in users to `/dashboard`.

### Planning, Calendar, and Scheduling

Planning is a major part of the repository and includes:

- day, week, and month calendar views
- task detail and day planning flows
- schedule creation and application flows
- workout-linked scheduling

### Workouts and Training

Training-related modules include:

- workout lists and sessions
- workout templates
- trainer-assigned plans
- workout feedback and logging
- exercise logging

### Health, Nutrition, and Daily Tracking

The repository contains active modules for:

- health records
- nutrition logging
- day health and day mode
- strength and exercise logging
- conditions and preference-driven defaults

### Goals, Check-Ins, and Progress

Progress-related areas include:

- goals
- check-ins
- achievements
- levels
- milestones surfaced through profile previews

### Messaging and AI

Communication surfaces include:

- inbox and message flows
- chat / AI coach surfaces
- notifications

AI behavior is environment-controlled and uses the configured model and API key from application properties.

### Profile and Personalization

Profile and preference work is a first-class feature area. The repository supports:

- editable user profile details
- avatar and image upload
- bio
- profile theme customization
- visible milestone selection
- weather and time preferences
- accessibility and equipment defaults
- quick-preferences onboarding plus full settings management

### Payments, Billing, and Merch

Commerce and billing surfaces include:

- pricing and checkout
- platform billing
- merch shop and merch checkout
- payment-provider configuration through environment variables

## Technical Architecture

### Backend

- Java 21
- Spring Boot 3.5.7
- Spring MVC
- Spring Security
- Spring Data JPA / Hibernate
- scheduled jobs enabled through `@EnableScheduling`

### Frontend

- Thymeleaf server-rendered templates
- shared fragments
- external JavaScript modules
- Tailwind/PostCSS CSS pipeline
- compiled stylesheet at `src/main/resources/static/css/app.css`

### Data

The application uses different runtime profiles:

- `local`: in-memory H2 database, schema plus seeded demo data, default port `8081`
- `render`: PostgreSQL, versioned Flyway migrations and no automatic demo-data
  seed, default port `8080`

The application boot path also normalises Render/PostgreSQL URLs in
[`src/main/java/uk/ac/cf/_5/group14/One_To_One/OneToOneApplication.java`](../src/main/java/uk/ac/cf/_5/group14/One_To_One/OneToOneApplication.java).
`APP_DATABASE_SCHEMA` can bind a deployment to a dedicated PostgreSQL schema;
the same value is applied to JDBC, Flyway and Hibernate.

## Configuration Model

### Shared Configuration

Common configuration lives in:

- [`src/main/resources/application.properties`](../src/main/resources/application.properties)
- [`src/main/resources/application-local.properties`](../src/main/resources/application-local.properties)
- [`src/main/resources/application-render.properties`](../src/main/resources/application-render.properties)

### Important Runtime Flags

The main repository-level flags and integrations include:

- `DEV_MODE`
- `OPENAI_API_KEY`
- `APP_AI_ENABLED`
- `APP_AI_MODEL`
- `DATABASE_URL`
- `DATABASE_USER`
- `DATABASE_PASSWORD`
- `APP_DATABASE_SCHEMA`
- `STRIPE_SECRET_KEY`
- `STRIPE_WEBHOOK_SECRET`
- `APP_BASE_URL`
- `SPRING_MAIL_*`
- `APP_SMS_PROVIDER` and `TWILIO_*`

## Seed Data and Demo Accounts

Local development seeds run from `classpath:data/*.sql`.

The demo auth seed lives in [`src/main/resources/data/00-auth-demo.sql`](../src/main/resources/data/00-auth-demo.sql) and includes demo accounts for:

- client
- trainer
- gym admin
- platform admin

## Repository Layout

Key paths:

- application entry point: [`src/main/java/uk/ac/cf/_5/group14/One_To_One/OneToOneApplication.java`](../src/main/java/uk/ac/cf/_5/group14/One_To_One/OneToOneApplication.java)
- Java source: [`src/main/java/uk/ac/cf/_5/group14/One_To_One`](../src/main/java/uk/ac/cf/_5/group14/One_To_One)
- templates: [`src/main/resources/templates`](../src/main/resources/templates)
- static assets: [`src/main/resources/static`](../src/main/resources/static)
- schema and seed data: [`src/main/resources`](../src/main/resources)
- tests: [`src/test/java`](../src/test/java)
- deployment container: [`Dockerfile`](../Dockerfile)

## Documentation Rule

Keep this file stable and broad.

When behavior changes in a way that is specific to active testing, development gates, or temporary branch state, update [System-Overview-Dev-Mode.md](./System-Overview-Dev-Mode.md) first. Move changes into this file only when they represent the normal baseline of the repository.
