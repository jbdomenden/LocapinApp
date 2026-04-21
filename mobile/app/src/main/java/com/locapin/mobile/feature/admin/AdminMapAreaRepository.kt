package com.locapin.mobile.feature.admin

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface AdminMapAreaRepository {
    val mapAreas: StateFlow<List<AdminMapArea>>
    fun togglePremium(id: String)
}

@Singleton
class InMemoryAdminMapAreaRepository @Inject constructor() : AdminMapAreaRepository {
    private val _mapAreas = MutableStateFlow(seedMapAreas())
    override val mapAreas: StateFlow<List<AdminMapArea>> = _mapAreas.asStateFlow()

    override fun togglePremium(id: String) {
        _mapAreas.update { existing ->
            existing.map { area ->
                if (area.id == id) area.copy(isPremium = !area.isPremium) else area
            }
        }
    }

    private fun seedMapAreas(): List<AdminMapArea> = listOf(
        AdminMapArea(
            id = "greenhills",
            name = "Greenhills",
            description = "Premier shopping and commercial district.",
            districtLabel = "District 2",
            centerLatitude = 14.6019,
            centerLongitude = 121.0446,
            polygonPoints = "470,215;550,165;630,125;690,160;715,230;810,215;920,315;830,335;750,330;650,410;570,490;530,480;470,320",
            hexColor = "#C7E2B0"
        ),
        AdminMapArea(
            id = "addition-hills",
            name = "Addition Hills",
            description = "Historic residential and civic area.",
            districtLabel = "District 1",
            centerLatitude = 14.5965,
            centerLongitude = 121.0334,
            polygonPoints = "470,460;530,480;570,490;545,560;515,585;460,580;395,575;345,605;315,565;260,535;325,540;430,560;470,540",
            hexColor = "#F0E6D2"
        ),
        AdminMapArea(
            id = "little-baguio",
            name = "Little Baguio",
            description = "Cultural and residential hub.",
            districtLabel = "District 1",
            centerLatitude = 14.6072,
            centerLongitude = 121.0316,
            polygonPoints = "350,260;460,280;470,320;550,460;520,485;490,385;450,315;380,345",
            hexColor = "#E9B7B7"
        ),
        AdminMapArea(
            id = "pasadena",
            name = "Pasadena",
            description = "Residential area and home to Ronac Art Center.",
            districtLabel = "District 1",
            centerLatitude = 14.6103,
            centerLongitude = 121.0385,
            polygonPoints = "300,175;340,175;370,140;420,180;460,230;460,280;350,260;310,215",
            hexColor = "#C9DFEE"
        ),
        AdminMapArea(
            id = "batis",
            name = "Batis",
            description = "Central residential community near Club Filipino.",
            districtLabel = "District 1",
            centerLatitude = 14.6035,
            centerLongitude = 121.0210,
            polygonPoints = "95,285;120,345;180,365;225,415;280,480;220,570;180,480;55,365;55,315",
            hexColor = "#C9DFEE"
        ),
        AdminMapArea(
            id = "kabayanan",
            name = "Kabayanan",
            description = "Historical heart of San Juan, home to Museo ng Katipunan.",
            districtLabel = "District 1",
            centerLatitude = 14.6042,
            centerLongitude = 121.0287,
            polygonPoints = "280,480;330,440;335,435;395,405;470,460;360,520",
            hexColor = "#F0E6D2"
        ),
        AdminMapArea(
            id = "santa-lucia",
            name = "Santa Lucia",
            description = "Quiet residential area featuring Santolan Town Plaza.",
            districtLabel = "District 1",
            centerLatitude = 14.6105,
            centerLongitude = 121.0234,
            polygonPoints = "380,320;380,345;450,315;490,385;395,405;385,365",
            hexColor = "#F0F4A4"
        ),
        AdminMapArea(
            id = "balong-bato",
            name = "Balong-Bato",
            description = "Northern riverside barangay cluster.",
            districtLabel = "District 1",
            centerLatitude = 14.6152,
            centerLongitude = 121.0304,
            polygonPoints = "150,175;185,145;220,140;260,150;300,175;310,215;280,260;180,230",
            hexColor = "#D7D3E4"
        ),
        AdminMapArea(
            id = "salapan",
            name = "Salapan",
            description = "Western boundary area.",
            districtLabel = "District 1",
            centerLatitude = 14.6105,
            centerLongitude = 121.0210,
            polygonPoints = "140,60;175,55;220,75;280,80;260,150;220,140;185,145;150,175;110,160;120,100",
            hexColor = "#F0F4A4"
        ),
        AdminMapArea(
            id = "ermitaño",
            name = "Ermitaño",
            description = "Mixed-use residential zone.",
            districtLabel = "District 1",
            centerLatitude = 14.6120,
            centerLongitude = 121.0250,
            polygonPoints = "280,80;340,95;370,140;340,175;300,175;260,150",
            hexColor = "#F4D2C1"
        )
    ).sortedBy { it.name.lowercase() }
}
