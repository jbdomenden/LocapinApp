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

data class AdminCategoriesListUiState(
    val searchQuery: String = "",
    val categories: List<AdminCategory> = emptyList()
)

@HiltViewModel
class AdminCategoriesListViewModel @Inject constructor(
    private val repository: AdminCategoryRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")

    val uiState: StateFlow<AdminCategoriesListUiState> = combine(
        searchQuery,
        repository.categories
    ) { query, categories ->
        val filtered = if (query.isBlank()) {
            categories
        } else {
            categories.filter { it.name.contains(query, ignoreCase = true) }
        }
        AdminCategoriesListUiState(searchQuery = query, categories = filtered)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AdminCategoriesListUiState()
    )

    fun onSearchQueryChange(value: String) {
        searchQuery.update { value }
    }

    fun deleteCategory(id: String) {
        repository.deleteCategory(id)
    }
}
