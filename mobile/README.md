# LocaPin Mobile (Android)

This repository is **mobile-only**.

- There is **no admin website** anymore.
- The product is one **Android app** used by both **Tourist** and **Admin** users.
- There is one **shared login** for all users.
- The **Admin path is currently mock** while backend work continues.
- **Mock sample accounts will be added in later phases.**

## Setup
1. Open `mobile/` in Android Studio.
2. Copy `mobile/local.properties.example` to `mobile/local.properties`.
3. Set SDK path:
   ```properties
   sdk.dir=/path/to/Android/Sdk
   ```
4. Sync Gradle and run `:app`.
