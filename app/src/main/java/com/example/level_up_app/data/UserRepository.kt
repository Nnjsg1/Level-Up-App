package com.example.level_up_app.data

import android.util.Log
import com.example.level_up_app.remote.RetrofitInstance

class UserRepository {

    private val apiService = RetrofitInstance.api

    /**
     * Realiza el login del usuario
     * @return LoginResponse con el resultado del login
     */
    suspend fun login(email: String, clave: String): LoginResponse {
        return try {
            val loginRequest = LoginRequest(email, clave)
            val response = apiService.login(loginRequest)

            if (response.isSuccessful) {
                response.body() ?: LoginResponse(
                    success = false,
                    message = "Respuesta vacía del servidor"
                )
            } else {
                // Manejar errores HTTP
                val errorMsg = when (response.code()) {
                    401 -> "Email o contraseña incorrectos"
                    404 -> "Usuario no encontrado"
                    500 -> "Error en el servidor"
                    else -> "Error de conexión (${response.code()})"
                }
                Log.e("UserRepository", "Error login: ${response.code()} - ${response.message()}")
                LoginResponse(success = false, message = errorMsg)
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception en login: ${e.message}", e)
            LoginResponse(
                success = false,
                message = "Error de conexión: ${e.localizedMessage}"
            )
        }
    }

    /**
     * Registra un nuevo usuario
     */
    suspend fun register(user: User): LoginResponse {
        return try {
            val response = apiService.register(user)

            if (response.isSuccessful) {
                response.body() ?: LoginResponse(
                    success = false,
                    message = "Respuesta vacía del servidor"
                )
            } else {
                val errorMsg = when (response.code()) {
                    400 -> "Email ya registrado"
                    500 -> "Error en el servidor"
                    else -> "Error al registrar (${response.code()})"
                }
                Log.e("UserRepository", "Error register: ${response.code()}")
                LoginResponse(success = false, message = errorMsg)
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception en register: ${e.message}", e)
            LoginResponse(
                success = false,
                message = "Error de conexión: ${e.localizedMessage}"
            )
        }
    }

    /**
     * Obtiene todos los usuarios
     */
    suspend fun fetchUsers(): List<User>? {
        return try {
            val response = apiService.getUsers()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("UserRepository", "Error fetching users: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception fetching users: ${e.message}")
            null
        }
    }

    /**
     * Obtiene un usuario por ID
     */
    suspend fun getUserById(id: String): User? {
        return try {
            val response = apiService.getUserById(id)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("UserRepository", "Error getting user: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Exception getting user: ${e.message}")
            null
        }
    }
}

