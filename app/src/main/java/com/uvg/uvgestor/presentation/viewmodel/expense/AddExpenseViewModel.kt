package com.uvg.uvgestor.presentation.viewmodel.expense

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.uvgestor.data.repository.AuthRepository
import com.uvg.uvgestor.data.repository.ExpenseRepository
import com.uvg.uvgestor.domain.model.NetworkResult
import com.uvg.uvgestor.ui.data.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class AddExpenseUiState(
    val title: String = "",
    val amount: String = "",
    val selectedTimePeriod: String = "",
    val selectedCategory: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false,
    val showConfirmationDialog: Boolean = false,
    val currentUserId: String? = null
)

sealed class AddExpenseUiEvent {
    data class TitleChanged(val title: String) : AddExpenseUiEvent()
    data class AmountChanged(val amount: String) : AddExpenseUiEvent()
    data class TimePeriodChanged(val period: String) : AddExpenseUiEvent()
    data class CategoryChanged(val category: String) : AddExpenseUiEvent()
    object SaveClicked : AddExpenseUiEvent()
    object ConfirmSave : AddExpenseUiEvent()
    object CancelSave : AddExpenseUiEvent()
    object ErrorDismissed : AddExpenseUiEvent()
    object SaveSuccessHandled : AddExpenseUiEvent()
}

class AddExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val expenseRepository = ExpenseRepository(application.applicationContext)
    private val authRepository = AuthRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            _uiState.value = _uiState.value.copy(currentUserId = currentUser?.id)
        }
    }

    fun onEvent(event: AddExpenseUiEvent) {
        when (event) {
            is AddExpenseUiEvent.TitleChanged -> {
                _uiState.value = _uiState.value.copy(
                    title = event.title,
                    error = null
                )
            }
            is AddExpenseUiEvent.AmountChanged -> {
                _uiState.value = _uiState.value.copy(
                    amount = event.amount,
                    error = null
                )
            }
            is AddExpenseUiEvent.TimePeriodChanged -> {
                _uiState.value = _uiState.value.copy(
                    selectedTimePeriod = event.period
                )
            }
            is AddExpenseUiEvent.CategoryChanged -> {
                _uiState.value = _uiState.value.copy(
                    selectedCategory = event.category
                )
            }
            AddExpenseUiEvent.SaveClicked -> {
                validateAndShowConfirmation()
            }
            AddExpenseUiEvent.ConfirmSave -> {
                _uiState.value = _uiState.value.copy(showConfirmationDialog = false)
                saveExpense()
            }
            AddExpenseUiEvent.CancelSave -> {
                _uiState.value = _uiState.value.copy(showConfirmationDialog = false)
            }
            AddExpenseUiEvent.ErrorDismissed -> {
                _uiState.value = _uiState.value.copy(error = null)
            }
            AddExpenseUiEvent.SaveSuccessHandled -> {
                _uiState.value = _uiState.value.copy(saveSuccess = false)
            }
        }
    }

    private fun validateAndShowConfirmation() {
        val currentState = _uiState.value

        when {
            currentState.title.isBlank() -> {
                _uiState.value = currentState.copy(
                    error = "Por favor ingresa un título para el gasto"
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

    private fun saveExpense() {
        val currentState = _uiState.value
        val userId = currentState.currentUserId

        if (userId == null) {
            _uiState.value = currentState.copy(
                error = "Error: Usuario no autenticado"
            )
            return
        }

        val expense = Expense(
            id = 0,
            title = currentState.title,
            amount = currentState.amount.toDouble(),
            timePeriod = currentState.selectedTimePeriod,
            category = currentState.selectedCategory,
            date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )

        viewModelScope.launch {
            expenseRepository.addExpense(expense, userId).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.value = currentState.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is NetworkResult.Success -> {
                        _uiState.value = AddExpenseUiState(
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