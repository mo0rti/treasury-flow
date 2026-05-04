package com.mortitech.treasuryflow.feature.auth.ui

import com.mortitech.treasuryflow.R
import com.mortitech.treasuryflow.core.common.AppResult
import com.mortitech.treasuryflow.core.model.User
import com.mortitech.treasuryflow.designsystem.text.UiText
import com.mortitech.treasuryflow.feature.auth.data.repository.AuthRepository
import com.mortitech.treasuryflow.feature.auth.domain.AuthProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SignInViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository = mockk<AuthRepository>()
    private lateinit var viewModel: SignInViewModel

    private val testUser = User(
        id = "1",
        email = "test@example.com",
        displayName = "Test User",
        authProvider = "LOCAL",
        role = "USER",
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SignInViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() {
        assertEquals(SignInUiState.Idle, viewModel.uiState.value)
    }


    @Test
    fun `sign in with password success`() = runTest(testDispatcher) {
        coEvery { authRepository.login(any(), any()) } returns AppResult.Success(testUser)

        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.signInWithPassword()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { authRepository.login("test@example.com", "password123") }
    }

    @Test
    fun `sign in with password failure shows error`() = runTest(testDispatcher) {
        coEvery { authRepository.login(any(), any()) } returns AppResult.Error("Invalid credentials")

        viewModel.onEmailChange("test@example.com")
        viewModel.onPasswordChange("wrong")
        viewModel.signInWithPassword()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is SignInUiState.Error)
        assertEquals(UiText.DynamicString("Invalid credentials"), (state as SignInUiState.Error).message)
    }


    @Test
    fun `oauth sign in success`() = runTest(testDispatcher) {
        coEvery { authRepository.oauthCallback(any(), any(), any()) } returns AppResult.Success(testUser)

        viewModel.signInWithOAuth(AuthProvider.GOOGLE, "auth-code", "redirect-uri")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { authRepository.oauthCallback(AuthProvider.GOOGLE, "auth-code", "redirect-uri") }
    }

    @Test
    fun `oauth setup required shows explicit error`() {
        viewModel.showOAuthSetupRequired(AuthProvider.GOOGLE)

        val state = viewModel.uiState.value
        assertTrue(state is SignInUiState.Error)
        assertEquals(
            UiText.StringResource(
                R.string.error_oauth_setup_required,
                args = listOf("Google"),
            ),
            (state as SignInUiState.Error).message,
        )
    }
}
