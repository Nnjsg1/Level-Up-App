package com.example.level_up_app.data

import android.util.Log
import com.example.level_up_app.remote.ApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Response

class ProductRepositoryTest {

    private lateinit var apiService: ApiService
    private lateinit var repository: ProductRepository

    private val mockCategory = Category(id = 1, name = "Gaming")

    private val mockProducts = listOf(
        Product(
            id = 1,
            name = "Teclado Mecánico",
            description = "Teclado gaming RGB",
            price = 89.99,
            imageUrl = "teclado.jpg",
            stock = 15,
            currency = "USD",
            category = mockCategory
        ),
        Product(
            id = 2,
            name = "Mouse Gamer",
            description = "Mouse óptico",
            price = 49.99,
            imageUrl = "mouse.jpg",
            stock = 25,
            currency = "USD",
            category = mockCategory
        )
    )

    @Before
    fun setup() {
        // Mock Android Log
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        apiService = mockk(relaxed = true)
        repository = ProductRepository(apiService)
    }

    @Test
    fun `obtener productos debe retornar lista de productos exitosamente`() = runTest {
        // Dado
        coEvery { apiService.getProducts() } returns Response.success(mockProducts)

        // Cuando
        val result = repository.fetchProducts()

        // Entonces
        assertNotNull(result)
        assertEquals(2, result?.size)
        assertEquals("Teclado Mecánico", result?.get(0)?.name)
        coVerify { apiService.getProducts() }
    }

    @Test
    fun `obtener productos debe retornar lista vacia cuando hay error de respuesta`() = runTest {
        // Dado
        coEvery { apiService.getProducts() } returns Response.error(
            404,
            "Not found".toResponseBody()
        )

        // Cuando
        val result = repository.fetchProducts()

        // Entonces
        assertNotNull(result)
        assertTrue(result?.isEmpty() == true)
    }

    @Test
    fun `obtener productos debe retornar null cuando hay excepcion`() = runTest {
        // Dado
        coEvery { apiService.getProducts() } throws Exception("Network error")

        // Cuando
        val result = repository.fetchProducts()

        // Entonces
        assertNull(result)
    }

    @Test
    fun `obtener producto por id debe retornar el producto exitosamente`() = runTest {
        // Dado
        val productId = 1L
        val expectedProduct = mockProducts[0]
        coEvery { apiService.getProductById(productId) } returns Response.success(expectedProduct)

        // Cuando
        val result = repository.getProductById(productId)

        // Entonces
        assertNotNull(result)
        assertEquals(productId, result?.id)
        assertEquals("Teclado Mecánico", result?.name)
        coVerify { apiService.getProductById(productId) }
    }

    @Test
    fun `crear producto debe retornar el producto creado exitosamente`() = runTest {
        // Dado
        val newProduct = mockProducts[0]
        coEvery { apiService.createProduct(newProduct) } returns Response.success(newProduct)

        // Cuando
        val result = repository.createProduct(newProduct)

        // Entonces
        assertNotNull(result)
        assertEquals(newProduct.name, result?.name)
        coVerify { apiService.createProduct(newProduct) }
    }

    @Test
    fun `crear producto debe retornar null cuando hay error`() = runTest {
        // Dado
        val newProduct = mockProducts[0]
        coEvery { apiService.createProduct(newProduct) } returns Response.error(
            400,
            "Bad request".toResponseBody()
        )

        // Cuando
        val result = repository.createProduct(newProduct)

        // Entonces
        assertNull(result)
    }

    @Test
    fun `actualizar producto debe retornar el producto actualizado exitosamente`() = runTest {
        // Dado
        val productId = 1L
        val updatedProduct = mockProducts[0].copy(name = "Teclado Actualizado")
        coEvery { apiService.updateProduct(productId, updatedProduct) } returns Response.success(updatedProduct)

        // Cuando
        val result = repository.updateProduct(productId, updatedProduct)

        // Entonces
        assertNotNull(result)
        assertEquals("Teclado Actualizado", result?.name)
        coVerify { apiService.updateProduct(productId, updatedProduct) }
    }

    @Test
    fun `eliminar producto debe retornar verdadero cuando se elimina exitosamente`() = runTest {
        // Dado
        val productId = 1L
        coEvery { apiService.deleteProduct(productId) } returns Response.success(Unit)

        // Cuando
        val result = repository.deleteProduct(productId)

        // Entonces
        assertTrue(result)
        coVerify { apiService.deleteProduct(productId) }
    }

    @Test
    fun `eliminar producto debe retornar falso cuando hay error`() = runTest {
        // Dado
        val productId = 1L
        coEvery { apiService.deleteProduct(productId) } returns Response.error(
            500,
            "Server error".toResponseBody()
        )

        // Cuando
        val result = repository.deleteProduct(productId)

        // Entonces
        assertFalse(result)
    }

    @Test
    fun `eliminar producto debe retornar falso cuando hay excepcion`() = runTest {
        // Dado
        val productId = 1L
        coEvery { apiService.deleteProduct(productId) } throws Exception("Network error")

        // Cuando
        val result = repository.deleteProduct(productId)

        // Entonces
        assertFalse(result)
    }

    @Test
    fun `obtener categorias debe retornar lista de categorias exitosamente`() = runTest {
        // Dado
        val mockCategories = listOf(
            Category(id = 1, name = "Gaming"),
            Category(id = 2, name = "Oficina")
        )
        coEvery { apiService.getCategories() } returns Response.success(mockCategories)

        // Cuando
        val result = repository.fetchCategories()

        // Entonces
        assertNotNull(result)
        assertEquals(2, result?.size)
        assertEquals("Gaming", result?.get(0)?.name)
        coVerify { apiService.getCategories() }
    }
}

