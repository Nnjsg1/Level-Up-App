package com.example.level_up_app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OrderItem(
    val id: Long? = null,
    val orderId: Long? = null,
    val productId: Long,
    val quantity: Int,
    val price: Double
)

@Serializable
data class Order(
    val id: Long? = null,
    val userId: Long,
    val status: String,
    val total: Double,
    val createdAt: String? = null,
    val items: List<OrderItem>
)

