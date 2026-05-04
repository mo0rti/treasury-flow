package com.mortitech.treasuryflow.modules.auth.service

import com.mortitech.treasuryflow.bootstrap.security.JwtTokenProvider
import com.mortitech.treasuryflow.modules.auth.dto.AuthResponse
import com.mortitech.treasuryflow.modules.auth.dto.LoginRequest

import com.mortitech.treasuryflow.modules.auth.dto.OAuthCallbackRequest

import com.mortitech.treasuryflow.modules.auth.dto.RefreshTokenRequest
import com.mortitech.treasuryflow.modules.auth.dto.RegisterRequest
import com.mortitech.treasuryflow.modules.auth.dto.UserResponse
import com.mortitech.treasuryflow.modules.auth.dto.toResponse
import com.mortitech.treasuryflow.modules.auth.error.AuthErrorCode
import com.mortitech.treasuryflow.modules.auth.model.AuthProvider
import com.mortitech.treasuryflow.modules.auth.model.User
import com.mortitech.treasuryflow.modules.auth.repository.UserRepository
import com.mortitech.treasuryflow.shared.exception.ConflictException
import com.mortitech.treasuryflow.shared.exception.UnauthorizedException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val oAuth2UserService: OAuth2UserService,
    private val passwordEncoder: PasswordEncoder
) {
    private val dummyPasswordHash = passwordEncoder.encode("timing-attack-placeholder")

    @Transactional
    fun oauthCallback(request: OAuthCallbackRequest): AuthResponse {
        val user = oAuth2UserService.processOAuthCallback(request.provider, request.code, request.redirectUri)
        return createFreshSession(user)
    }


    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw ConflictException(AuthErrorCode.EMAIL_ALREADY_REGISTERED)
        }

        val user = userRepository.save(
            User(
                email = request.email,
                displayName = request.displayName,
                passwordHash = passwordEncoder.encode(request.password),
                authProvider = AuthProvider.LOCAL
            )
        )

        return createFreshSession(user)
    }

    @Transactional
    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email)
        val passwordHash = user?.passwordHash ?: dummyPasswordHash
        val passwordMatches = passwordEncoder.matches(request.password, passwordHash)
        if (user == null || user.passwordHash == null || !passwordMatches) {
            throw UnauthorizedException(AuthErrorCode.INVALID_CREDENTIALS)
        }

        return createFreshSession(user)
    }

    @Transactional
    fun refreshToken(request: RefreshTokenRequest): AuthResponse {
        if (!jwtTokenProvider.validateRefreshToken(request.refreshToken)) {
            throw UnauthorizedException(AuthErrorCode.INVALID_REFRESH_TOKEN)
        }

        val userId = jwtTokenProvider.getUserIdFromToken(request.refreshToken)
        val user = userRepository.findById(userId)
            .orElseThrow { UnauthorizedException(AuthErrorCode.USER_NOT_FOUND) }

        if (!jwtTokenProvider.matchesRefreshTokenVersion(request.refreshToken, user.refreshTokenVersion)) {
            throw UnauthorizedException(AuthErrorCode.INVALID_REFRESH_TOKEN)
        }

        return createFreshSession(user)
    }

    @Transactional
    fun logout(user: User) {
        user.refreshTokenVersion += 1
        userRepository.save(user)
    }

    @Transactional(readOnly = true)
    fun getCurrentUser(user: User): UserResponse = user.toResponse()

    private fun createFreshSession(user: User): AuthResponse {
        user.refreshTokenVersion += 1
        val persistedUser = userRepository.save(user)

        return AuthResponse(
            accessToken = jwtTokenProvider.generateAccessToken(persistedUser),
            refreshToken = jwtTokenProvider.generateRefreshToken(persistedUser),
            expiresIn = jwtTokenProvider.getAccessTokenExpiry(),
            user = persistedUser.toResponse()
        )
    }
}
