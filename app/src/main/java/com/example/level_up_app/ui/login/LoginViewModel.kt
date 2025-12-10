package com.example.level_up_app.ui.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.level_up_app.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val formuLogin = MutableStateFlow(FormularioLogin())
    private val userRepository = UserRepository()

    // en la Screen ocupare el FormData
    val FormData: StateFlow<FormularioLogin> = formuLogin.asStateFlow()

    // crearemos metodos que permitan actualizar el valor de los atributos
    // de FormularioLogin

    fun actualizarEmail(email: String){
        formuLogin.value = formuLogin.value.copy(
            email = email
        )
    }
    fun actualizarPassword(pass: String){
        formuLogin.value = formuLogin.value.copy(
            password = pass
        )
    }

    private fun validarEmail(email: String): Boolean{
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    private fun errorNull(){
        formuLogin.value = formuLogin.value.copy(
            error = null,
        )
    }
    private fun mensajeError(mensaje: String){
        formuLogin.value = formuLogin.value.copy(
            error = mensaje,
            isLogin = false,
            isLoading = false
        )
    }

    fun Login(){
        viewModelScope.launch {
            val f = formuLogin.value

            // Validar que el email no esté vacío
            if(f.email.isEmpty()){
                mensajeError("Por favor ingresa tu email")
                return@launch
            }

            if(f.email.length<6){
                mensajeError("Email inválido")
                return@launch
            }else{
                errorNull()
            }
            if(!validarEmail(f.email)){
                mensajeError("Email inválido")
                return@launch
            }else{
                errorNull()
            }

            // Validar que la contraseña no esté vacía
            if(f.password.isEmpty()){
                mensajeError("Por favor ingresa tu contraseña")
                return@launch
            }

            // Validar contraseña >= 5 caracteres
            if (f.password.length >= 5){
                errorNull()
            }else{
                mensajeError("La contraseña debe tener al menos 5 caracteres")
                return@launch
            }

            // Si todas las validaciones pasan, intentar login con el backend
            if (f.error == null) {
                // Mostrar estado de carga
                formuLogin.value = formuLogin.value.copy(
                    isLoading = true,
                    error = null
                )

                try {
                    // Llamar al repositorio para hacer login (usando 'clave')
                    val response = userRepository.login(f.email, f.password)

                    if (response.success) {
                        // Login exitoso
                        Log.d("LoginViewModel", "Login exitoso: ${response.user?.name}")
                        formuLogin.value = formuLogin.value.copy(
                            error = null,
                            isLogin = true,
                            isLoading = false,
                            user = response.user
                        )
                    } else {
                        // Login fallido
                        Log.e("LoginViewModel", "Login fallido: ${response.message}")
                        mensajeError(response.message)
                    }
                } catch (e: Exception) {
                    Log.e("LoginViewModel", "Error en login: ${e.message}")
                    mensajeError("Error de conexión: ${e.localizedMessage}")
                }
            }
        }

    }

    // Método para limpiar el estado cuando el usuario cierra sesión
    fun limpiarEstado() {
        formuLogin.value = FormularioLogin()
    }
}

