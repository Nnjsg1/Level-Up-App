package com.example.level_up_app.ui.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.level_up_app.data.CartItem
import com.example.level_up_app.data.CartRepository
import com.example.level_up_app.data.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CartState(
    val cartItems: List<CartItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val total: Double = 0.0
)

class CartViewModel(
    private val cartRepository: CartRepository = CartRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartState())
    val uiState: StateFlow<CartState> = _uiState.asStateFlow()

    /**
     * Cargar carrito de un usuario
     */
    fun loadCart(userId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val cartItems = cartRepository.getCartByUser(userId)
                if (cartItems != null) {
                    val total = cartItems.sumOf { it.product.price * it.quantity }
                    _uiState.value = _uiState.value.copy(
                        cartItems = cartItems,
                        total = total,
                        isLoading = false,
                        error = null
                    )
                    Log.d("CartViewModel", "Carrito cargado: ${cartItems.size} items, Total: $$total")
                } else {
                    _uiState.value = _uiState.value.copy(
                        cartItems = emptyList(),
                        total = 0.0,
                        isLoading = false,
                        error = "Error al cargar el carrito"
                    )
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error cargando carrito: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error de conexión: ${e.localizedMessage}"
                )
            }
        }
    }

    /**
     * Agregar producto al carrito
     */
    fun addToCart(userId: Long, product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            try {
                val result = cartRepository.addToCart(userId, product.id, quantity)
                if (result != null) {
                    // Recargar carrito
                    loadCart(userId)
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Agregado al carrito"
                    )
                    Log.d("CartViewModel", "Producto agregado: ${product.name}")
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "No se pudo agregar al carrito"
                    )
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error agregando al carrito: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    error = "Error al agregar: ${e.localizedMessage}"
                )
            }
        }
    }

    /**
     * Actualizar cantidad de un producto
     */
    fun updateQuantity(userId: Long, productId: Long, newQuantity: Int) {
        viewModelScope.launch {
            try {
                if (newQuantity <= 0) {
                    removeFromCart(userId, productId)
                    return@launch
                }

                val result = cartRepository.updateQuantity(userId, productId, newQuantity)
                if (result != null) {
                    // Recargar carrito
                    loadCart(userId)
                    Log.d("CartViewModel", "Cantidad actualizada: $productId -> $newQuantity")
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "No se pudo actualizar la cantidad"
                    )
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error actualizando cantidad: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    error = "Error al actualizar: ${e.localizedMessage}"
                )
            }
        }
    }

    /**
     * Eliminar producto del carrito
     */
    fun removeFromCart(userId: Long, productId: Long) {
        viewModelScope.launch {
            try {
                val success = cartRepository.removeFromCart(userId, productId)
                if (success) {
                    // Recargar carrito
                    loadCart(userId)
                    _uiState.value = _uiState.value.copy(
                        successMessage = "Eliminado del carrito"
                    )
                    Log.d("CartViewModel", "Producto eliminado: $productId")
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "No se pudo eliminar del carrito"
                    )
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error eliminando del carrito: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    error = "Error al eliminar: ${e.localizedMessage}"
                )
            }
        }
    }

    /**
     * Vaciar carrito completo
     */
    fun clearCart(userId: Long) {
        viewModelScope.launch {
            try {
                val success = cartRepository.clearCart(userId)
                if (success) {
                    _uiState.value = _uiState.value.copy(
                        cartItems = emptyList(),
                        total = 0.0,
                        successMessage = "Carrito vaciado"
                    )
                    Log.d("CartViewModel", "Carrito vaciado")
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "No se pudo vaciar el carrito"
                    )
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error vaciando carrito: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    error = "Error al vaciar: ${e.localizedMessage}"
                )
            }
        }
    }

    /**
     * Incrementar cantidad de un producto
     */
    fun incrementQuantity(userId: Long, item: CartItem) {
        updateQuantity(userId, item.product.id, item.quantity + 1)
    }

    /**
     * Decrementar cantidad de un producto
     */
    fun decrementQuantity(userId: Long, item: CartItem) {
        updateQuantity(userId, item.product.id, item.quantity - 1)
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

