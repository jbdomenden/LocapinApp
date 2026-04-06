package com.locapin.mobile.data.repository

import com.locapin.mobile.core.datastore.UserPreferencesDataStore
import com.locapin.mobile.domain.repository.TouristFavoritesRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@Singleton
class TouristFavoritesRepositoryImpl @Inject constructor(
    private val prefs: UserPreferencesDataStore
) : TouristFavoritesRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val favoriteIds: StateFlow<Set<String>> = prefs.favoriteAttractionIds
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = emptySet()
        )

    override suspend fun setFavorite(id: String, save: Boolean) {
        prefs.setFavoriteAttraction(id = id, save = save)
    }
}
