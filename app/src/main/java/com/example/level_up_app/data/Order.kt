package com.example.level_up_app.data

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("status")
    val status: String = "pending",
    @SerializedName("total")
    val total: Double,
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("items")
    val items: List<OrderItem>? = null
)

data class OrderItem(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("orderId")
    val orderId: Int? = null,
    @SerializedName("productId")
    val productId: Long,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("price")
    val price: Double
)

data class CreateOrderRequest(
    val userId: Int,
    val status: String = "pending",
    val total: Double,
    val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    val productId: Long,
    val quantity: Int,
    val price: Double
)

