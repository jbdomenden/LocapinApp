package com.locapin.mobile.domain.repository

import com.locapin.mobile.core.common.LocaPinResult
import com.locapin.mobile.domain.model.AuthSession
import com.locapin.mobile.domain.model.Category
import com.locapin.mobile.domain.model.Destination
import com.locapin.mobile.domain.model.User
import com.locapin.mobile.domain.model.ZoneAttraction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    suspend fun login(email: String, password: String): LocaPinResult<AuthSession>
    suspend fun socialLogin(provider: String, idToken: String? = null, accessToken: String? = null): LocaPinResult<AuthSession>
    suspend fun register(name: String, email: String, password: String): LocaPinResult<AuthSession>
    suspend fun forgotPassword(email: String): LocaPinResult<Unit>
    suspend fun logout()
    suspend fun getCurrentSession(): AuthSession?
    suspend fun restoreSession(): AuthSession?
    val session: Flow<AuthSession?>
    val authToken: Flow<String?>
}

interface DestinationRepository {
    suspend fun getDestinations(
        query: String? = null,
        categoryId: String? = null,
        sort: String? = null,
        page: Int = 1,
        lat: Double? = null,
        lng: Double? = null
    ): LocaPinResult<List<Destination>>

    suspend fun getDestinationDetail(id: String): LocaPinResult<Destination>
    suspend fun getCategories(): LocaPinResult<List<Category>>
    suspend fun getFavorites(): LocaPinResult<List<Destination>>
    suspend fun setFavorite(id: String, save: Boolean): LocaPinResult<Unit>
}

interface ProfileRepository {
    suspend fun getProfile(): LocaPinResult<User>
}

interface TouristFavoritesRepository {
    val favoriteIds: StateFlow<Set<String>>
    suspend fun setFavorite(id: String, save: Boolean)
}

interface HistoryRepository {
    val history: Flow<List<VisitedAttraction>>
    suspend fun recordVisit(attraction: ZoneAttraction)
}

data class VisitedAttraction(
    val id: String,
    val name: String,
    val description: String?,
    val knownFor: String,
    val latitude: Double,
    val longitude: Double,
    val visitedAtEpochMs: Long,
    val category: String? = null,
    val area: String? = null
)
