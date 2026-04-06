# LocaPin Mobile (Android)

## 1. Project overview
LocaPin is a **mobile-only** repository.

- There is **no admin website** anymore.
- The product is one **Android app** for both **Tourist** and **Admin** users.
- Users sign in through one **shared login** entry point.
- After login, the app applies **role-based routing** to the Admin or Tourist flow.

## 2. Current architecture
At the moment, authentication and routing are organized as follows:

- **Shared auth entry:** one login flow is used for all roles.
- **Mock auth for now:** sign-in for current role testing is mock-enabled.
- **Role-based route:** once signed in, users are routed based on role.
  - **ADMIN** users go to the admin path (currently mock).
  - **TOURIST** users go to tourist modules.

## 3. Mock test accounts
Use these exact sample accounts for local testing:

### Admin
- Email: [admin@locapin.app](mailto:admin@locapin.app)
- Password: `Admin123!`

### Tourist
- Email: [tourist@locapin.app](mailto:tourist@locapin.app)
- Password: `Tourist123!`

## 4. Local configuration
`local.properties` is required for local runs and is **not committed** to git.

1. Copy `mobile/local.properties.example` to `mobile/local.properties`.
2. Set values for these keys:
   - `sdk.dir`
   - `LOCAPIN_API_BASE_URL`
   - `MAPS_API_KEY`
   - `GOOGLE_SERVER_CLIENT_ID`
   - `FACEBOOK_APP_ID`
   - `FACEBOOK_CLIENT_TOKEN`

## 5. How to run
1. Open the `mobile/` project in Android Studio.
2. Ensure `mobile/local.properties` exists and is populated.
3. Sync Gradle.
4. Run the `app` module on an emulator or Android device.

## 6. Current implementation status
- **Mock now:** login/auth behavior for role testing and the Admin dashboard flow are currently mock.
- **Backend-driven where available:** Tourist modules may still include backend-driven flows where already implemented.
