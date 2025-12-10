package com.example.level_up_app.data.repository

import com.example.level_up_app.data.model.User
import com.example.level_up_app.data.network.RetrofitClient

class UserRepository {
    private val api = RetrofitClient.apiService

    suspend fun getAllUsers(): Result<List<User>> = runCatching {
        val response = api.getAllUsers()
        if (!response.isSuccessful) error("HTTP ${response.code()}")
        response.body() ?: error("Empty body")
    }

    suspend fun createUser(user: User): Result<User> = runCatching {
        val response = api.createUser(user)
        if (!response.isSuccessful) error("HTTP ${response.code()}")
        response.body() ?: error("Empty body")
    }
}

