package com.example.level_up_app.data

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class Favorite(
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("productId")
    val productId: Long,
    @SerializedName("addedAt")
    val addedAt: String? = null
)

data class FavoriteWithProduct(
    val userId: Int,
    val productId: Long,
    val addedAt: String,
    val product: Product
)

