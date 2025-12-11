package com.example.level_up_app.data

import android.util.Log
import com.example.level_up_app.remote.ApiService
import com.example.level_up_app.remote.RetrofitInstance

class OrderRepository(
    private val apiService: ApiService = RetrofitInstance.api
) {

    suspend fun createOrderFromCart(userId: Long, cartItems: List<CartItem>): Order? {
        return try {
            val total = cartItems.sumOf { it.product.price * it.quantity }

            val orderItems = cartItems.map { cartItem ->
                OrderItemRequest(
                    productId = cartItem.product.id,
                    quantity = cartItem.quantity,
                    price = cartItem.product.price
                )
            }

            val orderRequest = CreateOrderRequest(
                userId = userId.toInt(),
                status = "pending",
                total = total,
                items = orderItems
            )

            Log.d("OrderRepository", "Creando orden: userId=$userId, total=$total, items=${orderItems.size}")
            val response = apiService.createOrder(orderRequest)

            if (response.isSuccessful) {
                Log.d("OrderRepository", "✅ Orden creada: ${response.body()?.id}")
                response.body()
            } else {
                Log.e("OrderRepository", "❌ Error creando orden: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "❌ Excepción creando orden", e)
            null
        }
    }

    suspend fun updateOrderStatus(orderId: Int, status: String): Order? {
        return try {
            val order = Order(id = orderId, userId = 0, status = status, total = 0.0)
            Log.d("OrderRepository", "Actualizando orden $orderId a estado: $status")

            val response = apiService.updateOrder(orderId, order)
            if (response.isSuccessful) {
                Log.d("OrderRepository", "✅ Orden actualizada: $orderId -> $status")
                response.body()
            } else {
                Log.e("OrderRepository", "❌ Error actualizando orden: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "❌ Excepción actualizando orden", e)
            null
        }
    }
}

