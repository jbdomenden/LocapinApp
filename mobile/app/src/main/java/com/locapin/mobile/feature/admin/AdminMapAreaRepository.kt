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
    fun getMapAreaById(id: String): AdminMapArea?
    fun createMapArea(input: AdminMapAreaInput)
    fun updateMapArea(id: String, input: AdminMapAreaInput)
    fun deleteMapArea(id: String)
}

data class AdminMapAreaInput(
    val name: String,
    val description: String,
    val districtLabel: String,
    val centerLatitude: Double,
    val centerLongitude: Double
)

@Singleton
class InMemoryAdminMapAreaRepository @Inject constructor() : AdminMapAreaRepository {
    private val _mapAreas = MutableStateFlow(seedMapAreas())
    override val mapAreas: StateFlow<List<AdminMapArea>> = _mapAreas.asStateFlow()

    override fun getMapAreaById(id: String): AdminMapArea? =
        _mapAreas.value.firstOrNull { it.id == id }

    override fun createMapArea(input: AdminMapAreaInput) {
        val newItem = input.toMapArea(UUID.randomUUID().toString())
        _mapAreas.update { existing -> (existing + newItem).sortedBy { it.name.lowercase() } }
    }

    override fun updateMapArea(id: String, input: AdminMapAreaInput) {
        _mapAreas.update { existing ->
            existing.map { area ->
                if (area.id == id) input.toMapArea(id) else area
            }.sortedBy { it.name.lowercase() }
        }
    }

    override fun deleteMapArea(id: String) {
        _mapAreas.update { existing -> existing.filterNot { it.id == id } }
    }

    private fun AdminMapAreaInput.toMapArea(id: String): AdminMapArea = AdminMapArea(
        id = id,
        name = name,
        description = description,
        districtLabel = districtLabel,
        centerLatitude = centerLatitude,
        centerLongitude = centerLongitude
    )

    private fun seedMapAreas(): List<AdminMapArea> = listOf(
        AdminMapArea(
            id = "pinaglabanan-area",
            name = "Pinaglabanan Area",
            description = "Historic civic cluster around Pinaglabanan Shrine.",
            districtLabel = "District 1",
            centerLatitude = 14.6006,
            centerLongitude = 121.0266
        ),
        AdminMapArea(
            id = "greenhills-area",
            name = "Greenhills Area",
            description = "Commercial and lifestyle district near Greenhills Center.",
            districtLabel = "District 2",
            centerLatitude = 14.6021,
            centerLongitude = 121.0451
        ),
        AdminMapArea(
            id = "little-baguio-area",
            name = "Little Baguio Area",
            description = "Mixed residential and cultural neighborhood.",
            districtLabel = "District 1",
            centerLatitude = 14.6072,
            centerLongitude = 121.0316
        ),
        AdminMapArea(
            id = "maytunas-area",
            name = "Maytunas Area",
            description = "Community zone connecting schools and barangay services.",
            districtLabel = "District 1",
            centerLatitude = 14.5965,
            centerLongitude = 121.0334
        ),
        AdminMapArea(
            id = "balong-bato-area",
            name = "Balong-Bato Area",
            description = "Northern riverside barangay cluster.",
            districtLabel = "District 1",
            centerLatitude = 14.6152,
            centerLongitude = 121.0304
        )
    ).sortedBy { it.name.lowercase() }
}
