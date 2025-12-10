package com.example.level_up_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.level_up_app.data.model.User
import com.example.level_up_app.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UserUiState {
    object Idle : UserUiState()
    object Loading : UserUiState()
    data class Success(val users: List<User>) : UserUiState()
    data class Error(val message: String) : UserUiState()
}

class UserViewModel : ViewModel() {
    private val repo = UserRepository()

    private val _state = MutableStateFlow<UserUiState>(UserUiState.Idle)
    val state: StateFlow<UserUiState> = _state.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun loadUsers() {
        viewModelScope.launch {
            _state.value = UserUiState.Loading
            repo.getAllUsers()
                .onSuccess { _state.value = UserUiState.Success(it) }
                .onFailure { _state.value = UserUiState.Error(it.message ?: "Error desconocido") }
        }
    }

    fun addUser(name: String, email: String, clave: String, isAdmin: Boolean = false) {
        viewModelScope.launch {
            _state.value = UserUiState.Loading
            repo.createUser(User(name = name, email = email, clave = clave, isAdmin = isAdmin))
                .onSuccess {
                    // tras crear, refrescar lista
                    _snackbarMessage.value = "Usuario creado exitosamente"
                    loadUsers()
                }
                .onFailure { _state.value = UserUiState.Error(it.message ?: "Error desconocido") }
        }
    }

    fun updateUser(id: Int, name: String, email: String, clave: String, isAdmin: Boolean) {
        viewModelScope.launch {
            try {
                repo.updateUser(id, User(id = id, name = name, email = email, clave = clave, isAdmin = isAdmin))
                    .onSuccess {
                        _snackbarMessage.value = "Usuario actualizado exitosamente"
                        loadUsers()
                    }
                    .onFailure {
                        _snackbarMessage.value = "Error al actualizar: ${it.message}"
                    }
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al actualizar: ${e.message}"
            }
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            try {
                repo.deleteUser(id)
                    .onSuccess {
                        _snackbarMessage.value = "Usuario eliminado exitosamente"
                        loadUsers()
                    }
                    .onFailure {
                        _snackbarMessage.value = "Error al eliminar: ${it.message}"
                    }
            } catch (e: Exception) {
                _snackbarMessage.value = "Error al eliminar: ${e.message}"
            }
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validateName(name: String): Boolean {
        return name.length >= 3
    }
}

