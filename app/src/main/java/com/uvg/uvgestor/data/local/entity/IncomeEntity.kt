package com.uvg.uvgestor.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uvg.uvgestor.ui.data.Income

@Entity(tableName = "incomes")
data class IncomeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val timePeriod: String,
    val category: String,
    val date: String,
    val userId: String,
    val syncedWithFirebase: Boolean = false,
    val firebaseId: String? = null
)

fun IncomeEntity.toIncome(): Income {
    return Income(
        id = id,
        title = title,
        amount = amount,
        timePeriod = timePeriod,
        category = category,
        date = date
    )
}

fun Income.toEntity(userId: String, syncedWithFirebase: Boolean = false, firebaseId: String? = null): IncomeEntity {
    return IncomeEntity(
        id = id,
        title = title,
        amount = amount,
        timePeriod = timePeriod,
        category = category,
        date = date,
        userId = userId,
        syncedWithFirebase = syncedWithFirebase,
        firebaseId = firebaseId
    )
}