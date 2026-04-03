package com.locapin.mobile.data.repository

import com.locapin.mobile.core.common.LocaPinResult
import com.locapin.mobile.data.local.SanJuanSeedDataSource
import com.locapin.mobile.data.remote.LocaPinApi
import com.locapin.mobile.domain.model.MapZone
import com.locapin.mobile.domain.model.ZoneAttraction
import com.locapin.mobile.domain.model.ZonePoint
import com.locapin.mobile.domain.repository.SegmentedMapRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SegmentedMapRepositoryImpl @Inject constructor(
    private val api: LocaPinApi,
    private val seedDataSource: SanJuanSeedDataSource
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

    override suspend fun getZoneAttractions(): LocaPinResult<List<ZoneAttraction>> = runCatching {
        api.mapAttractions().data?.map {
            ZoneAttraction(
                id = it.id,
                name = it.name,
                description = it.description,
                knownFor = it.knownFor,
                latitude = it.latitude,
                longitude = it.longitude,
                zoneId = it.zoneId,
                imageUrl = it.imageUrl,
                category = it.category
            )
        }
    }.fold(
        onSuccess = { LocaPinResult.Success(it ?: emptyList()) },
        onFailure = { LocaPinResult.Error(it.message ?: "Unable to load attractions from backend.") }
    )

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
