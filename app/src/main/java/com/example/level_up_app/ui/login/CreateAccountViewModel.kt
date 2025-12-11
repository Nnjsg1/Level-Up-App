package com.example.level_up_app.ui.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.level_up_app.data.model.User
import com.example.level_up_app.data.repository.UserRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateAccountViewModel : ViewModel() {
    private val formuCreateAccount = MutableStateFlow(FormularioCreateAccount())

    val FormData: StateFlow<FormularioCreateAccount> = formuCreateAccount.asStateFlow()

    private val userRepository = UserRepository()
    private val TAG = "CreateAccountViewModel"

    fun actualizarName(name: String){
        formuCreateAccount.value = formuCreateAccount.value.copy(
            name = name
        )
    }

    fun actualizarEmail(email: String){
        formuCreateAccount.value = formuCreateAccount.value.copy(
            email = email
        )
    }

    fun actualizarPassword(pass: String){
        formuCreateAccount.value = formuCreateAccount.value.copy(
            password = pass
        )
    }

    fun actualizarDob(dob: String){
        formuCreateAccount.value = formuCreateAccount.value.copy(
            dob = dob
        )
    }

    private fun validarEmail(email: String): Boolean{
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun errorNull(){
        formuCreateAccount.value = formuCreateAccount.value.copy(
            error = null,
        )
    }

    private fun mensajeError(mensaje: String){
        formuCreateAccount.value = formuCreateAccount.value.copy(
            error = mensaje,
            isAccountCreated = false
        )
    }

    private fun calcularEdad(fechaNacimiento: String): Int {
        return try {
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaNac = formato.parse(fechaNacimiento) ?: return 0

            val calNacimiento = Calendar.getInstance()
            calNacimiento.time = fechaNac

            val calHoy = Calendar.getInstance()

            var edad = calHoy.get(Calendar.YEAR) - calNacimiento.get(Calendar.YEAR)

            // Ajustar si no ha cumplido años este año
            if (calHoy.get(Calendar.DAY_OF_YEAR) < calNacimiento.get(Calendar.DAY_OF_YEAR)) {
                edad--
            }

            edad
        } catch (e: Exception) {
            0
        }
    }

    fun CreateAccount(){
        viewModelScope.launch {
            Log.d(TAG, "Iniciando validación de cuenta...")

            val f = formuCreateAccount.value

            // Validar nombre
            if(f.name.isEmpty()){
                mensajeError("El nombre es requerido")
                return@launch
            }else if(f.name.length < 3){
                mensajeError("El nombre debe tener al menos 3 caracteres")
                return@launch
            }else{
                errorNull()
            }

            // Validar email
            if(f.email.length < 6){
                mensajeError("El email es muy pequeño")
                return@launch
            }else{
                errorNull()
            }

            if(!validarEmail(f.email)){
                mensajeError("No tiene formato de email")
                return@launch
            }else{
                errorNull()
            }

            // Validar contraseña (igual o mayor a 5 caracteres)
            if (f.password.length >= 5){
                errorNull()
            }else{
                mensajeError("La contraseña debe tener al menos 5 caracteres")
                return@launch
            }

            // Validar fecha de nacimiento
            if(f.dob.isEmpty() || f.dob == "Seleccionar"){
                mensajeError("Debe seleccionar una fecha de nacimiento")
                return@launch
            }

            // Validar edad mayor a 18 años
            val edad = calcularEdad(f.dob)
            if(edad < 18){
                mensajeError("Debes tener al menos 18 años para crear una cuenta")
                return@launch
            }

            errorNull()

            // Si no hay errores, crear usuario en el backend
            Log.d(TAG, "Validación exitosa, enviando usuario al backend...")

            // Crear objeto User con los datos del formulario
            val newUser = User(
                name = f.name,
                email = f.email,
                clave = f.password,
                isAdmin = false
            )

            Log.d(TAG, "Usuario a enviar: $newUser")
            Log.d(TAG, "JSON que se enviará: {\"name\":\"${f.name}\",\"email\":\"${f.email}\",\"clave\":\"${f.password}\"}")

            // Llamar al repositorio para crear el usuario
            userRepository.createUser(newUser)
                .onSuccess { createdUser ->
                    Log.d(TAG, "✅ Usuario creado exitosamente: $createdUser")
                    formuCreateAccount.value = formuCreateAccount.value.copy(
                        error = "",
                        isAccountCreated = true
                    )
                }
                .onFailure { error ->
                    Log.e(TAG, "❌ Error al crear usuario: ${error.message}", error)
                    mensajeError("Error al crear cuenta: ${error.message}")
                }
        }
    }
}
