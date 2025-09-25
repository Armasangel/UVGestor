package com.uvg.uvgestor.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun DetailScreen(itemId: Int, navController: NavHostController) {
    Scaffold(topBar = { SmallTopAppBar(title = { Text("Detalle #$itemId") },
        navigationIcon = { IconButton(onClick = { navController.popBackStack() }){ Icon(Icons.Default.ArrowBack, contentDescription = "Volver") } }) }) {
        Text("Aquí va la información del item $itemId", modifier = Modifier.padding(16.dp))
    }
}
