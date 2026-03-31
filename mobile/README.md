# LocaPin Mobile (Android)

Production-ready Jetpack Compose Android client for LocaPin end users.

## Stack
- Kotlin + Jetpack Compose + Material 3
- Hilt DI
- Retrofit + Kotlin serialization
- DataStore for onboarding/session/recent searches
- Google Maps Compose + Fused Location Provider
- Single-activity architecture with Navigation Compose

## Setup
1. Open `/workspace/Locapin/mobile` in Android Studio (latest stable).
2. Create `~/.gradle/gradle.properties` (or project `local.properties`) with:
   ```properties
   LOCAPIN_API_BASE_URL=https://<your-ktor-host>/
   MAPS_API_KEY=<your_google_maps_key>
   ```
   You can copy `local.properties.example` for a project-local template.
3. Ensure Android SDK path is configured (required):
   - via `ANDROID_HOME` / `ANDROID_SDK_ROOT`, or
   - via `mobile/local.properties`:
   ```properties
   sdk.dir=C\Users\<you>\AppData\Local\Android\Sdk
   ```
4. Ensure backend API endpoints align with `LocaPinApi` routes in:

   - `app/src/main/java/com/locapin/mobile/data/remote/LocaPinApi.kt`
5. Sync and run on Pixel 8 emulator.

## Backend route assumptions
The current client assumes:
- `POST /auth/login`
- `POST /auth/register`
- `POST /auth/forgot-password`
- `GET /profile/me`
- `GET /destinations`, `GET /destinations/{id}`
- `GET /categories`
- `GET /favorites`, `POST /favorites/{id}`, `POST /favorites/{id}/remove`

Adjust only `LocaPinApi` DTOs/routes if backend shape differs; the repository/domain/UI layers are isolated from DTO contracts.

## Temporary seed data to replace later
The following data is currently seeded behind repository boundaries and should be replaced when backend map endpoints are finalized:
- Map zones and polygon overlays for San Juan
- Seed attractions:
  - Pinaglabanan Shrine
  - San Juan City Hall
  - Santuario del Santo Cristo Parish
  - Museo ng Katipunan
  - Greenhills Shopping Center
  - Club Filipino

Source: `app/src/main/java/com/locapin/mobile/data/local/SanJuanSeedDataSource.kt`.

## High-level project structure
```
mobile/
├─ app/src/main/java/com/locapin/mobile/
│  ├─ app/                  # Application entry + Hilt setup
│  ├─ core/
│  │  ├─ common/            # Result + shared UI state wrappers
│  │  ├─ datastore/         # DataStore preferences/session/recent searches
│  │  ├─ designsystem/      # Theme/colors/typography
│  │  ├─ location/          # Fused location abstraction
│  │  ├─ navigation/        # Route definitions
│  │  └─ network/           # Retrofit + OkHttp + repository DI modules
│  ├─ data/
│  │  ├─ local/             # Seed data + in-memory cache
│  │  ├─ remote/            # Retrofit API + DTOs
│  │  └─ repository/        # Repository implementations + mappers
│  ├─ domain/
│  │  ├─ model/             # Domain entities
│  │  ├─ repository/        # Repository contracts
│  │  └─ usecase/           # Auth-focused use cases
│  ├─ feature/
│  │  ├─ auth/ home/ explore/ map/ destination/
│  │  ├─ favorites/ profile/ search/ settings/
│  └─ ui/                   # Root nav host + shared app state VM
└─ app/src/main/res/        # Icons, themes, strings, backup rules
```

## Branding assets
- App uses `ic_locapin_logo.xml` for launcher/splash/auth branding hook points.
- Raw provided brand asset copied as `res/raw/locapin_logo.svg` for future richer branding usage.


## Segmented Map integration note
- The segmented San Juan experience is wired to the existing bottom navigation **Map** entry (`Routes.Map`).
- Temporary area/attraction seeds are isolated in `data/local/SanJuanSeedDataSource.kt` and automatically replaced by backend data when `GET /map/areas` and `GET /map/attractions` are available.

## Troubleshooting Gradle zip errors
If Android Studio/Gradle fails during configuration with:

`java.util.zip.ZipException: zip END header not found`

one of the cached Gradle artifacts is usually truncated/corrupted. From the repository root, run:

```bash
./gradlew --stop
rm -rf ~/.gradle/caches
rm -rf ~/.gradle/wrapper/dists
cd mobile
./gradlew :app:help --refresh-dependencies
```

Then re-sync the Android project in Android Studio.
