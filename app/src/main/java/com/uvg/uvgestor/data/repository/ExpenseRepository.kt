package com.uvg.uvgestor.data.repository

import com.uvg.uvgestor.domain.model.NetworkResult
import com.uvg.uvgestor.ui.data.Expense
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

/**
 * Fake repository simulating expense API operations
 * Includes network latency simulation and random error scenarios
 */
class ExpenseRepository {
    
    private val expenses = mutableListOf<Expense>()
    
    init {
        loadInitialData()
    }
    
    private fun loadInitialData() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Date()
        
        expenses.addAll(listOf(
            Expense(
                id = 1,
                title = "Café con amigos",
                amount = 45.00,
                timePeriod = "Diario",
                category = "Comida",
                date = dateFormat.format(today)
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
            )
        ))
    }
    
    /**
     * Fetches all expenses with simulated network delay
     * @return Flow emitting NetworkResult states
     */
    suspend fun getExpenses(): Flow<NetworkResult<List<Expense>>> = flow {
        emit(NetworkResult.Loading)
        
        // Simulate network latency (0.8-1.5 seconds)
        delay(Random.nextLong(800, 1500))
        
        // 25% chance of error
        if (Random.nextFloat() < 0.25f) {
            emit(NetworkResult.Error("Error al cargar gastos: Verifica tu conexión"))
            return@flow
        }
        
        emit(NetworkResult.Success(expenses.toList()))
    }
    
    /**
     * Adds a new expense with validation
     * @param expense Expense to add
     * @return Flow emitting NetworkResult states
     */
    suspend fun addExpense(expense: Expense): Flow<NetworkResult<Expense>> = flow {
        emit(NetworkResult.Loading)
        
        // Simulate network latency (1-2 seconds)
        delay(Random.nextLong(1000, 2000))
        
        // 15% chance of error
        if (Random.nextFloat() < 0.15f) {
            emit(NetworkResult.Error("No se pudo guardar el gasto: Intenta nuevamente"))
            return@flow
        }
        
        // Validate expense
        when {
            expense.title.isBlank() -> {
                emit(NetworkResult.Error("El título no puede estar vacío"))
            }
            expense.amount <= 0 -> {
                emit(NetworkResult.Error("El monto debe ser mayor a 0"))
            }
            else -> {
                val newExpense = expense.copy(id = System.currentTimeMillis().toInt())
                expenses.add(0, newExpense) // Add to beginning
                emit(NetworkResult.Success(newExpense))
            }
        }
    }
    
    /**
     * Deletes an expense by ID
     * @param expenseId ID of expense to delete
     * @return Flow emitting NetworkResult states
     */
    suspend fun deleteExpense(expenseId: Int): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading)
        delay(Random.nextLong(500, 1000))
        
        val removed = expenses.removeAll { it.id == expenseId }
        if (removed) {
            emit(NetworkResult.Success(Unit))
        } else {
            emit(NetworkResult.Error("Gasto no encontrado"))
        }
    }
    
    /**
     * Gets expenses filtered by time period
     * @param period Time period filter ("Diario", "Semanal", "Mensual", "Anual")
     * @return Flow emitting NetworkResult states
     */
    suspend fun getExpensesByPeriod(period: String): Flow<NetworkResult<List<Expense>>> = flow {
        emit(NetworkResult.Loading)
        delay(Random.nextLong(500, 1000))
        
        val calendar = Calendar.getInstance()
        val today = calendar.time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        val filtered = expenses.filter { expense ->
            val expenseDate = try {
                dateFormat.parse(expense.date)
            } catch (e: Exception) {
                null
            }
            
            when (period) {
                "Diario" -> {
                    // Expenses from today
                    expense.date == dateFormat.format(today)
                }
                "Semanal" -> {
                    // Expenses from this week
                    if (expenseDate == null) return@filter false
                    val daysDiff = ((today.time - expenseDate.time) / (1000 * 60 * 60 * 24)).toInt()
                    daysDiff in 0..7
                }
                "Mensual" -> {
                    // Expenses from this month
                    if (expenseDate == null) return@filter false
                    val expenseCalendar = Calendar.getInstance()
                    expenseCalendar.time = expenseDate
                    expenseCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                            expenseCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                }
                "Anual" -> {
                    // Expenses from this year
                    if (expenseDate == null) return@filter false
                    val expenseCalendar = Calendar.getInstance()
                    expenseCalendar.time = expenseDate
                    expenseCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                }
                else -> true
            }
        }
        
        emit(NetworkResult.Success(filtered))
    }
    
    /**
     * Gets expense by ID
     * @param id Expense ID
     * @return Expense if found, null otherwise
     */
    fun getExpenseById(id: Int): Expense? {
        return expenses.find { it.id == id }
    }
}
