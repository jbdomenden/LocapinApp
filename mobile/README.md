# LocaPin Mobile (Android)

Production Android app for LocaPin users (Jetpack Compose + Hilt + Retrofit + Maps Compose).

## 1) Setup
1. Open `mobile/` in Android Studio.
2. Copy `mobile/local.properties.example` to `mobile/local.properties`.
3. Set SDK path:
   ```properties
   sdk.dir=/path/to/Android/Sdk
   ```
4. Configure runtime properties:
   ```properties
   LOCAPIN_API_BASE_URL=https://your-backend.example.com/
   MAPS_API_KEY=YOUR_MAPS_API_KEY
   GOOGLE_SERVER_CLIENT_ID=YOUR_GOOGLE_SERVER_CLIENT_ID
   FACEBOOK_APP_ID=YOUR_FACEBOOK_APP_ID
   FACEBOOK_CLIENT_TOKEN=YOUR_FACEBOOK_CLIENT_TOKEN
   ```
5. Sync Gradle and run `:app` on a modern device/emulator (Pixel-class recommended).

## 2) API base URL configuration
- `LOCAPIN_API_BASE_URL` is read from Gradle property or environment variable.
- A trailing slash is automatically enforced in Gradle before wiring into `BuildConfig.API_BASE_URL`.

## 3) Social auth setup requirements
- **Google**: `GOOGLE_SERVER_CLIENT_ID` must be configured for ID token exchange.
- **Facebook**: `FACEBOOK_APP_ID` and `FACEBOOK_CLIENT_TOKEN` must be configured.
- Missing social config keeps app stable and returns user-facing error messages instead of fake success.

## 4) Location permission behavior
- App requests fine location for distance and in-app routing.
- If location is denied:
  - map browsing still works,
  - distance/routing actions show graceful guidance,
  - user can re-request permission from map UI.
- No external maps app handoff is used for the in-app Go flow.

## 5) Required backend endpoints
Auth:
- `POST /auth/login`
- `POST /auth/register`
- `POST /auth/social`
- `POST /auth/forgot-password`

User/data:
- `GET /profile/me`
- `GET /destinations`
- `GET /destinations/{id}`
- `GET /categories`
- `GET /favorites`
- `POST /favorites/{id}`
- `POST /favorites/{id}/remove`

Map:
- `GET /map/areas`
- `GET /map/attractions`
- `GET /map/route`

## 6) Backend-driven vs locally-isolated modules
- **Backend-driven**
  - auth/session exchange
  - destination/category/favorites data
  - map attractions
  - route points (when `GET /map/route` is available)
- **Locally isolated fallback**
  - San Juan map zone outlines can fallback to local seed if area endpoint is unavailable
  - visited attraction history is currently persisted locally (DataStore-backed repository) pending backend history endpoint support

## 7) Notes
- Keep mobile app connected to backend HTTP APIs only.
- Do not add DB credentials inside the Android app.
