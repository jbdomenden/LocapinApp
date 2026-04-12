package com.locapin.mobile.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.locapin.mobile.domain.model.ZoneAttraction
import com.locapin.mobile.domain.repository.HistoryRepository
import com.locapin.mobile.domain.repository.VisitedAttraction
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseHistoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : HistoryRepository {

    override val history: Flow<List<VisitedAttraction>> = callbackFlow {
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            trySend(emptyList())
            val authListener = FirebaseAuth.AuthStateListener { auth ->
                val newUserId = auth.currentUser?.uid
                if (newUserId != null) {
                    // Logic to restart flow or handle listener re-attachment
                }
            }
            firebaseAuth.addAuthStateListener(authListener)
            awaitClose { firebaseAuth.removeAuthStateListener(authListener) }
            return@callbackFlow
        }

        val registration = firestore.collection("users").document(userId)
            .collection("history")
            .orderBy("visitedAtEpochMs", Query.Direction.DESCENDING)
            .limit(100)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("FirebaseHistoryRepository", "Error fetching history: ${error.message}")
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(FirebaseVisitedAttractionModel::class.java)?.toDomain(doc.id)
                }.orEmpty()
                trySend(list)
            }
        awaitClose { registration.remove() }
    }

    override suspend fun recordVisit(attraction: ZoneAttraction) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val historyRef = firestore.collection("users").document(userId).collection("history")
        
        val model = FirebaseVisitedAttractionModel(
            id = attraction.id,
            name = attraction.name,
            description = attraction.description ?: "",
            knownFor = attraction.knownFor,
            latitude = attraction.latitude,
            longitude = attraction.longitude,
            visitedAtEpochMs = System.currentTimeMillis(),
            category = attraction.category ?: "",
            area = attraction.area ?: ""
        )
        
        // Use ID as document name to avoid duplicates
        historyRef.document(attraction.id).set(model).await()
    }
}

data class FirebaseVisitedAttractionModel(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val knownFor: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val visitedAtEpochMs: Long = 0,
    val category: String = "",
    val area: String = ""
) {
    fun toDomain(docId: String) = VisitedAttraction(
        id = id.ifBlank { docId },
        name = name,
        description = description,
        knownFor = knownFor,
        latitude = latitude,
        longitude = longitude,
        visitedAtEpochMs = visitedAtEpochMs,
        category = category,
        area = area
    )
}
