import SwiftUI
import SwiftData

@main
struct TreasuryflowApp: App {
    @State private var container = DependencyContainer()

    var body: some Scene {
        WindowGroup {
            AppRouter()
                .environment(container)
        }
        .modelContainer(for: [CachedExample.self])
    }
}
