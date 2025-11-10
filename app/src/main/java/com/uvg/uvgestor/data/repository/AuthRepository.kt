package com.uvg.uvgestor.data.repository

import android.content.Context
import com.uvg.uvgestor.data.local.database.AppDatabase
import com.uvg.uvgestor.data.local.entity.toEntity
import com.uvg.uvgestor.data.local.entity.toUser
import com.uvg.uvgestor.data.remote.FirebaseAuthDataSource
import com.uvg.uvgestor.domain.model.Guardian
import com.uvg.uvgestor.domain.model.NetworkResult
import com.uvg.uvgestor.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepository(private val context: Context) {
    
    private val firebaseAuth = FirebaseAuthDataSource()
    private val database = AppDatabase.getInstance(context)
    private val userDao = database.userDao()
    
    suspend fun login(email: String, password: String): Flow<NetworkResult<User>> = flow {
        emit(NetworkResult.Loading)
        
        try {
            when {
                email.isBlank() -> {
                    emit(NetworkResult.Error("El correo electrónico no puede estar vacío"))
                }
                password.isBlank() -> {
                    emit(NetworkResult.Error("La contraseña no puede estar vacía"))
                }
                !email.contains("@") -> {
                    emit(NetworkResult.Error("Formato de correo electrónico inválido"))
                }
                else -> {
                    val result = firebaseAuth.signIn(email, password)
                    
                    result.fold(
                        onSuccess = { user ->
                            userDao.insertUser(user.toEntity())
                            emit(NetworkResult.Success(user))
                        },
                        onFailure = { exception ->
                            val localUser = userDao.getUserById(email)
                            if (localUser != null) {
                                emit(NetworkResult.Success(localUser.toUser()))
                            } else {
                                emit(NetworkResult.Error("Error de autenticación: ${exception.message}"))
                            }
                        }
                    )
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Error inesperado: ${e.message}"))
        }
    }
    
    suspend fun register(
        email: String,
        password: String,
        name: String,
        guardianName: String? = null,
        guardianEmail: String? = null,
        guardianRelationship: String? = null
    ): Flow<NetworkResult<User>> = flow {
        emit(NetworkResult.Loading)
        
        try {
            when {
                email.isBlank() || password.isBlank() || name.isBlank() -> {
                    emit(NetworkResult.Error("Todos los campos son obligatorios"))
                }
                !email.contains("@") -> {
                    emit(NetworkResult.Error("Formato de correo electrónico inválido"))
                }
                password.length < 6 -> {
                    emit(NetworkResult.Error("La contraseña debe tener al menos 6 caracteres"))
                }
                else -> {
                    val result = firebaseAuth.signUp(email, password, name)
                    
                    result.fold(
                        onSuccess = { firebaseUser ->
                            val guardians = mutableListOf<Guardian>()
                            
                            if (guardianName != null && guardianEmail != null && guardianRelationship != null) {
                                val guardian = Guardian(
                                    id = "guardian-${System.currentTimeMillis()}",
                                    name = guardianName,
                                    email = guardianEmail,
                                    relationship = guardianRelationship,
                                    hasAccessToFinances = true
                                )
                                guardians.add(guardian)
                            }
                            
                            val user = firebaseUser.copy(
                                guardians = guardians,
                                isMinor = guardians.isNotEmpty()
                            )
                            
                            userDao.insertUser(user.toEntity())
                            emit(NetworkResult.Success(user))
                        },
                        onFailure = { exception ->
                            emit(NetworkResult.Error("Error al registrar: ${exception.message}"))
                        }
                    )
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Error inesperado: ${e.message}"))
        }
    }
    
    suspend fun logout(): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading)
        try {
            firebaseAuth.signOut()
            userDao.deleteAllUsers()
            emit(NetworkResult.Success(Unit))
        } catch (e: Exception) {
            emit(NetworkResult.Error("Error al cerrar sesión: ${e.message}"))
        }
    }
    
    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.isUserLoggedIn()
    }
    
    suspend fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.getCurrentUser()
        return if (firebaseUser != null) {
            User(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                name = firebaseUser.displayName ?: ""
            )
        } else {
            userDao.getCurrentUser()?.toUser()
        }
    }
}
