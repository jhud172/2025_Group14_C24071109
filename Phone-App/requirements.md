# Trainer Hub Requirements

## Functional Requirements

### Dashboard

- The application should provide a trainer dashboard with the total number of active clients, sessions booked on that day, overdue bills, and any critical notifications.

### Trainer Profile

- The application should indicate that the user is currently the active trainer and display as verified.
- The application should show that the trainer is affiliated with some gyms but can also work independently.

### Client Management

- The application should list down all the clients associated with the trainer.
- The application should have search and sorting features for clients.
- The application should provide an option for the trainer to create a new client account with validation of mandatory fields.
- The application should automatically assign the newly created client account to the active trainer.
- The application should ensure that a single client cannot be under multiple trainers simultaneously.
- The application should maintain the history of the clients' previous trainers.

### Client Detail

- The app should provide a client detail screen that includes tabs for overview, plans, sessions, payments, notes, and privacy.
- The app should show client information such as status, goals, contacts, and trainer allocation.

### Training Plans

- The app should enable trainers to create a plan for clients using a simple form.
- The app should generate a training plan that consists of the first week and at least one exercise.
- The app should enable trainers to have only one training plan per client and archive previous plans.
- The app should display an empty state for the plan when there is no plan.

### Sessions

- The app should enable trainers to schedule sessions and include date, time, location, and session type.
- The app should enable trainers to mark sessions as scheduled, done, or cancelled.
- The app should keep notes on sessions.

### Payments

- The app shall facilitate the creation of invoices by the trainer for the client.
- The app shall display the status of the invoice (draft, payable, late, paid).
- The app shall allow payments to be entered into the app.
- The app shall record invoices and payments with reference to the client.

### Notes and Privacy

- The app shall record notes on the client and individual training sessions.
- The app shall record any consent from the clients.
- The app shall facilitate the ability to export and delete data to comply with GDPR.

### Preferences and Reliability

- The app shall enable resetting of demo data to default seed values.
- The app shall record any user preferences like reminders and sorting of clients.
- The app shall schedule local reminders for training sessions and late invoices when enabled.

## Non-functional requirements

### Usability

- The app should provide an elegant and professional user interface appropriate for a coaching application.
- The app should be consistently laid out and utilize reusable widgets.
- The app should display important actions prominently, especially those related to forms.
- The app should not force authentication for access to basic functionality.

### Reliability

- The app should work offline using local data.
- The app should function without any issues on a fresh install.
- The app should restore its state after being closed and reopened.
- The app should handle empty states without disrupting the navigation.

### Maintainability

- The app should follow the MVVM design pattern by separating concerns like data access, logic, and view layer.
- The app should implement repository pattern in order to support future integration with backend.
- The app should avoid usage of outdated Android SDK.

### Privacy and compliance

- The app should make privacy actions easily accessible from the client's perspective.
- The app should model data collection and consent actions appropriately.
- The app should minimize data collection from users.

### Performance & scope

- The app should prioritize responsiveness and navigational aspects of its usage.
- The app should target only coaching-related features avoiding other aspects like news feeds or dietary advice.