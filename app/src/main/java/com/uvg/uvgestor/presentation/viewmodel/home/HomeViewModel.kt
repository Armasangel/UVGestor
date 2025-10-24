package com.uvg.uvgestor.presentation.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.uvgestor.data.repository.ExpenseRepository
import com.uvg.uvgestor.domain.model.NetworkResult
import com.uvg.uvgestor.ui.data.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val selectedPeriod: String = "Diario",
    val expenses: List<Expense> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalAmount: Double = 0.0,
    val expensesByCategory: Map<String, Double> = emptyMap()
)

sealed class HomeUiEvent {
    data class PeriodChanged(val period: String) : HomeUiEvent()
    object LoadExpenses : HomeUiEvent()
    object RetryLoad : HomeUiEvent()
    object ErrorDismissed : HomeUiEvent()
}

class HomeViewModel(
    private val expenseRepository: ExpenseRepository = ExpenseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())

    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // Load expenses when ViewModel is created
        loadExpenses()
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.PeriodChanged -> {
                _uiState.value = _uiState.value.copy(
                    selectedPeriod = event.period
                )
                loadExpensesByPeriod(event.period)
            }
            HomeUiEvent.LoadExpenses -> {
                loadExpenses()
            }
            HomeUiEvent.RetryLoad -> {
                loadExpensesByPeriod(_uiState.value.selectedPeriod)
            }
            HomeUiEvent.ErrorDismissed -> {
                _uiState.value = _uiState.value.copy(error = null)
            }
        }
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            expenseRepository.getExpenses().collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is NetworkResult.Success -> {
                        calculateStatistics(result.data)
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    private fun loadExpensesByPeriod(period: String) {
        viewModelScope.launch {
            expenseRepository.getExpensesByPeriod(period).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is NetworkResult.Success -> {
                        calculateStatistics(result.data)
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    private fun calculateStatistics(expenses: List<Expense>) {
        val total = expenses.sumOf { it.amount }
        val byCategory = expenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        _uiState.value = _uiState.value.copy(
            expenses = expenses,
            isLoading = false,
            error = null,
            totalAmount = total,
            expensesByCategory = byCategory
        )
    }
}