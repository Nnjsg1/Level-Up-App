package com.example.level_up_app.ui.login

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.level_up_app.screen.Fondo
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(
    viewModel: CreateAccountViewModel = remember { CreateAccountViewModel() },
    onCreate: (name: String, email: String, password: String, dob: String) -> Unit = { _, _, _, _ -> },
    onBackToLogin: () -> Unit = {}
) {
    val uiState by viewModel.FormData.collectAsState()
    val context = LocalContext.current

    // Navegar al Login cuando la cuenta sea creada exitosamente
    LaunchedEffect(uiState.isAccountCreated) {
        if (uiState.isAccountCreated) {
            onCreate(uiState.name, uiState.email, uiState.password, uiState.dob)
        }
    }

    // Configurar el DatePickerDialog
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Formato dd/MM/yyyy
                val fechaSeleccionada = String.format(
                    "%02d/%02d/%04d",
                    selectedDay,
                    selectedMonth + 1, // Los meses en Calendar van de 0-11
                    selectedYear
                )
                viewModel.actualizarDob(fechaSeleccionada)
            },
            year,
            month,
            day
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Fondo()
        Scaffold(containerColor = Color.Transparent) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Crea una cuenta nueva",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White)

                Text(
                    "¿Ya estás registrado? Inicia sesión acá.",
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable { onBackToLogin() },
                    color = Color.Cyan,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )

                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { viewModel.actualizarName(it) },
                    label = { Text("Nombre") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                )

                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { viewModel.actualizarEmail(it) },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.actualizarPassword(it) },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )

                // DatePicker para fecha de nacimiento
                val interactionSource = remember { MutableInteractionSource() }

                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect { interaction ->
                        if (interaction is PressInteraction.Release) {
                            datePickerDialog.show()
                        }
                    }
                }

                OutlinedTextField(
                    value = uiState.dob.ifEmpty { "" },
                    onValueChange = { },
                    label = { Text("Fecha de nacimiento (dd/mm/aaaa)") },
                    readOnly = true,
                    placeholder = { Text("Seleccionar fecha") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Seleccionar fecha"
                        )
                    },
                    interactionSource = interactionSource,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                uiState.error?.let {
                    if (it.isNotEmpty()) {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }

                Button(
                    onClick = { viewModel.CreateAccount() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Crear cuenta")
                }
            }
        }
    }
}