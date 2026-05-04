package com.mortitech.treasuryflow.feature.auth

import com.mortitech.treasuryflow.feature.auth.domain.AuthProvider

enum class AuthExperienceMode {
    EMAIL_PASSWORD_ONLY,
    SOCIAL_ONLY,
    MIXED,
}

object AuthConfig {
    val isEmailPasswordEnabled: Boolean = true

    val enabledSocialProviders: List<AuthProvider> = listOf(

        AuthProvider.GOOGLE,


        AuthProvider.APPLE,



    )

    val authExperienceMode: AuthExperienceMode
        get() = when {
            isEmailPasswordEnabled && enabledSocialProviders.isNotEmpty() -> AuthExperienceMode.MIXED
            isEmailPasswordEnabled -> AuthExperienceMode.EMAIL_PASSWORD_ONLY
            else -> AuthExperienceMode.SOCIAL_ONLY
        }

    init {
        require(AuthProvider.EMAIL_PASSWORD !in enabledSocialProviders) {
            "EMAIL_PASSWORD must not appear in enabledSocialProviders."
        }
        require(isEmailPasswordEnabled || enabledSocialProviders.isNotEmpty()) {
            "At least one auth mode must be enabled."
        }
    }
}
