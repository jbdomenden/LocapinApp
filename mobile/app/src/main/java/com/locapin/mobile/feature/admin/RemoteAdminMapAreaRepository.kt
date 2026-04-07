package com.locapin.mobile.feature.admin

import com.locapin.mobile.data.remote.AdminMapAreaRequest
import com.locapin.mobile.data.remote.MapAreaApiService
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class RemoteAdminMapAreaRepository @Inject constructor(
    private val mapAreaApiService: MapAreaApiService
) : AdminMapAreaRepository {
    private val _mapAreas = MutableStateFlow(emptyList<AdminMapArea>())
    override val mapAreas: StateFlow<List<AdminMapArea>> = _mapAreas.asStateFlow()

    override fun getMapAreaById(id: String): AdminMapArea? =
        _mapAreas.value.firstOrNull { it.id == id }

    override fun createMapArea(input: AdminMapAreaInput) {
        // Remote wiring intentionally deferred; stub keeps interface backend-ready.
    }

    override fun updateMapArea(id: String, input: AdminMapAreaInput) {
        // Remote wiring intentionally deferred; stub keeps interface backend-ready.
    }

    override fun deleteMapArea(id: String) {
        // Remote wiring intentionally deferred; stub keeps interface backend-ready.
    }

    private fun AdminMapAreaInput.toRequest() = AdminMapAreaRequest(
        name = name,
        description = description,
        districtLabel = districtLabel,
        centerLatitude = centerLatitude,
        centerLongitude = centerLongitude
    )
}
