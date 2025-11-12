package com.uvg.uvgestor.presentation.viewmodel.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.uvg.uvgestor.data.repository.AuthRepository
import com.uvg.uvgestor.data.repository.BudgetRepository
import com.uvg.uvgestor.data.repository.ExpenseRepository
import com.uvg.uvgestor.data.repository.IncomeRepository
import com.uvg.uvgestor.domain.model.NetworkResult
import com.uvg.uvgestor.ui.data.Budget
import com.uvg.uvgestor.ui.data.Expense
import com.uvg.uvgestor.ui.data.Income
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class HomeUiState(
    val selectedPeriod: String = "Mensual",
    val expenses: List<Expense> = emptyList(),
    val incomes: List<Income> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalExpenses: Double = 0.0,
    val totalIncomes: Double = 0.0,
    val balance: Double = 0.0,
    val expensesByCategory: Map<String, Double> = emptyMap(),
    val currentBudget: Budget? = null,
    val budgetPercentage: Int = 0,
    val showBudgetAlert: Boolean = false,
    val budgetAlertMessage: String = "",
    val currentUserId: String? = null
)

sealed class HomeUiEvent {
    data class PeriodChanged(val period: String) : HomeUiEvent()
    object LoadExpenses : HomeUiEvent()
    object RetryLoad : HomeUiEvent()
    object ErrorDismissed : HomeUiEvent()
    object BudgetAlertDismissed : HomeUiEvent()
}

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val expenseRepository = ExpenseRepository(application.applicationContext)
    private val incomeRepository = IncomeRepository(application.applicationContext)
    private val budgetRepository = BudgetRepository(application.applicationContext)
    private val authRepository = AuthRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            _uiState.value = _uiState.value.copy(currentUserId = currentUser?.id)
            if (currentUser != null) {
                observeFinancialData(currentUser.id)
                loadExpenses()
                loadIncomes()
            }
        }
    }

    private fun observeFinancialData(userId: String) {
        viewModelScope.launch {
            combine(
                expenseRepository.getExpensesFlow(userId),
                incomeRepository.getIncomesFlow(userId),
                budgetRepository.getCurrentMonthBudgetFlow(userId)
            ) { expenses, incomes, budget ->
                Triple(expenses, incomes, budget)
            }.collect { (expenses, incomes, budget) ->
                calculateStatistics(expenses, incomes, budget)
                checkBudgetAlerts(expenses, budget)
            }
        }
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
                loadIncomes()
            }
            HomeUiEvent.RetryLoad -> {
                loadExpensesByPeriod(_uiState.value.selectedPeriod)
            }
            HomeUiEvent.ErrorDismissed -> {
                _uiState.value = _uiState.value.copy(error = null)
            }
            HomeUiEvent.BudgetAlertDismissed -> {
                _uiState.value = _uiState.value.copy(showBudgetAlert = false)
            }
        }
    }

    private fun loadExpenses() {
        val userId = _uiState.value.currentUserId ?: return

        viewModelScope.launch {
            expenseRepository.getExpenses(userId).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is NetworkResult.Success -> {
                        // La actualización se hace en observeFinancialData
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

    private fun loadIncomes() {
        val userId = _uiState.value.currentUserId ?: return

        viewModelScope.launch {
            incomeRepository.getIncomes(userId).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        // Ya manejado en loadExpenses
                    }
                    is NetworkResult.Success -> {
                        // La actualización se hace en observeFinancialData
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
        val userId = _uiState.value.currentUserId ?: return

        viewModelScope.launch {
            expenseRepository.getExpensesByPeriod(userId, period).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is NetworkResult.Success -> {
                        val expenses = result.data
                        val totalExp = expenses.sumOf { it.amount }
                        val byCategory = expenses.groupBy { it.category }
                            .mapValues { entry -> entry.value.sumOf { it.amount } }

                        _uiState.value = _uiState.value.copy(
                            expenses = expenses,
                            isLoading = false,
                            error = null,
                            totalExpenses = totalExp,
                            expensesByCategory = byCategory
                        )
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

    private fun calculateStatistics(
        expenses: List<Expense>,
        incomes: List<Income>,
        budget: Budget?
    ) {
        val totalExp = expenses.sumOf { it.amount }
        val totalInc = incomes.sumOf { it.amount }
        val balance = totalInc - totalExp

        val byCategory = expenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val budgetPercentage = if (budget != null && budget.limitAmount > 0) {
            ((totalExp / budget.limitAmount) * 100).toInt()
        } else {
            0
        }

        _uiState.value = _uiState.value.copy(
            expenses = expenses,
            incomes = incomes,
            isLoading = false,
            error = null,
            totalExpenses = totalExp,
            totalIncomes = totalInc,
            balance = balance,
            expensesByCategory = byCategory,
            currentBudget = budget,
            budgetPercentage = budgetPercentage
        )
    }

    private fun checkBudgetAlerts(expenses: List<Expense>, budget: Budget?) {
        if (budget == null) return

        val totalExpenses = expenses.sumOf { it.amount }
        val percentage = (totalExpenses / budget.limitAmount) * 100

        val shouldShowAlert = when {
            percentage >= 100 && budget.alertAt100Percent -> {
                _uiState.value = _uiState.value.copy(
                    showBudgetAlert = true,
                    budgetAlertMessage = "⚠️ ¡Has superado tu presupuesto mensual! Gastos: Q${String.format("%.2f", totalExpenses)} / Q${String.format("%.2f", budget.limitAmount)}"
                )
                true
            }
            percentage >= 80 && budget.alertAt80Percent -> {
                _uiState.value = _uiState.value.copy(
                    showBudgetAlert = true,
                    budgetAlertMessage = "⚠️ Has alcanzado el ${percentage.toInt()}% de tu presupuesto mensual. Controla tus gastos."
                )
                true
            }
            else -> false
        }

        if (!shouldShowAlert) {
            _uiState.value = _uiState.value.copy(showBudgetAlert = false)
        }
    }
}