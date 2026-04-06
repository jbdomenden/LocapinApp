package com.locapin.mobile.data.repository

import com.locapin.mobile.core.common.LocaPinResult
import com.locapin.mobile.core.datastore.UserPreferencesDataStore
import com.locapin.mobile.data.auth.MockAuthDataSource
import com.locapin.mobile.domain.model.AuthSession
import com.locapin.mobile.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class FakeAuthRepository @Inject constructor(
    private val mockAuthDataSource: MockAuthDataSource,
    private val prefs: UserPreferencesDataStore
) : AuthRepository {

    override val session: Flow<AuthSession?> = prefs.session
    override val authToken: Flow<String?> = session.map { it?.token }

    override suspend fun login(email: String, password: String): LocaPinResult<AuthSession> {
        val account = mockAuthDataSource.findAccount(email, password)
            ?: return LocaPinResult.Error("Invalid credentials. Use one of the Quick Test Accounts.")

        val session = AuthSession(
            isLoggedIn = true,
            userId = account.userId,
            name = account.name,
            email = account.email,
            role = account.role,
            token = "mock-token-${account.userId}"
        )
        prefs.saveSession(session)
        return LocaPinResult.Success(session)
    }

    override suspend fun socialLogin(
        provider: String,
        idToken: String?,
        accessToken: String?
    ): LocaPinResult<AuthSession> = LocaPinResult.Error("$provider sign-in is disabled in mock auth mode.")

    override suspend fun register(name: String, email: String, password: String): LocaPinResult<AuthSession> =
        LocaPinResult.Error("Sign up is temporarily unavailable in mock auth mode.")

    override suspend fun forgotPassword(email: String): LocaPinResult<Unit> =
        LocaPinResult.Success(Unit)

    override suspend fun logout() = prefs.clearSession()
}
