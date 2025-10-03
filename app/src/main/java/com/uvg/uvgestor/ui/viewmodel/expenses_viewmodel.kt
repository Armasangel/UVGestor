package com.uvg.uvgestor.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.uvg.uvgestor.ui.data.Expense
import java.text.SimpleDateFormat
import java.util.*

class ExpensesViewModel : ViewModel() {

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses

    init {
        // Datos de ejemplo para testing
        loadSampleData()
    }

    private fun loadSampleData() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Date()

        _expenses.value = listOf(
            Expense(
                id = 4,
                title = "Almuerzo cafetería",
                amount = 55.00,
                timePeriod = "Diario",
                category = "Comida",
                date = dateFormat.format(Date(today.time - 1 * 24 * 60 * 60 * 1000))
            ),
            Expense(
                id = 5,
                title = "Gasolina mensual",
                amount = 400.00,
                timePeriod = "Mensual",
                category = "Transporte",
                date = dateFormat.format(Date(today.time - 5 * 24 * 60 * 60 * 1000))
            ),
            Expense(
                id = 6,
                title = "Netflix",
                amount = 60.00,
                timePeriod = "Mensual",
                category = "Ocio",
                date = dateFormat.format(Date(today.time - 10 * 24 * 60 * 60 * 1000))
            ),
            Expense(
                id = 2,
                title = "Uber a la universidad",
                amount = 30.00,
                timePeriod = "Diario",
                category = "Transporte",
                date = dateFormat.format(today)
            ),
            Expense(
                id = 3,
                title = "Cine con amigos",
                amount = 80.00,
                timePeriod = "Semanal",
                category = "Ocio",
                date = dateFormat.format(Date(today.time - 2 * 24 * 60 * 60 * 1000))
            ),
            Expense(
                id = 1,
                title = "Café con amigos",
                amount = 45.00,
                timePeriod = "Diario",
                category = "Comida",
                date = dateFormat.format(today)
            ),
        )
    }

    // Agregar un nuevo gasto
    fun addExpense(expense: Expense) {
        _expenses.value = _expenses.value + expense
    }

    // Obtener gastos filtrados por período de tiempo
    fun getExpensesByPeriod(period: String): List<Expense> {
        val calendar = Calendar.getInstance()
        val today = calendar.time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return _expenses.value.filter { expense ->
            val expenseDate = try {
                dateFormat.parse(expense.date)
            } catch (e: Exception) {
                null
            }

            when (period) {
                "Diario" -> {
                    // Gastos de hoy
                    expense.date == dateFormat.format(today)
                }
                "Semanal" -> {
                    // Gastos de esta semana
                    if (expenseDate == null) return@filter false
                    val daysDiff = ((today.time - expenseDate.time) / (1000 * 60 * 60 * 24)).toInt()
                    daysDiff in 0..7
                }
                "Mensual" -> {
                    // Gastos de este mes
                    if (expenseDate == null) return@filter false
                    val expenseCalendar = Calendar.getInstance()
                    expenseCalendar.time = expenseDate
                    expenseCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                            expenseCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                }
                "Anual" -> {
                    // Gastos de este año
                    if (expenseDate == null) return@filter false
                    val expenseCalendar = Calendar.getInstance()
                    expenseCalendar.time = expenseDate
                    expenseCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                }
                else -> true
            }
        }
    }

    // Obtener estadísticas por categoría
    fun getCategoryStatistics(period: String): Map<String, Double> {
        val filteredExpenses = getExpensesByPeriod(period)
        return filteredExpenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
    }

    // Obtener el total de gastos por período
    fun getTotalByPeriod(period: String): Double {
        return getExpensesByPeriod(period).sumOf { it.amount }
    }

    // Eliminar un gasto
    fun deleteExpense(expenseId: Int) {
        _expenses.value = _expenses.value.filter { it.id != expenseId }
    }

    // Obtener un gasto por ID
    fun getExpenseById(id: Int): Expense? {
        return _expenses.value.find { it.id == id }
    }
}