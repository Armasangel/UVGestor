package com.uvg.uvgestor.data.local.dao

import androidx.room.*
import com.uvg.uvgestor.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    fun getAllExpensesFlow(userId: String): Flow<List<ExpenseEntity>>
    
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    suspend fun getAllExpenses(userId: String): List<ExpenseEntity>
    
    @Query("SELECT * FROM expenses WHERE id = :expenseId")
    suspend fun getExpenseById(expenseId: Int): ExpenseEntity?
    
    @Query("SELECT * FROM expenses WHERE userId = :userId AND timePeriod = :period ORDER BY date DESC")
    suspend fun getExpensesByPeriod(userId: String, period: String): List<ExpenseEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(expenses: List<ExpenseEntity>)
    
    @Update
    suspend fun updateExpense(expense: ExpenseEntity)
    
    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)
    
    @Query("DELETE FROM expenses WHERE id = :expenseId")
    suspend fun deleteById(expenseId: Int)
    
    @Query("DELETE FROM expenses WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)
    
    @Query("SELECT * FROM expenses WHERE userId = :userId AND syncedWithFirebase = 0")
    suspend fun getUnsyncedExpenses(userId: String): List<ExpenseEntity>
    
    @Query("UPDATE expenses SET syncedWithFirebase = 1, firebaseId = :firebaseId WHERE id = :localId")
    suspend fun markAsSynced(localId: Int, firebaseId: String)
}
