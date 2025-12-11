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
    fun `fetchProducts should return products list on success`() = runTest {
        // Given
        coEvery { apiService.getProducts() } returns Response.success(mockProducts)

        // When
        val result = repository.fetchProducts()

        // Then
        assertNotNull(result)
        assertEquals(2, result?.size)
        assertEquals("Teclado Mecánico", result?.get(0)?.name)
        coVerify { apiService.getProducts() }
    }

    @Test
    fun `fetchProducts should return empty list on error response`() = runTest {
        // Given
        coEvery { apiService.getProducts() } returns Response.error(
            404,
            "Not found".toResponseBody()
        )

        // When
        val result = repository.fetchProducts()

        // Then
        assertNotNull(result)
        assertTrue(result?.isEmpty() == true)
    }

    @Test
    fun `fetchProducts should return null on exception`() = runTest {
        // Given
        coEvery { apiService.getProducts() } throws Exception("Network error")

        // When
        val result = repository.fetchProducts()

        // Then
        assertNull(result)
    }

    @Test
    fun `getProductById should return product on success`() = runTest {
        // Given
        val productId = 1L
        val expectedProduct = mockProducts[0]
        coEvery { apiService.getProductById(productId) } returns Response.success(expectedProduct)

        // When
        val result = repository.getProductById(productId)

        // Then
        assertNotNull(result)
        assertEquals(productId, result?.id)
        assertEquals("Teclado Mecánico", result?.name)
        coVerify { apiService.getProductById(productId) }
    }

    @Test
    fun `createProduct should return created product on success`() = runTest {
        // Given
        val newProduct = mockProducts[0]
        coEvery { apiService.createProduct(newProduct) } returns Response.success(newProduct)

        // When
        val result = repository.createProduct(newProduct)

        // Then
        assertNotNull(result)
        assertEquals(newProduct.name, result?.name)
        coVerify { apiService.createProduct(newProduct) }
    }

    @Test
    fun `createProduct should return null on error`() = runTest {
        // Given
        val newProduct = mockProducts[0]
        coEvery { apiService.createProduct(newProduct) } returns Response.error(
            400,
            "Bad request".toResponseBody()
        )

        // When
        val result = repository.createProduct(newProduct)

        // Then
        assertNull(result)
    }

    @Test
    fun `updateProduct should return updated product on success`() = runTest {
        // Given
        val productId = 1L
        val updatedProduct = mockProducts[0].copy(name = "Teclado Actualizado")
        coEvery { apiService.updateProduct(productId, updatedProduct) } returns Response.success(updatedProduct)

        // When
        val result = repository.updateProduct(productId, updatedProduct)

        // Then
        assertNotNull(result)
        assertEquals("Teclado Actualizado", result?.name)
        coVerify { apiService.updateProduct(productId, updatedProduct) }
    }

    @Test
    fun `deleteProduct should return true on successful deletion`() = runTest {
        // Given
        val productId = 1L
        coEvery { apiService.deleteProduct(productId) } returns Response.success(Unit)

        // When
        val result = repository.deleteProduct(productId)

        // Then
        assertTrue(result)
        coVerify { apiService.deleteProduct(productId) }
    }

    @Test
    fun `deleteProduct should return false on error`() = runTest {
        // Given
        val productId = 1L
        coEvery { apiService.deleteProduct(productId) } returns Response.error(
            500,
            "Server error".toResponseBody()
        )

        // When
        val result = repository.deleteProduct(productId)

        // Then
        assertFalse(result)
    }

    @Test
    fun `deleteProduct should return false on exception`() = runTest {
        // Given
        val productId = 1L
        coEvery { apiService.deleteProduct(productId) } throws Exception("Network error")

        // When
        val result = repository.deleteProduct(productId)

        // Then
        assertFalse(result)
    }

    @Test
    fun `fetchCategories should return categories list on success`() = runTest {
        // Given
        val mockCategories = listOf(
            Category(id = 1, name = "Gaming"),
            Category(id = 2, name = "Oficina")
        )
        coEvery { apiService.getCategories() } returns Response.success(mockCategories)

        // When
        val result = repository.fetchCategories()

        // Then
        assertNotNull(result)
        assertEquals(2, result?.size)
        assertEquals("Gaming", result?.get(0)?.name)
        coVerify { apiService.getCategories() }
    }
}

