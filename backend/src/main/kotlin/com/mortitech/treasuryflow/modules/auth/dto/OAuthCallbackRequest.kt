package com.mortitech.treasuryflow.modules.auth.dto


import jakarta.validation.constraints.NotBlank

data class OAuthCallbackRequest(
    @field:NotBlank val provider: String,
    @field:NotBlank val code: String,
    @field:NotBlank val redirectUri: String
)

