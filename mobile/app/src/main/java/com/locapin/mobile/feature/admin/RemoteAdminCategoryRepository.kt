package com.locapin.mobile.feature.admin

import com.locapin.mobile.data.remote.AdminCategoryRequest
import com.locapin.mobile.data.remote.CategoryApiService
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class RemoteAdminCategoryRepository @Inject constructor(
    private val categoryApiService: CategoryApiService
) : AdminCategoryRepository {
    private val _categories = MutableStateFlow(emptyList<AdminCategory>())
    override val categories: StateFlow<List<AdminCategory>> = _categories.asStateFlow()

    override fun getCategoryById(id: String): AdminCategory? =
        _categories.value.firstOrNull { it.id == id }

    override fun createCategory(input: AdminCategoryInput) {
        // Remote wiring intentionally deferred; stub keeps interface backend-ready.
    }

    override fun updateCategory(id: String, input: AdminCategoryInput) {
        // Remote wiring intentionally deferred; stub keeps interface backend-ready.
    }

    override fun deleteCategory(id: String) {
        // Remote wiring intentionally deferred; stub keeps interface backend-ready.
    }

    private fun AdminCategoryInput.toRequest() = AdminCategoryRequest(
        name = name,
        description = description
    )
}
