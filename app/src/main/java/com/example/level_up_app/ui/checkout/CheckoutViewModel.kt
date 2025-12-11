package com.example.level_up_app.ui.checkout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.level_up_app.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CheckoutState(
    val cartItems: List<CartItem> = emptyList(),
    val total: Double = 0.0,
    val isProcessing: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val order: Order? = null
)

class CheckoutViewModel(
    private val cartRepository: CartRepository = CartRepository(),
    private val orderRepository: OrderRepository = OrderRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutState())
    val uiState: StateFlow<CheckoutState> = _uiState.asStateFlow()

    fun loadCheckoutSummary(userId: Long) {
        viewModelScope.launch {
            try {
                val cartItems = cartRepository.getCartByUser(userId)
                if (cartItems != null) {
                    val total = cartItems.sumOf { it.product.price * it.quantity }
                    _uiState.value = _uiState.value.copy(
                        cartItems = cartItems,
                        total = total,
                        error = null
                    )
                    Log.d("CheckoutViewModel", "✅ Resumen cargado: ${cartItems.size} items, Total: $$total")
                }
            } catch (e: Exception) {
                Log.e("CheckoutViewModel", "❌ Error cargando resumen", e)
                _uiState.value = _uiState.value.copy(error = "Error: ${e.message}")
            }
        }
    }

    fun processCheckout(userId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true, error = null)
            Log.d("CheckoutViewModel", "🔷 INICIANDO CHECKOUT COMPLETO")

            try {
                // PASO 1: Crear la orden
                Log.d("CheckoutViewModel", "📝 PASO 1: Creando orden...")
                val order = orderRepository.createOrderFromCart(userId, _uiState.value.cartItems)

                if (order == null) {
                    _uiState.value = _uiState.value.copy(
                        isProcessing = false,
                        error = "Error al crear la orden"
                    )
                    Log.e("CheckoutViewModel", "❌ Fallo en PASO 1")
                    return@launch
                }
                Log.d("CheckoutViewModel", "✅ PASO 1 completado: Orden ${order.id} creada")

                // PASO 2: Simular procesamiento de pago (2 segundos)
                Log.d("CheckoutViewModel", "💳 PASO 2: Procesando pago...")
                delay(2000)
                Log.d("CheckoutViewModel", "✅ PASO 2 completado: Pago exitoso")

                // PASO 3: Actualizar orden a completada
                Log.d("CheckoutViewModel", "📋 PASO 3: Actualizando orden a completada...")
                orderRepository.updateOrderStatus(order.id!!, "completed")
                Log.d("CheckoutViewModel", "✅ PASO 3 completado: Orden marcada como completed")

                // PASO 4: VACIAR EL CARRITO
                Log.d("CheckoutViewModel", "🗑️ PASO 4: VACIANDO CARRITO...")
                val cartCleared = cartRepository.clearCart(userId)

                if (cartCleared) {
                    Log.d("CheckoutViewModel", "✅ PASO 4 completado: CARRITO VACIADO")
                } else {
                    Log.w("CheckoutViewModel", "⚠️ PASO 4: No se pudo vaciar el carrito")
                }

                // ÉXITO TOTAL
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    isSuccess = true,
                    order = order.copy(status = "completed"),
                    cartItems = emptyList(),
                    error = null
                )

                Log.d("CheckoutViewModel", "🎉🎉🎉 CHECKOUT COMPLETADO EXITOSAMENTE 🎉🎉🎉")

            } catch (e: Exception) {
                Log.e("CheckoutViewModel", "❌ Error en checkout", e)
                _uiState.value = _uiState.value.copy(
                    isProcessing = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    fun reset() {
        _uiState.value = CheckoutState()
    }
}

