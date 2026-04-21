package com.locapin.mobile.feature.admin

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAdminCategoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : AdminCategoryRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _categories = MutableStateFlow<List<AdminCategory>>(emptyList())
    override val categories: StateFlow<List<AdminCategory>> = _categories.asStateFlow()

    init {
        observeCategories()
    }

    private fun observeCategories() {
        repositoryScope.launch {
            callbackFlow {
                val subscription = firestore.collection("categories")
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) return@addSnapshotListener
                        val list = snapshot?.documents?.mapNotNull { doc ->
                            val name = doc.getString("name") ?: ""
                            val description = doc.getString("description") ?: ""
                            AdminCategory(id = doc.id, name = name, description = description)
                        }.orEmpty()
                        trySend(list)
                    }
                awaitClose { subscription.remove() }
            }.collectLatest { list ->
                _categories.value = list.sortedBy { it.name.lowercase() }
            }
        }
    }

    override fun getCategoryById(id: String): AdminCategory? =
        _categories.value.firstOrNull { it.id == id }

    override fun createCategory(input: AdminCategoryInput) {
        val data = mapOf(
            "name" to input.name,
            "description" to input.description
        )
        firestore.collection("categories").add(data)
    }

    override fun updateCategory(id: String, input: AdminCategoryInput) {
        val data = mapOf(
            "name" to input.name,
            "description" to input.description
        )
        firestore.collection("categories").document(id).set(data)
    }

    override fun deleteCategory(id: String) {
        firestore.collection("categories").document(id).delete()
    }
}
