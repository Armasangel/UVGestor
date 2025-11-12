package com.uvg.uvgestor.data.local.dao

import androidx.room.*
import com.uvg.uvgestor.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Query("SELECT * FROM budgets WHERE userId = :userId AND monthYear = :monthYear AND category IS NULL")
    suspend fun getGeneralBudget(userId: String, monthYear: String): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE userId = :userId AND monthYear = :monthYear AND category IS NULL")
    fun getGeneralBudgetFlow(userId: String, monthYear: String): Flow<BudgetEntity?>

    @Query("SELECT * FROM budgets WHERE userId = :userId AND monthYear = :monthYear AND category = :category")
    suspend fun getCategoryBudget(userId: String, monthYear: String, category: String): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE userId = :userId ORDER BY monthYear DESC")
    suspend fun getAllBudgets(userId: String): List<BudgetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity): Long

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    @Query("DELETE FROM budgets WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)
}