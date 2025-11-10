package com.uvg.uvgestor.data.local.entity



import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uvg.uvgestor.ui.data.Expense

@Entity(tableName = "expenses")
data class ExpenseEntity(
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

fun ExpenseEntity.toExpense(): Expense {
    return Expense(
        id = id,
        title = title,
        amount = amount,
        timePeriod = timePeriod,
        category = category,
        date = date
    )
}

fun Expense.toEntity(userId: String, syncedWithFirebase: Boolean = false, firebaseId: String? = null): ExpenseEntity {
    return ExpenseEntity(
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