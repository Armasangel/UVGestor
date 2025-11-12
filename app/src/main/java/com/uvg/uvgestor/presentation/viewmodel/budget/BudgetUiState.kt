package com.uvg.uvgestor.presentation.viewmodel.budget


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.uvgestor.data.repository.AuthRepository
import com.uvg.uvgestor.data.repository.BudgetRepository
import com.uvg.uvgestor.domain.model.NetworkResult
import com.uvg.uvgestor.ui.data.Budget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class BudgetUiState(
    val limitAmount: String = "",
    val currentBudget: Budget? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val currentUserId: String? = null
)

sealed class BudgetUiEvent {
    data class LimitAmountChanged(val amount: String) : BudgetUiEvent()
    object SaveClicked : BudgetUiEvent()
    object DeleteClicked : BudgetUiEvent()
    object ConfirmDelete : BudgetUiEvent()
    object CancelDelete : BudgetUiEvent()
    object ErrorDismissed : BudgetUiEvent()
    object SaveSuccessHandled : BudgetUiEvent()
}

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val budgetRepository = BudgetRepository(application.applicationContext)
    private val authRepository = AuthRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            _uiState.value = _uiState.value.copy(currentUserId = currentUser?.id)
            if (currentUser != null) {
                loadCurrentBudget(currentUser.id)
            }
        }
    }

    private fun loadCurrentBudget(userId: String) {
        viewModelScope.launch {
            val budget = budgetRepository.getCurrentMonthBudget(userId)
            _uiState.value = _uiState.value.copy(
                currentBudget = budget,
                limitAmount = budget?.limitAmount?.toString() ?: ""
            )
        }
    }

    fun onEvent(event: BudgetUiEvent) {
        when (event) {
            is BudgetUiEvent.LimitAmountChanged -> {
                _uiState.value = _uiState.value.copy(
                    limitAmount = event.amount,
                    error = null
                )
            }
            BudgetUiEvent.SaveClicked -> {
                saveBudget()
            }
            BudgetUiEvent.DeleteClicked -> {
                _uiState.value = _uiState.value.copy(showDeleteConfirmation = true)
            }
            BudgetUiEvent.ConfirmDelete -> {
                _uiState.value = _uiState.value.copy(showDeleteConfirmation = false)
                deleteBudget()
            }
            BudgetUiEvent.CancelDelete -> {
                _uiState.value = _uiState.value.copy(showDeleteConfirmation = false)
            }
            BudgetUiEvent.ErrorDismissed -> {
                _uiState.value = _uiState.value.copy(error = null)
            }
            BudgetUiEvent.SaveSuccessHandled -> {
                _uiState.value = _uiState.value.copy(saveSuccess = false)
            }
        }
    }

    private fun saveBudget() {
        val currentState = _uiState.value
        val userId = currentState.currentUserId

        if (userId == null) {
            _uiState.value = currentState.copy(
                error = "Error: Usuario no autenticado"
            )
            return
        }

        val amount = currentState.limitAmount.toDoubleOrNull()

        when {
            currentState.limitAmount.isBlank() -> {
                _uiState.value = currentState.copy(
                    error = "Por favor ingresa un monto límite"
                )
                return
            }
            amount == null || amount <= 0 -> {
                _uiState.value = currentState.copy(
                    error = "Por favor ingresa un monto válido mayor a 0"
                )
                return
            }
        }

        val calendar = Calendar.getInstance()
        val monthYear = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time)

        val budget = Budget(
            id = currentState.currentBudget?.id ?: 0,
            monthYear = monthYear,
            limitAmount = amount,
            category = null,
            alertAt80Percent = true,
            alertAt100Percent = true
        )

        viewModelScope.launch {
            budgetRepository.saveBudget(budget, userId).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.value = currentState.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is NetworkResult.Success -> {
                        _uiState.value = BudgetUiState(
                            saveSuccess = true,
                            currentUserId = userId,
                            currentBudget = result.data,
                            limitAmount = result.data.limitAmount.toString()
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

    private fun deleteBudget() {
        val currentState = _uiState.value
        val userId = currentState.currentUserId
        val budget = currentState.currentBudget

        if (userId == null || budget == null) {
            _uiState.value = currentState.copy(
                error = "No hay presupuesto para eliminar"
            )
            return
        }

        viewModelScope.launch {
            budgetRepository.deleteBudget(budget, userId).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.value = currentState.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is NetworkResult.Success -> {
                        _uiState.value = BudgetUiState(
                            currentUserId = userId,
                            saveSuccess = true
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