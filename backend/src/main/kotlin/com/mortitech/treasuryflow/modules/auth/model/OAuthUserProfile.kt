package com.mortitech.treasuryflow.modules.auth.model

data class OAuthUserProfile(
    val providerId: String,
    val email: String,
    val displayName: String,
    val avatarUrl: String?
)
