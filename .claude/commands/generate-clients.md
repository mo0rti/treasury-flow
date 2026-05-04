Generate typed API clients from the OpenAPI specification.

## What This Does

Reads `shared/api-contracts/openapi.yml` and generates typed client code for each platform using openapi-generator.

## Steps

1. Validate the OpenAPI spec: `task validate-api`
2. Run client generation: `task generate-clients`
3. Verify generated clients compile:
   - `task backend:build`
   - `task web-user-app:build`
   - `task web-admin-portal:build`
   - `task mobile-android:build`
   - `task mobile-ios:build` (Mac only)

## Generated Output

- **TypeScript** (web-user-app + web-admin-portal): `web-user-app/src/lib/api/generated/`, `web-admin-portal/src/lib/api/generated/`
- **Kotlin** (Android): `mobile-android/app/src/main/kotlin/.../data/remote/generated/`
- **Swift** (iOS): `mobile-ios/treasuryflow/Data/Network/Generated/`

## When to Run

- After adding or modifying endpoints in `openapi.yml`
- After changing request/response schemas
- After running `/add-endpoint`
