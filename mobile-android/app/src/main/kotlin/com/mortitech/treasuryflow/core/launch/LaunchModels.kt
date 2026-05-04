package com.mortitech.treasuryflow.core.launch

enum class LaunchDestination {
    Welcome,
    Auth,
    Main,
}

sealed interface LaunchUiState {
    data object Loading : LaunchUiState
    data class Ready(val destination: LaunchDestination) : LaunchUiState
}
