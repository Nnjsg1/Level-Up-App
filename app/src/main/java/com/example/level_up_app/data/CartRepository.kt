package com.example.level_up_app.data

import android.util.Log
import com.example.level_up_app.remote.ApiService
import com.example.level_up_app.remote.RetrofitInstance

class CartRepository(
    private val apiService: ApiService = RetrofitInstance.api,
    private val productRepository: ProductRepository = ProductRepository()
) {

    /**
     * Obtener carrito de un usuario
     * Filtra automáticamente productos descontinuados
     */
    suspend fun getCartByUser(userId: Long): List<CartItem>? {
        return try {
            val response = apiService.getCartByUser(userId.toInt())
            if (response.isSuccessful) {
                val cartList = response.body() ?: emptyList()
                Log.d("CartRepository", "Carrito obtenido: ${cartList.size} items")

                // Convertir Cart a CartItem con Product completo
                val cartItems = mutableListOf<CartItem>()
                cartList.forEach { cart ->
                    val product = productRepository.getProductById(cart.productId)
                    if (product != null) {
                        // Verificar si el producto está descontinuado
                        if (product.discontinued) {
                            // Eliminar del carrito en el backend
                            Log.d("CartRepository", "Producto descontinuado detectado: ${product.id}, eliminando del carrito...")
                            removeFromCart(userId, cart.productId)
                        } else {
                            // Solo agregar productos activos
                            cartItems.add(
                                CartItem(
                                    product = product,
                                    quantity = cart.quantity
                                )
                            )
                        }
                    }
                }
                Log.d("CartRepository", "Productos activos en carrito: ${cartItems.size}")
                cartItems
            } else {
                Log.e("CartRepository", "Error obteniendo carrito: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Excepción obteniendo carrito: ${e.message}", e)
            null
        }
    }

    /**
     * Agregar producto al carrito
     */
    suspend fun addToCart(userId: Long, productId: Long, quantity: Int = 1): Cart? {
        return try {
            val request = AddToCartRequest(userId.toInt(), productId, quantity)
            val response = apiService.addToCart(request)
            if (response.isSuccessful) {
                Log.d("CartRepository", "Producto agregado al carrito: $productId")
                response.body()
            } else {
                Log.e("CartRepository", "Error agregando al carrito: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Excepción agregando al carrito: ${e.message}", e)
            null
        }
    }

    /**
     * Actualizar cantidad de un producto en el carrito
     */
    suspend fun updateQuantity(userId: Long, productId: Long, quantity: Int): Cart? {
        return try {
            val request = AddToCartRequest(userId.toInt(), productId, quantity)
            val response = apiService.updateCartItem(userId.toInt(), productId, request)
            if (response.isSuccessful) {
                Log.d("CartRepository", "Cantidad actualizada: $productId -> $quantity")
                response.body()
            } else {
                Log.e("CartRepository", "Error actualizando cantidad: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Excepción actualizando cantidad: ${e.message}", e)
            null
        }
    }

    /**
     * Eliminar producto del carrito
     */
    suspend fun removeFromCart(userId: Long, productId: Long): Boolean {
        return try {
            val response = apiService.removeFromCart(userId.toInt(), productId)
            if (response.isSuccessful) {
                Log.d("CartRepository", "Producto eliminado del carrito: $productId")
                true
            } else {
                Log.e("CartRepository", "Error eliminando del carrito: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Excepción eliminando del carrito: ${e.message}", e)
            false
        }
    }

    /**
     * Vaciar carrito completo
     */
    suspend fun clearCart(userId: Long): Boolean {
        return try {
            val response = apiService.clearCart(userId.toInt())
            if (response.isSuccessful) {
                Log.d("CartRepository", "Carrito vaciado: $userId")
                true
            } else {
                Log.e("CartRepository", "Error vaciando carrito: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "Excepción vaciando carrito: ${e.message}", e)
            false
        }
    }
}

