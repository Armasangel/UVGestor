package com.uvg.uvgestor.data.repository

import android.content.Context
import com.uvg.uvgestor.data.local.database.AppDatabase
import com.uvg.uvgestor.data.local.entity.toEntity
import com.uvg.uvgestor.data.local.entity.toExpense
import com.uvg.uvgestor.data.remote.FirebaseExpenseDataSource
import com.uvg.uvgestor.domain.model.NetworkResult
import com.uvg.uvgestor.ui.data.Expense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

class ExpenseRepository(private val context: Context) {
    
    private val firebaseDataSource = FirebaseExpenseDataSource()
    private val database = AppDatabase.getInstance(context)
    private val expenseDao = database.expenseDao()
    
    suspend fun getExpenses(userId: String): Flow<NetworkResult<List<Expense>>> = flow {
        emit(NetworkResult.Loading)
        
        try {
            val localExpenses = expenseDao.getAllExpenses(userId).map { it.toExpense() }
            
            if (localExpenses.isNotEmpty()) {
                emit(NetworkResult.Success(localExpenses))
            }
            
            val result = firebaseDataSource.getExpenses(userId)
            
            result.fold(
                onSuccess = { firebaseExpenses ->
                    firebaseExpenses.forEach { expense ->
                        expenseDao.insertExpense(
                            expense.toEntity(
                                userId = userId,
                                syncedWithFirebase = true
                            )
                        )
                    }
                    
                    val updatedExpenses = expenseDao.getAllExpenses(userId).map { it.toExpense() }
                    emit(NetworkResult.Success(updatedExpenses))
                },
                onFailure = { exception ->
                    if (localExpenses.isEmpty()) {
                        emit(NetworkResult.Error("Error al cargar gastos: ${exception.message}"))
                    }
                }
            )
        } catch (e: Exception) {
            emit(NetworkResult.Error("Error inesperado: ${e.message}"))
        }
    }
    
    fun getExpensesFlow(userId: String): Flow<List<Expense>> {
        return expenseDao.getAllExpensesFlow(userId).map { entities ->
            entities.map { it.toExpense() }
        }
    }
    
    suspend fun addExpense(expense: Expense, userId: String): Flow<NetworkResult<Expense>> = flow {
        emit(NetworkResult.Loading)
        
        try {
            when {
                expense.title.isBlank() -> {
                    emit(NetworkResult.Error("El título no puede estar vacío"))
                }
                expense.amount <= 0 -> {
                    emit(NetworkResult.Error("El monto debe ser mayor a 0"))
                }
                else -> {
                    val localId = expenseDao.insertExpense(
                        expense.toEntity(userId = userId, syncedWithFirebase = false)
                    ).toInt()
                    
                    val savedExpense = expense.copy(id = localId)
                    emit(NetworkResult.Success(savedExpense))
                    
                    val result = firebaseDataSource.addExpense(savedExpense, userId)
                    
                    result.fold(
                        onSuccess = { firebaseId ->
                            expenseDao.markAsSynced(localId, firebaseId)
                        },
                        onFailure = { 
                            // El gasto se guarda localmente aunque falle Firebase
                        }
                    )
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Error al guardar gasto: ${e.message}"))
        }
    }
    
    suspend fun deleteExpense(expenseId: Int): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading)
        try {
            expenseDao.deleteById(expenseId)
            emit(NetworkResult.Success(Unit))
        } catch (e: Exception) {
            emit(NetworkResult.Error("Error al eliminar gasto: ${e.message}"))
        }
    }
    
    suspend fun getExpensesByPeriod(userId: String, period: String): Flow<NetworkResult<List<Expense>>> = flow {
        emit(NetworkResult.Loading)
        
        try {
            val calendar = Calendar.getInstance()
            val today = calendar.time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            
            val allExpenses = expenseDao.getAllExpenses(userId).map { it.toExpense() }
            
            val filtered = allExpenses.filter { expense ->
                val expenseDate = try {
                    dateFormat.parse(expense.date)
                } catch (e: Exception) {
                    null
                }
                
                when (period) {
                    "Diario" -> expense.date == dateFormat.format(today)
                    "Semanal" -> {
                        if (expenseDate == null) return@filter false
                        val daysDiff = ((today.time - expenseDate.time) / (1000 * 60 * 60 * 24)).toInt()
                        daysDiff in 0..7
                    }
                    "Mensual" -> {
                        if (expenseDate == null) return@filter false
                        val expenseCalendar = Calendar.getInstance()
                        expenseCalendar.time = expenseDate
                        expenseCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                                expenseCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                    }
                    "Anual" -> {
                        if (expenseDate == null) return@filter false
                        val expenseCalendar = Calendar.getInstance()
                        expenseCalendar.time = expenseDate
                        expenseCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
                    }
                    else -> true
                }
            }
            
            emit(NetworkResult.Success(filtered))
        } catch (e: Exception) {
            emit(NetworkResult.Error("Error al filtrar gastos: ${e.message}"))
        }
    }
    
    suspend fun syncPendingExpenses(userId: String) {
        try {
            val unsyncedExpenses = expenseDao.getUnsyncedExpenses(userId)
            
            unsyncedExpenses.forEach { expenseEntity ->
                val expense = expenseEntity.toExpense()
                val result = firebaseDataSource.addExpense(expense, userId)
                
                result.onSuccess { firebaseId ->
                    expenseDao.markAsSynced(expenseEntity.id, firebaseId)
                }
            }
        } catch (e: Exception) {
            // Log error pero no propagar
        }
    }
    
    fun getExpenseById(id: Int): Expense? {
        return null
    }
}
