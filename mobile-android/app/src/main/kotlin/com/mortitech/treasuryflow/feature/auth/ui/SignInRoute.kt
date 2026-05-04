package com.mortitech.treasuryflow.feature.auth.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mortitech.treasuryflow.feature.auth.AuthConfig

@Composable
fun SignInRoute(
    showBackToSocial: Boolean,
    onBackToSocial: () -> Unit,
    viewModel: SignInViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val email by viewModel.email.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()

    SignInScreen(
        uiState = uiState,
        email = email,
        password = password,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSignInWithPassword = viewModel::signInWithPassword,
        showBackToSocial = showBackToSocial,
        onBackToSocial = onBackToSocial,
        onDismissError = viewModel::clearError,
    )

}
