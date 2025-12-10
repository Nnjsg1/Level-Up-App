package com.example.level_up_app.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.level_up_app.ui.viewmodel.UserUiState
import com.example.level_up_app.ui.viewmodel.UserViewModel

@Composable
fun UserListScreen(viewModel: UserViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadUsers() }

    when (val s = state) {
        is UserUiState.Idle -> Text("Cargando usuarios...")
        is UserUiState.Loading -> CircularProgressIndicator()
        is UserUiState.Success -> {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(s.users) { u ->
                    Text("${u.name} - ${u.email}")
                }
            }
        }
        is UserUiState.Error -> Text("Error: ${s.message}")
    }
}

@Composable
fun CreateUserScreen(viewModel: UserViewModel = viewModel()) {
    val name = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val clave = remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Nombre") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = email.value, onValueChange = { email.value = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = clave.value, onValueChange = { clave.value = it }, label = { Text("Clave") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.addUser(name.value, email.value, clave.value) }) {
            Text("Crear usuario")
        }
    }
}

