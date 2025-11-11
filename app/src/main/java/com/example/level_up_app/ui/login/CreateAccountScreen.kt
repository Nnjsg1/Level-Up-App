package com.example.level_up_app.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.level_up_app.screen.Fondo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(
    onCreate: (name: String, email: String, password: String, dob: String) -> Unit = { _, _, _, _ -> },
    onBackToLogin: () -> Unit = {}
) {
    val name = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val dob = remember { mutableStateOf("") } // placeholder string for date

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
                Text("Crea una cuenta nueva", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "¿Ya estás registrado? Iniciá sesión acá.",
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable { onBackToLogin() },
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = { Text("Nombre") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                )

                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )

                OutlinedTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )

                // Simple DOB placeholder
                OutlinedTextField(
                    value = dob.value.ifEmpty { "Seleccionar" },
                    onValueChange = { /* no direct edit - replace with date picker logic if desired */ },
                    label = { Text("Fecha de nacimiento") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .clickable {
                            // placeholder: open date picker or set a fixed value for now
                            dob.value = "01/01/2000"
                        }
                )

                Button(
                    onClick = { onCreate(name.value, email.value, password.value, dob.value) },
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