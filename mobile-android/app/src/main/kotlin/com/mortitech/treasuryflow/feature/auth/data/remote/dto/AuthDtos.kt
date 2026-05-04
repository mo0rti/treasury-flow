package com.mortitech.treasuryflow.feature.auth.data.remote.dto

import com.mortitech.treasuryflow.core.model.User
import com.mortitech.treasuryflow.feature.auth.domain.AuthProvider
import kotlinx.serialization.Serializable

@Serializable
data class OAuthCallbackRequest(
    val provider: AuthProvider,
    val code: String,
    val redirectUri: String,
)


@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val displayName: String,
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)


@Serializable
data class RefreshTokenRequest(
    val refreshToken: String,
)

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val user: UserResponse,
)

@Serializable
data class UserResponse(
    val id: String,
    val email: String,
    val displayName: String,
    val avatarUrl: String? = null,
    val authProvider: String,
    val role: String,
) {
    fun toDomain(): User = User(
        id = id,
        email = email,
        displayName = displayName,
        avatarUrl = avatarUrl,
        authProvider = authProvider,
        role = role,
    )
}
