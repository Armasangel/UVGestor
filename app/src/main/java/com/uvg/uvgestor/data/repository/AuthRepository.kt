package com.uvg.uvgestor.data.repository

import com.uvg.uvgestor.domain.model.NetworkResult
import com.uvg.uvgestor.domain.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class AuthRepository {
    
    private val registeredUsers = mutableMapOf<String, Pair<String, User>>()
    
    init {
        registeredUsers["test@uvg.edu.gt"] = Pair(
            "password123",
            User(
                id = "test-user-001",
                email = "test@uvg.edu.gt",
                name = "Usuario de Prueba"
            )
        )
    }
    
    /**
     * @param email 
     * @param password 
     * @return 
     */
    suspend fun login(email: String, password: String): Flow<NetworkResult<User>> = flow {
        emit(NetworkResult.Loading)
        
        delay(Random.nextLong(1000, 3000))
        
        if (Random.nextFloat() < 0.3f) {
            emit(NetworkResult.Error(
                message = "Error de red: No se pudo conectar al servidor",
                throwable = Exception("Network timeout")
            ))
            return@flow
        }
        
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
            !registeredUsers.containsKey(email) -> {
                emit(NetworkResult.Error("Usuario no encontrado"))
            }
            registeredUsers[email]?.first != password -> {
                emit(NetworkResult.Error("Contraseña incorrecta"))
            }
            else -> {
                val user = registeredUsers[email]!!.second
                emit(NetworkResult.Success(user))
            }
        }
    }
    
    /**
     * @param email
     * @param password 
     * @param name 
     * @return
     */
    suspend fun register(email: String, password: String, name: String): Flow<NetworkResult<User>> = flow {
        emit(NetworkResult.Loading)
        
        delay(Random.nextLong(1500, 2500))
        
        if (Random.nextFloat() < 0.2f) {
            emit(NetworkResult.Error(
                message = "Error de conexión: Intenta nuevamente",
                throwable = Exception("Connection failed")
            ))
            return@flow
        }
        
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
            registeredUsers.containsKey(email) -> {
                emit(NetworkResult.Error("Este correo ya está registrado"))
            }
            else -> {
                val newUser = User(
                    id = "user-${System.currentTimeMillis()}",
                    email = email,
                    name = name
                )
                registeredUsers[email] = Pair(password, newUser)
                emit(NetworkResult.Success(newUser))
            }
        }
    }
    
    suspend fun logout(): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading)
        delay(500)
        emit(NetworkResult.Success(Unit))
    }
}
