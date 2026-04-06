# LocaPin Mobile (Android) — Mock Auth Mode

LocaPin is now a **mobile-only** system. There is no separate admin website in scope.

## Current temporary auth mode
Admin and tourist login currently run on a local fake auth layer so development can continue while backend admin auth is finalized.

### Sample accounts

**Admin**
- Email: `admin@locapin.app`
- Password: `Admin123!`

**Tourist**
- Email: `tourist@locapin.app`
- Password: `Tourist123!`

Optional extras:
- `admin2@locapin.app` / `Admin123!`
- `tourist2@locapin.app` / `Tourist123!`

## Behavior
- Single login screen for all users.
- On success, role-aware routing sends:
  - `ADMIN` → admin dashboard
  - `TOURIST` → tourist dashboard
- Session is persisted locally (DataStore) with:
  - `isLoggedIn`, `userId`, `name`, `email`, `role`, `session token placeholder`
- Logout clears session and returns to login.

## Architecture notes
- UI depends on `AuthRepository` abstraction.
- Current implementation uses `FakeAuthRepository` + `MockAuthDataSource`.
- This structure is intentionally ready to swap with real backend auth later with minimal refactor.

## Setup
1. Open `mobile/` in Android Studio.
2. Copy `mobile/local.properties.example` to `mobile/local.properties`.
3. Set SDK path:
   ```properties
   sdk.dir=/path/to/Android/Sdk
   ```
4. Sync Gradle and run `:app`.
