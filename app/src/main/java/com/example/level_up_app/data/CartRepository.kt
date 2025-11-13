package com.example.level_up_app.data

import androidx.compose.runtime.mutableStateListOf

object CartRepository {
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> get() = _cartItems

    fun addToCart(product: Product) {
        // Buscar si el producto ya está en el carrito
        val existingItem = _cartItems.find { it.product.id == product.id }

        if (existingItem != null) {
            // Si ya existe, incrementar la cantidad
            val index = _cartItems.indexOf(existingItem)
            _cartItems[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            // Si no existe, agregarlo con cantidad 1
            _cartItems.add(CartItem(product = product, quantity = 1))
        }
    }

    fun removeFromCart(productId: String) {
        _cartItems.removeAll { it.product.id == productId }
    }

    fun updateQuantity(productId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(productId)
            return
        }

        val existingItem = _cartItems.find { it.product.id == productId }
        if (existingItem != null) {
            val index = _cartItems.indexOf(existingItem)
            _cartItems[index] = existingItem.copy(quantity = newQuantity)
        }
    }

    fun getTotal(): Double {
        return _cartItems.sumOf { it.product.price * it.quantity }
    }

    fun clearCart() {
        _cartItems.clear()
    }
}

