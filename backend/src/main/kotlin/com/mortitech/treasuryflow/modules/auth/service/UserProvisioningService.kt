package com.mortitech.treasuryflow.modules.auth.service

import com.mortitech.treasuryflow.modules.auth.model.AuthProvider
import com.mortitech.treasuryflow.modules.auth.model.OAuthUserProfile
import com.mortitech.treasuryflow.modules.auth.model.User
import com.mortitech.treasuryflow.modules.auth.repository.UserRepository
import com.mortitech.treasuryflow.modules.auth.error.AuthErrorCode
import com.mortitech.treasuryflow.shared.exception.ConflictException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserProvisioningService(
    private val userRepository: UserRepository
) {

    @Transactional
    fun provisionOAuthUser(provider: AuthProvider, userProfile: OAuthUserProfile): User {
        val existing = userRepository.findByAuthProviderAndProviderId(provider, userProfile.providerId)
        if (existing != null) {
            existing.displayName = userProfile.displayName
            existing.avatarUrl = userProfile.avatarUrl
            return userRepository.save(existing)
        }

        val byEmail = userRepository.findByEmail(userProfile.email)
        if (byEmail != null) {
            throw ConflictException(
                AuthErrorCode.OAUTH_ACCOUNT_LINK_REQUIRED,
                message = "An account with email ${userProfile.email} already exists. Sign in with the original method before linking $provider."
            )
        }

        return userRepository.save(
            User(
                email = userProfile.email,
                displayName = userProfile.displayName,
                avatarUrl = userProfile.avatarUrl,
                authProvider = provider,
                providerId = userProfile.providerId
            )
        )
    }

    @Transactional(readOnly = true)
    fun findExistingOAuthUser(provider: AuthProvider, userProfile: OAuthUserProfile): User? =
        userRepository.findByAuthProviderAndProviderId(provider, userProfile.providerId)
            ?: userRepository.findByEmail(userProfile.email)
}
