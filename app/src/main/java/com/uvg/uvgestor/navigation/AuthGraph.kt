package com.uvg.uvgestor.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.uvg.uvgestor.ui.screens.LoginScreen
import com.uvg.uvgestor.ui.screens.RegisterScreen
import com.uvg.uvgestor.ui.screens.WelcomeScreen

/**
 * Auth Navigation Graph - Handles authentication flow
 * This is a nested navigation graph containing:
 * - Welcome Screen (entry point)
 * - Login Screen
 * - Register Screen
 * 
 * @param navController The NavHostController for navigation
 */
fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.Welcome.route,
        route = Screen.AUTH_GRAPH_ROUTE
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(navController = navController)
        }
        
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
    }
}
