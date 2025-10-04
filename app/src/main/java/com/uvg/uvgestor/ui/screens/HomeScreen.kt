package com.uvg.uvgestor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.uvg.uvgestor.navigation.Screen
import com.uvg.uvgestor.ui.viewmodel.ExpensesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, vm: ExpensesViewModel = viewModel()) {
    val expenses by vm.expenses.collectAsState()
    var selectedTimePeriod by remember { mutableStateOf("Diario") }

    val uvgGreen = Color(0xFF00C853)
    val backgroundColor = Color(0xFFF5F5F5)

    // Calcular estadísticas según período seleccionado
    val filteredExpenses = vm.getExpensesByPeriod(selectedTimePeriod)
    val totalAmount = filteredExpenses.sumOf { it.amount }
    val expensesByCategory = filteredExpenses.groupBy { it.category }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "UVGestor",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                actions = {
                    // Botón de cerrar sesión
                    IconButton(
                        onClick = {
                            navController.navigate(Screen.Welcome.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = Color.Red
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddExpense.route) },
                containerColor = uvgGreen,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Agregar Gasto",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Selector de período de tiempo
            TimePeriodSelector(
                selectedPeriod = selectedTimePeriod,
                onPeriodSelected = { selectedTimePeriod = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card con gráfica y estadísticas
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Resumen de Gastos - $selectedTimePeriod",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Gráfica (área izquierda)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            ExpenseChart(
                                expenses = filteredExpenses,
                                totalAmount = totalAmount
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Estadísticas por categoría (área derecha)
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            expensesByCategory.forEach { (category, expensesList) ->
                                val categoryTotal = expensesList.sumOf { it.amount }
                                val percentage = if (totalAmount > 0) {
                                    (categoryTotal / totalAmount * 100).toInt()
                                } else 0

                                CategoryStatItem(
                                    category = category,
                                    amount = categoryTotal,
                                    percentage = percentage
                                )
                            }

                            if (expensesByCategory.isEmpty()) {
                                Text(
                                    "Sin gastos registrados",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Total
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Total:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "Q${String.format("%.2f", totalAmount)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = uvgGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Lista de gastos recientes
            Text(
                text = "Gastos Recientes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredExpenses.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No hay gastos registrados",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                filteredExpenses.takeLast(5).reversed().forEach { expense ->
                    ExpenseListItem(expense)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun TimePeriodSelector(
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit
) {
    val periods = listOf("Diario", "Semanal", "Mensual", "Anual")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        periods.forEach { period ->
            FilterChip(
                selected = selectedPeriod == period,
                onClick = { onPeriodSelected(period) },
                label = {
                    Text(
                        period,
                        fontSize = 14.sp,
                        fontWeight = if (selectedPeriod == period) FontWeight.Bold else FontWeight.Normal
                    )
                },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF00C853),
                    selectedLabelColor = Color.White,
                    containerColor = Color.White
                )
            )
        }
    }
}

@Composable
fun ExpenseChart(expenses: List<com.uvg.uvgestor.ui.data.Expense>, totalAmount: Double) {
    val categories = expenses.groupBy { it.category }
    val colors = mapOf(
        "Comida" to Color(0xFFFF6B6B),
        "Transporte" to Color(0xFF4ECDC4),
        "Ocio" to Color(0xFFFFD93D)
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Gráfica circular simple
        Box(
            modifier = Modifier
                .size(150.dp)
                .background(Color(0xFFE8F5E9), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Q${String.format("%.0f", totalAmount)}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00C853)
                )
                Text(
                    "Total",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun CategoryStatItem(category: String, amount: Double, percentage: Int) {
    val categoryColor = when (category) {
        "Comida" -> Color(0xFFFF6B6B)
        "Transporte" -> Color(0xFF4ECDC4)
        "Ocio" -> Color(0xFFFFD93D)
        else -> Color.Gray
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(categoryColor, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                category,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                "Q${String.format("%.2f", amount)}",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
        Text(
            "$percentage%",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = categoryColor
        )
    }
}

@Composable
fun ExpenseListItem(expense: com.uvg.uvgestor.ui.data.Expense) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    expense.title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "${expense.category} • ${expense.timePeriod}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Text(
                "Q${String.format("%.2f", expense.amount)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00C853)
            )
        }
    }
}