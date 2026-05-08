package com.locapin.mobile.feature.admin

import com.locapin.mobile.data.remote.MapAreaApiService
import com.locapin.mobile.data.remote.AdminMapAreaRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Singleton
class RemoteAdminMapAreaRepository @Inject constructor(
    private val mapAreaApiService: MapAreaApiService
) : AdminMapAreaRepository {
    private val _mapAreas = MutableStateFlow<List<AdminMapArea>>(emptyList())
    override val mapAreas: StateFlow<List<AdminMapArea>> = _mapAreas.asStateFlow()
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        refresh()
    }

    private fun refresh() {
        scope.launch {
            try {
                val response = mapAreaApiService.getMapAreas()
                if (response.data != null) {
                    val areas = response.data.map { dto ->
                        AdminMapArea(
                            id = dto.id,
                            name = dto.displayName,
                            description = "",
                            districtLabel = "",
                            centerLatitude = dto.centerLat,
                            centerLongitude = dto.centerLng,
                            polygonPoints = dto.polygonPoints.joinToString(";") { "${it.lng},${it.lat}" },
                            hexColor = dto.hexColor ?: "#F0F4A4",
                            isPremium = dto.isPremium ?: false
                        )
                    }
                    _mapAreas.update { areas }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    override fun togglePremium(id: String) {
        scope.launch {
            try {
                val response = mapAreaApiService.togglePremium(id)
                if (response.data != null) {
                    _mapAreas.update { current ->
                        current.map { 
                            if (it.id == id) it.copy(isPremium = response.data.isPremium ?: false) else it
                        }
                    }
                }
            } catch (e: Exception) {
                // Fallback or error handling
            }
        }
    }

    override fun addMapArea(area: AdminMapArea) {
        scope.launch {
            try {
                val request = AdminMapAreaRequest(
                    name = area.name,
                    description = area.description,
                    districtLabel = area.districtLabel,
                    centerLatitude = area.centerLatitude,
                    centerLongitude = area.centerLongitude,
                    polygonPoints = area.polygonPoints,
                    hexColor = area.hexColor
                )
                val response = mapAreaApiService.createMapArea(request)
                if (response.data != null) {
                    refresh()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    override fun updateMapArea(area: AdminMapArea) {
        scope.launch {
            try {
                val request = AdminMapAreaRequest(
                    name = area.name,
                    description = area.description,
                    districtLabel = area.districtLabel,
                    centerLatitude = area.centerLatitude,
                    centerLongitude = area.centerLongitude,
                    polygonPoints = area.polygonPoints,
                    hexColor = area.hexColor
                )
                val response = mapAreaApiService.updateMapArea(area.id, request)
                if (response.data != null) {
                    refresh()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    override fun deleteMapArea(id: String) {
        scope.launch {
            try {
                mapAreaApiService.deleteMapArea(id)
                refresh()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
