package com.example.level_up_app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int? = null,
    val name: String,
    val email: String,
    val clave: String,
    val isAdmin: Boolean = false,
    val createdAt: String? = null
)

