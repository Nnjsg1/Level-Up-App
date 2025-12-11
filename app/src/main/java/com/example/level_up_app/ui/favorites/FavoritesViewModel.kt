package com.example.level_up_app.ui.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.level_up_app.data.Favorite
import com.example.level_up_app.data.FavoritesRepository
import com.example.level_up_app.data.Product
import com.example.level_up_app.data.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FavoritesState(
    val favoriteProducts: List<Product> = emptyList(),
    val favoriteIds: Set<Long> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class FavoritesViewModel(
    private val favoritesRepository: FavoritesRepository = FavoritesRepository(),
    private val productRepository: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesState())
    val uiState: StateFlow<FavoritesState> = _uiState.asStateFlow()

    /**
     * Cargar favoritos de un usuario
     * Filtra automáticamente productos descontinuados
     */
    fun loadFavorites(userId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val favorites = favoritesRepository.getFavoritesByUser(userId)
                if (favorites != null) {
                    // Obtener los detalles de cada producto
                    val products = mutableListOf<Product>()
                    val validFavoriteIds = mutableSetOf<Long>()

                    favorites.forEach { favorite ->
                        val product = productRepository.getProductById(favorite.productId)
                        if (product != null) {
                            // Verificar si el producto está descontinuado
                            if (product.discontinued) {
                                // Eliminar de favoritos en el backend
                                Log.d("FavoritesViewModel", "Producto descontinuado detectado: ${product.id}, eliminando de favoritos...")
                                favoritesRepository.removeFromFavorites(userId, product.id)
                            } else {
                                // Solo agregar productos activos
                                products.add(product)
                                validFavoriteIds.add(product.id)
                            }
                        }
                    }

                    _uiState.value = _uiState.value.copy(
                        favoriteProducts = products,
                        favoriteIds = validFavoriteIds,
                        isLoading = false,
                        error = null
                    )
                    Log.d("FavoritesViewModel", "Favoritos cargados: ${products.size} productos activos")
                } else {
                    _uiState.value = _uiState.value.copy(
                        favoriteProducts = emptyList(),
                        favoriteIds = emptySet(),
                        isLoading = false,
                        error = "Error al cargar favoritos"
                    )
                }
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Error cargando favoritos: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error de conexión: ${e.localizedMessage}"
                )
            }
        }
    }

    /**
     * Agregar producto a favoritos
     */
    fun addToFavorites(userId: Long, product: Product) {
        viewModelScope.launch {
            try {
                val favorite = favoritesRepository.addToFavorites(userId, product.id)
                if (favorite != null) {
                    // Actualizar la lista local
                    val updatedProducts = _uiState.value.favoriteProducts + product
                    val updatedIds = _uiState.value.favoriteIds + product.id

                    _uiState.value = _uiState.value.copy(
                        favoriteProducts = updatedProducts,
                        favoriteIds = updatedIds,
                        successMessage = "Agregado a favoritos"
                    )
                    Log.d("FavoritesViewModel", "Producto agregado a favoritos: ${product.name}")
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "No se pudo agregar a favoritos"
                    )
                }
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Error agregando a favoritos: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    error = "Error al agregar: ${e.localizedMessage}"
                )
            }
        }
    }

    /**
     * Eliminar producto de favoritos
     */
    fun removeFromFavorites(userId: Long, productId: Long) {
        viewModelScope.launch {
            try {
                val success = favoritesRepository.removeFromFavorites(userId, productId)
                if (success) {
                    // Actualizar la lista local
                    val updatedProducts = _uiState.value.favoriteProducts.filter { it.id != productId }
                    val updatedIds = _uiState.value.favoriteIds - productId

                    _uiState.value = _uiState.value.copy(
                        favoriteProducts = updatedProducts,
                        favoriteIds = updatedIds,
                        successMessage = "Eliminado de favoritos"
                    )
                    Log.d("FavoritesViewModel", "Producto eliminado de favoritos: $productId")
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "No se pudo eliminar de favoritos"
                    )
                }
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Error eliminando de favoritos: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    error = "Error al eliminar: ${e.localizedMessage}"
                )
            }
        }
    }

    /**
     * Verificar si un producto es favorito
     */
    fun isFavorite(productId: Long): Boolean {
        return _uiState.value.favoriteIds.contains(productId)
    }

    /**
     * Toggle favorito (agregar o eliminar)
     */
    fun toggleFavorite(userId: Long, product: Product) {
        if (isFavorite(product.id)) {
            removeFromFavorites(userId, product.id)
        } else {
            addToFavorites(userId, product)
        }
    }

    /**
     * Limpiar mensajes
     */
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            successMessage = null,
            error = null
        )
    }
}

