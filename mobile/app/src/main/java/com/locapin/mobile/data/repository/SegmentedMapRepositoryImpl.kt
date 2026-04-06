package com.locapin.mobile.data.repository

import com.locapin.mobile.core.common.LocaPinResult
import com.locapin.mobile.data.local.SanJuanSeedDataSource
import com.locapin.mobile.data.remote.LocaPinApi
import com.locapin.mobile.domain.model.MapZone
import com.locapin.mobile.domain.model.ZoneAttraction
import com.locapin.mobile.domain.model.ZonePoint
import com.locapin.mobile.domain.repository.SegmentedMapRepository
import com.locapin.mobile.feature.admin.AdminAttractionRepository
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SegmentedMapRepositoryImpl @Inject constructor(
    private val api: LocaPinApi,
    private val seedDataSource: SanJuanSeedDataSource,
    private val adminAttractionRepository: AdminAttractionRepository
) : SegmentedMapRepository {

    override suspend fun getMapZones(): LocaPinResult<List<MapZone>> = runCatching {
        api.mapAreas().data?.map {
            MapZone(
                id = it.id,
                displayName = it.displayName,
                polygonPoints = it.polygonPoints.map { p -> ZonePoint(p.lat, p.lng) },
                centerLat = it.centerLat,
                centerLng = it.centerLng
            )
        }
    }.fold(
        onSuccess = { LocaPinResult.Success(it ?: seedDataSource.mapZones()) },
        onFailure = { LocaPinResult.Success(seedDataSource.mapZones()) }
    )

    override suspend fun getZoneAttractions(): LocaPinResult<List<ZoneAttraction>> {
        val attractions = adminAttractionRepository.attractions.value
            .asSequence()
            .filter { it.isVisible }
            .map { item ->
                ZoneAttraction(
                    id = item.id,
                    name = item.name,
                    description = item.description,
                    knownFor = item.knownFor,
                    latitude = item.latitude,
                    longitude = item.longitude,
                    zoneId = item.area.toZoneId(),
                    area = item.area,
                    category = item.category
                )
            }
            .sortedBy { it.name.lowercase(Locale.getDefault()) }
            .toList()
        return LocaPinResult.Success(attractions)
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
