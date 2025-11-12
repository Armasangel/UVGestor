package com.uvg.uvgestor.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.uvg.uvgestor.ui.data.Income
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseIncomeDataSource {

    private val firestore = FirebaseFirestore.getInstance()
    private val incomesCollection = firestore.collection("incomes")

    suspend fun addIncome(income: Income, userId: String): Result<String> {
        return try {
            val incomeMap = hashMapOf(
                "title" to income.title,
                "amount" to income.amount,
                "timePeriod" to income.timePeriod,
                "category" to income.category,
                "date" to income.date,
                "userId" to userId,
                "createdAt" to System.currentTimeMillis()
            )

            val docRef = incomesCollection.add(incomeMap).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getIncomes(userId: String): Result<List<Income>> {
        return try {
            val snapshot = incomesCollection
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val incomes = snapshot.documents.mapNotNull { doc ->
                try {
                    Income(
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

            Result.success(incomes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getIncomesFlow(userId: String): Flow<List<Income>> = callbackFlow {
        val listener = incomesCollection
            .whereEqualTo("userId", userId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val incomes = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Income(
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

                trySend(incomes)
            }

        awaitClose { listener.remove() }
    }

    suspend fun deleteIncome(firebaseId: String): Result<Unit> {
        return try {
            incomesCollection.document(firebaseId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateIncome(firebaseId: String, income: Income): Result<Unit> {
        return try {
            val incomeMap = hashMapOf(
                "title" to income.title,
                "amount" to income.amount,
                "timePeriod" to income.timePeriod,
                "category" to income.category,
                "date" to income.date,
                "updatedAt" to System.currentTimeMillis()
            )

            incomesCollection.document(firebaseId).update(incomeMap as Map<String, Any>).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}