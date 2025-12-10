package com.example.level_up_app.data

data class User(
    val id: Long = 0,
    val name: String = "",
    val email: String = "",
    val clave: String = "", // Contraseña (sin encriptar)
    val isAdmin: Boolean = false,
    val createdAt: String = ""
)
