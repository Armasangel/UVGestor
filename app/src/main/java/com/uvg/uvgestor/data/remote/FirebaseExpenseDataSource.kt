package com.uvg.uvgestor.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.uvg.uvgestor.ui.data.Expense
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseExpenseDataSource {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val expensesCollection = firestore.collection("expenses")
    
    suspend fun addExpense(expense: Expense, userId: String): Result<String> {
        return try {
            val expenseMap = hashMapOf(
                "title" to expense.title,
                "amount" to expense.amount,
                "timePeriod" to expense.timePeriod,
                "category" to expense.category,
                "date" to expense.date,
                "userId" to userId,
                "createdAt" to System.currentTimeMillis()
            )
            
            val docRef = expensesCollection.add(expenseMap).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getExpenses(userId: String): Result<List<Expense>> {
        return try {
            val snapshot = expensesCollection
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val expenses = snapshot.documents.mapNotNull { doc ->
                try {
                    Expense(
                        id = doc.id.hashCode(),
                        title = doc.getString("title") ?: "",
                        amount = doc.getDouble("amount") ?: 0.0,
                        timePeriod = doc.getString("timePeriod") ?: "",
                        category = doc.getString("category") ?: "",
                        date = doc.getString("date") ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            Result.success(expenses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getExpensesFlow(userId: String): Flow<List<Expense>> = callbackFlow {
        val listener = expensesCollection
            .whereEqualTo("userId", userId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val expenses = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Expense(
                            id = doc.id.hashCode(),
                            title = doc.getString("title") ?: "",
                            amount = doc.getDouble("amount") ?: 0.0,
                            timePeriod = doc.getString("timePeriod") ?: "",
                            category = doc.getString("category") ?: "",
                            date = doc.getString("date") ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                
                trySend(expenses)
            }
        
        awaitClose { listener.remove() }
    }
    
    suspend fun deleteExpense(firebaseId: String): Result<Unit> {
        return try {
            expensesCollection.document(firebaseId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateExpense(firebaseId: String, expense: Expense): Result<Unit> {
        return try {
            val expenseMap = hashMapOf(
                "title" to expense.title,
                "amount" to expense.amount,
                "timePeriod" to expense.timePeriod,
                "category" to expense.category,
                "date" to expense.date,
                "updatedAt" to System.currentTimeMillis()
            )
            
            expensesCollection.document(firebaseId).update(expenseMap as Map<String, Any>).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
