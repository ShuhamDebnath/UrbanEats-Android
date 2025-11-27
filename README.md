UrbanEats - Premium Food Delivery App 🍔

UrbanEats is a fully functional, offline-first E-Commerce application built with modern Android standards. It features a Node.js backend, MongoDB database, and a reactive Jetpack Compose UI.

📱 Screenshots

Home Feed

Search & Explore

Cart & Bill

Order Tracking

<img src="screenshots/home.png" width="200"/>

<img src="screenshots/search.png" width="200"/>

<img src="screenshots/cart.png" width="200"/>

<img src="screenshots/track.png" width="200"/>

Profile & Settings

Login & Auth

Success Screen

No Internet

<img src="screenshots/profile.png" width="200"/>

<img src="screenshots/login.png" width="200"/>

<img src="screenshots/success.png" width="200"/>

<img src="screenshots/no_internet.png" width="200"/>

(Note: Upload your screenshots to a screenshots folder in your repo to make these visible)

🛠 Tech Stack

Architecture: Clean Architecture (Presentation, Domain, Data) with MVVM pattern.

UI: 100% Jetpack Compose (Material 3 Design System) with Custom Theming.

Dependency Injection: Koin (Lightweight, Kotlin-first).

Networking: Ktor Client (Multiplatform-ready).

Local Database: Room (SQLite) for offline caching and Single Source of Truth.

Async: Kotlin Coroutines & Flows.

Background Work: WorkManager (Order Notifications).

Image Loading: Coil.

Backend: Node.js, Express, MongoDB (Mongoose), Cloudinary (Image Storage).

CI/CD: GitHub Actions (Automated Unit Tests & Release Build).

🚀 Key Features

Offline-First Experience: Users can browse the menu and view their cart even without internet.

Dynamic Product Options: Products support server-driven sizes and add-ons (e.g., "Extra Cheese").

Real-time Search: Debounced search functionality that queries the backend efficiently.

Persistent Cart: Cart state is saved in the local database and survives app restarts.

Order Tracking: Simulated real-time tracking timeline with background notifications.

Secure Authentication: JWT-based auth with auto-login and secure session management (DataStore).

User Profile: Update name and profile picture (uploaded to Cloudinary).

Theme Engine: Supports Light, Dark, and System Default modes with immediate UI updates.

Address Management: Full CRUD for delivery addresses, synced with the server.

Robust Error Handling: Dedicated screens for failures and connectivity issues.

🏗 Architecture

The app follows the Single Source of Truth (SSOT) principle. The UI observes the Database, and the Repository syncs the Database with the Network.

graph TD
UI[Compose UI] -->|Observes| VM[ViewModel]
VM -->|Collects| UC[Use Cases]
UC -->|Calls| Repo[Repository]
Repo -->|Reads/Writes| DB[(Room Database)]
Repo -->|Fetches| API[Ktor Client]


🔧 Setup & Installation

Prerequisites:

Android Studio Koala/Ladybug or newer.

Node.js (for local backend).

MongoDB Atlas Account.

1. Backend Setup:

cd backend
npm install
# Create a .env file with DB_CONNECT, TOKEN_SECRET, and CLOUDINARY keys
node server.js


2. Android Setup:

Clone the repository:

git clone [https://github.com/ShuhamDebnath/UrbanEats-Android.git](https://github.com/ShuhamDebnath/UrbanEats-Android.git)


Open the project in Android Studio.

Wait for Gradle Sync to finish.

Run on an Emulator or Physical Device.

🧪 Testing

The project includes Unit Tests for ViewModels and UseCases, and Instrumentation Tests for Room Database.

./gradlew testDebugUnitTest


📄 License

This project is for educational purposes and portfolio demonstration.