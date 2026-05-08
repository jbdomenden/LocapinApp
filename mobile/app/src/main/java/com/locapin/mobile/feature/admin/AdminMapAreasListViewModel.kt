package com.locapin.mobile.feature.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.locapin.mobile.feature.map.SanJuanMapData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminMapAreasListUiState(
    val searchQuery: String = "",
    val mapAreas: List<AdminMapArea> = emptyList(),
    val isImporting: Boolean = false,
    val message: String? = null,
    val pendingPremiumToggles: Map<String, Boolean> = emptyMap()
)

@HiltViewModel
class AdminMapAreasListViewModel @Inject constructor(
    private val repository: AdminMapAreaRepository,
    private val attractionRepository: AdminAttractionRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val isImporting = MutableStateFlow(false)
    private val message = MutableStateFlow<String?>(null)
    private val pendingPremiumToggles = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    val uiState: StateFlow<AdminMapAreasListUiState> = combine(
        searchQuery,
        repository.mapAreas,
        isImporting,
        message,
        pendingPremiumToggles
    ) { query, mapAreas, importing, msg, pending ->
        val filtered = if (query.isBlank()) {
            mapAreas
        } else {
            mapAreas.filter { it.name.contains(query, ignoreCase = true) }
        }
        
        // Apply pending toggles to the displayed list
        val updatedAreas = filtered.map { area ->
            pending[area.id]?.let { area.copy(isPremium = it) } ?: area
        }

        AdminMapAreasListUiState(
            searchQuery = query,
            mapAreas = updatedAreas,
            isImporting = importing,
            message = msg,
            pendingPremiumToggles = pending
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AdminMapAreasListUiState()
    )

    fun onSearchQueryChange(value: String) {
        searchQuery.update { value }
    }

    fun clearMessage() {
        message.update { null }
    }

    fun togglePremium(id: String) {
        viewModelScope.launch {
            val areas = repository.mapAreas.value
            val area = areas.find { it.id == id } ?: return@launch
            val originalValue = area.isPremium
            
            pendingPremiumToggles.update { current ->
                val newValue = !(current[id] ?: originalValue)
                if (newValue == originalValue) {
                    current - id
                } else {
                    current + (id to newValue)
                }
            }
        }
    }

    fun savePremiumChanges() {
        val changes = pendingPremiumToggles.value
        if (changes.isEmpty()) return

        viewModelScope.launch {
            changes.forEach { (id, isPremium) ->
                val currentAreas = repository.mapAreas.value
                val area = currentAreas.find { it.id == id }
                if (area != null && area.isPremium != isPremium) {
                    repository.togglePremium(id)
                }
            }
            pendingPremiumToggles.update { emptyMap() }
            message.update { "Premium settings saved successfully" }
        }
    }

    fun deleteMapArea(id: String) {
        viewModelScope.launch {
            repository.deleteMapArea(id)
            message.update { "Map area deleted successfully" }
        }
    }
}
