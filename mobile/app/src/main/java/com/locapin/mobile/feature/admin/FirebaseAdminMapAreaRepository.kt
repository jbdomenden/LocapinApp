package com.locapin.mobile.feature.admin

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAdminMapAreaRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : AdminMapAreaRepository {
    private val collection = firestore.collection("map_areas")
    private val _mapAreas = MutableStateFlow<List<AdminMapArea>>(emptyList())
    override val mapAreas: StateFlow<List<AdminMapArea>> = _mapAreas.asStateFlow()
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            observeMapAreas().collect { areas ->
                _mapAreas.value = areas
            }
        }
    }

    private fun observeMapAreas(): Flow<List<AdminMapArea>> = callbackFlow {
        val subscription = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val areas = snapshot?.documents?.mapNotNull { doc ->
                AdminMapArea(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    description = doc.getString("description") ?: "",
                    districtLabel = doc.getString("districtLabel") ?: "",
                    centerLatitude = doc.getDouble("centerLatitude") ?: 0.0,
                    centerLongitude = doc.getDouble("centerLongitude") ?: 0.0,
                    polygonPoints = doc.getString("polygonPoints") ?: "",
                    pathData = doc.getString("pathData") ?: "",
                    hexColor = doc.getString("hexColor") ?: "#F0F4A4",
                    isPremium = doc.getBoolean("isPremium") ?: false,
                    gridRotation = doc.getDouble("gridRotation")?.toFloat() ?: 45f,
                    gridDensity = doc.getDouble("gridDensity")?.toFloat() ?: 30f
                )
            } ?: emptyList()
            trySend(areas)
        }
        awaitClose { subscription.remove() }
    }

    override fun togglePremium(id: String) {
        val area = _mapAreas.value.find { it.id == id } ?: return
        collection.document(id).update("isPremium", !area.isPremium)
    }

    override fun addMapArea(area: AdminMapArea) {
        val id = if (area.id.isBlank()) area.name.lowercase().trim().replace(" ", "-") else area.id
        collection.document(id).set(area)
    }

    override fun updateMapArea(area: AdminMapArea) {
        collection.document(area.id).set(area)
    }

    override fun deleteMapArea(id: String) {
        collection.document(id).delete()
    }
}
