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
    val message: String? = null
)

@HiltViewModel
class AdminMapAreasListViewModel @Inject constructor(
    private val repository: AdminMapAreaRepository,
    private val attractionRepository: AdminAttractionRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val isImporting = MutableStateFlow(false)
    private val message = MutableStateFlow<String?>(null)

    val uiState: StateFlow<AdminMapAreasListUiState> = combine(
        searchQuery,
        repository.mapAreas,
        isImporting,
        message
    ) { query, mapAreas, importing, msg ->
        val filtered = if (query.isBlank()) {
            mapAreas
        } else {
            mapAreas.filter { it.name.contains(query, ignoreCase = true) }
        }
        AdminMapAreasListUiState(
            searchQuery = query,
            mapAreas = filtered,
            isImporting = importing,
            message = msg
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
        repository.togglePremium(id)
    }
}
