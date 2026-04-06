package com.locapin.mobile.feature.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class AdminMapAreasListUiState(
    val searchQuery: String = "",
    val mapAreas: List<AdminMapArea> = emptyList()
)

@HiltViewModel
class AdminMapAreasListViewModel @Inject constructor(
    private val repository: AdminMapAreaRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")

    val uiState: StateFlow<AdminMapAreasListUiState> = combine(
        searchQuery,
        repository.mapAreas
    ) { query, mapAreas ->
        val filtered = if (query.isBlank()) {
            mapAreas
        } else {
            mapAreas.filter { it.name.contains(query, ignoreCase = true) }
        }
        AdminMapAreasListUiState(searchQuery = query, mapAreas = filtered)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AdminMapAreasListUiState()
    )

    fun onSearchQueryChange(value: String) {
        searchQuery.update { value }
    }

    fun deleteMapArea(id: String) {
        repository.deleteMapArea(id)
    }
}
