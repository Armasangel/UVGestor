package com.uvg.uvgestor.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Transactions : Screen("transactions")
    object TransactionDetail : Screen("transactionDetail/{id}") {
        fun createRoute(id: Int) = "transactionDetail/$id"
    }
}
