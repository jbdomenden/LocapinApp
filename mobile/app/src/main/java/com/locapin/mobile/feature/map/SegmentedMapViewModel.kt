package com.locapin.mobile.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locapin.mobile.core.common.LocaPinResult
import com.locapin.mobile.core.location.LocationProvider
import com.locapin.mobile.domain.model.MapZone
import com.locapin.mobile.domain.model.ZoneAttraction
import com.locapin.mobile.domain.repository.SegmentedMapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class MapPermissionState { UNKNOWN, GRANTED, DENIED }

data class SegmentedMapUiState(
    val isLoading: Boolean = true,
    val zones: List<MapZone> = emptyList(),
    val attractions: List<ZoneAttraction> = emptyList(),
    val selectedZoneId: String? = null,
    val selectedAttractionId: String? = null,
    val navigationAttractionId: String? = null,
    val routePath: List<Pair<Double, Double>> = emptyList(),
    val userLocation: Pair<Double, Double>? = null,
    val permissionState: MapPermissionState = MapPermissionState.UNKNOWN,
    val errorMessage: String? = null
) {
    val visibleAttractions: List<ZoneAttraction>
        get() = attractions.filter { it.zoneId == selectedZoneId }

    val selectedAttraction: ZoneAttraction?
        get() = visibleAttractions.firstOrNull { it.id == selectedAttractionId } ?: visibleAttractions.firstOrNull()

    val navigationAttraction: ZoneAttraction?
        get() = visibleAttractions.firstOrNull { it.id == navigationAttractionId }
}

@HiltViewModel
class SegmentedMapViewModel @Inject constructor(
    private val repository: SegmentedMapRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {
    private val _uiState = MutableStateFlow(SegmentedMapUiState())
    val uiState: StateFlow<SegmentedMapUiState> = _uiState.asStateFlow()

    init {
        loadMapData()
    }

    fun loadMapData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val zonesResult = repository.getMapZones()
            val attractionsResult = repository.getZoneAttractions()
            val zones = (zonesResult as? LocaPinResult.Success)?.data.orEmpty()
            val attractions = (attractionsResult as? LocaPinResult.Success)?.data.orEmpty()
            val error = listOfNotNull(
                (zonesResult as? LocaPinResult.Error)?.message,
                (attractionsResult as? LocaPinResult.Error)?.message
            ).joinToString("\n").ifBlank { null }
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                zones = zones,
                attractions = attractions,
                errorMessage = error
            )
        }
    }

    fun onZoneSelected(zoneId: String) {
        val firstAttraction = _uiState.value.attractions.firstOrNull { it.zoneId == zoneId }
        _uiState.value = _uiState.value.copy(
            selectedZoneId = zoneId,
            selectedAttractionId = firstAttraction?.id,
            navigationAttractionId = null,
            routePath = emptyList(),
            errorMessage = null
        )
    }

    fun onAttractionSelected(attractionId: String) {
        _uiState.value = _uiState.value.copy(selectedAttractionId = attractionId, errorMessage = null)
    }

    fun onGoToAttraction(attractionId: String) {
        val state = _uiState.value
        if (state.permissionState != MapPermissionState.GRANTED) {
            _uiState.value = state.copy(errorMessage = "Location permission is required for in-app navigation.")
            return
        }
        viewModelScope.launch {
            val attraction = state.visibleAttractions.firstOrNull { it.id == attractionId } ?: return@launch
            val user = locationProvider.getLastKnownLocation()
            if (user == null) {
                _uiState.value = _uiState.value.copy(errorMessage = "Current GPS location unavailable.")
                return@launch
            }
            val routeResult = repository.getRoutePath(
                originLat = user.first,
                originLng = user.second,
                destinationLat = attraction.latitude,
                destinationLng = attraction.longitude
            )
            val path = (routeResult as? LocaPinResult.Success)?.data.orEmpty()
            val routeError = (routeResult as? LocaPinResult.Error)?.message
            _uiState.value = _uiState.value.copy(
                userLocation = user,
                navigationAttractionId = attractionId,
                routePath = path,
                errorMessage = routeError
            )
        }
    }

    fun onGoToAttraction(attractionId: String) {
        _uiState.value = _uiState.value.copy(navigationAttractionId = attractionId)
    }

    fun onPermissionResult(granted: Boolean) {
        _uiState.value = _uiState.value.copy(
            permissionState = if (granted) MapPermissionState.GRANTED else MapPermissionState.DENIED
        )
        if (granted) refreshLocation()
    }

    fun refreshLocation() {
        viewModelScope.launch {
            val user = locationProvider.getLastKnownLocation()
            _uiState.value = _uiState.value.copy(
                userLocation = user,
                errorMessage = if (user == null) "Current GPS location unavailable." else _uiState.value.errorMessage
            )
            val nav = _uiState.value.navigationAttraction
            if (user != null && nav != null) {
                val routeResult = repository.getRoutePath(
                    originLat = user.first,
                    originLng = user.second,
                    destinationLat = nav.latitude,
                    destinationLng = nav.longitude
                )
                _uiState.value = _uiState.value.copy(
                    routePath = (routeResult as? LocaPinResult.Success)?.data.orEmpty(),
                    errorMessage = (routeResult as? LocaPinResult.Error)?.message
                )
            }
        }
    }

    fun distanceTextFor(attraction: ZoneAttraction): String {
        val user = _uiState.value.userLocation
        return when {
            _uiState.value.permissionState != MapPermissionState.GRANTED -> "Location permission required to calculate distance"
            user == null -> "Current location unavailable"
            else -> formatDistanceMeters(distanceMeters(user.first, user.second, attraction.latitude, attraction.longitude))
        }
    }

    private fun distanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val result = FloatArray(1)
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, result)
        return result[0]
    }

    private fun formatDistanceMeters(meters: Float): String {
        return if (meters < 1000) "${meters.roundToInt()} m away"
        else "${"%.1f".format(meters / 1000f)} km away"
    }
}
