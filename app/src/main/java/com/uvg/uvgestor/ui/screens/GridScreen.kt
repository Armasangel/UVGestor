package com.tuempresa.tuapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tuempresa.tuapp.viewmodel.ItemsViewModel
import com.tuempresa.tuapp.navigation.Screen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GridScreen(navController: NavHostController, vm: ItemsViewModel = viewModel()) {
    val items by vm.items.collectAsState()
    Scaffold(topBar = { SmallTopAppBar(title = { Text("Grid") }) }) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(items) { item ->
                Card(modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .clickable { navController.navigate(Screen.Detail.createRoute(item.id)) }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(item.title, style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(6.dp))
                        Text(item.subtitle, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
