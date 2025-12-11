package com.example.level_up_app.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.level_up_app.data.User
import com.example.level_up_app.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileEditState(
    val name: String = "",
    val clave: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class ProfileEditViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileEditState())
    val uiState: StateFlow<ProfileEditState> = _uiState.asStateFlow()

    private val userRepository = UserRepository()

    fun setInitialData(name: String) {
        _uiState.value = _uiState.value.copy(
            name = name,
            clave = ""
        )
    }

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name, error = null)
    }

    fun updateClave(clave: String) {
        _uiState.value = _uiState.value.copy(clave = clave, error = null)
    }

    fun saveChanges(userId: Long, onSuccess: (User) -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value

            // Validaciones
            if (state.name.isEmpty()) {
                _uiState.value = state.copy(error = "El nombre no puede estar vacío")
                return@launch
            }

            if (state.name.length < 2) {
                _uiState.value = state.copy(error = "El nombre debe tener al menos 2 caracteres")
                return@launch
            }

            if (state.clave.isNotEmpty() && state.clave.length < 5) {
                _uiState.value = state.copy(error = "La contraseña debe tener al menos 5 caracteres")
                return@launch
            }

            // Mostrar estado de carga
            _uiState.value = state.copy(isLoading = true, error = null)

            try {
                // Si no se ingresó contraseña nueva, usar un valor por defecto o mantener la actual
                val claveToUpdate = if (state.clave.isEmpty()) {
                    // Aquí podrías manejar de otra forma si el backend lo requiere
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "Debes ingresar la nueva contraseña"
                    )
                    return@launch
                } else {
                    state.clave
                }

                val response = userRepository.updateUser(userId, state.name, claveToUpdate)

                if (response.success && response.user != null) {
                    Log.d("ProfileEditViewModel", "Usuario actualizado exitosamente")
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = null,
                        isSuccess = true
                    )
                    onSuccess(response.user)
                } else {
                    Log.e("ProfileEditViewModel", "Error: ${response.message}")
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = response.message
                    )
                }
            } catch (e: Exception) {
                Log.e("ProfileEditViewModel", "Exception: ${e.message}", e)
                _uiState.value = state.copy(
                    isLoading = false,
                    error = "Error de conexión: ${e.localizedMessage}"
                )
            }
        }
    }

    fun resetSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
}

