package com.example.level_up_app.data

import com.google.gson.annotations.SerializedName

data class Cart(
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("productId")
    val productId: Long,
    @SerializedName("productTitle")
    val productTitle: String? = null,
    @SerializedName("productImage")
    val productImage: String? = null,
    @SerializedName("productPrice")
    val productPrice: Double? = null,
    @SerializedName("productCurrency")
    val productCurrency: String? = null,
    @SerializedName("quantity")
    val quantity: Int = 1,
    @SerializedName("addedAt")
    val addedAt: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)


data class AddToCartRequest(
    val userId: Int,
    val productId: Long,
    val quantity: Int = 1
)

