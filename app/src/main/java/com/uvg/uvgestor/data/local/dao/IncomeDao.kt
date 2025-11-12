package com.uvg.uvgestor.data.local.dao


import androidx.room.*
import com.uvg.uvgestor.data.local.entity.IncomeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {

    @Query("SELECT * FROM incomes WHERE userId = :userId ORDER BY date DESC")
    fun getAllIncomesFlow(userId: String): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM incomes WHERE userId = :userId ORDER BY date DESC")
    suspend fun getAllIncomes(userId: String): List<IncomeEntity>

    @Query("SELECT * FROM incomes WHERE id = :incomeId")
    suspend fun getIncomeById(incomeId: Int): IncomeEntity?

    @Query("SELECT * FROM incomes WHERE userId = :userId AND timePeriod = :period ORDER BY date DESC")
    suspend fun getIncomesByPeriod(userId: String, period: String): List<IncomeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: IncomeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(incomes: List<IncomeEntity>)

    @Update
    suspend fun updateIncome(income: IncomeEntity)

    @Delete
    suspend fun deleteIncome(income: IncomeEntity)

    @Query("DELETE FROM incomes WHERE id = :incomeId")
    suspend fun deleteById(incomeId: Int)

    @Query("DELETE FROM incomes WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)

    @Query("SELECT * FROM incomes WHERE userId = :userId AND syncedWithFirebase = 0")
    suspend fun getUnsyncedIncomes(userId: String): List<IncomeEntity>

    @Query("UPDATE incomes SET syncedWithFirebase = 1, firebaseId = :firebaseId WHERE id = :localId")
    suspend fun markAsSynced(localId: Int, firebaseId: String)

    @Query("SELECT SUM(amount) FROM incomes WHERE userId = :userId")
    suspend fun getTotalIncome(userId: String): Double?
}