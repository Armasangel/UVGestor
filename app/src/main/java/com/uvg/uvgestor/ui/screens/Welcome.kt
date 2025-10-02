package com.uvg.uvgestor.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

import com.uvg.uvgestor.navigation.Screen

@Composable
fun WelcomeScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Sección superior con imagen y textos
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {

                // Por ahora usamos un placeholder
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .padding(bottom = 32.dp),
                    contentAlignment = Alignment.Center
                ) {



                    // Placeholder
                    Text(
                        text = "💰",
                        fontSize = 120.sp,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                }

                // Título
                Text(
                    text = "Bienvenido a UVGestor",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F1F1F),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Descripción
                Text(
                    text = "Gestiona tus finanzas universitarias de forma\nsencilla y eficiente. Regístrate o inicia sesión\npara comenzar.",
                    fontSize = 15.sp,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }

            // Botones en la parte inferior
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Botón Registrarse (verde)
                Button(
                    onClick = {
                        // TODO: Navegar a pantalla de registro cuando esté implementada
                        // Por ahora navega al Home
                        navController.navigate(Screen.Home.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00E676)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Registrarse",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botón Iniciar Sesión (gris claro)
                OutlinedButton(
                    onClick = {
                        // TODO: Navegar a pantalla de inicio de sesión cuando esté implementada
                        // Por ahora navega al Home
                        navController.navigate(Screen.Login.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFFF5F5F5),
                        contentColor = Color(0xFF333333)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = null
                ) {
                    Text(
                        text = "Iniciar Sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Footer
                Text(
                    text = "© 2024 UVGestor. Todos los derechos reservados.",
                    fontSize = 12.sp,
                    color = Color(0xFF00E676),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}