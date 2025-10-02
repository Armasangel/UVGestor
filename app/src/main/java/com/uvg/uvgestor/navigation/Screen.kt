package com.uvg.uvgestor.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Transactions : Screen("transactions")
    object TransactionDetail : Screen("transactionDetail/{id}") {
        fun createRoute(id: Int) = "transactionDetail/$id"
    }
}
