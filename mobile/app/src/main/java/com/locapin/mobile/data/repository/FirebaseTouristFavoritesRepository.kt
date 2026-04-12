package com.locapin.mobile.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.locapin.mobile.domain.repository.TouristFavoritesRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Singleton
class FirebaseTouristFavoritesRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : TouristFavoritesRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())

    override val favoriteIds: StateFlow<Set<String>> = _favoriteIds
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = emptySet()
        )

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        scope.launch {
            // Wait for user to be logged in
            firebaseAuth.addAuthStateListener { auth ->
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val docRef = firestore.collection("users").document(userId)
                    docRef.addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            android.util.Log.e("FirebaseTouristFavoritesRepository", "Error fetching favorites: ${error.message}")
                            return@addSnapshotListener
                        }
                        if (snapshot != null && snapshot.exists()) {
                            val favorites = snapshot.get("favorites") as? List<String>
                            _favoriteIds.value = favorites?.toSet() ?: emptySet()
                        } else {
                            _favoriteIds.value = emptySet()
                        }
                    }
                } else {
                    _favoriteIds.value = emptySet()
                }
            }
        }
    }

    override suspend fun setFavorite(id: String, save: Boolean) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val docRef = firestore.collection("users").document(userId)
        
        try {
            if (save) {
                docRef.update("favorites", FieldValue.arrayUnion(id)).await()
            } else {
                docRef.update("favorites", FieldValue.arrayRemove(id)).await()
            }
        } catch (e: Exception) {
            // If document doesn't exist, create it
            if (save) {
                docRef.set(mapOf("favorites" to listOf(id))).await()
            }
        }
    }
}
