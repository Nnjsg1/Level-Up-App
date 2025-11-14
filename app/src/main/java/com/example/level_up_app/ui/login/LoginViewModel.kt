package com.example.level_up_app.ui.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val formuLogin = MutableStateFlow(FormularioLogin())

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
            isLogin = false
        )
    }
    fun Login(){
        viewModelScope.launch {
            // conectar con la API
            // reglas de negocio (SET)
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
            if (f.error==null){
                formuLogin.value = formuLogin.value.copy(
                    error = "",
                    isLogin = true
                )
            }
        }

    }
}

