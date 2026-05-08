package com.locapin.mobile.feature.admin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
    val imageUrl: String = "",
    val isVisible: Boolean = true,
    val isUploading: Boolean = false,
    val rating: String = "0.0",
    val reviews: String = "0",
    val errors: Map<String, String> = emptyMap()
)

@HiltViewModel
class AdminAttractionFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: AdminAttractionRepository,
    categoryRepository: AdminCategoryRepository,
    areaRepository: AdminMapAreaRepository
) : ViewModel() {

    private val attractionId: String? = savedStateHandle[ARG_ATTRACTION_ID]

    val categories: StateFlow<List<AdminCategory>> = categoryRepository.categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val areas: StateFlow<List<AdminMapArea>> = areaRepository.mapAreas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
    fun onImageUrlChange(value: String) = update { copy(imageUrl = value) }
    fun onVisibilityChange(value: Boolean) = update { copy(isVisible = value) }
    fun onRatingChange(value: String) = update { copy(rating = value) }
    fun onReviewsChange(value: String) = update { copy(reviews = value) }

    fun onImageSelected(uri: android.net.Uri) {
        viewModelScope.launch {
            uiState = uiState.copy(isUploading = true)
            val downloadUrl = repository.uploadImage(uri)
            if (downloadUrl != null) {
                uiState = uiState.copy(imageUrl = downloadUrl, isUploading = false)
            } else {
                uiState = uiState.copy(
                    isUploading = false,
                    errors = uiState.errors + ("imageUrl" to "Failed to upload image")
                )
            }
        }
    }

    private fun calculateDistanceFromSTIMesa(lat: Double, lng: Double): String {
        // STI College Sta. Mesa Coordinates (Approximate)
        val stiLat = 14.6009
        val stiLng = 121.0117

        val earthRadius = 6371.0 // kilometers
        val dLat = Math.toRadians(lat - stiLat)
        val dLng = Math.toRadians(lng - stiLng)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(stiLat)) * Math.cos(Math.toRadians(lat)) *
                Math.sin(dLng / 2) * Math.sin(dLng / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val distanceKm = earthRadius * c

        return "%.1f km".format(java.util.Locale.US, distanceKm)
    }

    fun save(): Boolean {
        val validationErrors = validate(uiState)
        if (validationErrors.isNotEmpty()) {
            uiState = uiState.copy(errors = validationErrors)
            return false
        }

        val lat = uiState.latitude.toDouble()
        val lng = uiState.longitude.toDouble()
        val distance = calculateDistanceFromSTIMesa(lat, lng)

        val payload = AdminAttractionInput(
            name = uiState.name.trim(),
            knownFor = uiState.knownFor.trim(),
            description = uiState.description.trim(),
            category = uiState.category.trim(),
            latitude = lat,
            longitude = lng,
            area = uiState.area.trim(),
            isVisible = uiState.isVisible,
            imageUrl = uiState.imageUrl.trim().ifEmpty { null },
            distance = distance,
            rating = uiState.rating.toDoubleOrNull() ?: 0.0,
            reviews = uiState.reviews.toIntOrNull() ?: 0
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
        if (state.category.isBlank()) errors["category"] = "Category is required"

        val lat = state.latitude.toDoubleOrNull()
        if (state.latitude.isBlank()) {
            errors["latitude"] = "Latitude is required"
        } else if (lat == null || lat !in -90.0..1000.0) {
            errors["latitude"] = "Latitude must be a valid number"
        }

        val lng = state.longitude.toDoubleOrNull()
        if (state.longitude.isBlank()) {
            errors["longitude"] = "Longitude is required"
        } else if (lng == null || lng !in -180.0..1000.0) {
            errors["longitude"] = "Longitude must be a valid number"
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
        imageUrl = imageUrl ?: "",
        isVisible = isVisible,
        rating = rating.toString(),
        reviews = reviews.toString()
    )
}
