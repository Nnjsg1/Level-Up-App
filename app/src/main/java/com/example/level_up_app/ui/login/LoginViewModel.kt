package com.example.level_up_app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginFormState(
    val nombre: String = "",
    val password: String = "",
    val nombreError: String? = null,
    val passwordError: String? = null
)

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginFormState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<String>()
    val events = _events.asSharedFlow()

    fun onNombreChange(value: String) {
        _uiState.update { it.copy(nombre = value, nombreError = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun login() {
        val state = _uiState.value
        val nombreError = if (state.nombre.isBlank()) "Nombre requerido" else null
        val passwordError = if (state.password.isBlank()) "Contraseña requerida" else null

        _uiState.update { it.copy(nombreError = nombreError, passwordError = passwordError) }

        if (nombreError == null && passwordError == null) {
            viewModelScope.launch {
                _events.emit("Login exitoso")
            }
        }
    }
}
