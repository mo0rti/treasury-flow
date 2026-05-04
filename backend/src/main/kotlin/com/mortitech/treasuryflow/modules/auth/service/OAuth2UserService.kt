package com.mortitech.treasuryflow.modules.auth.service

import com.mortitech.treasuryflow.bootstrap.properties.OAuthProperties
import com.mortitech.treasuryflow.modules.auth.error.AuthErrorCode
import com.mortitech.treasuryflow.modules.auth.model.AuthProvider
import com.mortitech.treasuryflow.modules.auth.model.OAuthUserProfile
import com.mortitech.treasuryflow.modules.auth.model.User
import com.mortitech.treasuryflow.shared.exception.UnauthorizedException

import org.slf4j.LoggerFactory

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class OAuth2UserService(
    private val oAuthProperties: OAuthProperties,
    private val restTemplate: RestTemplate,
    private val userProvisioningService: UserProvisioningService
) {

    private val log = LoggerFactory.getLogger(javaClass)


    fun processOAuthCallback(provider: String, code: String, redirectUri: String): User {
        val authProvider = try {
            AuthProvider.valueOf(provider.uppercase())
        } catch (_: Exception) {
            throw UnauthorizedException(
                AuthErrorCode.INVALID_OAUTH_PROVIDER,
                message = "Unsupported OAuth provider: $provider"
            )
        }

        val userProfile = when (authProvider) {

            AuthProvider.GOOGLE -> exchangeGoogleCode(code, redirectUri)


            AuthProvider.APPLE -> exchangeAppleCode(code, redirectUri)



            else -> throw UnauthorizedException(
                AuthErrorCode.OAUTH_PROVIDER_NOT_ENABLED,
                message = "OAuth provider $provider is not enabled"
            )
        }

        return try {
            userProvisioningService.provisionOAuthUser(authProvider, userProfile)
        } catch (ex: DataIntegrityViolationException) {
            userProvisioningService.findExistingOAuthUser(authProvider, userProfile)
                ?: throw ex
        }
    }


    private fun exchangeGoogleCode(code: String, redirectUri: String): OAuthUserProfile {
        val googleProperties = oAuthProperties.google
            ?: throw UnauthorizedException(
                AuthErrorCode.OAUTH_PROVIDER_NOT_CONFIGURED,
                message = "Google OAuth is not configured"
            )

        val tokenResponse = exchangeCodeForToken(
            tokenUri = googleProperties.tokenUri,
            clientId = googleProperties.clientId,
            clientSecret = googleProperties.clientSecret,
            code = code,
            redirectUri = redirectUri
        )
        val accessToken = tokenResponse["access_token"] as? String
            ?: throw UnauthorizedException(
                AuthErrorCode.OAUTH_ACCESS_TOKEN_MISSING,
                message = "Failed to get Google access token"
            )

        val headers = HttpHeaders().apply { setBearerAuth(accessToken) }
        val userInfo = restTemplate.exchange(
            googleProperties.userInfoUri,
            HttpMethod.GET,
            HttpEntity<Void>(headers),
            Map::class.java
        ).body ?: throw UnauthorizedException(
            AuthErrorCode.OAUTH_USER_INFO_FAILED,
            message = "Failed to get Google user info"
        )

        val providerId = userInfo["sub"] as? String
            ?: throw UnauthorizedException(
                AuthErrorCode.OAUTH_PROVIDER_ID_MISSING,
                message = "Missing Google subject in user info"
            )
        val email = userInfo["email"] as? String
            ?: throw UnauthorizedException(
                AuthErrorCode.OAUTH_EMAIL_UNAVAILABLE,
                message = "Google account email is unavailable"
            )
        val emailVerified = userInfo["email_verified"] as? Boolean
        if (emailVerified != true) {
            throw UnauthorizedException(
                AuthErrorCode.OAUTH_EMAIL_NOT_VERIFIED,
                message = "Google account email is not verified"
            )
        }

        return OAuthUserProfile(
            providerId = providerId,
            email = email,
            displayName = userInfo["name"] as? String ?: email,
            avatarUrl = userInfo["picture"] as? String
        )
    }


    private fun exchangeAppleCode(code: String, redirectUri: String): OAuthUserProfile {
        val appleProperties = oAuthProperties.apple
            ?: throw UnauthorizedException(
                AuthErrorCode.OAUTH_PROVIDER_NOT_CONFIGURED,
                message = "Apple OAuth is not configured"
            )

        val tokenResponse = exchangeCodeForToken(
            tokenUri = appleProperties.tokenUri,
            clientId = appleProperties.clientId,
            clientSecret = generateAppleClientSecret(),
            code = code,
            redirectUri = redirectUri
        )
        val idToken = tokenResponse["id_token"] as? String
            ?: throw UnauthorizedException(
                AuthErrorCode.OAUTH_ACCESS_TOKEN_MISSING,
                message = "Failed to get Apple ID token"
            )

        val payload = decodeJwtPayload(idToken)
        val providerId = payload["sub"] as? String
            ?: throw UnauthorizedException(
                AuthErrorCode.OAUTH_PROVIDER_ID_MISSING,
                message = "Missing Apple subject in ID token"
            )
        val email = payload["email"] as? String
            ?: throw UnauthorizedException(
                AuthErrorCode.OAUTH_EMAIL_UNAVAILABLE,
                message = "Apple account email is unavailable"
            )
        return OAuthUserProfile(
            providerId = providerId,
            email = email,
            displayName = email,
            avatarUrl = null
        )
    }

    private fun generateAppleClientSecret(): String {
        log.warn("Apple client secret generation not fully implemented - provide your own implementation")
        return ""
    }

    private fun decodeJwtPayload(jwt: String): Map<String, Any> {
        val parts = jwt.split(".")
        if (parts.size != 3) {
            throw UnauthorizedException(
                AuthErrorCode.OAUTH_ID_TOKEN_INVALID,
                message = "Invalid ID token format"
            )
        }
        val payload = String(java.util.Base64.getUrlDecoder().decode(parts[1]))
        @Suppress("UNCHECKED_CAST")
        return com.fasterxml.jackson.databind.ObjectMapper().readValue(payload, Map::class.java) as Map<String, Any>
    }


    @Suppress("UNCHECKED_CAST")
    private fun exchangeCodeForToken(
        tokenUri: String,
        clientId: String,
        clientSecret: String,
        code: String,
        redirectUri: String
    ): Map<String, Any> {
        val params = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "authorization_code")
            add("code", code)
            add("redirect_uri", redirectUri)
            add("client_id", clientId)
            add("client_secret", clientSecret)
        }
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_FORM_URLENCODED }
        val response = restTemplate.postForObject(tokenUri, HttpEntity(params, headers), Map::class.java)
            ?: throw UnauthorizedException(AuthErrorCode.OAUTH_TOKEN_EXCHANGE_FAILED)
        return response as Map<String, Any>
    }
}
