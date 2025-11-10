package com.uvg.uvgestor.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.uvg.uvgestor.domain.model.User
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSource {
    
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    
    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Usuario no encontrado")
            Result.success(firebaseUser.toUser())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signUp(email: String, password: String, name: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Error al crear usuario")
            
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            
            firebaseUser.updateProfile(profileUpdates).await()
            
            Result.success(firebaseUser.toUser())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signOut() {
        auth.signOut()
    }
    
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
    
    private fun FirebaseUser.toUser(): User {
        return User(
            id = uid,
            email = email ?: "",
            name = displayName ?: "",
            createdAt = metadata?.creationTimestamp ?: System.currentTimeMillis()
        )
    }
}
