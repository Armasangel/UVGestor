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
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Gasto") }
    var showDialog by remember { mutableStateOf(false) }
    
    // Colores del tema
    val backgroundColor = Color(0xFFF8FCF8)
    val primaryGreen = Color(0xFF479E47)
    val lightGreen = Color(0xFFE6F4E6)
    val darkText = Color(0xFF0D1C0D)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Agregar Transacción",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = darkText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = darkText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo de título
            Text(
                "Título",
                style = MaterialTheme.typography.titleMedium,
                color = darkText,
                fontWeight = FontWeight.SemiBold
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ej: Compra de libros") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = primaryGreen,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = primaryGreen
                ),
                shape = RoundedCornerShape(12.dp)
            )
            
            // Campo de monto
            Text(
                "Monto (Q)",
                style = MaterialTheme.typography.titleMedium,
                color = darkText,
                fontWeight = FontWeight.SemiBold
            )
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("0.00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = primaryGreen,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = primaryGreen
                ),
                shape = RoundedCornerShape(12.dp)
            )
            
            // Tipo de transacción
            Text(
                "Tipo de transacción",
                style = MaterialTheme.typography.titleMedium,
                color = darkText,
                fontWeight = FontWeight.SemiBold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botón Gasto
                Button(
                    onClick = { selectedType = "Gasto" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedType == "Gasto") primaryGreen else lightGreen,
                        contentColor = if (selectedType == "Gasto") Color.White else darkText
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Gasto", fontWeight = FontWeight.Medium)
                }
                
                // Botón Ingreso
                Button(
                    onClick = { selectedType = "Ingreso" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedType == "Ingreso") primaryGreen else lightGreen,
                        contentColor = if (selectedType == "Ingreso") Color.White else darkText
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Ingreso", fontWeight = FontWeight.Medium)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botón guardar
            Button(
                onClick = {
                    if (title.isNotBlank() && amount.isNotBlank()) {
                        showDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryGreen,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = title.isNotBlank() && amount.isNotBlank()
            ) {
                Text(
                    "Guardar Transacción",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
    
    // Diálogo de confirmación
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("¡Transacción guardada!") },
            text = { Text("Tu $selectedType de Q$amount ha sido registrado exitosamente.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("Aceptar", color = primaryGreen)
                }
            },
            containerColor = Color.White
        )
    }
}
