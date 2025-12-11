package com.example.level_up_app.data

import android.util.Log
import com.example.level_up_app.remote.ApiService
import com.example.level_up_app.remote.RetrofitInstance

class FavoritesRepository(
    private val apiService: ApiService = RetrofitInstance.api
) {

    /**
     * Obtener favoritos de un usuario
     */
    suspend fun getFavoritesByUser(userId: Long): List<Favorite>? {
        return try {
            val response = apiService.getFavoritesByUser(userId.toInt())
            if (response.isSuccessful) {
                Log.d("FavoritesRepository", "Favoritos obtenidos: ${response.body()?.size}")
                response.body()
            } else {
                Log.e("FavoritesRepository", "Error obteniendo favoritos: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("FavoritesRepository", "Excepción obteniendo favoritos: ${e.message}", e)
            null
        }
    }

    /**
     * Agregar producto a favoritos
     */
    suspend fun addToFavorites(userId: Long, productId: Long): Favorite? {
        return try {
            val favorite = Favorite(userId = userId.toInt(), productId = productId)
            val response = apiService.createFavorite(favorite)
            if (response.isSuccessful) {
                Log.d("FavoritesRepository", "Favorito agregado: $productId")
                response.body()
            } else {
                Log.e("FavoritesRepository", "Error agregando favorito: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("FavoritesRepository", "Excepción agregando favorito: ${e.message}", e)
            null
        }
    }

    /**
     * Eliminar producto de favoritos
     */
    suspend fun removeFromFavorites(userId: Long, productId: Long): Boolean {
        return try {
            val response = apiService.deleteFavorite(userId.toInt(), productId)
            if (response.isSuccessful) {
                Log.d("FavoritesRepository", "Favorito eliminado: $productId")
                true
            } else {
                Log.e("FavoritesRepository", "Error eliminando favorito: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("FavoritesRepository", "Excepción eliminando favorito: ${e.message}", e)
            false
        }
    }

    /**
     * Obtener todos los favoritos (para admins)
     */
    suspend fun getAllFavorites(): List<Favorite>? {
        return try {
            val response = apiService.getAllFavorites()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("FavoritesRepository", "Error obteniendo todos los favoritos: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("FavoritesRepository", "Excepción obteniendo todos los favoritos: ${e.message}")
            null
        }
    }
}


