/*package com.uvg.uvgestor.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.uvg.uvgestor.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialAdviceScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    
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
                        "Consejos Financieros",
                        modifier = Modifier.fillMaxWidth(),
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
        bottomBar = {
            NavigationBar(
                containerColor = backgroundColor,
                contentColor = primaryGreen
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio", fontSize = 12.sp) },
                    selected = false,
                    onClick = { navController.navigate(Screen.Home.route) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryGreen,
                        selectedTextColor = primaryGreen,
                        unselectedIconColor = primaryGreen,
                        unselectedTextColor = primaryGreen,
                        indicatorColor = lightGreen
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = "Agregar") },
                    label = { Text("+", fontSize = 12.sp) },
                    selected = false,
                    onClick = { navController.navigate(Screen.AddExpense.route) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryGreen,
                        selectedTextColor = primaryGreen,
                        unselectedIconColor = primaryGreen,
                        unselectedTextColor = primaryGreen,
                        indicatorColor = lightGreen
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Ajustes") },
                    label = { Text("Ajustes", fontSize = 12.sp) },
                    selected = true,
                    onClick = { /* Ya estamos aquí */ },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = darkText,
                        selectedTextColor = darkText,
                        unselectedIconColor = primaryGreen,
                        unselectedTextColor = primaryGreen,
                        indicatorColor = lightGreen
                    )
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
        ) {
            // Barra de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                placeholder = {
                    Text(
                        "¿Qué tipo de consejo buscas?",
                        color = primaryGreen
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = primaryGreen
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = lightGreen,
                    unfocusedContainerColor = lightGreen,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = primaryGreen
                ),
                shape = RoundedCornerShape(12.dp)
            )
            
            // Filtros
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip("Gastos", lightGreen, darkText)
                FilterChip("Ahorro", lightGreen, darkText)
                FilterChip("Inversión", lightGreen, darkText)
            }
            
            // Título y descripción
            Text(
                text = "¿Por qué es importante tu salud financiera?",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = darkText,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            )
            
            Text(
                text = "Una buena administración financiera universitaria es crucial porque reduce el estrés, te permite concentrarte en tus estudios y evita deudas peligrosas. Por eso dejamos ésta parte para tus dudas, problemas o lo que sea que necesites.",
                style = MaterialTheme.typography.bodyMedium,
                color = darkText,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Imagen ilustrativa (placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .aspectRatio(2f / 3f)
                    .background(
                        color = Color(0xFFE8D5C4),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Imagen ilustrativa",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun FilterChip(text: String, backgroundColor: Color, textColor: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor,
        modifier = Modifier.height(32.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
*/