package com.locapin.mobile.data.local

import com.locapin.mobile.domain.model.MapZone
import com.locapin.mobile.domain.model.ZonePoint
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SanJuanSeedDataSource @Inject constructor() {

    fun mapZones(): List<MapZone> = listOf(
        MapZone(
            id = "pinaglabanan",
            displayName = "Pinaglabanan",
            polygonPoints = listOf(
                ZonePoint(14.6062, 121.0296),
                ZonePoint(14.6044, 121.0339),
                ZonePoint(14.6023, 121.0350),
                ZonePoint(14.6008, 121.0318),
                ZonePoint(14.6024, 121.0281)
            ),
            centerLat = 14.6030,
            centerLng = 121.0316
        ),
        MapZone(
            id = "city_center",
            displayName = "City Center",
            polygonPoints = listOf(
                ZonePoint(14.6038, 121.0348),
                ZonePoint(14.6026, 121.0402),
                ZonePoint(14.5995, 121.0401),
                ZonePoint(14.5988, 121.0352),
                ZonePoint(14.6009, 121.0332)
            ),
            centerLat = 14.6012,
            centerLng = 121.0370
        ),
        MapZone(
            id = "greenhills",
            displayName = "Greenhills",
            polygonPoints = listOf(
                ZonePoint(14.6055, 121.0430),
                ZonePoint(14.6040, 121.0505),
                ZonePoint(14.5996, 121.0525),
                ZonePoint(14.5962, 121.0462),
                ZonePoint(14.5994, 121.0414)
            ),
            centerLat = 14.6010,
            centerLng = 121.0472
        )
    )
}
