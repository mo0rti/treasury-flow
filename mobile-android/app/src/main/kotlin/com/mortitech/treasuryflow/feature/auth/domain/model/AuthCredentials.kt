package com.mortitech.treasuryflow.feature.auth.domain.model

data class AuthCredentials(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
)
