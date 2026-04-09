package com.locapin.mobile.data.repository

import com.locapin.mobile.core.common.LocaPinResult
import com.locapin.mobile.data.auth.MockAuthDataSource
import com.locapin.mobile.data.session.SessionManager
import com.locapin.mobile.domain.model.AuthRole
import com.locapin.mobile.domain.model.AuthSession
import com.locapin.mobile.domain.repository.AuthRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class FakeAuthRepository @Inject constructor(
    private val mockAuthDataSource: MockAuthDataSource,
    private val sessionManager: SessionManager
) : AuthRepository {

    override val session: Flow<AuthSession?> = sessionManager.sessionFlow

    override val authToken: Flow<String?> = session.map { currentSession ->
        if (currentSession?.isLoggedIn == true) {
            "mock-authenticated"
        } else {
            null
        }
    }

    override suspend fun login(email: String, password: String): LocaPinResult<AuthSession> {
        val user = mockAuthDataSource.findUser(email, password)
            ?: return LocaPinResult.Error("Invalid credentials.")

        val authSession = AuthSession(
            userId = user.id,
            name = user.name,
            email = user.email,
            role = user.role,
            isLoggedIn = true
        )

        sessionManager.saveSession(authSession)
        return LocaPinResult.Success(authSession)
    }

    override suspend fun socialLogin(
        provider: String,
        idToken: String?,
        accessToken: String?
    ): LocaPinResult<AuthSession> {
        val authSession = AuthSession(
            userId = "mock-${provider}-${UUID.randomUUID()}",
            name = when (provider.lowercase()) {
                "google" -> "Google Tourist"
                "facebook" -> "Facebook Tourist"
                else -> "Social Tourist"
            },
            email = when (provider.lowercase()) {
                "google" -> "google.tourist@locapin.app"
                "facebook" -> "facebook.tourist@locapin.app"
                else -> "social.tourist@locapin.app"
            },
            role = AuthRole.TOURIST,
            isLoggedIn = true
        )

        sessionManager.saveSession(authSession)
        return LocaPinResult.Success(authSession)
    }

    override suspend fun register(name: String, email: String, password: String): LocaPinResult<AuthSession> {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            return LocaPinResult.Error("Please complete all required fields.")
        }

        val authSession = AuthSession(
            userId = "mock-register-${UUID.randomUUID()}",
            name = name.trim(),
            email = email.trim().lowercase(),
            role = AuthRole.TOURIST,
            isLoggedIn = true
        )

        sessionManager.saveSession(authSession)
        return LocaPinResult.Success(authSession)
    }

    override suspend fun forgotPassword(email: String): LocaPinResult<Unit> =
        LocaPinResult.Success(Unit)

    override suspend fun logout() {
        sessionManager.clearSession()
    }

    override suspend fun getCurrentSession(): AuthSession? = sessionManager.getSession()

    override suspend fun restoreSession(): AuthSession? = sessionManager.getSession()
}
