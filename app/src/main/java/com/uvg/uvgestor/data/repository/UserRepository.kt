package com.uvg.uvgestor.data.repository

import com.uvg.uvgestor.domain.model.NetworkResult
import com.uvg.uvgestor.domain.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

/**
 * Fake repository for user profile operations
 * Simulates API calls for user data management
 */
class UserRepository {
    
    // Simulated current user session
    private var currentUser: User? = null
    
    /**
     * Gets the current logged-in user profile
     * @return Flow emitting NetworkResult with User data
     */
    suspend fun getCurrentUser(): Flow<NetworkResult<User>> = flow {
        emit(NetworkResult.Loading)
        
        // Simulate network latency
        delay(Random.nextLong(500, 1000))
        
        // 10% chance of error
        if (Random.nextFloat() < 0.1f) {
            emit(NetworkResult.Error("Error al cargar perfil de usuario"))
            return@flow
        }
        
        if (currentUser != null) {
            emit(NetworkResult.Success(currentUser!!))
        } else {
            emit(NetworkResult.Error("No hay usuario autenticado"))
        }
    }
    
    /**
     * Updates the current user profile
     * @param user Updated user data
     * @return Flow emitting NetworkResult
     */
    suspend fun updateUser(user: User): Flow<NetworkResult<User>> = flow {
        emit(NetworkResult.Loading)
        
        // Simulate network latency
        delay(Random.nextLong(1000, 2000))
        
        // 15% chance of error
        if (Random.nextFloat() < 0.15f) {
            emit(NetworkResult.Error("No se pudo actualizar el perfil"))
            return@flow
        }
        
        currentUser = user
        emit(NetworkResult.Success(user))
    }
    
    /**
     * Sets the current logged-in user
     * @param user User to set as current
     */
    fun setCurrentUser(user: User) {
        currentUser = user
    }
    
    /**
     * Clears the current user session
     */
    fun clearCurrentUser() {
        currentUser = null
    }
    
    /**
     * Checks if there's a user currently logged in
     * @return true if user is logged in, false otherwise
     */
    fun isUserLoggedIn(): Boolean {
        return currentUser != null
    }
    
    /**
     * Gets user statistics (for dashboard/profile screen)
     * @return Flow emitting NetworkResult with user stats
     */
    suspend fun getUserStats(): Flow<NetworkResult<UserStats>> = flow {
        emit(NetworkResult.Loading)
        
        delay(Random.nextLong(800, 1500))
        
        if (currentUser == null) {
            emit(NetworkResult.Error("Usuario no autenticado"))
            return@flow
        }
        
        // Simulate gathering user statistics
        val stats = UserStats(
            totalExpenses = Random.nextInt(10, 100),
            totalAmount = Random.nextDouble(1000.0, 10000.0),
            averageExpense = Random.nextDouble(50.0, 500.0),
            mostUsedCategory = listOf("Comida", "Transporte", "Ocio").random()
        )
        
        emit(NetworkResult.Success(stats))
    }
}

/**
 * Data class for user statistics
 */
data class UserStats(
    val totalExpenses: Int,
    val totalAmount: Double,
    val averageExpense: Double,
    val mostUsedCategory: String
)
