# Retrospective

Trainer Hub app succeeded in achieving its primary aim to create a dedicated trainer-oriented offline-only Android application with a clear flow and a possibility to test it without having to sign up in the app itself. It provides a comprehensive workflow with all necessary functionalities: a dashboard, clients and plan management, session tracking, payments tracking, and no log-in process involved.

Positive aspects:

- Scope was maintained at a realistic level – all major functionality is implemented and there are no critical bugs.
- Utilization of Room database with seeded data and possibility to reset it allows for consistent testing.
- Jetpack Compose proved to help in achieving consistent UI design.

Trade-offs:

- Payment and privacy features are simulated instead of integrating with respective online services.
- Plan creation is limited by a simplified flow without supporting multiple weeks.

If I had more time:

- Implement automated tests.
- Improve user experience with better input data validation and date/time pickers.
- Create a simple backend integration but leave the app offline-first as a basis.

In conclusion, the assignment tasks have been successfully completed.