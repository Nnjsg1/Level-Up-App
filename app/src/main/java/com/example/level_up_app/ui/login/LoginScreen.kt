package com.example.level_up_app.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
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
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.level_up_app.R
import com.example.level_up_app.screen.Fondo
import com.example.level_up_app.screen.Fondo_2
import androidx.compose.material3.ButtonDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = remember { LoginViewModel() },
    onNavigateToCreateAccount: () -> Unit = {},
    onNavigateToRememberPass: () -> Unit = {},
    onNavigateToMain: () -> Unit = {}
){
    val uiState by viewModel.FormData.collectAsState()

    // Navegar al Main cuando el login sea exitoso
    LaunchedEffect(uiState.isLogin) {
        if (uiState.isLogin) {
            onNavigateToMain()
        }
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
                Image(painter = painterResource(R.drawable.logo),
                    contentDescription = "fondo_login",
                    modifier = Modifier
                        .height(100.dp)
                )
                Text(

                    "Inicio de sesión",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )

                Text(
                    "¿No tienes cuenta? Registrate acá.",
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable { onNavigateToCreateAccount() },
                    color = Color.Cyan,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )

                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { viewModel.actualizarEmail(it)},
                    label = { Text("Email")},
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Email Icon"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                )

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.actualizarPassword(it) },
                    label = { Text("Contraseña")},
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password Icon"
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
                Text(
                    "¿Olvidaste la contraseña?",
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.End)
                        .clickable { onNavigateToRememberPass() },
                    color = Color.Cyan,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
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
                    onClick = { viewModel.Login() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("Inicio Sesion")
                }
                Button(
                    colors= ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black,
                        Color.Transparent
                    ),
                    onClick = {onNavigateToMain()}
                ){
                }
            }
        }
    }
}

