package com.example.level_up_app.data

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Long = 0,
    @SerializedName("title")
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    @SerializedName("image")
    val imageUrl: String = "",
    val stock: Int = 0,
    val currency: String = "CLP",
    val category: Category? = null,
    val tags: List<Tag> = emptyList()
)

data class Category(
    val id: Long = 0,
    val name: String = ""
)

data class Tag(
    val id: Long = 0,
    val name: String = ""
)

