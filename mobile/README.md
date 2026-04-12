# LocaPin Mobile (Android)

## 1. Project Overview
LocaPin is a comprehensive tourism application for San Juan City, Metro Manila. It serves as a unified platform for both tourists and administrators, offering an interactive map-based exploration experience.

- **Unified Platform**: A single Android application for all user roles.
- **Interactive Map**: Explore San Juan City through custom-rendered map sectors.
- **Role-Based Experience**: Tailored dashboards for Tourists and Administrators.
- **Modern UI/UX**: Built with Jetpack Compose, featuring branded elements and high-fidelity social login integration.

## 2. Core Features
### For Tourists
- **Interactive City Map**: Tap on sectors to discover local attractions.
- **Attraction Discovery**: Browse curated lists of historical sites, shopping centers, and more.
- **Distance Estimation**: View distances from key landmarks (e.g., STI Sta. Mesa).
- **Favorites**: Save your favorite spots for quick access.
- **Ad-Free Option**: One-time premium upgrade to remove all advertisements.

### For Administrators
- **Map Management**: Define and edit map sectors and coordinates.
- **Attraction Management**: Add, update, or remove tourism content.
- **Real-time Updates**: Changes reflect instantly for all users.

## 3. Architecture & Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Asynchronous Programming**: Coroutines & Flow
- **Dependency Injection**: Hilt
- **Navigation**: Jetpack Compose Navigation
- **Image Loading**: Coil
- **Backend**: Firebase (Auth & Firestore) / Mock implementation for rapid prototyping.

## 4. Getting Started
### Local Configuration
`local.properties` is required for local runs and is **not committed** to git.

1. Copy `local.properties.example` to `local.properties`.
2. Set values for the following keys:
   - `sdk.dir`: Path to your Android SDK.
   - `LOCAPIN_API_BASE_URL`: API endpoint.
   - `MAPS_API_KEY`: Google Maps API Key.
   - `GOOGLE_SERVER_CLIENT_ID`: Firebase Google Auth Client ID.
   - `FACEBOOK_APP_ID` & `FACEBOOK_CLIENT_TOKEN`: Facebook Login credentials.

### Installation
1. Open the `mobile/` project in Android Studio.
2. Sync Gradle to download dependencies.
3. Ensure a `google-services.json` file is present in the `app/` directory.
4. Run the `app` module on an emulator or physical device.

## 5. Mock Test Accounts
Use these credentials for testing specific role flows:

### Admin
- **Email**: `admin@locapin.app`
- **Password**: `Admin123!`

### Tourist
- **Email**: `tourist@locapin.app`
- **Password**: `Tourist123!`

## 6. Project Status
- [x] Refined Auth UI with Google & Facebook integration.
- [x] Mandatory EULA & Terms agreement flow.
- [x] Interactive San Juan City Map component.
- [x] Tourist Dashboard & Attraction Details.
- [ ] Real-time Navigation (In Progress).
- [ ] Social Media Sharing (Planned).
