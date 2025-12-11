package com.example.level_up_app.data.repository

import android.util.Log
import com.example.level_up_app.data.model.CreateUserRequest
import com.example.level_up_app.data.model.User
import com.example.level_up_app.data.network.RetrofitClient

class UserRepository {
    private val api = RetrofitClient.apiService
    private val TAG = "UserRepository"

    suspend fun getAllUsers(): Result<List<User>> = runCatching {
        val response = api.getAllUsers()
        if (!response.isSuccessful) {
            Log.e(TAG, "Error getAllUsers: HTTP ${response.code()} - ${response.message()}")
            error("HTTP ${response.code()}: ${response.message()}")
        }
        response.body() ?: error("Empty body")
    }

    suspend fun createUser(user: User): Result<User> = runCatching {
        Log.d(TAG, "🔵 Enviando POST a http://localhost:8080/api/users")
        Log.d(TAG, "🔵 Usuario original: $user")

        // Crear el DTO con solo los campos requeridos
        val request = CreateUserRequest(
            name = user.name,
            email = user.email,
            clave = user.clave
        )

        Log.d(TAG, "🔵 Request DTO: $request")
        Log.d(TAG, "🔵 JSON exacto a enviar: {\"name\":\"${request.name}\",\"email\":\"${request.email}\",\"clave\":\"${request.clave}\"}")

        val response = api.createUserWithRequest(request)

        Log.d(TAG, "🔵 Respuesta recibida - Status: ${response.code()}")

        if (!response.isSuccessful) {
            // Leer errorBody una sola vez
            val errorBody = response.errorBody()?.string() ?: "Sin detalles"
            Log.e(TAG, "❌ Error createUser: HTTP ${response.code()} - ${response.message()}")
            Log.e(TAG, "❌ Error body completo: $errorBody")
            Log.e(TAG, "❌ Headers de respuesta: ${response.headers()}")
            error("HTTP ${response.code()}: $errorBody")
        }

        val createdUser = response.body() ?: error("Empty body")
        Log.d(TAG, "✅ Usuario creado exitosamente: $createdUser")
        createdUser
    }

    suspend fun updateUser(id: Int, user: User): Result<User> = runCatching {
        Log.d(TAG, "🔵 Actualizando usuario ID: $id")
        val response = api.updateUser(id, user)

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string() ?: "Sin detalles"
            Log.e(TAG, "❌ Error updateUser: HTTP ${response.code()} - ${response.message()}")
            Log.e(TAG, "❌ Error body: $errorBody")
            error("HTTP ${response.code()}: $errorBody")
        }

        val updatedUser = response.body() ?: error("Empty body")
        Log.d(TAG, "✅ Usuario actualizado exitosamente: $updatedUser")
        updatedUser
    }

    suspend fun deactivateUser(id: Int): Result<User> = runCatching {
        Log.d(TAG, "🔵 Desactivando usuario ID: $id")
        val response = api.deactivateUser(id)

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string() ?: "Sin detalles"
            Log.e(TAG, "❌ Error deactivateUser: HTTP ${response.code()} - ${response.message()}")
            Log.e(TAG, "❌ Error body: $errorBody")
            error("HTTP ${response.code()}: $errorBody")
        }

        val deactivatedUser = response.body() ?: error("Empty body")
        Log.d(TAG, "✅ Usuario desactivado exitosamente: $deactivatedUser")
        deactivatedUser
    }

    suspend fun activateUser(id: Int): Result<User> = runCatching {
        Log.d(TAG, "🔵 Activando usuario ID: $id")
        val response = api.activateUser(id)

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string() ?: "Sin detalles"
            Log.e(TAG, "❌ Error activateUser: HTTP ${response.code()} - ${response.message()}")
            Log.e(TAG, "❌ Error body: $errorBody")
            error("HTTP ${response.code()}: $errorBody")
        }

        val activatedUser = response.body() ?: error("Empty body")
        Log.d(TAG, "✅ Usuario activado exitosamente: $activatedUser")
        activatedUser
    }

    suspend fun getActiveUsers(): Result<List<User>> = runCatching {
        Log.d(TAG, "🔵 Obteniendo usuarios activos")
        val response = api.getActiveUsers()

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string() ?: "Sin detalles"
            Log.e(TAG, "❌ Error getActiveUsers: HTTP ${response.code()}")
            error("HTTP ${response.code()}: $errorBody")
        }

        val users = response.body() ?: error("Empty body")
        Log.d(TAG, "✅ ${users.size} usuarios activos obtenidos")
        users
    }

    suspend fun getInactiveUsers(): Result<List<User>> = runCatching {
        Log.d(TAG, "🔵 Obteniendo usuarios inactivos")
        val response = api.getInactiveUsers()

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string() ?: "Sin detalles"
            Log.e(TAG, "❌ Error getInactiveUsers: HTTP ${response.code()}")
            error("HTTP ${response.code()}: $errorBody")
        }

        val users = response.body() ?: error("Empty body")
        Log.d(TAG, "✅ ${users.size} usuarios inactivos obtenidos")
        users
    }
}

