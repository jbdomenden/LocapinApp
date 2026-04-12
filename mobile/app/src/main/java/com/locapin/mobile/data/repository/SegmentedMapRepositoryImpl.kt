package com.locapin.mobile.data.repository

import com.locapin.mobile.core.common.LocaPinResult
import com.locapin.mobile.data.local.SanJuanSeedDataSource
import com.locapin.mobile.data.remote.LocaPinApi
import com.locapin.mobile.domain.model.MapZone
import com.locapin.mobile.domain.model.ZoneAttraction
import com.locapin.mobile.domain.model.ZonePoint
import com.locapin.mobile.domain.repository.SegmentedMapRepository
import com.locapin.mobile.feature.admin.AdminAttraction
import com.locapin.mobile.feature.admin.AdminAttractionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SegmentedMapRepositoryImpl @Inject constructor(
    private val firestore: com.google.firebase.firestore.FirebaseFirestore,
    private val api: LocaPinApi,
    private val seedDataSource: SanJuanSeedDataSource,
    private val adminAttractionRepository: AdminAttractionRepository
) : SegmentedMapRepository {

    override suspend fun getMapZones(): LocaPinResult<List<MapZone>> {
        return try {
            val snapshot = firestore.collection("map_zones").get().await()
            val zones = snapshot.documents.mapNotNull { doc ->
                val pointsData = doc.get("polygonPoints") as? List<Map<String, Any>>
                val polygonPoints = pointsData?.map { 
                    val lat = (it["lat"] as? Number)?.toDouble() ?: 0.0
                    val lng = (it["lng"] as? Number)?.toDouble() ?: 0.0
                    ZonePoint(lat, lng) 
                } ?: emptyList()
                
                MapZone(
                    id = doc.id,
                    displayName = doc.getString("displayName") ?: "",
                    polygonPoints = polygonPoints,
                    centerLat = doc.getDouble("centerLat") ?: 0.0,
                    centerLng = doc.getDouble("centerLng") ?: 0.0
                )
            }
            if (zones.isEmpty()) LocaPinResult.Success(seedDataSource.mapZones())
            else LocaPinResult.Success(zones)
        } catch (e: Exception) {
            LocaPinResult.Success(seedDataSource.mapZones())
        }
    }

    override fun getZoneAttractionsFlow(): Flow<LocaPinResult<List<ZoneAttraction>>> = 
        adminAttractionRepository.attractions.map { attractions ->
            val zoneAttractions = attractions
                .filter { it.isVisible }
                .map { item ->
                    ZoneAttraction(
                        id = item.id,
                        name = item.name,
                        description = item.description,
                        knownFor = item.knownFor,
                        latitude = item.latitude,
                        longitude = item.longitude,
                        zoneId = item.area.lowercase(Locale.US),
                        area = item.area,
                        imageUrl = item.imageUrl,
                        category = item.category,
                        distance = item.distance
                    )
                }
                .sortedBy { it.name.lowercase(Locale.US) }
            LocaPinResult.Success(zoneAttractions)
        }

    override suspend fun getZoneAttractions(): LocaPinResult<List<ZoneAttraction>> {
        return getZoneAttractionsFlow().first()
    }

    override suspend fun getRoutePath(
        originLat: Double,
        originLng: Double,
        destinationLat: Double,
        destinationLng: Double
    ): LocaPinResult<List<Pair<Double, Double>>> = runCatching {
        api.mapRoute(originLat, originLng, destinationLat, destinationLng).data?.map { it.lat to it.lng }
    }.fold(
        onSuccess = { points ->
            val safePoints = points?.takeIf { it.size >= 2 }
                ?: interpolateFallback(originLat, originLng, destinationLat, destinationLng)
            LocaPinResult.Success(safePoints)
        },
        onFailure = {
            LocaPinResult.Success(interpolateFallback(originLat, originLng, destinationLat, destinationLng))
        }
    )

    private fun String.toZoneId(): String {
        val areaKey = lowercase(Locale.getDefault())
        return when {
            "greenhills" in areaKey -> "greenhills"
            "pinaglabanan" in areaKey -> "pinaglabanan"
            else -> "city_center"
        }
    }

    private fun interpolateFallback(
        originLat: Double,
        originLng: Double,
        destinationLat: Double,
        destinationLng: Double,
        steps: Int = 24
    ): List<Pair<Double, Double>> {
        return (0..steps).map { step ->
            val fraction = step.toDouble() / steps.toDouble()
            val lat = originLat + (destinationLat - originLat) * fraction
            val lng = originLng + (destinationLng - originLng) * fraction
            lat to lng
        }
    }
}
