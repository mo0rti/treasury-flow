package com.mortitech.treasuryflow.feature.auth.data.repository

import com.mortitech.treasuryflow.core.common.AppResult
import com.mortitech.treasuryflow.core.common.appResultOf
import com.mortitech.treasuryflow.core.datastore.TokenStorage
import com.mortitech.treasuryflow.core.model.User
import com.mortitech.treasuryflow.core.network.ApiService
import com.mortitech.treasuryflow.core.session.SessionManager
import com.mortitech.treasuryflow.feature.auth.data.remote.dto.*
import com.mortitech.treasuryflow.feature.auth.domain.AuthProvider
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenStorage: TokenStorage,
    private val sessionManager: SessionManager,
) {

    suspend fun oauthCallback(provider: AuthProvider, code: String, redirectUri: String): AppResult<User> =
        appResultOf {
            val response = apiService.oauthCallback(OAuthCallbackRequest(provider, code, redirectUri))
            tokenStorage.saveTokens(response.accessToken, response.refreshToken)
            response.user.toDomain()
        }


    suspend fun register(email: String, password: String, displayName: String): AppResult<User> =
        appResultOf {
            val response = apiService.register(RegisterRequest(email, password, displayName))
            tokenStorage.saveTokens(response.accessToken, response.refreshToken)
            response.user.toDomain()
        }

    suspend fun login(email: String, password: String): AppResult<User> =
        appResultOf {
            val response = apiService.login(LoginRequest(email, password))
            tokenStorage.saveTokens(response.accessToken, response.refreshToken)
            response.user.toDomain()
        }


    suspend fun refreshToken(): AppResult<User> =
        appResultOf {
            val refreshToken = tokenStorage.refreshToken.firstOrNull()
                ?: throw IllegalStateException("No refresh token available")
            val response = apiService.refreshToken(RefreshTokenRequest(refreshToken))
            tokenStorage.saveTokens(response.accessToken, response.refreshToken)
            response.user.toDomain()
        }

    suspend fun getCurrentUser(): AppResult<User> =
        appResultOf {
            apiService.getCurrentUser().toDomain()
        }

    suspend fun logout() {
        sessionManager.onLogout()
    }
}
