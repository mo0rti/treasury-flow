package com.mortitech.treasuryflow.core.model

data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val avatarUrl: String? = null,
    val authProvider: String,
    val role: String,
)
