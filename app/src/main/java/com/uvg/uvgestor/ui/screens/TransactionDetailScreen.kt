package com.uvg.uvgestor.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.uvg.uvgestor.ui.viewmodel.TransactionsViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(id: Int, navController: NavHostController, vm: TransactionsViewModel = viewModel()) {
    val transaction = vm.getTransactionById(id)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Transacción #$id") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (transaction != null) {
                Text("Título: ${transaction.title}", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text("Monto: Q${transaction.amount}", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                Text("Tipo: ${transaction.type}", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                Text("Fecha: ${transaction.date}", style = MaterialTheme.typography.bodySmall)
            } else {
                Text("Transacción no encontrada", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}