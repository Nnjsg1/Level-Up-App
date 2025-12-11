package com.example.level_up_app.data

import android.util.Log
import com.example.level_up_app.remote.ApiService
import io.mockk.*
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Response

class CartRepositoryTest {

    private lateinit var apiService: ApiService
    private lateinit var productRepository: ProductRepository
    private lateinit var cartRepository: CartRepository

    private val mockCategory = Category(id = 1, name = "Gaming")

    private val mockProduct = Product(
        id = 1,
        name = "Teclado Mecánico",
        description = "Teclado gaming RGB",
        price = 89.99,
        imageUrl = "teclado.jpg",
        stock = 15,
        discontinued = false,
        category = mockCategory
    )

    private val discontinuedProduct = Product(
        id = 2,
        name = "Mouse Antiguo",
        description = "Mouse descontinuado",
        price = 29.99,
        imageUrl = "mouse.jpg",
        stock = 0,
        discontinued = true,
        category = mockCategory
    )

    private val mockCarts = listOf(
        Cart(
            userId = 1,
            productId = 1L,
            productTitle = "Teclado Mecánico",
            productImage = "teclado.jpg",
            productPrice = 89.99,
            quantity = 2
        ),
        Cart(
            userId = 1,
            productId = 2L,
            productTitle = "Mouse Antiguo",
            productImage = "mouse.jpg",
            productPrice = 29.99,
            quantity = 1
        )
    )

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        apiService = mockk(relaxed = true)
        productRepository = mockk(relaxed = true)
        cartRepository = CartRepository(apiService, productRepository)
    }

    @Test
    fun `getCartByUser debe retornar solo productos activos y eliminar descontinuados`() = runTest {
        // Dado
        val userId = 1L
        coEvery { apiService.getCartByUser(userId.toInt()) } returns Response.success(mockCarts)
        coEvery { productRepository.getProductById(1L) } returns mockProduct
        coEvery { productRepository.getProductById(2L) } returns discontinuedProduct
        coEvery { apiService.removeFromCart(userId.toInt(), 2L) } returns Response.success(Unit)

        // Cuando
        val result = cartRepository.getCartByUser(userId)

        // Entonces
        assertNotNull(result)
        assertEquals(1, result?.size) // Solo el producto activo
        assertEquals(mockProduct, result?.get(0)?.product)
        coVerify { apiService.removeFromCart(userId.toInt(), 2L) } // Verificar que se eliminó el descontinuado
    }

    @Test
    fun `getCartByUser debe manejar respuesta de error`() = runTest {
        // Dado
        val userId = 1L
        coEvery { apiService.getCartByUser(userId.toInt()) } returns Response.error(
            404,
            "Not found".toResponseBody()
        )

        // Cuando
        val result = cartRepository.getCartByUser(userId)

        // Entonces
        assertNotNull(result)
        assertTrue(result?.isEmpty() == true)
    }

    @Test
    fun `getCartByUser debe manejar excepciones`() = runTest {
        // Dado
        val userId = 1L
        coEvery { apiService.getCartByUser(userId.toInt()) } throws Exception("Network error")

        // Cuando
        val result = cartRepository.getCartByUser(userId)

        // Entonces
        assertNull(result)
    }

    @Test
    fun `addToCart debe agregar producto exitosamente`() = runTest {
        // Dado
        val userId = 1L
        val productId = 1L
        val quantity = 2
        val expectedCart = mockCarts[0]

        coEvery { apiService.addToCart(any()) } returns Response.success(expectedCart)

        // Cuando
        val result = cartRepository.addToCart(userId, productId, quantity)

        // Entonces
        assertNotNull(result)
        assertEquals(productId, result?.productId)
        assertEquals(quantity, result?.quantity)
    }

    @Test
    fun `addToCart debe retornar null cuando hay error`() = runTest {
        // Dado
        val userId = 1L
        val productId = 1L

        coEvery { apiService.addToCart(any()) } returns Response.error(
            400,
            "Bad request".toResponseBody()
        )

        // Cuando
        val result = cartRepository.addToCart(userId, productId)

        // Entonces
        assertNull(result)
    }

    @Test
    fun `updateQuantity debe actualizar cantidad exitosamente`() = runTest {
        // Dado
        val userId = 1L
        val productId = 1L
        val newQuantity = 5
        val updatedCart = mockCarts[0].copy(quantity = newQuantity)

        coEvery { apiService.updateCartItem(any(), any(), any()) } returns Response.success(updatedCart)

        // Cuando
        val result = cartRepository.updateQuantity(userId, productId, newQuantity)

        // Entonces
        assertNotNull(result)
        assertEquals(newQuantity, result?.quantity)
    }

    @Test
    fun `removeFromCart debe eliminar producto exitosamente`() = runTest {
        // Dado
        val userId = 1L
        val productId = 1L

        coEvery { apiService.removeFromCart(userId.toInt(), productId) } returns Response.success(Unit)

        // Cuando
        val result = cartRepository.removeFromCart(userId, productId)

        // Entonces
        assertTrue(result)
        coVerify { apiService.removeFromCart(userId.toInt(), productId) }
    }

    @Test
    fun `removeFromCart debe retornar false cuando hay error`() = runTest {
        // Dado
        val userId = 1L
        val productId = 1L

        coEvery { apiService.removeFromCart(userId.toInt(), productId) } returns Response.error(
            404,
            "Not found".toResponseBody()
        )

        // Cuando
        val result = cartRepository.removeFromCart(userId, productId)

        // Entonces
        assertFalse(result)
    }

    @Test
    fun `clearCart debe vaciar carrito exitosamente`() = runTest {
        // Dado
        val userId = 1L

        coEvery { apiService.clearCart(userId.toInt()) } returns Response.success(Unit)

        // Cuando
        val result = cartRepository.clearCart(userId)

        // Entonces
        assertTrue(result)
        coVerify { apiService.clearCart(userId.toInt()) }
    }

    @Test
    fun `clearCart debe manejar excepciones`() = runTest {
        // Dado
        val userId = 1L

        coEvery { apiService.clearCart(userId.toInt()) } throws Exception("Network error")

        // Cuando
        val result = cartRepository.clearCart(userId)

        // Entonces
        assertFalse(result)
    }
}

