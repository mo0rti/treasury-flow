# TreasuryFlow

A finance operations platform for payout approvals, settlements, and transaction oversight

## Built With Prism

This workspace was generated with [Prism](https://github.com/mo0rti/prism), a
multi-platform workspace generator and workflow model for AI-assisted product
development.

If you want to understand how this project is structured, why the docs and AI
context files are laid out this way, or how to generate a similar workspace,
start with the Prism repository.

## Architecture

This generated workspace currently includes:

- **backend/** - Spring Boot 4 (Kotlin) REST API -> Azure Container Apps
- **web-user-app/** - Next.js user-facing web app (TypeScript) -> Cloudflare via OpenNext
- **web-admin-portal/** - Next.js admin web portal (TypeScript) -> Cloudflare via OpenNext
- **mobile-android/** - Android app (Kotlin/Jetpack Compose, MVVM)
- **mobile-ios/** - iOS app (Swift/SwiftUI, MVVM)
- **shared/** - OpenAPI contract and shared design tokens
- **knowledge/** - Product wiki and delivery workflow metadata
- **docs/** - Human-readable architecture and operations docs
- **infra/** - Azure deployment scripts


## First-Time Setup

This project includes a living product wiki at `knowledge/wiki/`. Before building
the first feature, initialize the wiki and advisory board:

- **Claude Code:** `/setup-project`
- **Codex:** `$setup-project`
- **Cursor:** ask the agent to "run setup-project"

This setup step interviews the team about domain risks, creates
`knowledge/wiki/advisory/BOARD.md`, and prepares the wiki for feature intake.

## Quick Start

```bash
# Review local configuration
# Backend and local infrastructure defaults are already generated in `.env`
# Android API config is already generated in `mobile-android/local.config.properties`
# iOS API config is already generated in `mobile-ios/Config/Debug.xcconfig`
# Update `mobile-ios/Config/Release.xcconfig` before production or TestFlight builds
cp web-user-app/.env.example web-user-app/.env.local
cp web-admin-portal/.env.example web-admin-portal/.env.local
# Edit the local config files as needed for your machine and credentials

# Start the local database first
docker compose up -d db

# Start the backend locally
# task backend:run sets SPRING_PROFILES_ACTIVE=local
task backend:run

# If you are running from IntelliJ IDEA, use the `local` profile
# and keep real OAuth credentials in local overrides or IDE env vars

# Start the user web app
task web-user-app:dev

# Start the admin web portal
task web-admin-portal:dev

# Generate clients after any OpenAPI contract change
# npm install -g @openapitools/openapi-generator-cli
task generate-clients
```

## Documentation

- [Product Context](CONTEXT.md) for AI entry points, command surfaces, and workflow expectations
- [Product Wiki Schema](knowledge/wiki/SCHEMA.md) for the structure and rules of the living product wiki
- [AI Agents](docs/ai-agents.md) for the command layer, skill map, and platform guidance structure
- [Architecture Overview](docs/architecture.md) for system boundaries, platform map, and auth flow
- [API Conventions](docs/api/conventions.md) for URL structure, versioning, auth headers, and error responses
- [Backend Guide](backend/docs/guide.md) for backend structure, conventions, commands, and local run guidance
- [Backend Entities](backend/docs/entities/README.md) for user and transaction data-model documentation
- [Azure Deployment](backend/docs/azure-setup.md) for backend infrastructure setup and deployment steps
- [User Web App Guide](web-user-app/docs/guide.md) for web-user-app structure, conventions, and commands
- [Admin Web Portal Guide](web-admin-portal/docs/guide.md) for internal web structure, conventions, and commands
- [Android Guide](mobile-android/docs/guide.md) for Android architecture, setup, and verification workflow
- [iOS Guide](mobile-ios/docs/guide.md) for iOS structure, build setup, and verification workflow
- [Cloudflare Deployment](docs/deployment/cloudflare-setup.md) for preview and production deployment setup for web slices
- [CI/CD](docs/deployment/ci-cd.md) for the generated GitHub Actions workflow layout and deployment pipeline

## Common Tasks

| Action | Command |
|--------|---------|
| Generate API clients | `task generate-clients` |
| Install user web app deps | `task web-user-app:install` |
| Install admin web portal deps | `task web-admin-portal:install` |
| Start backend | `task backend:run` |
| Start user web app | `task web-user-app:dev` |
| Start admin web portal | `task web-admin-portal:dev` |
| Run all tests | `task test` |
| Lint all platforms | `task lint` |
