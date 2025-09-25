package com.uvg.uvgestor.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.uvg.uvgestor.ui.data.Transaction
import com.uvg.uvgestor.navigation.Screen
import com.uvg.uvgestor.ui.components.TransactionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(navController: NavHostController) {
    val sampleTransactions = listOf(
        Transaction(1, "Café", 15.0, "Gasto", "2025-09-20"),
        Transaction(2, "Beca", 1000.0, "Ingreso", "2025-09-15"),
        Transaction(3, "Libro", 200.0, "Gasto", "2025-09-10")
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("Transacciones") }) }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(sampleTransactions) { tx ->
                TransactionCard(transaction = tx) {
                    navController.navigate(Screen.TransactionDetail.createRoute(tx.id))
                }
            }
        }
    }
}