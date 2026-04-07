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

data class AdminAttractionsListUiState(
    val searchQuery: String = "",
    val attractions: List<AdminAttraction> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class AdminAttractionsListViewModel @Inject constructor(
    private val repository: AdminAttractionRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")

    val uiState: StateFlow<AdminAttractionsListUiState> = combine(
        searchQuery,
        repository.attractions,
        repository.errorMessage
    ) { query, attractions, errorMessage ->
        val filtered = if (query.isBlank()) {
            attractions
        } else {
            attractions.filter { it.name.contains(query, ignoreCase = true) }
        }
        AdminAttractionsListUiState(searchQuery = query, attractions = filtered, errorMessage = errorMessage)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AdminAttractionsListUiState()
    )

    fun onSearchQueryChange(value: String) {
        searchQuery.update { value }
    }

    fun onErrorShown() {
        repository.clearError()
    }

    fun deleteAttraction(id: String) {
        repository.deleteAttraction(id)
    }
}
