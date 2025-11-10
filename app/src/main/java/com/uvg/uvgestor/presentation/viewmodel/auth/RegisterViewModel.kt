package com.uvg.uvgestor.presentation.viewmodel.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.uvgestor.data.repository.AuthRepository
import com.uvg.uvgestor.domain.model.NetworkResult
import com.uvg.uvgestor.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val registerSuccess: User? = null,
    val requiresGuardian: Boolean = false,
    val guardianName: String = "",
    val guardianEmail: String = "",
    val guardianRelationship: String = ""
)


sealed class RegisterUiEvent {
    data class NameChanged(val name: String) : RegisterUiEvent()
    data class EmailChanged(val email: String) : RegisterUiEvent()
    data class PasswordChanged(val password: String) : RegisterUiEvent()
    object RegisterClicked : RegisterUiEvent()
    object ErrorDismissed : RegisterUiEvent()
    object RegisterSuccessHandled : RegisterUiEvent()
    data class RequiresGuardianChanged(val required: Boolean) : RegisterUiEvent()
    data class GuardianNameChanged(val name: String) : RegisterUiEvent()
    data class GuardianEmailChanged(val email: String) : RegisterUiEvent()
    data class GuardianRelationshipChanged(val relationship: String) : RegisterUiEvent()
}

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEvent(event: RegisterUiEvent) {
        when (event) {
            is RegisterUiEvent.NameChanged -> {
                _uiState.value = _uiState.value.copy(
                    name = event.name,
                    error = null
                )
            }
            is RegisterUiEvent.EmailChanged -> {
                _uiState.value = _uiState.value.copy(
                    email = event.email,
                    error = null
                )
            }
            is RegisterUiEvent.PasswordChanged -> {
                _uiState.value = _uiState.value.copy(
                    password = event.password,
                    error = null
                )
            }
            is RegisterUiEvent.RequiresGuardianChanged -> {
                _uiState.value = _uiState.value.copy(
                    requiresGuardian = event.required,
                    error = null,
                    guardianName = if (!event.required) "" else _uiState.value.guardianName,
                    guardianEmail = if (!event.required) "" else _uiState.value.guardianEmail,
                    guardianRelationship = if (!event.required) "" else _uiState.value.guardianRelationship
                )
            }
            is RegisterUiEvent.GuardianNameChanged -> {
                _uiState.value = _uiState.value.copy(
                    guardianName = event.name,
                    error = null
                )
            }
            is RegisterUiEvent.GuardianEmailChanged -> {
                _uiState.value = _uiState.value.copy(
                    guardianEmail = event.email,
                    error = null
                )
            }
            is RegisterUiEvent.GuardianRelationshipChanged -> {
                _uiState.value = _uiState.value.copy(
                    guardianRelationship = event.relationship,
                    error = null
                )
            }
            RegisterUiEvent.RegisterClicked -> {
                register()
            }
            RegisterUiEvent.ErrorDismissed -> {
                _uiState.value = _uiState.value.copy(error = null)
            }
            RegisterUiEvent.RegisterSuccessHandled -> {
                _uiState.value = _uiState.value.copy(registerSuccess = null)
            }
        }
    }

    private fun register() {
        val currentState = _uiState.value

        when {
            currentState.name.isBlank() || currentState.email.isBlank() ||
                    currentState.password.isBlank() -> {
                _uiState.value = currentState.copy(
                    error = "Por favor completa todos los campos obligatorios"
                )
                return
            }
            currentState.password.length < 6 -> {
                _uiState.value = currentState.copy(
                    error = "La contraseña debe tener al menos 6 caracteres"
                )
                return
            }
            currentState.requiresGuardian && currentState.guardianName.isBlank() -> {
                _uiState.value = currentState.copy(
                    error = "Por favor ingresa el nombre del tutor"
                )
                return
            }
            currentState.requiresGuardian && currentState.guardianEmail.isBlank() -> {
                _uiState.value = currentState.copy(
                    error = "Por favor ingresa el correo del tutor"
                )
                return
            }
            currentState.requiresGuardian && !currentState.guardianEmail.contains("@") -> {
                _uiState.value = currentState.copy(
                    error = "Formato de correo del tutor inválido"
                )
                return
            }
            currentState.requiresGuardian && currentState.guardianRelationship.isBlank() -> {
                _uiState.value = currentState.copy(
                    error = "Por favor selecciona la relación con el tutor"
                )
                return
            }
        }

        viewModelScope.launch {
            authRepository.register(
                email = currentState.email,
                password = currentState.password,
                name = currentState.name,
                guardianName = if (currentState.requiresGuardian) currentState.guardianName else null,
                guardianEmail = if (currentState.requiresGuardian) currentState.guardianEmail else null,
                guardianRelationship = if (currentState.requiresGuardian) currentState.guardianRelationship else null
            ).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.value = currentState.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is NetworkResult.Success -> {
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            error = null,
                            registerSuccess = result.data
                        )
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
}
