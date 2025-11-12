package com.uvg.uvgestor.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.uvg.uvgestor.presentation.viewmodel.income.AddIncomeUiEvent
import com.uvg.uvgestor.presentation.viewmodel.income.AddIncomeViewModel

@Composable
fun AddIncomeScreen(
    navController: NavHostController,
    viewModel: AddIncomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            viewModel.onEvent(AddIncomeUiEvent.SaveSuccessHandled)
            navController.popBackStack()
        }
    }

    if (uiState.showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(AddIncomeUiEvent.CancelSave) },
            title = {
                Text(
                    text = "Confirmar Ingreso",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "¿Deseas guardar este ingreso?",
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Título:",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                                Text(
                                    uiState.title,
                                    fontSize = 14.sp
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Monto:",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                                Text(
                                    "Q${uiState.amount}",
                                    fontSize = 14.sp,
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Categoría:",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                                Text(
                                    uiState.selectedCategory,
                                    fontSize = 14.sp
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Período:",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                                Text(
                                    uiState.selectedTimePeriod,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onEvent(AddIncomeUiEvent.ConfirmSave) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text("Guardar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onEvent(AddIncomeUiEvent.CancelSave) }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    AddIncomeContent(
        title = uiState.title,
        amount = uiState.amount,
        selectedTimePeriod = uiState.selectedTimePeriod,
        selectedCategory = uiState.selectedCategory,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onTitleChange = { viewModel.onEvent(AddIncomeUiEvent.TitleChanged(it)) },
        onAmountChange = { viewModel.onEvent(AddIncomeUiEvent.AmountChanged(it)) },
        onTimePeriodChange = { viewModel.onEvent(AddIncomeUiEvent.TimePeriodChanged(it)) },
        onCategoryChange = { viewModel.onEvent(AddIncomeUiEvent.CategoryChanged(it)) },
        onSaveClick = { viewModel.onEvent(AddIncomeUiEvent.SaveClicked) },
        onErrorDismiss = { viewModel.onEvent(AddIncomeUiEvent.ErrorDismissed) },
        onBackClick = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncomeContent(
    title: String,
    amount: String,
    selectedTimePeriod: String,
    selectedCategory: String,
    isLoading: Boolean,
    error: String?,
    onTitleChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onTimePeriodChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onErrorDismiss: () -> Unit,
    onBackClick: () -> Unit
) {
    val uvgGreen = Color(0xFF4CAF50)
    val backgroundColor = Color(0xFFF5F5F5)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Agregar Ingreso",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, enabled = !isLoading) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
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
                        Column {
                            Text(
                                "Título del Ingreso",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF333333)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = title,
                                onValueChange = onTitleChange,
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Ej: Pago de beca mensual") },
                                enabled = !isLoading,
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

                        Column {
                            Text(
                                "Monto del Ingreso",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF333333)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = amount,
                                onValueChange = { newValue ->
                                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                        onAmountChange(newValue)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("0.00") },
                                enabled = !isLoading,
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
                                    IncomeTimePeriodOption(
                                        period = period,
                                        selected = selectedTimePeriod == period,
                                        onSelect = { onTimePeriodChange(period) },
                                        color = uvgGreen,
                                        enabled = !isLoading
                                    )
                                }
                            }
                        }

                        Divider(color = Color(0xFFE0E0E0))

                        Column {
                            Text(
                                "Categoría del Ingreso",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF333333)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            val categories = listOf(
                                "Salario" to "💼",
                                "Beca" to "🎓",
                                "Freelance" to "💻",
                                "Otros" to "💰"
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                categories.take(2).forEach { (category, emoji) ->
                                    IncomeCategoryOption(
                                        category = category,
                                        emoji = emoji,
                                        selected = selectedCategory == category,
                                        onSelect = { onCategoryChange(category) },
                                        modifier = Modifier.weight(1f),
                                        enabled = !isLoading
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                categories.drop(2).forEach { (category, emoji) ->
                                    IncomeCategoryOption(
                                        category = category,
                                        emoji = emoji,
                                        selected = selectedCategory == category,
                                        onSelect = { onCategoryChange(category) },
                                        modifier = Modifier.weight(1f),
                                        enabled = !isLoading
                                    )
                                }
                            }
                        }
                    }
                }

                if (error != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                error,
                                modifier = Modifier.weight(1f),
                                color = Color(0xFFD32F2F),
                                fontSize = 14.sp
                            )
                            TextButton(onClick = onErrorDismiss) {
                                Text("OK")
                            }
                        }
                    }
                }

                Button(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = uvgGreen
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            "Guardar Ingreso",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isLoading,
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
}

@Composable
fun IncomeTimePeriodOption(
    period: String,
    selected: Boolean,
    onSelect: () -> Unit,
    color: Color,
    enabled: Boolean = true
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        onClick = onSelect,
        enabled = enabled,
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
                enabled = enabled,
                colors = RadioButtonDefaults.colors(
                    selectedColor = color,
                    unselectedColor = Color(0xFFBDBDBD)
                )
            )
        }
    }
}

@Composable
fun IncomeCategoryOption(
    category: String,
    emoji: String,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val categoryColor = when (category) {
        "Salario" -> Color(0xFF2196F3)
        "Beca" -> Color(0xFF9C27B0)
        "Freelance" -> Color(0xFFFF9800)
        "Otros" -> Color(0xFF4CAF50)
        else -> Color.Gray
    }

    Card(
        modifier = modifier.height(100.dp),
        onClick = onSelect,
        enabled = enabled,
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