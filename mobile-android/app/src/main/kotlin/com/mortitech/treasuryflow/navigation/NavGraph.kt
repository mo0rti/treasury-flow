package com.mortitech.treasuryflow.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.mortitech.treasuryflow.feature.auth.AuthConfig
import com.mortitech.treasuryflow.feature.auth.AuthExperienceMode
import com.mortitech.treasuryflow.core.launch.LaunchDestination
import com.mortitech.treasuryflow.core.launch.LaunchUiState
import com.mortitech.treasuryflow.feature.auth.ui.SignInRoute
import com.mortitech.treasuryflow.feature.auth.ui.SocialSignInRoute
import com.mortitech.treasuryflow.feature.example.ui.ExampleListRoute
import com.mortitech.treasuryflow.feature.welcome.ui.WelcomeRoute

@Composable
fun NavGraph(
    launchUiState: LaunchUiState,
) {
    if (launchUiState !is LaunchUiState.Ready) return

    LaunchGate(launchDestination = launchUiState.destination)
}

@Composable
fun LaunchGate(
    launchDestination: LaunchDestination,
) {
    key(launchDestination) {
        val navController = rememberNavController()
        val startDestination: Screen = when (launchDestination) {
            LaunchDestination.Welcome -> Screen.Welcome
            LaunchDestination.Auth -> Screen.AuthRoot
            LaunchDestination.Main -> Screen.ExampleList
        }
        val authStartDestination: Screen = when (AuthConfig.authExperienceMode) {
            AuthExperienceMode.EMAIL_PASSWORD_ONLY -> Screen.SignIn
            AuthExperienceMode.SOCIAL_ONLY, AuthExperienceMode.MIXED -> Screen.SocialSignIn
        }

        NavHost(navController = navController, startDestination = startDestination) {
            composable<Screen.Welcome> {
                WelcomeRoute(
                    onFinished = {
                        navController.navigate(Screen.AuthRoot) {
                            popUpTo(Screen.Welcome) { inclusive = true }
                        }
                    },
                )
            }

            navigation<Screen.AuthRoot>(startDestination = authStartDestination) {
                composable<Screen.SocialSignIn> {
                    SocialSignInRoute(
                        onContinueWithEmailPassword = {
                            navController.navigate(Screen.SignIn)
                        },
                    )
                }
                composable<Screen.SignIn> {
                    SignInRoute(
                        showBackToSocial = AuthConfig.authExperienceMode == AuthExperienceMode.MIXED,
                        onBackToSocial = { navController.popBackStack() },
                    )
                }
            }

            composable<Screen.ExampleList> {
                ExampleListRoute()
            }
        }
    }
}
