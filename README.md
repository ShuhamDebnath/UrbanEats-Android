# UrbanEats - Scalable Q-Commerce Android Application 🍔

![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![Clean Architecture](https://img.shields.io/badge/Clean%20Architecture-orange?style=for-the-badge)

## 📱 Project Overview
UrbanEats is a fully functional food delivery application built to demonstrate **industry-standard Android development practices**. Unlike typical tutorial apps, this project focuses on scalability, offline-first data persistence, and complex state management.

**Role:** Full Stack (Android + Node.js Backend)
**Development Time:** 30 Days (Intensive)

## 🛠 Tech Stack & Architecture
This project adheres to **Modern Android Development (MAD)** guidelines.

* **Architecture:** MVVM (Model-View-ViewModel) with Clean Architecture (Presentation, Domain, Data layers).
* **UI:** 100% Jetpack Compose (Material 3).
* **Dependency Injection:** Hilt.
* **Async Processing:** Coroutines & Kotlin Flow.
* **Networking:** Retrofit 2 + OkHttp3.
* **Local Database:** Room (Offline caching & Single Source of Truth).
* **Image Loading:** Coil.
* **Navigation:** Jetpack Navigation Compose (Type-safe navigation).
* **Backend:** Node.js, Express, MongoDB (Custom built API).

## 🚀 Key Features
* **Authentication:** JWT based secure login/signup.
* **Complex UI Composition:** Nested `LazyColumn` and `LazyRow` with performance optimizations.
* **Offline-First:** Users can view menus and their cart history without internet (Room Database).
* **Optimistic Updates:** UI updates instantly before server confirmation for a snappy UX.
* **Search:** Debounced search functionality utilizing `Flow` operators.
* **Background Tasks:** WorkManager for syncing orders in the background.

## 📸 Screenshots
*(Leave this blank for now, we will add these on Day 25)*
| Home Feed | Product Detail | Cart & Checkout | Dark Mode |
|:---:|:---:|:---:|:---:|
| ![Home](url) | ![Detail](url) | ![Cart](url) | ![Dark](url) |

## 🏗 Setup & Installation
1. Clone the repo: `git clone https://github.com/ShuhamDebnath/UrbanEats-Android.git`
2. Add your `local.properties` file with API keys.
3. Build and Run.

## 🤝 Contact
**Suham Debnath**
[LinkedIn Link] | [Email Link]
