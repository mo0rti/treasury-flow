---
name: security-auth
description: Backend security and authentication conventions for this Spring Boot project. Use when changing public versus authenticated routes, JWT handling, current-user access, OAuth or password-auth flows, or security-related configuration under `backend/`.
---

# Security & Authentication

Use this skill for backend auth exposure, JWT, and security-boundary decisions.

## Role Boundary

- Use this skill for public/private route decisions, JWT flow, auth configuration, and current-user access.
- Use `$spring-boot-conventions` for general controller and service structure.
- Use `$error-handling` for auth-related exception codes and API error responses.
- Use `$endpoint` when the task spans contract, DTO, controller, and persistence changes.

## Current Security Model

- Stateless JWT: `SessionCreationPolicy.STATELESS`
- Password auth uses `BCryptPasswordEncoder`
- Password inputs are bounded to BCrypt's 72-byte effective limit
- `JwtAuthenticationFilter` runs before `UsernamePasswordAuthenticationFilter`
- CORS allowed origins come from `app.cors.allowed-origins`
- Route access is defined with request matchers in `bootstrap/SecurityConfig.kt`
- Method-security annotations are not enabled by default in this template
- Refresh tokens carry a version claim and are rotated on every successful login, OAuth callback, and refresh
- Local email/password registration does not include a full email-verification workflow by default
- Auth endpoints are not rate-limited by a distributed/shared store in this scaffold

## Public Endpoints


- `/api/v1/auth/oauth/callback`

- `/api/v1/auth/register`
- `/api/v1/auth/login`
- `/api/v1/auth/refresh`
- `/actuator/health`
- `/swagger-ui.html`, `/swagger-ui/**`, `/v3/api-docs/**` only when SpringDoc is explicitly enabled

## Authenticated Endpoints

- `/api/v1/auth/me`
- `/api/v1/auth/logout`
- Every other backend route not explicitly permitted in `SecurityConfig`

## Rules

- Keep public matchers explicit; never widen them to `/api/v1/auth/**`.
- Update `SecurityConfig` whenever endpoint exposure changes.
- Use `@AuthenticationPrincipal` in controllers when possible.
- Reach for `SecurityContextHolder` only where controller injection is not practical.
- Keep secrets in environment-backed config, never hardcoded Kotlin values.
- Align route security with `shared/api-contracts/openapi.yml`.
- Access and refresh tokens must carry an explicit token-type claim, and each usage path must validate the expected type before accepting the token.
- Rotate refresh tokens on every successful refresh and reject reused refresh tokens.
- Do not silently link an OAuth sign-in to an existing account that shares only an email address; require an explicit account-linking flow instead.

## Auth-Specific Modeling

- Follow `$spring-boot-conventions` for DTO splitting, mapper placement, enum files, and general module layout.
- Keep normalized OAuth provider data as an internal model such as `OAuthUserProfile`, not as an API DTO and not as a helper class buried in `service/`.
- Keep provider-specific HTTP exchange and payload normalization out of controllers.

## Security Configuration

- Bind JWT, CORS, and OAuth provider settings with typed `@ConfigurationProperties`.
- Keep auth-related properties grouped under `bootstrap/properties/` instead of scattering repeated `@Value` keys.
- Validate that any newly added provider is represented consistently across config properties, OAuth service flow, and public route exposure.
- Do not wrap outbound OAuth HTTP calls in long-lived database transactions; fetch remote provider data first, then persist within a narrower transaction boundary if needed.

- For first startup after generation, a local profile may provide placeholder OAuth values so the backend can boot before real provider credentials are configured.
- The local profile may also provide a bootstrap-only JWT secret so first startup works without exporting `JWT_SECRET`.
- Treat placeholder OAuth values as a bootstrap convenience only; real OAuth flows still require real credentials supplied through IDE environment variables or local overrides.

- Remove global fallback secrets from shared config and require environment-backed secrets outside the local/test profiles.

## Key Files

- `backend/src/main/kotlin/com/mortitech/treasuryflow/bootstrap/SecurityConfig.kt`
- `backend/src/main/kotlin/com/mortitech/treasuryflow/bootstrap/properties/CorsProperties.kt`
- `backend/src/main/kotlin/com/mortitech/treasuryflow/bootstrap/properties/JwtProperties.kt`
- `backend/src/main/kotlin/com/mortitech/treasuryflow/bootstrap/properties/OAuthProperties.kt`
- `backend/src/main/kotlin/com/mortitech/treasuryflow/bootstrap/security/JwtAuthenticationFilter.kt`
- `backend/src/main/kotlin/com/mortitech/treasuryflow/bootstrap/security/JwtTokenProvider.kt`

- `backend/src/main/kotlin/com/mortitech/treasuryflow/bootstrap/OAuth2Config.kt`

- `backend/src/main/resources/application.yml`
- `shared/api-contracts/openapi.yml`

## Minimum Verification

- Re-check unauthenticated access to any route whose matcher changed.
- Re-check intended public auth endpoints after auth-surface changes.
- Verify that `/api/v1/auth/refresh` accepts only refresh tokens and that protected routes reject refresh tokens as bearer credentials.
- Verify that refresh-token reuse fails after a successful refresh or logout.
- Update the OpenAPI contract if endpoint security changed.
