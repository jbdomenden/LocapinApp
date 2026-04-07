package com.locapin.mobile.data.repository

import com.locapin.mobile.core.datastore.UserPreferencesDataStore
import com.locapin.mobile.domain.model.ZoneAttraction
import com.locapin.mobile.domain.repository.HistoryRepository
import com.locapin.mobile.domain.repository.VisitedAttraction
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val prefs: UserPreferencesDataStore
) : HistoryRepository {
    override val history: Flow<List<VisitedAttraction>> = prefs.visitedAttractions.map { list ->
        list.sortedByDescending { it.visitedAtEpochMs }.map {
            VisitedAttraction(
                id = it.id,
                name = it.name,
                description = it.description,
                knownFor = it.knownFor,
                latitude = it.latitude,
                longitude = it.longitude,
                visitedAtEpochMs = it.visitedAtEpochMs,
                category = it.category,
                area = it.area
            )
        }
    }

    override suspend fun recordVisit(attraction: ZoneAttraction) {
        val current = prefs.visitedAttractions.first().toMutableList()
        current.removeAll { it.id == attraction.id }
        current.add(
            0,
            UserPreferencesDataStore.VisitedAttractionRecord(
                id = attraction.id,
                name = attraction.name,
                description = attraction.description,
                knownFor = attraction.knownFor,
                latitude = attraction.latitude,
                longitude = attraction.longitude,
                visitedAtEpochMs = System.currentTimeMillis(),
                category = attraction.category,
                area = attraction.area
            )
        )
        prefs.saveVisitedAttractions(current.take(100))
    }
}
