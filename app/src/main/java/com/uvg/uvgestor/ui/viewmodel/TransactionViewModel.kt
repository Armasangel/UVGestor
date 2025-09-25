package com.uvg.uvgestor.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.uvg.uvgestor.ui.data.Transaction

class TransactionsViewModel : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())

    val transactions: StateFlow<List<Transaction>> = _transactions

    init {
        // Datos simulados iniciales
        _transactions.value = listOf(
            Transaction(1, "Café", 15.0, "Gasto", "2025-09-20"),
            Transaction(2, "Beca", 1000.0, "Ingreso", "2025-09-15"),
            Transaction(3, "Libro", 200.0, "Gasto", "2025-09-10")
        )
    }

    // Función para agregar transacciones en el futuro
    fun addTransaction(transaction: Transaction) {
        _transactions.value += transaction
    }

    // Función para obtener una transacción por id
    fun getTransactionById(id: Int): Transaction? {
        return _transactions.value.find { it.id == id }
    }
}