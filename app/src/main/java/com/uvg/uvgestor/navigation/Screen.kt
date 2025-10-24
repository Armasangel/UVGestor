package com.uvg.uvgestor.navigation



sealed class Screen(val route: String) {


    object Welcome : Screen("welcome")

    object Login : Screen("login")

    object Register : Screen("register")


    object Home : Screen("home")


    object Transactions : Screen("transactions")


    object AddExpense : Screen("addExpense")


    object TransactionDetail : Screen("transactionDetail/{id}") {
        /**
         * @param id
         * @return
         */
        fun createRoute(id: Int) = "transactionDetail/$id"
    }



    companion object {

        const val AUTH_GRAPH_ROUTE = "auth_graph"

        const val MAIN_GRAPH_ROUTE = "main_graph"
    }
}
