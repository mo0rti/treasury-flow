package com.mortitech.treasuryflow.app

import androidx.compose.runtime.Composable
import com.mortitech.treasuryflow.core.launch.LaunchUiState
import com.mortitech.treasuryflow.navigation.NavGraph

@Composable
fun AppRoot(
    launchUiState: LaunchUiState,
) {
    NavGraph(launchUiState = launchUiState)
}
