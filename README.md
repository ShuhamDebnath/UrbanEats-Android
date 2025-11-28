# UrbanEats - Scalable Q-Commerce Android Application 🍔

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Node.js](https://img.shields.io/badge/Node.js-43853D?style=for-the-badge&logo=node.js&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)

## 📱 Project Overview

**UrbanEats** is a fully functional, offline-first food delivery application built to demonstrate industry-standard Android development practices.  
Unlike typical tutorial apps, this project focuses on scalability, offline-first data persistence, and complex state management using the **MVI (Model-View-Intent)** pattern.

- **Role:** Full Stack (Android + Node.js Backend)
- **Development Time:** 30 Days (Intensive)
- **Goal:** Production-ready architecture with robust error handling and automated testing.

---


## 📸 Screenshots

| Home Feed | Search & Explore | Cart & Bill | Order Tracking |
| :---: | :---: | :---: | :---: |
| <img src="screenshots/home.png" width="200"/> | <img src="screenshots/search.png" width="200"/> | <img src="screenshots/cart.png" width="200"/> | <img src="screenshots/track.png" width="200"/> |

| Profile & Settings | Login & Auth | Success Screen | No Internet |
| :---: | :---: | :---: | :---: |
| <img src="screenshots/profile.png" width="200"/> | <img src="screenshots/login.png" width="200"/> | <img src="screenshots/success.png" width="200"/> | <img src="screenshots/no_internet.png" width="200"/> |

---

## 🛠 Tech Stack & Architecture

This project adheres to **Modern Android Development (MAD)** guidelines.

### 🤖 Android Client
* **Architecture:** Clean Architecture (Presentation, Domain, Data layers) with MVVM/MVI pattern.
* **UI:** 100% Jetpack Compose (Material 3 Design System) with custom theming.
* **Dependency Injection:** Koin (Lightweight, Kotlin-first framework).
* **Networking:** Ktor Client (Multiplatform-ready engine with content negotiation).
* **Local Database:** Room (SQLite) for offline caching and Single Source of Truth (SSOT).
* **Async Processing:** Kotlin Coroutines & Flows.
* **Background Work:** WorkManager (Simulated Order Notifications).
* **Image Loading:** Coil.

### ⚙️ Backend & DevOps
* **Backend:** Node.js, Express.js.
* **Database:** MongoDB Atlas (Mongoose ODM).
* **Media Storage:** Cloudinary (for Profile Image uploads).
* **CI/CD:** GitHub Actions (Automated Unit Tests & Release APK generation).

---

## 🚀 Key Features

* **Offline-First Experience:** Users can browse the menu and view their cart even without internet. The app syncs when connectivity returns.
* **Dynamic Product Options:** Products support server-driven sizes and add-ons (e.g., "Extra Cheese"), configured via the backend.
* **Real-time Search:** Optimized search with Debouncing to reduce API calls and server load.
* **Persistent Cart:** Cart state is saved locally in Room database and survives app kills/restarts.
* **Order Tracking:** Simulated real-time tracking timeline with background notifications using WorkManager.
* **Secure Authentication:** JWT-based auth with auto-login and secure session management (DataStore).
* **User Profile:** Full CRUD for user profile, including image upload to Cloudinary.
* **Theme Engine:** Supports Light, Dark, and System Default modes with immediate UI updates.
* **Address Management:** Multi-address support synced with the server.
* **Robust Error Handling:** Dedicated screens for API failures and connectivity issues.

---

## 🏗 Architecture Diagram

The app follows the **Single Source of Truth (SSOT)** principle. The UI observes the Database, and the Repository syncs the Database with the Network.

```mermaid
graph TD
    UI[Compose UI] -->|Observes State| VM[ViewModel]
    VM -->|Executes| UC[Use Cases]
    UC -->|Calls| Repo[Repository]
    Repo -->|Reads/Writes| DB[(Room Database)]
    Repo -->|Fetches| API[Ktor Client]
    API -->|JSON| Backend[Node.js Server]
````

-----

## 🔧 Setup & Installation

### Prerequisites

* Android Studio Koala/Ladybug or newer.
* Node.js (for local backend).
* MongoDB Atlas Account.

### 1\. Backend Setup

```bash
cd backend
npm install
# Create a .env file with DB_CONNECT, TOKEN_SECRET, and CLOUDINARY keys
node server.js
````

### 2\. Android Setup

1.  Clone the repository:
    ```bash
    git clone [https://github.com/ShuhamDebnath/UrbanEats-Android.git](https://github.com/ShuhamDebnath/UrbanEats-Android.git)
    ```
2.  Open the project in Android Studio.
3.  Wait for Gradle Sync to finish.
4.  Run on an Emulator or Physical Device.

-----

## 🧪 Testing

The project includes comprehensive tests to ensure reliability.

* **Unit Tests:** For ViewModels and UseCases (Logic verification).
* **Instrumented Tests:** For Room Database and basic UI flows.

Run tests via terminal:

```bash
./gradlew testDebugUnitTest
```

-----

## 🤝 Contact

**Suham Debnath**

[LinkedIn Profile](https://www.linkedin.com/in/shuham-debnath-74970a292/) | [Email Me](mailto:shuhamdebnath55@gmail.com)

-----

## 📄 License

This project is for educational purposes and portfolio demonstration.
"""