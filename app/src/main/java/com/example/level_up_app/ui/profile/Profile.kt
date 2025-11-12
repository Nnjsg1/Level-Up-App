package com.example.level_up_app.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Box

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        //Aqui hago el circulo para almacenar o que muestre una imagen
        Surface(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(120.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        ) {
            // Use una caja para centrar la imagen y que este en medio y bonito
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar placeholder",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        SnackbarHost(hostState = snackbarHostState)

        Text(text = "NOMBRE")
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Text(text = "EMAIL", modifier = Modifier.padding(top = 12.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Text(text = "CONTRASEÑA", modifier = Modifier.padding(top = 12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Button(
            onClick = {
                scope.launch { snackbarHostState.showSnackbar("Guardado") }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text(text = "Guardar Cambios")
        }
    }
}
