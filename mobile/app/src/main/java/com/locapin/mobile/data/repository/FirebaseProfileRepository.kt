package com.locapin.mobile.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.locapin.mobile.core.common.LocaPinResult
import com.locapin.mobile.domain.model.User
import com.locapin.mobile.domain.repository.ProfileRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseProfileRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ProfileRepository {

    override suspend fun getProfile(): LocaPinResult<User> = runCatching {
        val currentUser = firebaseAuth.currentUser ?: return LocaPinResult.Error("Not logged in")
        val doc = try { 
            firestore.collection("users").document(currentUser.uid).get().await()
        } catch (e: Exception) {
            null
        }
        
        User(
            id = currentUser.uid,
            name = doc?.getString("name") ?: currentUser.displayName ?: "User",
            email = currentUser.email ?: "",
            avatarUrl = doc?.getString("avatarUrl") ?: currentUser.photoUrl?.toString()
        )
    }.fold(
        onSuccess = { LocaPinResult.Success(it) },
        onFailure = { LocaPinResult.Error(it.message ?: "Failed to load profile") }
    )
}
