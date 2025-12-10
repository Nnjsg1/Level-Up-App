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
                    loadUsers()
                }
                .onFailure { _state.value = UserUiState.Error(it.message ?: "Error desconocido") }
        }
    }
}

