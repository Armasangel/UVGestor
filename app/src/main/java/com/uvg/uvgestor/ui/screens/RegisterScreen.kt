package com.uvg.uvgestor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.uvg.uvgestor.navigation.Screen
import com.uvg.uvgestor.presentation.viewmodel.auth.RegisterUiEvent
import com.uvg.uvgestor.presentation.viewmodel.auth.RegisterViewModel

@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.registerSuccess) {
        uiState.registerSuccess?.let {
            viewModel.onEvent(RegisterUiEvent.RegisterSuccessHandled)
            navController.navigate(Screen.MAIN_GRAPH_ROUTE) {
                popUpTo(Screen.AUTH_GRAPH_ROUTE) {
                    inclusive = true
                    saveState = false
                }
                launchSingleTop = true
            }
        }
    }

    RegisterContent(
        name = uiState.name,
        email = uiState.email,
        password = uiState.password,
        isLoading = uiState.isLoading,
        error = uiState.error,
        requiresGuardian = uiState.requiresGuardian,
        guardianName = uiState.guardianName,
        guardianEmail = uiState.guardianEmail,
        guardianRelationship = uiState.guardianRelationship,
        onNameChange = { viewModel.onEvent(RegisterUiEvent.NameChanged(it)) },
        onEmailChange = { viewModel.onEvent(RegisterUiEvent.EmailChanged(it)) },
        onPasswordChange = { viewModel.onEvent(RegisterUiEvent.PasswordChanged(it)) },
        onRequiresGuardianChange = { viewModel.onEvent(RegisterUiEvent.RequiresGuardianChanged(it)) },
        onGuardianNameChange = { viewModel.onEvent(RegisterUiEvent.GuardianNameChanged(it)) },
        onGuardianEmailChange = { viewModel.onEvent(RegisterUiEvent.GuardianEmailChanged(it)) },
        onGuardianRelationshipChange = { viewModel.onEvent(RegisterUiEvent.GuardianRelationshipChanged(it)) },
        onRegisterClick = { viewModel.onEvent(RegisterUiEvent.RegisterClicked) },
        onErrorDismiss = { viewModel.onEvent(RegisterUiEvent.ErrorDismissed) },
        onBackClick = { navController.popBackStack() },
        onLoginClick = {
            navController.popBackStack()
            navController.navigate(Screen.Login.route)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterContent(
    name: String,
    email: String,
    password: String,
    isLoading: Boolean,
    error: String?,
    requiresGuardian: Boolean,
    guardianName: String,
    guardianEmail: String,
    guardianRelationship: String,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRequiresGuardianChange: (Boolean) -> Unit,
    onGuardianNameChange: (String) -> Unit,
    onGuardianEmailChange: (String) -> Unit,
    onGuardianRelationshipChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onErrorDismiss: () -> Unit,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Registro de usuario",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Menú */ }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menú"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
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
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            color = Color(0xFFE8F5E9),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "UVG",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Bienvenido",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F1F1F),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Crea tu cuenta en UVGestor",
                    fontSize = 14.sp,
                    color = Color(0xFF9E9E9E),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Campo Nombre
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Nombre completo",
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = onNameChange,
                        placeholder = { Text("Tu nombre", color = Color(0xFFBDBDBD)) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedBorderColor = Color(0xFF00C853)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Campo Email
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Correo electrónico",
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        placeholder = { Text("tu@email.com", color = Color(0xFFBDBDBD)) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedBorderColor = Color(0xFF00C853)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Campo Contraseña
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Contraseña",
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        placeholder = { Text("Tu contraseña", color = Color(0xFFBDBDBD)) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(
                                    text = if (passwordVisible) "👁️" else "👁️",
                                    fontSize = 20.sp,
                                    color = Color(0xFF9E9E9E)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedBorderColor = Color(0xFF00C853)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider(color = Color(0xFFE0E0E0))

                Spacer(modifier = Modifier.height(24.dp))

                // Checkbox para registrar tutor
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isLoading) {
                            onRequiresGuardianChange(!requiresGuardian)
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = requiresGuardian,
                        onCheckedChange = onRequiresGuardianChange,
                        enabled = !isLoading,
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF00C853),
                            uncheckedColor = Color(0xFFBDBDBD)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Registrar tutor/padre",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1F1F1F)
                        )
                        Text(
                            text = "Para estudiantes menores de edad",
                            fontSize = 12.sp,
                            color = Color(0xFF9E9E9E)
                        )
                    }
                }

                // Mostrar campos de tutor si está marcado
                if (requiresGuardian) {
                    Spacer(modifier = Modifier.height(20.dp))

                    // Información del tutor
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F9FF)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Información del Tutor",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F1F1F)
                            )

                            Text(
                                text = "El tutor tendrá acceso a tu información financiera",
                                fontSize = 12.sp,
                                color = Color(0xFF666666),
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Campo Nombre del Tutor
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Nombre del tutor *",
                                    fontSize = 14.sp,
                                    color = Color(0xFF666666),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                OutlinedTextField(
                                    value = guardianName,
                                    onValueChange = onGuardianNameChange,
                                    placeholder = { Text("Nombre completo", color = Color(0xFFBDBDBD)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !isLoading,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedBorderColor = Color(0xFFE0E0E0),
                                        focusedBorderColor = Color(0xFF00C853),
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Campo Email del Tutor
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Correo del tutor *",
                                    fontSize = 14.sp,
                                    color = Color(0xFF666666),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                OutlinedTextField(
                                    value = guardianEmail,
                                    onValueChange = onGuardianEmailChange,
                                    placeholder = { Text("tutor@email.com", color = Color(0xFFBDBDBD)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !isLoading,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedBorderColor = Color(0xFFE0E0E0),
                                        focusedBorderColor = Color(0xFF00C853),
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    singleLine = true
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Selector de Relación
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Relación *",
                                    fontSize = 14.sp,
                                    color = Color(0xFF666666),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                val relationships = listOf("Padre", "Madre", "Tutor Legal")

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    relationships.forEach { relationship ->
                                        GuardianRelationshipOption(
                                            relationship = relationship,
                                            selected = guardianRelationship == relationship,
                                            onSelect = { onGuardianRelationshipChange(relationship) },
                                            enabled = !isLoading
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón Registrarse
                Button(
                    onClick = onRegisterClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00C853)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = "Crear cuenta",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFE0E0E0)
                    )
                    Text(
                        text = "  o  ",
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFE0E0E0)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¿Ya tienes cuenta?  ",
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E)
                    )
                    TextButton(
                        onClick = onLoginClick,
                        contentPadding = PaddingValues(0.dp),
                        enabled = !isLoading
                    ) {
                        Text(
                            text = "Iniciar sesión",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF00C853)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }

            error?.let {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = onErrorDismiss) {
                            Text("OK")
                        }
                    }
                ) {
                    Text(it)
                }
            }
        }
    }
}

@Composable
fun GuardianRelationshipOption(
    relationship: String,
    selected: Boolean,
    onSelect: () -> Unit,
    enabled: Boolean = true
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        onClick = onSelect,
        enabled = enabled,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFF00C853).copy(alpha = 0.1f) else Color.White
        ),
        border = if (selected) {
            androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF00C853))
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
        },
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                relationship,
                fontSize = 16.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) Color(0xFF00C853) else Color(0xFF333333)
            )

            RadioButton(
                selected = selected,
                onClick = onSelect,
                enabled = enabled,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFF00C853),
                    unselectedColor = Color(0xFFBDBDBD)
                )
            )
        }
    }
}