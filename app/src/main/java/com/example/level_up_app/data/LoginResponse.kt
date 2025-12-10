package com.example.level_up_app.data

data class LoginResponse(
    val success: Boolean = false,
    val message: String = "",
    val user: User? = null
)

