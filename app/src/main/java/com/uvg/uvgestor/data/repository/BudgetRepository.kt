package com.uvg.uvgestor.data.repository


import android.content.Context
import com.uvg.uvgestor.data.local.database.AppDatabase
import com.uvg.uvgestor.data.local.entity.toBudget
import com.uvg.uvgestor.data.local.entity.toEntity
import com.uvg.uvgestor.domain.model.NetworkResult
import com.uvg.uvgestor.ui.data.Budget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

class BudgetRepository(private val context: Context) {

    private val database = AppDatabase.getInstance(context)
    private val budgetDao = database.budgetDao()

    fun getCurrentMonthBudgetFlow(userId: String): Flow<Budget?> {
        val calendar = Calendar.getInstance()
        val monthYear = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time)

        return budgetDao.getGeneralBudgetFlow(userId, monthYear).map { entity ->
            entity?.toBudget()
        }
    }

    suspend fun getCurrentMonthBudget(userId: String): Budget? {
        val calendar = Calendar.getInstance()
        val monthYear = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time)

        return budgetDao.getGeneralBudget(userId, monthYear)?.toBudget()
    }

    suspend fun saveBudget(budget: Budget, userId: String): Flow<NetworkResult<Budget>> = flow {
        emit(NetworkResult.Loading)

        try {
            when {
                budget.limitAmount <= 0 -> {
                    emit(NetworkResult.Error("El límite debe ser mayor a 0"))
                }
                else -> {
                    val entity = budget.toEntity(userId)
                    budgetDao.insertBudget(entity)
                    emit(NetworkResult.Success(budget))
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Error al guardar presupuesto: ${e.message}"))
        }
    }

    suspend fun deleteBudget(budget: Budget, userId: String): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading)
        try {
            val entity = budget.toEntity(userId)
            budgetDao.deleteBudget(entity)
            emit(NetworkResult.Success(Unit))
        } catch (e: Exception) {
            emit(NetworkResult.Error("Error al eliminar presupuesto: ${e.message}"))
        }
    }

    suspend fun getAllBudgets(userId: String): List<Budget> {
        return budgetDao.getAllBudgets(userId).map { it.toBudget() }
    }
}