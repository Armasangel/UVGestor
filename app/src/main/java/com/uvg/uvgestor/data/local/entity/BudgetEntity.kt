package com.uvg.uvgestor.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uvg.uvgestor.ui.data.Budget

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val monthYear: String, // "2025-11"
    val limitAmount: Double,
    val category: String? = null,
    val alertAt80Percent: Boolean = true,
    val alertAt100Percent: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

fun BudgetEntity.toBudget(): Budget {
    return Budget(
        id = id,
        monthYear = monthYear,
        limitAmount = limitAmount,
        category = category,
        alertAt80Percent = alertAt80Percent,
        alertAt100Percent = alertAt100Percent
    )
}

fun Budget.toEntity(userId: String): BudgetEntity {
    return BudgetEntity(
        id = id,
        userId = userId,
        monthYear = monthYear,
        limitAmount = limitAmount,
        category = category,
        alertAt80Percent = alertAt80Percent,
        alertAt100Percent = alertAt100Percent
    )
}