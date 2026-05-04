# iOS Repo Orientation - TreasuryFlow

Use this doc when you need the fastest path through the generated iOS slice.

## Start Here

1. `project.yml` - XcodeGen source of truth
2. `Taskfile.yml` - generate, build, and test entry points
3. `treasuryflow/App.swift` - app entry
4. `treasuryflow/DI/DependencyContainer.swift` - dependency wiring
5. `treasuryflow/Navigation/AppRouter.swift` and `Navigation/Route.swift` - auth gate and routing
6. `treasuryflow/Data/Network/APIClient.swift` and `APIEndpoint.swift` - transport layer
7. `treasuryflow/Data/Repository/` - protocol plus implementation repository files
8. `treasuryflow/Data/Storage/TokenStorage.swift` - session persistence
9. `treasuryflow/UI/` - screens, shared UI, and theme

## If The Task Is About...

- Architecture or file placement: read [architecture.md](architecture.md) and [file-structure.md](file-structure.md)
- Build, XcodeGen, or runtime config: read [build-and-environments.md](build-and-environments.md)
- API contracts or auth flow: read [networking-and-auth.md](networking-and-auth.md) and [session-and-token-storage.md](session-and-token-storage.md)
- Routing or screen behavior: read [navigation-and-screens.md](navigation-and-screens.md)
- Shared styling or reusable UI: read [design-system-and-theme.md](design-system-and-theme.md)
- Naming or day-to-day coding rules: read [naming-conventions.md](naming-conventions.md) and [conventions-and-workflow.md](conventions-and-workflow.md)
