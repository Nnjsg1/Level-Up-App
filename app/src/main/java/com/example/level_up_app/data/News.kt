package com.example.level_up_app.data

import com.google.gson.annotations.SerializedName

data class News(
    val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val summary: String = "",
    val image: String = "",
    val thumbnail: String = "",
    val author: String = "Admin",
    val category: String = "General",
    val views: Int = 0,
    @SerializedName("isPublished")
    val isPublished: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = ""
)

