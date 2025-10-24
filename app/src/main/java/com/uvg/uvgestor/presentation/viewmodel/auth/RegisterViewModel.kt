package com.uvg.uvgestor.presentation.viewmodel.auth

import androidx.lifecycle.ViewModel
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
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val registerSuccess: User? = null
)


sealed class RegisterUiEvent {
    data class NameChanged(val name: String) : RegisterUiEvent()
    data class EmailChanged(val email: String) : RegisterUiEvent()
    data class PasswordChanged(val password: String) : RegisterUiEvent()
    data class ConfirmPasswordChanged(val confirmPassword: String) : RegisterUiEvent()
    object RegisterClicked : RegisterUiEvent()
    object ErrorDismissed : RegisterUiEvent()
    object RegisterSuccessHandled : RegisterUiEvent()
}

class RegisterViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

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
            is RegisterUiEvent.ConfirmPasswordChanged -> {
                _uiState.value = _uiState.value.copy(
                    confirmPassword = event.confirmPassword,
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
                    currentState.password.isBlank() || currentState.confirmPassword.isBlank() -> {
                _uiState.value = currentState.copy(
                    error = "Por favor completa todos los campos"
                )
                return
            }
            currentState.password != currentState.confirmPassword -> {
                _uiState.value = currentState.copy(
                    error = "Las contraseñas no coinciden"
                )
                return
            }
            currentState.password.length < 6 -> {
                _uiState.value = currentState.copy(
                    error = "La contraseña debe tener al menos 6 caracteres"
                )
                return
            }
        }

        viewModelScope.launch {
            authRepository.register(
                email = currentState.email,
                password = currentState.password,
                name = currentState.name
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
