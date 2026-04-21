package com.locapin.mobile.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.locapin.mobile.core.common.LocaPinResult
import com.locapin.mobile.data.session.SessionManager
import com.locapin.mobile.domain.model.AuthRole
import com.locapin.mobile.domain.model.AuthSession
import com.locapin.mobile.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val sessionManager: SessionManager
) : AuthRepository {

    override val session: Flow<AuthSession?> = sessionManager.sessionFlow

    override val authToken: Flow<String?> = session.map { currentSession ->
        if (currentSession?.isLoggedIn == true) {
            firebaseAuth.currentUser?.getIdToken(false)?.await()?.token
        } else {
            null
        }
    }

    override suspend fun login(email: String, password: String): LocaPinResult<AuthSession> = runCatching {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val user = result.user ?: return LocaPinResult.Error("Failed to sign in")

        // Fetch role from Firestore
        val userDoc = try {
            firestore.collection("users").document(user.uid).get().await()
        } catch (e: Exception) {
            null
        }

        val roleString = userDoc?.getString("role")
        val role = if (roleString?.uppercase() == "ADMIN") AuthRole.ADMIN else AuthRole.TOURIST

        val authSession = AuthSession(
            userId = user.uid,
            name = userDoc?.getString("name") ?: user.displayName ?: "User",
            email = user.email ?: email,
            role = role,
            isLoggedIn = true
        )

        sessionManager.saveSession(authSession)
        LocaPinResult.Success(authSession)
    }.getOrElse { LocaPinResult.Error(it.message ?: "Login failed") }

    override suspend fun socialLogin(
        provider: String,
        idToken: String?,
        accessToken: String?
    ): LocaPinResult<AuthSession> = runCatching {
        val credential = when (provider) {
            "google" -> {
                if (idToken == null) return LocaPinResult.Error("Google ID token is missing")
                com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            }
            else -> return LocaPinResult.Error("Social provider $provider not supported yet")
        }

        val result = firebaseAuth.signInWithCredential(credential).await()
        val user = result.user ?: return LocaPinResult.Error("Failed to sign in with $provider")

        val authSession = AuthSession(
            userId = user.uid,
            name = user.displayName ?: "User",
            email = user.email ?: "",
            role = AuthRole.TOURIST,
            isLoggedIn = true
        )

        sessionManager.saveSession(authSession)
        LocaPinResult.Success(authSession)
    }.getOrElse { LocaPinResult.Error(it.message ?: "Social login failed") }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        agreedToEula: Boolean,
        agreedToTerms: Boolean,
        agreedToPrivacy: Boolean
    ): LocaPinResult<AuthSession> = runCatching {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: return LocaPinResult.Error("Registration failed")
        
        // Update display name in Firebase Auth
        val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
            displayName = name
        }
        user.updateProfile(profileUpdates).await()

        // Create user document in Firestore with default role and legal compliance flags
        val userData = mapOf(
            "name" to name,
            "email" to email,
            "role" to "TOURIST",
            "agreedToEula" to agreedToEula,
            "agreedToTerms" to agreedToTerms,
            "agreedToPrivacy" to agreedToPrivacy,
            "createdAt" to com.google.firebase.Timestamp.now()
        )
        firestore.collection("users").document(user.uid).set(userData).await()

        val authSession = AuthSession(
            userId = user.uid,
            name = name,
            email = user.email ?: email,
            role = AuthRole.TOURIST,
            isLoggedIn = true
        )

        sessionManager.saveSession(authSession)
        LocaPinResult.Success(authSession)
    }.getOrElse { LocaPinResult.Error(it.message ?: "Registration failed") }

    override suspend fun forgotPassword(email: String): LocaPinResult<Unit> = runCatching {
        firebaseAuth.sendPasswordResetEmail(email).await()
        LocaPinResult.Success(Unit)
    }.getOrElse { LocaPinResult.Error(it.message ?: "Failed to send reset email") }

    override suspend fun logout() {
        firebaseAuth.signOut()
        sessionManager.clearSession()
    }

    override suspend fun getCurrentSession(): AuthSession? = sessionManager.getSession()

    override suspend fun restoreSession(): AuthSession? = sessionManager.getSession()
}
