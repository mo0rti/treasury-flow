# TreasuryFlow

A finance operations platform for payout approvals, settlements, and transaction oversight

## First-time setup

If this is a newly scaffolded project, initialize the product wiki before feature work:

- Claude Code: `/setup-project`
- Codex: `$setup-project`
- Cursor: ask the agent to "run setup-project"

See `CONTEXT.md` and `knowledge/wiki/SCHEMA.md` for the full cross-tool workflow.

## Product knowledge wiki

This project uses a shared product wiki at `knowledge/wiki/` as the single source of truth
for what to build. Before implementing any feature:

1. Read `knowledge/wiki/WIKI_REPORT.md` if it exists for a quick orientation summary.
2. If `knowledge/wiki/WIKI_REPORT.md` is absent, run `/feature-status` first to generate it.
3. Read `knowledge/wiki/index.md` and confirm the feature is in `ready-for-dev` or `in-dev`
   status and that `advisory-review` is not `pending`. If `advisory-review` is `pending`,
   stop and inform the human - a board review should happen before implementation.
4. Read `knowledge/wiki/features/[feature-id]-[slug].md` for full context.
5. Read `knowledge/wiki/platform-requirements/[feature-id]-[THIS_PLATFORM].md` for your
   platform-specific implementation requirements.
6. Read `knowledge/wiki/api-contracts/[feature-id].md` if this feature has an API surface.
7. Check `knowledge/wiki/business-rules/` for rules that apply to this feature.

`WIKI_REPORT.md` is only an orientation artifact. The underlying feature, design,
platform-requirement, and rule pages remain the source of truth.

**Do not implement features without a wiki page.** Ask the human to run `/po-intake [folder]`
first.

If you discover information during implementation that should update the wiki, propose the
update and ask for confirmation before writing, or route a question using
`/ask F-XXX "..." --to po`.

## Architecture

Generated workspace with the selected platform slices. API-first:
`shared/api-contracts/openapi.yml` drives typed clients and should stay aligned with
the implemented platform routes.

```text
backend/       -> Spring Boot 4 (Kotlin 2.2+, Java 21) - Azure Container Apps
web-user-app/  -> Next.js + TypeScript user-facing web app - Cloudflare via OpenNext
web-admin-portal/  -> Next.js + TypeScript admin web portal - Cloudflare via OpenNext
mobile-android/ -> Kotlin + Jetpack Compose (MVVM, Hilt, Room)
mobile-ios/     -> Swift 6 + SwiftUI (MVVM, SwiftData) - Mac/Xcode only
shared/        -> OpenAPI spec + design tokens
knowledge/     -> Living product wiki (single source of truth for features)
docs/          -> Human-readable reference documentation
infra/         -> Azure setup and deployment scripts
```

## Common Commands

| Task | Command |
|------|---------|
| Generate API clients | `task generate-clients` |
| Start backend | `task backend:run` |
| Backend tests | `task backend:test` |
| Start user web app | `task web-user-app:dev` |
| Start admin web portal | `task web-admin-portal:dev` |
| Build Android | `task mobile-android:build` |
| Android tests | `task mobile-android:test` |
| Build iOS (Mac) | `task mobile-ios:build` |
| iOS tests (Mac) | `task mobile-ios:test` |
| Run all tests | `task test` |

## Wiki commands (Claude Code slash commands)

### Orient / read-only

- `/feature-status` - pipeline view plus refresh of `knowledge/wiki/WIKI_REPORT.md`
- `/prep-sprint` - developer session view: what is ready to build, what is blocked, what is pending board review
- `/wiki-show F-XXX` - focused context bundle for one feature
- `/wiki-blockers` - blockers view using the canonical blocker categories
- `/wiki-query "text"` - retrieval-assisted wiki search across features, rules, design, and requirements
- `/wiki-owner po|designer|dev` - role dashboard for open questions, waiting work, and stale items
- `/wiki-platform <platform-id>` - platform queue for active features, requirement state, and blockers

Read `knowledge/wiki/WIKI_REPORT.md` first when present. If it is absent, run
`/feature-status` before deep reading.

### Lifecycle / write

- `/setup-project` - interactive project initialization (run once after scaffolding)
- `/po-intake [folder]` - process raw PO notes into feature specs
- `/po-clarify` - answer open questions assigned to PO
- `/po-handoff [F-XXX]` - move feature to ready-for-design
- `/design-intake [F-XXX] [folder]` - attach design artifacts to a feature
- `/design-clarify` - answer open questions assigned to Designer
- `/design-handoff [F-XXX]` - move feature to ready-for-dev
- `/dev-done [F-XXX]` - mark a feature as shipped

### Audit / review

- `/board-review [F-XXX]`
- `/lint-wiki`
- `/ask [F-XXX] "question" --to po|designer|dev`
- `/audit-feature [F-XXX]`

## Auth

- Username + Password
- Google OAuth
- Apple Sign-In


Backend issues JWT after successful auth. Clients store token and send as `Authorization: Bearer`.

## Key Documentation

- `knowledge/wiki/SCHEMA.md` - wiki conventions and operational rules
- `knowledge/wiki/index.md` - feature status board
- `knowledge/wiki/WIKI_REPORT.md` - generated orientation summary when present
- `knowledge/wiki/advisory/BOARD.md` - advisory board composition
- `docs/architecture.md` - system design, platform map, tech decisions
- `docs/api/conventions.md` - API naming, pagination, error format
- `docs/deployment/ci-cd.md` - CI/CD setup
- `shared/api-contracts/openapi.yml` - API contract (source of truth)
- `backend/docs/entities/` - entity definitions, fields, relationships
- `backend/docs/azure-setup.md` - Azure deployment
- `web-user-app/docs/guide.md` - user-facing web app structure, auth flow, deployment
- `web-admin-portal/docs/guide.md` - admin shell, credentials auth, operational workflows
- `docs/deployment/cloudflare-setup.md` - Cloudflare/OpenNext deployment


## Conventions

Workspace-level conventions list only the slices present in the generated workspace.

- **API-first**: Define endpoints in OpenAPI -> generate clients -> implement
- **Backend module structure**: `bootstrap/`, `shared/`, and `modules/<domain>/`
- **Security**: JWT-protected routes with explicit public auth endpoints
- **Backend testing**: JUnit Jupiter 6 + MockK for service logic, plus Spring integration coverage for security and request-path behavior
- **Mobile MVVM**: ViewModel + UiState/ViewState on the generated mobile platforms
- **Android DI**: Hilt-backed feature and network wiring
- **Android testing**: use the platform guide for JUnit, Compose/UI, and integration coverage
- **iOS DI**: protocol-based container with async/await flows
- **iOS testing**: use the platform guide for XCTest, async flows, and feature-level coverage
- **Web platform structure**: keep route, auth, and client-generation rules aligned with the selected web platform guides


Generated-workspace assumption:

- Open the **generated repository root** as the workspace when using Claude Code.
- The generated repo keeps shared Claude skills at `.claude/skills/` in the repo root.
- Open root-first even when you plan to work in only one platform folder.
- This generated workspace includes multiple platform folders. Read the root guidance first, then defer to the platform-local files for the slices you touch.

- Platform folders in the generated repo are subfolders of that workspace, not separate skill roots.

## Platform-Specific Context

Each platform has its own `CLAUDE.md` with detailed patterns:
- `backend/CLAUDE.md` - Spring Boot 4, security, JPA, Flyway
- `web-user-app/CLAUDE.md` - Next.js, next-intl, marketing + dashboard flows
- `web-admin-portal/CLAUDE.md` - Admin web portal, credentials auth, management tables
- `mobile-android/CLAUDE.md` - Compose, Hilt, Retrofit 3, Room, MVVM skills, mobile-android/docs/ references
- `mobile-ios/CLAUDE.md` - SwiftUI, async/await, Keychain, SwiftData, local iOS skills, mobile-ios/docs/ references


## Adding Features

1. Read `knowledge/wiki/WIKI_REPORT.md` if present, or run `/feature-status` first.
2. If no feature page exists, run `/po-intake [folder]` to create one from raw notes.
3. After design is complete, run `/design-handoff [F-XXX]` to generate platform-requirements.
4. Add endpoints to `shared/api-contracts/openapi.yml` and run `task generate-clients`.
5. Implement the selected platform slices per `knowledge/wiki/platform-requirements/`.
6. When shipped, run `/dev-done [F-XXX]`.
