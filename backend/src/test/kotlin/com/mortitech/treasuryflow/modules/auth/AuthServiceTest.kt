package com.mortitech.treasuryflow.modules.auth

import com.mortitech.treasuryflow.modules.auth.dto.*
import com.mortitech.treasuryflow.modules.auth.model.*
import com.mortitech.treasuryflow.modules.auth.repository.UserRepository
import com.mortitech.treasuryflow.modules.auth.service.AuthService
import com.mortitech.treasuryflow.bootstrap.security.JwtTokenProvider
import com.mortitech.treasuryflow.shared.exception.ConflictException
import com.mortitech.treasuryflow.shared.exception.UnauthorizedException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {

    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @BeforeEach
    fun setup() {
        userRepository.deleteAll()
    }

    @Test
    fun `register creates user and returns tokens`() {
        val request = RegisterRequest(
            email = "test@example.com",
            password = "password123",
            displayName = "Test User"
        )
        val response = authService.register(request)

        assertNotNull(response.accessToken)
        assertNotNull(response.refreshToken)
        assertEquals("test@example.com", response.user.email)
        assertEquals("Test User", response.user.displayName)
        assertTrue(jwtTokenProvider.validateAccessToken(response.accessToken))
        assertTrue(jwtTokenProvider.validateRefreshToken(response.refreshToken))
    }

    @Test
    fun `register with duplicate email throws conflict`() {
        val request = RegisterRequest(
            email = "test@example.com",
            password = "password123",
            displayName = "Test User"
        )
        authService.register(request)

        assertThrows(ConflictException::class.java) {
            authService.register(request)
        }
    }

    @Test
    fun `login with valid credentials returns tokens`() {
        authService.register(RegisterRequest(
            email = "test@example.com",
            password = "password123",
            displayName = "Test User"
        ))

        val response = authService.login(LoginRequest(
            email = "test@example.com",
            password = "password123"
        ))

        assertNotNull(response.accessToken)
        assertEquals("test@example.com", response.user.email)
    }

    @Test
    fun `login with wrong password throws unauthorized`() {
        authService.register(RegisterRequest(
            email = "test@example.com",
            password = "password123",
            displayName = "Test User"
        ))

        assertThrows(UnauthorizedException::class.java) {
            authService.login(LoginRequest(
                email = "test@example.com",
                password = "wrongpassword"
            ))
        }
    }

    @Test
    fun `refresh token returns new tokens`() {
        val user = userRepository.save(User(
            email = "test@example.com",
            displayName = "Test User",
            authProvider = AuthProvider.LOCAL
        ))

        val refreshToken = jwtTokenProvider.generateRefreshToken(user)
        val response = authService.refreshToken(RefreshTokenRequest(refreshToken))

        assertNotNull(response.accessToken)
        assertNotNull(response.refreshToken)

        assertThrows(UnauthorizedException::class.java) {
            authService.refreshToken(RefreshTokenRequest(refreshToken))
        }
    }

    @Test
    fun `logout invalidates existing refresh tokens`() {
        val response = authService.register(RegisterRequest(
            email = "logout@example.com",
            password = "password123",
            displayName = "Logout User"
        ))
        val user = userRepository.findByEmail("logout@example.com")!!

        authService.logout(user)

        assertThrows(UnauthorizedException::class.java) {
            authService.refreshToken(RefreshTokenRequest(response.refreshToken))
        }
    }

    @Test
    fun `refresh rejects access token`() {
        val user = userRepository.save(User(
            email = "test@example.com",
            displayName = "Test User",
            authProvider = AuthProvider.LOCAL
        ))

        val accessToken = jwtTokenProvider.generateAccessToken(user)

        assertThrows(UnauthorizedException::class.java) {
            authService.refreshToken(RefreshTokenRequest(accessToken))
        }
    }

    @Test
    fun `getCurrentUser returns user data`() {
        val user = userRepository.save(User(
            email = "test@example.com",
            displayName = "Test User",
            authProvider = AuthProvider.LOCAL
        ))

        val response = authService.getCurrentUser(user)

        assertEquals(user.id, response.id)
        assertEquals("test@example.com", response.email)
    }
}
