package com.locapin.mobile.feature.admin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val ARG_AREA_ID = "areaId"

data class AdminMapAreaFormUiState(
    val areaId: String? = null,
    val name: String = "",
    val description: String = "",
    val districtLabel: String = "",
    val centerLatitude: String = "",
    val centerLongitude: String = "",
    val polygonPoints: String = "",
    val pathData: String = "",
    val hexColor: String = "#F0F4A4",
    val isPremium: Boolean = false,
    val gridRotation: String = "45",
    val gridDensity: String = "30",
    val isLoading: Boolean = false,
    val errors: Map<String, String> = emptyMap()
)

@HiltViewModel
class AdminMapAreaFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: AdminMapAreaRepository
) : ViewModel() {

    private val areaId: String? = savedStateHandle[ARG_AREA_ID]

    var uiState by mutableStateOf(AdminMapAreaFormUiState(areaId = areaId))
        private set

    init {
        if (areaId != null) {
            loadArea(id = areaId)
        }
    }

    private fun loadArea(id: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            val area = repository.mapAreas.value.find { it.id == id }
            if (area != null) {
                uiState = area.toUiState()
            }
            uiState = uiState.copy(isLoading = false)
        }
    }

    fun onNameChange(value: String) = update { copy(name = value) }
    fun onDescriptionChange(value: String) = update { copy(description = value) }
    fun onDistrictLabelChange(value: String) = update { copy(districtLabel = value) }
    fun onCenterLatitudeChange(value: String) = update { copy(centerLatitude = value) }
    fun onCenterLongitudeChange(value: String) = update { copy(centerLongitude = value) }
    fun onPolygonPointsChange(value: String) = update { copy(polygonPoints = value) }
    fun onPathDataChange(value: String) = update { copy(pathData = value) }
    fun onHexColorChange(value: String) = update { copy(hexColor = value) }
    fun onIsPremiumChange(value: Boolean) = update { copy(isPremium = value) }
    fun onGridRotationChange(value: String) = update { copy(gridRotation = value) }
    fun onGridDensityChange(value: String) = update { copy(gridDensity = value) }

    fun save(): Boolean {
        val validationErrors = validate(uiState)
        if (validationErrors.isNotEmpty()) {
            uiState = uiState.copy(errors = validationErrors)
            return false
        }

        val area = AdminMapArea(
            id = uiState.areaId ?: "", // Repository handles ID generation for new areas usually
            name = uiState.name.trim(),
            description = uiState.description.trim(),
            districtLabel = uiState.districtLabel.trim(),
            centerLatitude = uiState.centerLatitude.toDouble(),
            centerLongitude = uiState.centerLongitude.toDouble(),
            polygonPoints = uiState.polygonPoints.trim(),
            pathData = uiState.pathData.trim(),
            hexColor = uiState.hexColor.trim(),
            isPremium = uiState.isPremium,
            gridRotation = uiState.gridRotation.toFloatOrNull() ?: 45f,
            gridDensity = uiState.gridDensity.toFloatOrNull() ?: 30f
        )

        if (uiState.areaId == null) {
            repository.addMapArea(area)
        } else {
            repository.updateMapArea(area)
        }
        return true
    }

    private fun validate(state: AdminMapAreaFormUiState): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        if (state.name.isBlank()) errors["name"] = "Name is required"
        if (state.centerLatitude.toDoubleOrNull() == null) errors["centerLatitude"] = "Invalid latitude"
        if (state.centerLongitude.toDoubleOrNull() == null) errors["centerLongitude"] = "Invalid longitude"
        if (state.polygonPoints.isBlank() && state.pathData.isBlank()) {
            errors["polygonPoints"] = "Either polygon points or path data is required"
        }
        return errors
    }

    private fun update(transform: AdminMapAreaFormUiState.() -> AdminMapAreaFormUiState) {
        uiState = uiState.transform().copy(errors = emptyMap())
    }

    private fun AdminMapArea.toUiState() = AdminMapAreaFormUiState(
        areaId = id,
        name = name,
        description = description,
        districtLabel = districtLabel,
        centerLatitude = centerLatitude.toString(),
        centerLongitude = centerLongitude.toString(),
        polygonPoints = polygonPoints,
        pathData = pathData,
        hexColor = hexColor,
        isPremium = isPremium,
        gridRotation = gridRotation.toString(),
        gridDensity = gridDensity.toString()
    )
}

