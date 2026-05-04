# Architecture Overview - TreasuryFlow

## System Description

A finance operations platform for payout approvals, settlements, and transaction oversight

## Platform Map

| Platform | Tech Stack | Hosting | Purpose |
|----------|-----------|---------|---------|
| Backend | Spring Boot 4, Kotlin 2.2+, Java 21 | Azure Container Apps | REST API, business logic, auth |
| User Web App | Next.js 14.2, React 18, TypeScript 5.6, Tailwind CSS 3, NextAuth 5, next-intl 4 | Cloudflare via OpenNext | User-facing marketing and authenticated dashboard |
| Admin Web Portal | Next.js 14.2, React 18, TypeScript 5.6, Tailwind CSS 3, NextAuth 5 | Cloudflare via OpenNext | Internal admin operations and management UI |
| Android | Kotlin 2.2+, Java 21, Jetpack Compose, Hilt, Retrofit 3 | Google Play | Native Android app |
| iOS | Swift 6, SwiftUI, iOS 17+, XcodeGen | App Store | Native iOS app |
| Database | PostgreSQL 16 | Azure Database | Data persistence |

## Architecture Pattern


### Mobile: MVVM (Model-View-ViewModel)

Both Android and iOS follow the same MVVM architecture:

```
View (UI)  ->  ViewModel (State + Logic)  ->  Repository  ->  Data Source (API/Local)
```

| Layer | Android | iOS |
|-------|---------|-----|
| View | `@Composable` functions | SwiftUI `View` structs |
| ViewModel | `@HiltViewModel` + `StateFlow<UiState>` | `@Observable` + `ViewState` enum |
| Repository | Concrete repository classes (`@Inject` + Hilt) | Protocol + implementation (`DependencyContainer`) |
| API Client | Retrofit 3 + OkHttp + Kotlinx Serialization | `APIClient` actor over URLSession + `async/await` |
| Local Storage | DataStore | Keychain |
| DI | Hilt | `DependencyContainer` via SwiftUI environment |


### API Contract

Single source of truth: [shared/api-contracts/openapi.yml](../shared/api-contracts/openapi.yml)

All platform API clients are **generated** from this spec. Never manually edit generated client code. Run `task generate-clients` after any spec change.

### Authentication

Local email/password auth is always included. Selected OAuth providers: google, apple.

Flow:

1. Client submits login credentials or completes the OAuth provider callback
2. Backend validates credentials or exchanges the OAuth authorization code
3. Backend issues JWT access and refresh tokens
4. Client stores the tokens locally
5. Client attaches the access token to authenticated API requests

### Design Tokens

Shared visual language: [shared/design-tokens/tokens.json](../shared/design-tokens/tokens.json)

Colors, spacing, typography, and border-radius values consumed by all platforms.

## Key Conventions

1. **API contract first** - define endpoints in OpenAPI before implementing
2. **Documentation first** - create feature/entity docs before coding
3. **Feature modules** - backend domains live under `modules/<domain>/`
4. **Consistent naming** - docs, contract, and code should refer to the same feature names (for example, `transactions`)
5. **Never edit generated code** - modify the OpenAPI spec and regenerate

## Related Docs

- [API Conventions](api/conventions.md) for path versioning, auth headers, and error payloads
- [Backend Guide](../backend/docs/guide.md) for backend package layout and coding conventions
- [Product Context](../CONTEXT.md) for command surfaces and workflow expectations
