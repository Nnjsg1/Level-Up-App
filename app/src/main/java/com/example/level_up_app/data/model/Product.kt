package com.example.level_up_app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductImage(
    val id: Int? = null,
    val productId: Int,
    val url: String,
    val altText: String? = null
)

@Serializable
data class Tag(
    val id: Int? = null,
    val name: String
)

@Serializable
data class Product(
    val id: Int? = null,
    val title: String,
    val description: String,
    val price: Double,
    val currency: String,
    val categoryId: Int,
    val stock: Int,
    val discontinued: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val images: List<ProductImage>? = null,
    val tags: List<Tag>? = null
)
