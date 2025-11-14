package com.example.level_up_app.ui.login

import android.util.Patterns
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.level_up_app.screen.Fondo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RememberPassScreen(
    onBack: () -> Unit = {}
) {
    val email = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Fondo()
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "¿Olvidaste la contraseña?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Text(
                    text = "Puedes crear una nueva.",
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.White
                )

                OutlinedTextField(
                    value = email.value,
                    onValueChange = {
                        email.value = it
                        errorMessage.value = ""
                    },
                    label = { Text("EMAIL") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                )

                if (errorMessage.value.isNotEmpty()) {
                    Text(
                        text = errorMessage.value,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Button(
                    onClick = {
                        // Validar email
                        if (email.value.isEmpty()) {
                            errorMessage.value = "Por favor ingresa tu email"
                        } else if (email.value.length < 6) {
                            errorMessage.value = "El email es invalido"
                        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
                            errorMessage.value = "No tiene formato de email"
                        } else {
                            errorMessage.value = ""
                            scope.launch { snackbarHostState.showSnackbar("Correo enviado") }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    Text("Enviar")
                }

                TextButton(
                    onClick = { onBack() },
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text("Volver")
                }
            }
        }
    }
}
