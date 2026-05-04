---
to: "<%= platform === 'mobile-android' ? 'mobile-android/app/src/main/kotlin/com/mortitech/treasuryflow/feature/' + h.changeCase.camel(feature) + '/ui/' + h.changeCase.pascal(name) + 'Screen.kt' : null %>"
---
package com.mortitech.treasuryflow.feature.<%= h.changeCase.camel(feature) %>.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.mortitech.treasuryflow.designsystem.components.ErrorView
import com.mortitech.treasuryflow.designsystem.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <%= h.changeCase.pascal(name) %>Screen(
    uiState: <%= h.changeCase.pascal(name) %>UiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("<%= h.changeCase.title(name) %>") },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (uiState) {
                is <%= h.changeCase.pascal(name) %>UiState.Idle,
                is <%= h.changeCase.pascal(name) %>UiState.Loading -> {
                    LoadingIndicator()
                }
                is <%= h.changeCase.pascal(name) %>UiState.Success -> {
                    // TODO: Screen content
                    Text("TODO: Implement <%= h.changeCase.title(name) %> screen")
                }
                is <%= h.changeCase.pascal(name) %>UiState.Error -> {
                    ErrorView(
                        message = uiState.message,
                        onRetry = onRetry,
                    )
                }
            }
        }
    }
}
