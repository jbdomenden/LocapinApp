package com.locapin.mobile.feature.admin

import com.locapin.mobile.data.remote.MapAreaApiService
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
        _mapAreas.update { current ->
            current.map { 
                if (it.id == id) it.copy(isPremium = !it.isPremium) else it
            }
        }
    }
}
