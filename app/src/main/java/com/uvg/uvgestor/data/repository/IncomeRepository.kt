package com.uvg.uvgestor.data.repository

import android.content.Context
import com.uvg.uvgestor.data.local.database.AppDatabase
import com.uvg.uvgestor.data.local.entity.toEntity
import com.uvg.uvgestor.data.local.entity.toIncome
import com.uvg.uvgestor.data.remote.FirebaseIncomeDataSource
import com.uvg.uvgestor.domain.model.NetworkResult
import com.uvg.uvgestor.ui.data.Income
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class IncomeRepository(private val context: Context) {

    private val firebaseDataSource = FirebaseIncomeDataSource()
    private val database = AppDatabase.getInstance(context)
    private val incomeDao = database.incomeDao()

    suspend fun getIncomes(userId: String): Flow<NetworkResult<List<Income>>> = flow {
        emit(NetworkResult.Loading)

        try {
            val localIncomes = incomeDao.getAllIncomes(userId).map { it.toIncome() }

            if (localIncomes.isNotEmpty()) {
                emit(NetworkResult.Success(localIncomes))
            }

            val result = firebaseDataSource.getIncomes(userId)

            result.fold(
                onSuccess = { firebaseIncomes ->
                    firebaseIncomes.forEach { income ->
                        incomeDao.insertIncome(
                            income.toEntity(
                                userId = userId,
                                syncedWithFirebase = true
                            )
                        )
                    }

                    val updatedIncomes = incomeDao.getAllIncomes(userId).map { it.toIncome() }
                    emit(NetworkResult.Success(updatedIncomes))
                },
                onFailure = { exception ->
                    if (localIncomes.isEmpty()) {
                        emit(NetworkResult.Error("Error al cargar ingresos: ${exception.message}"))
                    }
                }
            )
        } catch (e: Exception) {
            emit(NetworkResult.Error("Error inesperado: ${e.message}"))
        }
    }

    fun getIncomesFlow(userId: String): Flow<List<Income>> {
        return incomeDao.getAllIncomesFlow(userId).map { entities ->
            entities.map { it.toIncome() }
        }
    }

    suspend fun addIncome(income: Income, userId: String): Flow<NetworkResult<Income>> = flow {
        emit(NetworkResult.Loading)

        try {
            when {
                income.title.isBlank() -> {
                    emit(NetworkResult.Error("El título no puede estar vacío"))
                }
                income.amount <= 0 -> {
                    emit(NetworkResult.Error("El monto debe ser mayor a 0"))
                }
                else -> {
                    val localId = incomeDao.insertIncome(
                        income.toEntity(userId = userId, syncedWithFirebase = false)
                    ).toInt()

                    val savedIncome = income.copy(id = localId)
                    emit(NetworkResult.Success(savedIncome))

                    val result = firebaseDataSource.addIncome(savedIncome, userId)

                    result.fold(
                        onSuccess = { firebaseId ->
                            incomeDao.markAsSynced(localId, firebaseId)
                        },
                        onFailure = {
                            // El ingreso se guarda localmente aunque falle Firebase
                        }
                    )
                }
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error("Error al guardar ingreso: ${e.message}"))
        }
    }

    suspend fun deleteIncome(incomeId: Int): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading)
        try {
            incomeDao.deleteById(incomeId)
            emit(NetworkResult.Success(Unit))
        } catch (e: Exception) {
            emit(NetworkResult.Error("Error al eliminar ingreso: ${e.message}"))
        }
    }

    suspend fun getTotalIncome(userId: String): Double {
        return incomeDao.getTotalIncome(userId) ?: 0.0
    }
}