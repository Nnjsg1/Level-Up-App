package com.example.level_up_app.data.model

import kotlinx.serialization.Serializable

/**
 * DTO para crear un nuevo usuario.
 * Solo contiene los campos requeridos por el backend.
 */
@Serializable
data class CreateUserRequest(
    val name: String,
    val email: String,
    val clave: String
)

