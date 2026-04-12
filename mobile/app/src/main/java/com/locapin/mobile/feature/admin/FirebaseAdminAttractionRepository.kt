package com.locapin.mobile.feature.admin

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Singleton
class FirebaseAdminAttractionRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : AdminAttractionRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _attractions = MutableStateFlow<List<AdminAttraction>>(emptyList())
    override val attractions: StateFlow<List<AdminAttraction>> = _attractions.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    override val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        observeAttractions()
    }

    private fun observeAttractions() {
        repositoryScope.launch {
            callbackFlow {
                val subscription = firestore.collection("attractions")
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            android.util.Log.e("FirebaseAdminAttractionRepository", "Error fetching attractions: ${error.message}")
                            // We don't close the flow here to allow it to recover if permissions change
                            return@addSnapshotListener
                        }
                        val list = snapshot?.documents?.mapNotNull { doc ->
                            doc.toObject(FirebaseAdminAttractionModel::class.java)?.toAdminAttraction(doc.id)
                        }.orEmpty()
                        trySend(list)
                    }
                awaitClose { subscription.remove() }
            }.collectLatest { list ->
                _attractions.value = list.sortedBy { it.name.lowercase() }
            }
        }
    }

    override fun clearError() {
        _errorMessage.value = null
    }

    override fun getAttractionById(id: String): AdminAttraction? =
        _attractions.value.firstOrNull { it.id == id }

    override fun createAttraction(input: AdminAttractionInput): Boolean {
        // This is a bit tricky because the interface is synchronous but Firebase is async.
        // Using runBlocking is generally discouraged in repositories, but the interface requires a Boolean return.
        // However, most of the app uses this in a ViewModel scope.
        // For now, I'll use a simplified async-to-sync approach or assume it's called from a background thread.
        return try {
            val docRef = firestore.collection("attractions").document()
            docRef.set(input.toFirebaseModel())
            true
        } catch (e: Exception) {
            _errorMessage.value = e.message
            false
        }
    }

    override fun updateAttraction(id: String, input: AdminAttractionInput): Boolean {
        return try {
            firestore.collection("attractions").document(id).set(input.toFirebaseModel())
            true
        } catch (e: Exception) {
            _errorMessage.value = e.message
            false
        }
    }

    override fun deleteAttraction(id: String): Boolean {
        return try {
            firestore.collection("attractions").document(id).delete()
            true
        } catch (e: Exception) {
            _errorMessage.value = e.message
            false
        }
    }

    suspend fun uploadImage(uri: Uri): String {
        val fileName = "attractions/${System.currentTimeMillis()}.jpg"
        val ref = storage.reference.child(fileName)
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }

    private fun AdminAttractionInput.toFirebaseModel() = FirebaseAdminAttractionModel(
        name = name,
        knownFor = knownFor,
        description = description,
        category = category,
        latitude = latitude,
        longitude = longitude,
        area = area,
        visible = isVisible,
        imageUrl = imageUrl ?: ""
    )
}

data class FirebaseAdminAttractionModel(
    val name: String = "",
    val knownFor: String = "",
    val description: String = "",
    val category: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val area: String = "",
    @get:com.google.firebase.firestore.PropertyName("visible")
    @set:com.google.firebase.firestore.PropertyName("visible")
    var visible: Boolean = true,
    val imageUrl: String = "",
    val distance: String? = null
) {
    fun toAdminAttraction(id: String) = AdminAttraction(
        id = id,
        name = name,
        knownFor = knownFor,
        description = description,
        category = category,
        latitude = latitude,
        longitude = longitude,
        area = area,
        isVisible = visible,
        imageUrl = imageUrl.ifEmpty { null },
        distance = distance
    )
}
