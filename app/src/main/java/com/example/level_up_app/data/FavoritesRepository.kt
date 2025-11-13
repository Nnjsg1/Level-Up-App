package com.example.level_up_app.data

import androidx.compose.runtime.mutableStateListOf

object FavoritesRepository {
    private val _favorites = mutableStateListOf<Product>()
    val favorites: List<Product> get() = _favorites

    fun addToFavorites(product: Product) {
        // Verificar si el producto ya está en favoritos
        if (!_favorites.any { it.id == product.id }) {
            _favorites.add(product)
        }
    }

    fun removeFromFavorites(productId: String) {
        _favorites.removeAll { it.id == productId }
    }

    fun isFavorite(productId: String): Boolean {
        return _favorites.any { it.id == productId }
    }

    fun toggleFavorite(product: Product) {
        if (isFavorite(product.id)) {
            removeFromFavorites(product.id)
        } else {
            addToFavorites(product)
        }
    }
}

