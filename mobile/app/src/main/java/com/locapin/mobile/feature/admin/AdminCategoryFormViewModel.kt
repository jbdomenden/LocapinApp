package com.locapin.mobile.feature.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val ARG_CATEGORY_ID = "categoryId"

data class AdminCategoryFormUiState(
    val categoryId: String? = null,
    val name: String = "",
    val description: String = "",
    val errors: Map<String, String> = emptyMap()
)

@HiltViewModel
class AdminCategoryFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: AdminCategoryRepository
) : ViewModel() {

    private val categoryId: String? = savedStateHandle[ARG_CATEGORY_ID]

    var uiState by mutableStateOf(
        categoryId
            ?.let(repository::getCategoryById)
            ?.toUiState()
            ?: AdminCategoryFormUiState(categoryId = categoryId)
    )
        private set

    fun onNameChange(value: String) = update { copy(name = value) }
    fun onDescriptionChange(value: String) = update { copy(description = value) }

    fun save(): Boolean {
        val validationErrors = validate(uiState)
        if (validationErrors.isNotEmpty()) {
            uiState = uiState.copy(errors = validationErrors)
            return false
        }

        val payload = AdminCategoryInput(
            name = uiState.name.trim(),
            description = uiState.description.trim()
        )

        val id = uiState.categoryId
        if (id == null) repository.createCategory(payload) else repository.updateCategory(id, payload)
        return true
    }

    private fun validate(state: AdminCategoryFormUiState): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        if (state.name.isBlank()) errors["name"] = "Name is required"
        return errors
    }

    private fun update(transform: AdminCategoryFormUiState.() -> AdminCategoryFormUiState) {
        uiState = uiState.transform().copy(errors = uiState.errors - "name")
    }

    private fun AdminCategory.toUiState(): AdminCategoryFormUiState = AdminCategoryFormUiState(
        categoryId = id,
        name = name,
        description = description
    )
}
