package com.example.level_up_app.data

data class News(
    val id: String = "",
    val title: String = "",
    val summary: String = "",
    val content: String = "",
    val imageUrl: String = "",
    val videoPath: String = "", // Ruta local del video
    val date: String = "",
    val category: String = ""
)

