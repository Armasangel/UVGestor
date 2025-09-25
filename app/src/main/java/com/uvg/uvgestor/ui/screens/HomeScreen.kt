package com.uvg.uvgestor.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tuempresa.tuapp.viewmodel.ItemsViewModel
import com.tuempresa.tuapp.navigation.Screen

@Composable
fun HomeScreen(navController: NavHostController, vm: ItemsViewModel = viewModel()) {
    val items by vm.items.collectAsState()

    Scaffold(topBar = { SmallTopAppBar(title = { Text("Mi App - Home") }) }) { padding ->
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ) {
            items(items) { item ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { navController.navigate(Screen.Detail.createRoute(item.id)) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(item.title, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(item.subtitle, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
