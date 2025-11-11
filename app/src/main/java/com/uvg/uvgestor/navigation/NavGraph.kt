package com.uvg.uvgestor.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost


@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.AUTH_GRAPH_ROUTE
    ) {

        authGraph(navController)

        mainGraph(navController)
    }
}