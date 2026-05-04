package com.mortitech.treasuryflow.feature.welcome.ui

data class WelcomeUiState(
    val pages: List<WelcomePage>,
    val isCompleting: Boolean = false,
)
