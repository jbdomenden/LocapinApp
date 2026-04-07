package com.locapin.mobile.feature.admin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val ARG_ATTRACTION_ID = "attractionId"

data class AdminAttractionFormUiState(
    val attractionId: String? = null,
    val name: String = "",
    val knownFor: String = "",
    val description: String = "",
    val category: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val area: String = "",
    val isVisible: Boolean = true,
    val errors: Map<String, String> = emptyMap()
)

@HiltViewModel
class AdminAttractionFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: AdminAttractionRepository
) : ViewModel() {

    private val attractionId: String? = savedStateHandle[ARG_ATTRACTION_ID]

    var uiState by mutableStateOf(
        attractionId
            ?.let(repository::getAttractionById)
            ?.toUiState()
            ?: AdminAttractionFormUiState(attractionId = attractionId)
    )
        private set

    fun onNameChange(value: String) = update { copy(name = value) }
    fun onKnownForChange(value: String) = update { copy(knownFor = value) }
    fun onDescriptionChange(value: String) = update { copy(description = value) }
    fun onCategoryChange(value: String) = update { copy(category = value) }
    fun onLatitudeChange(value: String) = update { copy(latitude = value) }
    fun onLongitudeChange(value: String) = update { copy(longitude = value) }
    fun onAreaChange(value: String) = update { copy(area = value) }
    fun onVisibilityChange(value: Boolean) = update { copy(isVisible = value) }

    fun save(): Boolean {
        val validationErrors = validate(uiState)
        if (validationErrors.isNotEmpty()) {
            uiState = uiState.copy(errors = validationErrors)
            return false
        }

        val payload = AdminAttractionInput(
            name = uiState.name.trim(),
            knownFor = uiState.knownFor.trim(),
            description = uiState.description.trim(),
            category = uiState.category.trim(),
            latitude = uiState.latitude.toDouble(),
            longitude = uiState.longitude.toDouble(),
            area = uiState.area.trim(),
            isVisible = uiState.isVisible
        )

        val id = uiState.attractionId
        val didSave = if (id == null) repository.createAttraction(payload) else repository.updateAttraction(id, payload)
        if (!didSave) {
            uiState = uiState.copy(
                errors = uiState.errors + ("form" to (repository.errorMessage.value ?: "Unable to save attraction."))
            )
        }
        return didSave
    }

    private fun validate(state: AdminAttractionFormUiState): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (state.name.isBlank()) errors["name"] = "Name is required"
        if (state.knownFor.isBlank()) errors["knownFor"] = "Known for is required"
        if (state.description.isBlank()) errors["description"] = "Description is required"

        val lat = state.latitude.toDoubleOrNull()
        if (state.latitude.isBlank()) {
            errors["latitude"] = "Latitude is required"
        } else if (lat == null || lat !in -90.0..90.0) {
            errors["latitude"] = "Latitude must be between -90 and 90"
        }

        val lng = state.longitude.toDoubleOrNull()
        if (state.longitude.isBlank()) {
            errors["longitude"] = "Longitude is required"
        } else if (lng == null || lng !in -180.0..180.0) {
            errors["longitude"] = "Longitude must be between -180 and 180"
        }

        return errors
    }

    private fun update(transform: AdminAttractionFormUiState.() -> AdminAttractionFormUiState) {
        uiState = uiState.transform().copy(errors = uiState.errors - setOf("name", "knownFor", "description", "latitude", "longitude", "form"))
    }

    private fun AdminAttraction.toUiState(): AdminAttractionFormUiState = AdminAttractionFormUiState(
        attractionId = id,
        name = name,
        knownFor = knownFor,
        description = description,
        category = category,
        latitude = latitude.toString(),
        longitude = longitude.toString(),
        area = area,
        isVisible = isVisible
    )
}
