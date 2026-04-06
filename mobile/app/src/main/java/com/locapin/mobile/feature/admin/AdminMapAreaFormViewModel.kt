package com.locapin.mobile.feature.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val ARG_MAP_AREA_ID = "mapAreaId"

data class AdminMapAreaFormUiState(
    val mapAreaId: String? = null,
    val name: String = "",
    val description: String = "",
    val districtLabel: String = "",
    val centerLatitude: String = "",
    val centerLongitude: String = "",
    val errors: Map<String, String> = emptyMap()
)

@HiltViewModel
class AdminMapAreaFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: AdminMapAreaRepository
) : ViewModel() {

    private val mapAreaId: String? = savedStateHandle[ARG_MAP_AREA_ID]

    var uiState by mutableStateOf(
        mapAreaId
            ?.let(repository::getMapAreaById)
            ?.toUiState()
            ?: AdminMapAreaFormUiState(mapAreaId = mapAreaId)
    )
        private set

    fun onNameChange(value: String) = update(clearErrors = listOf("name")) { copy(name = value) }
    fun onDescriptionChange(value: String) = update { copy(description = value) }
    fun onDistrictLabelChange(value: String) = update { copy(districtLabel = value) }
    fun onCenterLatitudeChange(value: String) = update(clearErrors = listOf("centerLatitude")) { copy(centerLatitude = value) }
    fun onCenterLongitudeChange(value: String) = update(clearErrors = listOf("centerLongitude")) { copy(centerLongitude = value) }

    fun save(): Boolean {
        val validationErrors = validate(uiState)
        if (validationErrors.isNotEmpty()) {
            uiState = uiState.copy(errors = validationErrors)
            return false
        }

        val payload = AdminMapAreaInput(
            name = uiState.name.trim(),
            description = uiState.description.trim(),
            districtLabel = uiState.districtLabel.trim(),
            centerLatitude = uiState.centerLatitude.trim().toDouble(),
            centerLongitude = uiState.centerLongitude.trim().toDouble()
        )

        val id = uiState.mapAreaId
        if (id == null) repository.createMapArea(payload) else repository.updateMapArea(id, payload)
        return true
    }

    private fun validate(state: AdminMapAreaFormUiState): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (state.name.isBlank()) {
            errors["name"] = "Name is required"
        }

        val latitude = state.centerLatitude.trim().toDoubleOrNull()
        when {
            state.centerLatitude.isBlank() -> errors["centerLatitude"] = "Center latitude is required"
            latitude == null -> errors["centerLatitude"] = "Enter a valid numeric latitude"
            latitude !in -90.0..90.0 -> errors["centerLatitude"] = "Latitude must be between -90 and 90"
        }

        val longitude = state.centerLongitude.trim().toDoubleOrNull()
        when {
            state.centerLongitude.isBlank() -> errors["centerLongitude"] = "Center longitude is required"
            longitude == null -> errors["centerLongitude"] = "Enter a valid numeric longitude"
            longitude !in -180.0..180.0 -> errors["centerLongitude"] = "Longitude must be between -180 and 180"
        }

        return errors
    }

    private fun update(
        clearErrors: List<String> = emptyList(),
        transform: AdminMapAreaFormUiState.() -> AdminMapAreaFormUiState
    ) {
        uiState = uiState.transform().copy(errors = uiState.errors - clearErrors.toSet())
    }

    private fun AdminMapArea.toUiState(): AdminMapAreaFormUiState = AdminMapAreaFormUiState(
        mapAreaId = id,
        name = name,
        description = description,
        districtLabel = districtLabel,
        centerLatitude = centerLatitude.toString(),
        centerLongitude = centerLongitude.toString()
    )
}
