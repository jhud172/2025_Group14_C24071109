# Description of Android API

The Trainer Hub app makes use of several different APIs to deliver a completely offline-capable application.

Jetpack Compose is applied for the creation of the full application user interface including the dashboard, client list, client details tabs, and all of the forms. The reason behind the selection of Jetpack Compose is the possibility to compose UI with reusable components, ensuring consistency and screen updates during development.

Navigation Compose is used to implement navigation throughout the screens of the dashboard, clients, client details, and form pages.

ViewModel is employed for maintaining UI state data in a lifecycle-aware manner since the app requires stability of data in case of rotation or screen switching. It is supplemented by StateFlow which provides a reactive mechanism to update UI according to the updated data such as client, plan, or invoice creation.

Room is responsible for local storage. Room stores everything from basic data like clients, sessions, plans, and payments. Room was chosen as it offers structured storage and makes sure that the application operates entirely offline, which is crucial for ensuring the accuracy of the assessment process.

DataStore is responsible for storing basic settings like reminders and sorting preferences for clients. DataStore is easy to use and can be used for storing small bits of information that don’t require a complete database.

WorkManager is responsible for managing background operations, like sending reminders about upcoming sessions and overdue payments. This makes sure that the application can perform background tasks without any external services.

In summary, these APIs were chosen as they allow creating a reliable, offline-first application that can be easily expanded in the future.