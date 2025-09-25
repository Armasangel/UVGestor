package com.uvg.uvgestor.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.uvg.uvgestor.navigation.Screen
import com.uvg.uvgestor.ui.components.TransactionCard
import com.uvg.uvgestor.ui.viewmodel.TransactionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(navController: NavHostController, vm: TransactionsViewModel = viewModel()) {
    val transactions by vm.transactions.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Transacciones") }) }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(transactions) { tx ->
                TransactionCard(transaction = tx) {
                    navController.navigate(Screen.TransactionDetail.createRoute(tx.id))
                }
            }
        }
    }
}
