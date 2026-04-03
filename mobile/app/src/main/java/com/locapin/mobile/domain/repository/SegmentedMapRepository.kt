package com.locapin.mobile.domain.repository

import com.locapin.mobile.core.common.LocaPinResult
import com.locapin.mobile.domain.model.MapZone
import com.locapin.mobile.domain.model.ZoneAttraction

interface SegmentedMapRepository {
    suspend fun getMapZones(): LocaPinResult<List<MapZone>>
    suspend fun getZoneAttractions(): LocaPinResult<List<ZoneAttraction>>
    suspend fun getRoutePath(
        originLat: Double,
        originLng: Double,
        destinationLat: Double,
        destinationLng: Double
    ): LocaPinResult<List<Pair<Double, Double>>>
}
