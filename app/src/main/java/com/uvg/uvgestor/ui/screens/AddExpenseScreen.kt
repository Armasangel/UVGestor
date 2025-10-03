package com.uvg.uvgestor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.uvg.uvgestor.ui.data.Expense
import com.uvg.uvgestor.ui.viewmodel.ExpensesViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    navController: NavHostController,
    vm: ExpensesViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedTimePeriod by remember { mutableStateOf("Diario") }
    var selectedCategory by remember { mutableStateOf("Comida") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    val uvgGreen = Color(0xFF00C853)
    val backgroundColor = Color(0xFFF5F5F5)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Agregar Gasto",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = uvgGreen,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Card principal del formulario
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Parámetro 1: Título del gasto
                    Column {
                        Text(
                            "Título del Gasto",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF333333)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Ej: Almuerzo en cafetería") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = uvgGreen,
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                cursorColor = uvgGreen
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                    
                    // Parámetro 2: Monto del gasto
                    Column {
                        Text(
                            "Monto del Gasto",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF333333)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { newValue ->
                                // Solo permitir números y punto decimal
                                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    amount = newValue
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("0.00") },
                            leadingIcon = {
                                Text(
                                    "Q",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF666666),
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = uvgGreen,
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                cursorColor = uvgGreen
                            ),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            singleLine = true
                        )
                    }
                    
                    Divider(color = Color(0xFFE0E0E0))
                    
                    // Parámetro 3: Período de tiempo
                    Column {
                        Text(
                            "Período de Tiempo",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF333333)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        val timePeriods = listOf("Diario", "Semanal", "Mensual", "Anual")
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            timePeriods.forEach { period ->
                                TimePeriodOption(
                                    period = period,
                                    selected = selectedTimePeriod == period,
                                    onSelect = { selectedTimePeriod = period },
                                    color = uvgGreen
                                )
                            }
                        }
                    }
                    
                    Divider(color = Color(0xFFE0E0E0))
                    
                    // Parámetro 4: Categoría
                    Column {
                        Text(
                            "Categoría del Gasto",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF333333)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        val categories = listOf(
                            "Comida" to "🍔",
                            "Transporte" to "🚗",
                            "Ocio" to "🎮"
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            categories.forEach { (category, emoji) ->
                                CategoryOption(
                                    category = category,
                                    emoji = emoji,
                                    selected = selectedCategory == category,
                                    onSelect = { selectedCategory = category },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
            
            // Mensaje de error
            if (showError) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        errorMessage,
                        modifier = Modifier.padding(12.dp),
                        color = Color(0xFFD32F2F),
                        fontSize = 14.sp
                    )
                }
            }
            
            // Botón guardar
            Button(
                onClick = {
                    // Validación
                    when {
                        title.isBlank() -> {
                            showError = true
                            errorMessage = "Por favor ingresa un título para el gasto"
                        }
                        amount.isBlank() || amount.toDoubleOrNull() == null || amount.toDouble() <= 0 -> {
                            showError = true
                            errorMessage = "Por favor ingresa un monto válido"
                        }
                        else -> {
                            // Crear el gasto
                            val newExpense = Expense(
                                id = System.currentTimeMillis().toInt(),
                                title = title,
                                amount = amount.toDouble(),
                                timePeriod = selectedTimePeriod,
                                category = selectedCategory,
                                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    .format(Date())
                            )
                            
                            // Agregar al ViewModel
                            vm.addExpense(newExpense)
                            
                            // Navegar de regreso
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = uvgGreen
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Guardar Gasto",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            // Botón cancelar
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF666666)
                )
            ) {
                Text(
                    "Cancelar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TimePeriodOption(
    period: String,
    selected: Boolean,
    onSelect: () -> Unit,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        onClick = onSelect,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) color.copy(alpha = 0.1f) else Color.White
        ),
        border = if (selected) {
            androidx.compose.foundation.BorderStroke(2.dp, color)
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
        },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                period,
                fontSize = 16.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) color else Color(0xFF333333)
            )
            
            RadioButton(
                selected = selected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = color,
                    unselectedColor = Color(0xFFBDBDBD)
                )
            )
        }
    }
}

@Composable
fun CategoryOption(
    category: String,
    emoji: String,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = when (category) {
        "Comida" -> Color(0xFFFF6B6B)
        "Transporte" -> Color(0xFF4ECDC4)
        "Ocio" -> Color(0xFFFFD93D)
        else -> Color.Gray
    }
    
    Card(
        modifier = modifier.height(100.dp),
        onClick = onSelect,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) categoryColor.copy(alpha = 0.15f) else Color.White
        ),
        border = if (selected) {
            androidx.compose.foundation.BorderStroke(3.dp, categoryColor)
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
        },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                emoji,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                category,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) categoryColor else Color(0xFF666666)
            )
        }
    }
}
