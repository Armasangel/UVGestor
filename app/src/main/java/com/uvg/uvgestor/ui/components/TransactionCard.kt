package com.uvg.uvgestor.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uvg.uvgestor.ui.data.Transaction

@Composable
fun TransactionCard(transaction: Transaction, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(transaction.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text("${transaction.type}: Q${transaction.amount}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(2.dp))
            Text(transaction.date, style = MaterialTheme.typography.bodySmall)
        }
    }
}