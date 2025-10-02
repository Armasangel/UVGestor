/*package com.uvg.uvgestor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(navController: NavController) {
    var selectedCategory by remember { mutableStateOf("Comida") }
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var isMonthlyPayment by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(13) }
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var expandedCategory by remember { mutableStateOf(false) }
    
    // Color verde de UVG
    val uvgGreen = Color(0xFF00A859)
    val lightGray = Color(0xFFF9FAFB)
    
    val categories = listOf(
        "Comida",
        "Transporte",
        "Ocio",
        "Materiales de estudio",
        "Otros"
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Agregar Gasto",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBackIos,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(32.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = uvgGreen
                )
            )
        },
        containerColor = lightGray
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Categoría
                Column {
                    Text(
                        "Categoría",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    ExposedDropdownMenuBox(
                        expanded = expandedCategory,
                        onExpandedChange = { expandedCategory = it }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = uvgGreen,
                                unfocusedBorderColor = Color(0xFFD1D5DB),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { expandedCategory = false }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        selectedCategory = category
                                        expandedCategory = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Nombre
                Column {
                    Text(
                        "Nombre",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ej: Almuerzo en la cafetería") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = uvgGreen,
                            unfocusedBorderColor = Color(0xFFD1D5DB),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = uvgGreen
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                
                // Precio
                Column {
                    Text(
                        "Precio",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("0.00") },
                        leadingIcon = {
                            Text(
                                "Q",
                                color = Color(0xFF6B7280),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = uvgGreen,
                            unfocusedBorderColor = Color(0xFFD1D5DB),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = uvgGreen
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                
                // Tipo
                Column {
                    Text(
                        "Tipo",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF374151)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tipo,
                        onValueChange = { tipo = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ej: Gasto único, Suscripción") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = uvgGreen,
                            unfocusedBorderColor = Color(0xFFD1D5DB),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = uvgGreen
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                
                // Calendario
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Header del calendario
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                currentMonth.add(Calendar.MONTH, -1)
                                currentMonth = currentMonth.clone() as Calendar
                            }) {
                                Icon(
                                    Icons.Default.ArrowBackIos,
                                    contentDescription = "Mes anterior",
                                    tint = Color(0xFF6B7280),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            
                            Text(
                                SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))
                                    .format(currentMonth.time)
                                    .replaceFirstChar { it.uppercase() },
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1F2937)
                            )
                            
                            IconButton(onClick = {
                                currentMonth.add(Calendar.MONTH, 1)
                                currentMonth = currentMonth.clone() as Calendar
                            }) {
                                Icon(
                                    Icons.Default.ArrowForwardIos,
                                    contentDescription = "Mes siguiente",
                                    tint = Color(0xFF6B7280),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Días de la semana
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            listOf("Do", "Lu", "Ma", "Mi", "Ju", "Vi", "Sá").forEach { day ->
                                Text(
                                    day,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFF6B7280),
                                    fontSize = 12.sp
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Grid de días
                        CalendarGrid(
                            currentMonth = currentMonth,
                            selectedDate = selectedDate,
                            onDateSelected = { selectedDate = it },
                            uvgGreen = uvgGreen
                        )
                    }
                }
                
                // Checkbox pago mensual
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isMonthlyPayment,
                        onCheckedChange = { isMonthlyPayment = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = uvgGreen,
                            uncheckedColor = Color(0xFFD1D5DB)
                        )
                    )
                    Text(
                        "Configurar como pago mensual",
                        fontSize = 14.sp,
                        color = Color(0xFF1F2937)
                    )
                }
            }
            
            // Footer con botón de voz
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloatingActionButton(
                    onClick = { /* Acción de voz */ },
                    containerColor = Color(0xFFEF4444),
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = "Grabar por voz",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Text(
                    "O prueba nuestra nueva opción de llenado por voz",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun CalendarGrid(
    currentMonth: Calendar,
    selectedDate: Int,
    onDateSelected: (Int) -> Unit,
    uvgGreen: Color
) {
    val calendar = currentMonth.clone() as Calendar
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    
    val prevMonth = currentMonth.clone() as Calendar
    prevMonth.add(Calendar.MONTH, -1)
    val daysInPrevMonth = prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    
    Column {
        var dayCounter = 1
        var prevMonthDay = daysInPrevMonth - firstDayOfWeek + 1
        
        for (week in 0..5) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (dayOfWeek in 0..6) {
                    val index = week * 7 + dayOfWeek
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            index < firstDayOfWeek -> {
                                // Días del mes anterior
                                Text(
                                    text = prevMonthDay.toString(),
                                    color = Color(0xFFD1D5DB),
                                    fontSize = 14.sp
                                )
                                prevMonthDay++
                            }
                            dayCounter <= daysInMonth -> {
                                // Días del mes actual
                                val isSelected = dayCounter == selectedDate
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(if (isSelected) uvgGreen else Color.Transparent)
                                        .clickable { onDateSelected(dayCounter) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayCounter.toString(),
                                        color = if (isSelected) Color.White else Color(0xFF1F2937),
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                                dayCounter++
                            }
                        }
                    }
                }
            }
            
            if (dayCounter > daysInMonth) break
        }
    }
}
*/