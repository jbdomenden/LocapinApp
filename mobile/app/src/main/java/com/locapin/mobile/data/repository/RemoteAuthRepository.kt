package com.locapin.mobile.data.repository

import com.locapin.mobile.core.common.LocaPinResult
import com.locapin.mobile.data.remote.AuthApiService
import com.locapin.mobile.data.remote.AuthRequest
import com.locapin.mobile.data.remote.ForgotPasswordRequest
import com.locapin.mobile.data.remote.RegisterRequest
import com.locapin.mobile.data.remote.SocialAuthRequest
import com.locapin.mobile.data.session.SessionManager
import com.locapin.mobile.domain.model.AuthRole
import com.locapin.mobile.domain.model.AuthSession
import com.locapin.mobile.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class RemoteAuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager
) : AuthRepository {

    override val session: Flow<AuthSession?> = sessionManager.sessionFlow

    override val authToken: Flow<String?> = session.map { currentSession ->
        if (currentSession?.isLoggedIn == true) "remote-authenticated" else null
    }

    override suspend fun login(email: String, password: String): LocaPinResult<AuthSession> = runCatching {
        val response = authApiService.login(AuthRequest(identifier = email, password = password, email = email))
        val user = response.data?.user ?: return LocaPinResult.Error(response.error ?: "Unable to login")
        val authSession = AuthSession(
            userId = user.id,
            name = user.name,
            email = user.email,
            role = AuthRole.TOURIST,
            isLoggedIn = true
        )
        sessionManager.saveSession(authSession)
        LocaPinResult.Success(authSession)
    }.getOrElse { LocaPinResult.Error(it.message ?: "Unable to login") }

    override suspend fun socialLogin(
        provider: String,
        idToken: String?,
        accessToken: String?
    ): LocaPinResult<AuthSession> = runCatching {
        val response = authApiService.socialAuth(
            SocialAuthRequest(provider = provider, idToken = idToken, accessToken = accessToken)
        )
        val user = response.data?.user ?: return LocaPinResult.Error(response.error ?: "Unable to login")
        val authSession = AuthSession(
            userId = user.id,
            name = user.name,
            email = user.email,
            role = AuthRole.TOURIST,
            isLoggedIn = true
        )
        sessionManager.saveSession(authSession)
        LocaPinResult.Success(authSession)
    }.getOrElse { LocaPinResult.Error(it.message ?: "Unable to login") }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        agreedToEula: Boolean,
        agreedToTerms: Boolean,
        agreedToPrivacy: Boolean
    ): LocaPinResult<AuthSession> = runCatching {
        val response = authApiService.register(
            RegisterRequest(
                name = name,
                email = email,
                password = password,
                agreedToEula = agreedToEula,
                agreedToTerms = agreedToTerms,
                agreedToPrivacy = agreedToPrivacy
            )
        )
        val user = response.data?.user ?: return LocaPinResult.Error(response.error ?: "Unable to register")
        val authSession = AuthSession(
            userId = user.id,
            name = user.name,
            email = user.email,
            role = AuthRole.TOURIST,
            isLoggedIn = true
        )
        sessionManager.saveSession(authSession)
        LocaPinResult.Success(authSession)
    }.getOrElse { LocaPinResult.Error(it.message ?: "Unable to register") }

    override suspend fun forgotPassword(email: String): LocaPinResult<Unit> = runCatching {
        authApiService.forgotPassword(ForgotPasswordRequest(email))
        LocaPinResult.Success(Unit)
    }.getOrElse { LocaPinResult.Error(it.message ?: "Unable to process request") }

    override suspend fun logout() {
        sessionManager.clearSession()
    }

    override suspend fun getCurrentSession(): AuthSession? = sessionManager.getSession()

    override suspend fun restoreSession(): AuthSession? = sessionManager.getSession()
}
