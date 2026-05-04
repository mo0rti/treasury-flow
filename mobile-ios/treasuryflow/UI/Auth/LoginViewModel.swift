import Foundation
import Observation

enum LoginViewState: Equatable {
    case idle
    case loading
    case success
    case error(String)
}

@MainActor
@Observable
final class LoginViewModel {
    var viewState: LoginViewState = .idle

    var email: String = ""
    var password: String = ""


    private let authRepository: AuthRepository

    init(authRepository: AuthRepository) {
        self.authRepository = authRepository
    }


    var isFormValid: Bool {
        !email.isEmpty && !password.isEmpty
    }

    func loginWithPassword() async {
        viewState = .loading
        do {
            _ = try await authRepository.login(email: email, password: password)
            viewState = .success
        } catch {
            viewState = .error(error.localizedDescription)
        }
    }

    func register(displayName: String) async {
        viewState = .loading
        do {
            _ = try await authRepository.register(email: email, password: password, displayName: displayName)
            viewState = .success
        } catch {
            viewState = .error(error.localizedDescription)
        }
    }


    func loginWithOAuth(provider: String, code: String, redirectUri: String) async {
        viewState = .loading
        do {
            _ = try await authRepository.oauthCallback(provider: provider, code: code, redirectUri: redirectUri)
            viewState = .success
        } catch {
            viewState = .error(error.localizedDescription)
        }
    }

    func showOAuthSetupRequired(provider: String) {
        let displayName = provider.capitalized
        viewState = .error("\(displayName) sign-in needs a real native OAuth handoff before this button can be used.")
    }

    func clearError() {
        viewState = .idle
    }
}
