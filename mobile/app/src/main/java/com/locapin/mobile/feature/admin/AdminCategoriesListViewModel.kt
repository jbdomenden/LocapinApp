package com.locapin.mobile.feature.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminCategoriesListUiState(
    val searchQuery: String = "",
    val categories: List<AdminCategory> = emptyList()
)

@HiltViewModel
class AdminCategoriesListViewModel @Inject constructor(
    private val repository: AdminCategoryRepository,
    private val attractionRepository: AdminAttractionRepository
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

    fun populateFromAttractions() {
        viewModelScope.launch {
            val currentAttractions = attractionRepository.attractions.first()
            val currentCategories = repository.categories.first()
            
            val uniqueCategoryNames = currentAttractions.map { it.category }
                .filter { it.isNotBlank() }
                .distinct()
            
            uniqueCategoryNames.forEach { name ->
                if (currentCategories.none { it.name.equals(name, ignoreCase = true) }) {
                    repository.createCategory(AdminCategoryInput(name = name, description = "Automatically imported from attractions."))
                }
            }
        }
    }

    fun onSearchQueryChange(value: String) {
        searchQuery.update { value }
    }

    fun deleteCategory(id: String) {
        repository.deleteCategory(id)
    }
}
