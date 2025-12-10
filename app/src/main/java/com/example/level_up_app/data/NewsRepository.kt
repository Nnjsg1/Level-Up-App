package com.example.level_up_app.data

import android.util.Log
import com.example.level_up_app.remote.RetrofitInstance

class NewsRepository {
    private val apiService = RetrofitInstance.api

    /**
     * Obtiene todas las noticias publicadas
     */
    suspend fun fetchPublishedNews(): List<News>? {
        return try {
            val response = apiService.getPublishedNews()
            if (response.isSuccessful) {
                Log.d("NewsRepository", "Noticias obtenidas: ${response.body()?.size}")
                response.body()
            } else {
                Log.e("NewsRepository", "Error fetching news: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("NewsRepository", "Exception fetching news: ${e.message}", e)
            null
        }
    }

    /**
     * Obtiene todas las noticias (incluye borradores)
     */
    suspend fun fetchAllNews(): List<News>? {
        return try {
            val response = apiService.getAllNews()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("NewsRepository", "Error fetching all news: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("NewsRepository", "Exception fetching all news: ${e.message}")
            null
        }
    }

    /**
     * Obtiene una noticia por ID
     */
    suspend fun getNewsById(id: Long): News? {
        return try {
            val response = apiService.getNewsById(id)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("NewsRepository", "Error getting news: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("NewsRepository", "Exception getting news: ${e.message}")
            null
        }
    }

    /**
     * Obtiene noticias por categoría (solo publicadas)
     */
    suspend fun getNewsByCategory(category: String): List<News>? {
        return try {
            val response = apiService.getPublishedNewsByCategory(category)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("NewsRepository", "Error getting news by category: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("NewsRepository", "Exception getting news by category: ${e.message}")
            null
        }
    }

    /**
     * Busca noticias por título
     */
    suspend fun searchNews(title: String): List<News>? {
        return try {
            val response = apiService.searchNews(title)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("NewsRepository", "Error searching news: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("NewsRepository", "Exception searching news: ${e.message}")
            null
        }
    }

    /**
     * Incrementa el contador de vistas de una noticia
     */
    suspend fun incrementViews(id: Long): Boolean {
        return try {
            val response = apiService.incrementNewsViews(id)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("NewsRepository", "Exception incrementing views: ${e.message}")
            false
        }
    }

    /**
     * Crea una nueva noticia
     */
    suspend fun createNews(news: News): News? {
        return try {
            val response = apiService.createNews(news)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("NewsRepository", "Error creating news: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("NewsRepository", "Exception creating news: ${e.message}")
            null
        }
    }

    /**
     * Actualiza una noticia existente
     */
    suspend fun updateNews(id: Long, news: News): News? {
        return try {
            val response = apiService.updateNews(id, news)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("NewsRepository", "Error updating news: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("NewsRepository", "Exception updating news: ${e.message}")
            null
        }
    }

    /**
     * Elimina una noticia
     */
    suspend fun deleteNews(id: Long): Boolean {
        return try {
            val response = apiService.deleteNews(id)
            if (response.isSuccessful) {
                true
            } else {
                Log.e("NewsRepository", "Error deleting news: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("NewsRepository", "Exception deleting news: ${e.message}")
            false
        }
    }
}

