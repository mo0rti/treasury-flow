package com.mortitech.treasuryflow.modules.auth.controller

import com.mortitech.treasuryflow.modules.auth.dto.AuthResponse
import com.mortitech.treasuryflow.modules.auth.dto.LoginRequest

import com.mortitech.treasuryflow.modules.auth.dto.OAuthCallbackRequest

import com.mortitech.treasuryflow.modules.auth.dto.RefreshTokenRequest
import com.mortitech.treasuryflow.modules.auth.dto.RegisterRequest
import com.mortitech.treasuryflow.modules.auth.dto.UserResponse
import com.mortitech.treasuryflow.modules.auth.model.User
import com.mortitech.treasuryflow.modules.auth.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth")
class AuthController(
    private val authService: AuthService
) {


    @PostMapping("/oauth/callback")
    @Operation(summary = "Authenticate with an OAuth provider callback")
    fun oauthCallback(@Valid @RequestBody request: OAuthCallbackRequest): ResponseEntity<AuthResponse> {
        val response = authService.oauthCallback(request)
        return ResponseEntity.ok(response)
    }


    @PostMapping("/register")
    @Operation(summary = "Register a local user account")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        val response = authService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate with email and password")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        val response = authService.login(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access and refresh tokens")
    fun refreshToken(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<AuthResponse> {
        val response = authService.refreshToken(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/logout")
    @Operation(summary = "Invalidate refresh tokens for the current user")
    fun logout(@AuthenticationPrincipal user: User): ResponseEntity<Void> {
        authService.logout(user)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/me")
    @Operation(summary = "Get the current authenticated user")
    fun getCurrentUser(@AuthenticationPrincipal user: User): ResponseEntity<UserResponse> {
        val response = authService.getCurrentUser(user)
        return ResponseEntity.ok(response)
    }
}
