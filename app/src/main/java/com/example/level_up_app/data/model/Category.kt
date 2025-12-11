package com.example.level_up_app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Long? = null,
    val name: String
)

