package com.example.level_up_app.utils

object ImageUtils {
    // Base URL del servidor (sin /api/)
    private const val BASE_URL = "http://10.0.2.2:8080/"

    /**
     * Construye la URL completa de una imagen desde la ruta relativa almacenada en la BD
     * Ejemplo: "uploads/carats.png" -> "http://10.0.2.2:8080/uploads/carats.png"
     */
    fun getImageUrl(imagePath: String?): String {
        if (imagePath.isNullOrEmpty()) {
            return "" // Retorna vacío si no hay imagen
        }

        // Si la ruta ya es una URL completa, retornarla tal cual
        if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
            return imagePath
        }

        // Construir URL completa
        return BASE_URL + imagePath.removePrefix("/")
    }
}

