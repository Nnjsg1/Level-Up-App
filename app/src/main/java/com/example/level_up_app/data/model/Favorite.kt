package com.example.level_up_app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Favorite(
    val userId: Long,
    val productId: Long,
    val addedAt: String? = null
)

