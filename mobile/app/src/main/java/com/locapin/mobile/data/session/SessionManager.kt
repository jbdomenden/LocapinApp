package com.locapin.mobile.data.session

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.locapin.mobile.domain.model.AuthRole
import com.locapin.mobile.domain.model.AuthSession
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.authSessionDataStore by preferencesDataStore(name = "auth_session")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val sessionFlow: Flow<AuthSession?> = context.authSessionDataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences -> preferences.toAuthSession() }

    suspend fun saveSession(session: AuthSession) {
        context.authSessionDataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = session.isLoggedIn
            prefs[USER_ID] = session.userId
            prefs[USER_NAME] = session.name
            prefs[USER_EMAIL] = session.email
            prefs[USER_ROLE] = session.role.name
        }
    }

    suspend fun clearSession() {
        context.authSessionDataStore.edit { prefs ->
            prefs.remove(IS_LOGGED_IN)
            prefs.remove(USER_ID)
            prefs.remove(USER_NAME)
            prefs.remove(USER_EMAIL)
            prefs.remove(USER_ROLE)
        }
    }

    suspend fun getSession(): AuthSession? = sessionFlow.first()

    private fun Preferences.toAuthSession(): AuthSession? {
        val isLoggedIn = this[IS_LOGGED_IN] ?: false
        val userId = this[USER_ID]
        val name = this[USER_NAME]
        val email = this[USER_EMAIL]
        val roleRaw = this[USER_ROLE]

        if (!isLoggedIn || userId.isNullOrBlank() || name.isNullOrBlank() || email.isNullOrBlank() || roleRaw.isNullOrBlank()) {
            return null
        }

        val role = runCatching { AuthRole.valueOf(roleRaw) }.getOrNull() ?: return null
        return AuthSession(
            userId = userId,
            name = name,
            email = email,
            role = role,
            isLoggedIn = true
        )
    }

    private companion object {
        val IS_LOGGED_IN: Preferences.Key<Boolean> = booleanPreferencesKey("is_logged_in")
        val USER_ID: Preferences.Key<String> = stringPreferencesKey("user_id")
        val USER_NAME: Preferences.Key<String> = stringPreferencesKey("user_name")
        val USER_EMAIL: Preferences.Key<String> = stringPreferencesKey("user_email")
        val USER_ROLE: Preferences.Key<String> = stringPreferencesKey("user_role")
    }
}
