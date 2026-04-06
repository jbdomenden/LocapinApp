package com.locapin.mobile.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.locapin.mobile.domain.model.AuthSession
import com.locapin.mobile.domain.model.UserRole
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.userPrefs by preferencesDataStore(name = "locapin_user_prefs")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }

    val hasCompletedOnboarding: Flow<Boolean> = context.userPrefs.data.map {
        it[ONBOARDING_KEY] ?: false
    }

    val session: Flow<AuthSession?> = context.userPrefs.data.map { prefs ->
        val isLoggedIn = prefs[SESSION_LOGGED_IN] ?: false
        val userId = prefs[SESSION_USER_ID]
        val name = prefs[SESSION_NAME]
        val email = prefs[SESSION_EMAIL]
        val roleRaw = prefs[SESSION_ROLE]
        val token = prefs[SESSION_TOKEN]

        if (!isLoggedIn || userId.isNullOrBlank() || name.isNullOrBlank() || email.isNullOrBlank() || roleRaw.isNullOrBlank()) {
            null
        } else {
            AuthSession(
                isLoggedIn = true,
                userId = userId,
                name = name,
                email = email,
                role = UserRole.valueOf(roleRaw),
                token = token.orEmpty()
            )
        }
    }

    val authToken: Flow<String?> = session.map { it?.token?.takeIf(String::isNotBlank) }

    val recentSearches: Flow<List<String>> = context.userPrefs.data.map {
        it[RECENT_SEARCHES_KEY]?.split("||")?.filter(String::isNotBlank) ?: emptyList()
    }

    val visitedAttractions: Flow<List<VisitedAttractionRecord>> = context.userPrefs.data.map { prefs ->
        prefs[VISITED_ATTRACTIONS_KEY]
            ?.takeIf { it.isNotBlank() }
            ?.let { raw -> runCatching { json.decodeFromString<List<VisitedAttractionRecord>>(raw) }.getOrDefault(emptyList()) }
            ?: emptyList()
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.userPrefs.edit { it[ONBOARDING_KEY] = completed }
    }

    suspend fun saveSession(session: AuthSession) {
        context.userPrefs.edit { prefs ->
            prefs[SESSION_LOGGED_IN] = session.isLoggedIn
            prefs[SESSION_USER_ID] = session.userId
            prefs[SESSION_NAME] = session.name
            prefs[SESSION_EMAIL] = session.email
            prefs[SESSION_ROLE] = session.role.name
            prefs[SESSION_TOKEN] = session.token
        }
    }

    suspend fun setAuthToken(token: String?) {
        context.userPrefs.edit { prefs ->
            if (token.isNullOrBlank()) prefs.remove(SESSION_TOKEN) else prefs[SESSION_TOKEN] = token
        }
    }

    suspend fun addRecentSearch(query: String) {
        context.userPrefs.edit { prefs ->
            val current = prefs[RECENT_SEARCHES_KEY]
                ?.split("||")
                ?.filter(String::isNotBlank)
                ?.toMutableList()
                ?: mutableListOf()
            current.remove(query)
            current.add(0, query)
            prefs[RECENT_SEARCHES_KEY] = current.take(8).joinToString("||")
        }
    }

    suspend fun clearSession() {
        context.userPrefs.edit { prefs ->
            prefs.remove(SESSION_LOGGED_IN)
            prefs.remove(SESSION_USER_ID)
            prefs.remove(SESSION_NAME)
            prefs.remove(SESSION_EMAIL)
            prefs.remove(SESSION_ROLE)
            prefs.remove(SESSION_TOKEN)
        }
    }

    suspend fun saveVisitedAttractions(history: List<VisitedAttractionRecord>) {
        context.userPrefs.edit { prefs ->
            prefs[VISITED_ATTRACTIONS_KEY] = json.encodeToString(history)
        }
    }

    @Serializable
    data class VisitedAttractionRecord(
        val id: String,
        val name: String,
        val description: String? = null,
        val knownFor: String,
        val latitude: Double,
        val longitude: Double,
        val visitedAtEpochMs: Long
    )

    private companion object {
        val ONBOARDING_KEY: Preferences.Key<Boolean> = booleanPreferencesKey("onboarding_done")
        val SESSION_LOGGED_IN: Preferences.Key<Boolean> = booleanPreferencesKey("session_logged_in")
        val SESSION_USER_ID: Preferences.Key<String> = stringPreferencesKey("session_user_id")
        val SESSION_NAME: Preferences.Key<String> = stringPreferencesKey("session_name")
        val SESSION_EMAIL: Preferences.Key<String> = stringPreferencesKey("session_email")
        val SESSION_ROLE: Preferences.Key<String> = stringPreferencesKey("session_role")
        val SESSION_TOKEN: Preferences.Key<String> = stringPreferencesKey("session_token")
        val RECENT_SEARCHES_KEY: Preferences.Key<String> = stringPreferencesKey("recent_searches")
        val VISITED_ATTRACTIONS_KEY: Preferences.Key<String> = stringPreferencesKey("visited_attractions")
    }
}
