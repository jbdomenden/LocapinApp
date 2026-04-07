package com.locapin.mobile.feature.admin

import com.locapin.mobile.data.remote.AdminAttractionRequest
import com.locapin.mobile.data.remote.AttractionApiService
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class RemoteAdminAttractionRepository @Inject constructor(
    private val attractionApiService: AttractionApiService
) : AdminAttractionRepository {
    private val _attractions = MutableStateFlow(emptyList<AdminAttraction>())
    override val attractions: StateFlow<List<AdminAttraction>> = _attractions.asStateFlow()

    override fun getAttractionById(id: String): AdminAttraction? =
        _attractions.value.firstOrNull { it.id == id }

    override fun createAttraction(input: AdminAttractionInput) {
        // Remote wiring intentionally deferred; stub keeps interface backend-ready.
    }

    override fun updateAttraction(id: String, input: AdminAttractionInput) {
        // Remote wiring intentionally deferred; stub keeps interface backend-ready.
    }

    override fun deleteAttraction(id: String) {
        // Remote wiring intentionally deferred; stub keeps interface backend-ready.
    }

    private fun AdminAttractionInput.toRequest() = AdminAttractionRequest(
        name = name,
        knownFor = knownFor,
        description = description,
        category = category,
        latitude = latitude,
        longitude = longitude,
        area = area,
        isVisible = isVisible
    )
}
