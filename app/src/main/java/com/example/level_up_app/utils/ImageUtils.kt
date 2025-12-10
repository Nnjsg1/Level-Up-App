package com.example.level_up_app.utils

object ImageUtils {
    // Base URL del servidor (sin /api/)
    private const val BASE_URL = "http://10.0.2.2:8080"

    /**
     * Convierte una URL relativa o absoluta en una URL completa
     */
    fun getImageUrl(imageUrl: String): String {
        return when {
            imageUrl.isEmpty() -> ""
            imageUrl.startsWith("http://") || imageUrl.startsWith("https://") -> imageUrl
            imageUrl.startsWith("/") -> "$BASE_URL$imageUrl"
            else -> "$BASE_URL/uploads/$imageUrl"
        }
    }
}
