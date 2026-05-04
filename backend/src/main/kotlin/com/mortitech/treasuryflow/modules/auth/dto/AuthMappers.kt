package com.mortitech.treasuryflow.modules.auth.dto

import com.mortitech.treasuryflow.modules.auth.model.User

fun User.toResponse() = UserResponse(
    id = id,
    email = email,
    displayName = displayName,
    avatarUrl = avatarUrl,
    authProvider = authProvider,
    role = role
)
