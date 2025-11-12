package com.uvg.uvgestor.presentation.viewmodel.income

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.uvgestor.data.repository.AuthRepository
import com.uvg.uvgestor.data.repository.IncomeRepository
import com.uvg.uvgestor.domain.model.NetworkResult
import com.uvg.uvgestor.ui.data.Income
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddIncomeViewModel(application: Application) : AndroidViewModel(application) {

    private val incomeRepository = IncomeRepository(application.applicationContext)
    private val authRepository = AuthRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(AddIncomeUiState())
    val uiState: StateFlow<AddIncomeUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            _uiState.value = _uiState.value.copy(currentUserId = currentUser?.id)
        }
    }

    fun onEvent(event: AddIncomeUiEvent) {
        when (event) {
            is AddIncomeUiEvent.TitleChanged -> {
                _uiState.value = _uiState.value.copy(
                    title = event.title,
                    error = null
                )
            }
            is AddIncomeUiEvent.AmountChanged -> {
                _uiState.value = _uiState.value.copy(
                    amount = event.amount,
                    error = null
                )
            }
            is AddIncomeUiEvent.TimePeriodChanged -> {
                _uiState.value = _uiState.value.copy(
                    selectedTimePeriod = event.period
                )
            }
            is AddIncomeUiEvent.CategoryChanged -> {
                _uiState.value = _uiState.value.copy(
                    selectedCategory = event.category
                )
            }
            AddIncomeUiEvent.SaveClicked -> {
                validateAndShowConfirmation()
            }
            AddIncomeUiEvent.ConfirmSave -> {
                _uiState.value = _uiState.value.copy(showConfirmationDialog = false)
                saveIncome()
            }
            AddIncomeUiEvent.CancelSave -> {
                _uiState.value = _uiState.value.copy(showConfirmationDialog = false)
            }
            AddIncomeUiEvent.ErrorDismissed -> {
                _uiState.value = _uiState.value.copy(error = null)
            }
            AddIncomeUiEvent.SaveSuccessHandled -> {
                _uiState.value = _uiState.value.copy(saveSuccess = false)
            }
        }
    }

    private fun validateAndShowConfirmation() {
        val currentState = _uiState.value

        when {
            currentState.title.isBlank() -> {
                _uiState.value = currentState.copy(
                    error = "Por favor ingresa un título para el ingreso"
                )
                return
            }
            currentState.amount.isBlank() -> {
                _uiState.value = currentState.copy(
                    error = "Por favor ingresa un monto"
                )
                return
            }
            currentState.amount.toDoubleOrNull() == null || currentState.amount.toDouble() <= 0 -> {
                _uiState.value = currentState.copy(
                    error = "Por favor ingresa un monto válido mayor a 0"
                )
                return
            }
            currentState.selectedTimePeriod.isBlank() -> {
                _uiState.value = currentState.copy(
                    error = "Por favor selecciona un período de tiempo"
                )
                return
            }
            currentState.selectedCategory.isBlank() -> {
                _uiState.value = currentState.copy(
                    error = "Por favor selecciona una categoría"
                )
                return
            }
        }

        _uiState.value = currentState.copy(showConfirmationDialog = true)
    }

    private fun saveIncome() {
        val currentState = _uiState.value
        val userId = currentState.currentUserId

        if (userId == null) {
            _uiState.value = currentState.copy(
                error = "Error: Usuario no autenticado"
            )
            return
        }

        val income = Income(
            id = 0,
            title = currentState.title,
            amount = currentState.amount.toDouble(),
            timePeriod = currentState.selectedTimePeriod,
            category = currentState.selectedCategory,
            date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )

        viewModelScope.launch {
            incomeRepository.addIncome(income, userId).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.value = currentState.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is NetworkResult.Success -> {
                        _uiState.value = AddIncomeUiState(
                            saveSuccess = true,
                            currentUserId = userId
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