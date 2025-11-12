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
import com.uvg.uvgestor.presentation.viewmodel.budget.BudgetUiEvent
import com.uvg.uvgestor.presentation.viewmodel.budget.BudgetViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BudgetScreen(
    navController: NavHostController,
    viewModel: BudgetViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            viewModel.onEvent(BudgetUiEvent.SaveSuccessHandled)
            navController.popBackStack()
        }
    }

    if (uiState.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(BudgetUiEvent.CancelDelete) },
            title = {
                Text(
                    text = "Eliminar Presupuesto",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("¿Estás seguro que deseas eliminar el presupuesto mensual? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onEvent(BudgetUiEvent.ConfirmDelete) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onEvent(BudgetUiEvent.CancelDelete) }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    BudgetContent(
        limitAmount = uiState.limitAmount,
        currentBudget = uiState.currentBudget,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onLimitAmountChange = { viewModel.onEvent(BudgetUiEvent.LimitAmountChanged(it)) },
        onSaveClick = { viewModel.onEvent(BudgetUiEvent.SaveClicked) },
        onDeleteClick = { viewModel.onEvent(BudgetUiEvent.DeleteClicked) },
        onErrorDismiss = { viewModel.onEvent(BudgetUiEvent.ErrorDismissed) },
        onBackClick = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetContent(
    limitAmount: String,
    currentBudget: com.uvg.uvgestor.ui.data.Budget?,
    isLoading: Boolean,
    error: String?,
    onLimitAmountChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onErrorDismiss: () -> Unit,
    onBackClick: () -> Unit
) {
    val uvgGreen = Color(0xFF00C853)
    val backgroundColor = Color(0xFFF5F5F5)

    val calendar = Calendar.getInstance()
    val currentMonth = SimpleDateFormat("MMMM yyyy", Locale("es", "GT")).format(calendar.time)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Presupuesto Mensual",
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
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            "📊 Acerca del Presupuesto",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Configura un límite mensual de gastos para mantener tus finanzas bajo control. Recibirás alertas cuando alcances el 80% y 100% de tu presupuesto.",
                            fontSize = 14.sp,
                            color = Color(0xFF333333),
                            lineHeight = 20.sp
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Presupuesto de $currentMonth",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )

                        if (currentBudget != null) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFFF3E0)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        "Presupuesto Actual",
                                        fontSize = 12.sp,
                                        color = Color(0xFF666666)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Q${String.format("%.2f", currentBudget.limitAmount)}",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFF6F00)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "✅ Alertas activadas al 80% y 100%",
                                        fontSize = 12.sp,
                                        color = Color(0xFF666666)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                "Actualizar Límite:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF333333)
                            )
                        } else {
                            Text(
                                "No tienes un presupuesto configurado para este mes. ¡Créalo ahora!",
                                fontSize = 14.sp,
                                color = Color(0xFF666666)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                "Límite de Gastos:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF333333)
                            )
                        }

                        OutlinedTextField(
                            value = limitAmount,
                            onValueChange = { newValue ->
                                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    onLimitAmountChange(newValue)
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

                        Text(
                            "💡 Consejo: Establece un límite realista basado en tus ingresos mensuales.",
                            fontSize = 12.sp,
                            color = Color(0xFF666666),
                            lineHeight = 18.sp
                        )
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
                            if (currentBudget != null) "Actualizar Presupuesto" else "Crear Presupuesto",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                if (currentBudget != null) {
                    OutlinedButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFD32F2F)
                        )
                    ) {
                        Text(
                            "Eliminar Presupuesto",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
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