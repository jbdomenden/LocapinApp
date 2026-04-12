package com.locapin.mobile.domain.repository

import com.locapin.mobile.core.common.LocaPinResult
import com.locapin.mobile.domain.model.MapZone
import com.locapin.mobile.domain.model.ZoneAttraction

interface SegmentedMapRepository {
    suspend fun getMapZones(): LocaPinResult<List<MapZone>>
    fun getZoneAttractionsFlow(): kotlinx.coroutines.flow.Flow<LocaPinResult<List<ZoneAttraction>>>
    suspend fun getZoneAttractions(): LocaPinResult<List<ZoneAttraction>>
    suspend fun getRoutePath(
        originLat: Double,
        originLng: Double,
        destinationLat: Double,
        destinationLng: Double
    ): LocaPinResult<List<Pair<Double, Double>>>
}
