package com.locapin.mobile.feature.admin

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface AdminCategoryRepository {
    val categories: StateFlow<List<AdminCategory>>
    fun getCategoryById(id: String): AdminCategory?
    fun createCategory(input: AdminCategoryInput)
    fun updateCategory(id: String, input: AdminCategoryInput)
    fun deleteCategory(id: String)
}

data class AdminCategoryInput(
    val name: String,
    val description: String
)

@Singleton
class InMemoryAdminCategoryRepository @Inject constructor() : AdminCategoryRepository {
    private val _categories = MutableStateFlow(seedCategories())
    override val categories: StateFlow<List<AdminCategory>> = _categories.asStateFlow()

    override fun getCategoryById(id: String): AdminCategory? =
        _categories.value.firstOrNull { it.id == id }

    override fun createCategory(input: AdminCategoryInput) {
        val newItem = AdminCategory(
            id = UUID.randomUUID().toString(),
            name = input.name,
            description = input.description
        )
        _categories.update { existing -> (existing + newItem).sortedBy { it.name.lowercase() } }
    }

    override fun updateCategory(id: String, input: AdminCategoryInput) {
        _categories.update { existing ->
            existing.map { category ->
                if (category.id == id) {
                    category.copy(name = input.name, description = input.description)
                } else {
                    category
                }
            }.sortedBy { it.name.lowercase() }
        }
    }

    override fun deleteCategory(id: String) {
        _categories.update { existing -> existing.filterNot { it.id == id } }
    }

    private fun seedCategories(): List<AdminCategory> = listOf(
        AdminCategory(
            id = "historical",
            name = "Historical",
            description = "Landmarks, shrines, and heritage sites in San Juan."
        ),
        AdminCategory(
            id = "museum",
            name = "Museum",
            description = "Museums featuring city culture, art, and history."
        ),
        AdminCategory(
            id = "art",
            name = "Art",
            description = "Galleries and spaces for modern and local art."
        ),
        AdminCategory(
            id = "shopping",
            name = "Shopping",
            description = "Retail and lifestyle destinations for visitors."
        ),
        AdminCategory(
            id = "park",
            name = "Park",
            description = "Parks and open public recreation spaces."
        )
    ).sortedBy { it.name.lowercase() }
}
