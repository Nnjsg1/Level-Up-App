package com.example.level_up_app.data

import android.util.Log
import com.example.level_up_app.remote.ApiService
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)

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
    fun `actualizar producto debe retornar null cuando hay error`() = runTest {
        // Dado
        val productId = 1L
        val updatedProduct = mockProducts[0]
        coEvery { apiService.updateProduct(productId, updatedProduct) } returns Response.error(
            400,
            "Bad request".toResponseBody()
        )

        // Cuando
        val result = repository.updateProduct(productId, updatedProduct)

        // Entonces
        assertNull(result)
    }

    @Test
    fun `discontinueProduct debe marcar producto como descontinuado`() = runTest {
        // Dado
        val productId = 1L
        val discontinuedProduct = mockProducts[0].copy(discontinued = true)
        coEvery { apiService.discontinueProduct(productId) } returns Response.success(discontinuedProduct)

        // Cuando
        val result = repository.discontinueProduct(productId)

        // Entonces
        assertNotNull(result)
        assertTrue(result?.discontinued == true)
        coVerify { apiService.discontinueProduct(productId) }
    }

    @Test
    fun `discontinueProduct debe retornar null cuando hay error`() = runTest {
        // Dado
        val productId = 1L
        coEvery { apiService.discontinueProduct(productId) } returns Response.error(
            404,
            "Not found".toResponseBody()
        )

        // Cuando
        val result = repository.discontinueProduct(productId)

        // Entonces
        assertNull(result)
    }

    @Test
    fun `reactivateProduct debe reactivar producto descontinuado`() = runTest {
        // Dado
        val productId = 1L
        val reactivatedProduct = mockProducts[0].copy(discontinued = false)
        coEvery { apiService.reactivateProduct(productId) } returns Response.success(reactivatedProduct)

        // Cuando
        val result = repository.reactivateProduct(productId)

        // Entonces
        assertNotNull(result)
        assertFalse(result?.discontinued == true)
        coVerify { apiService.reactivateProduct(productId) }
    }

    @Test
    fun `fetchActiveProducts debe retornar solo productos activos`() = runTest {
        // Dado
        val activeProducts = mockProducts
        coEvery { apiService.getActiveProducts() } returns Response.success(activeProducts)

        // Cuando
        val result = repository.fetchActiveProducts()

        // Entonces
        assertNotNull(result)
        assertEquals(2, result?.size)
        assertTrue(result?.all { !it.discontinued } == true)
    }

    @Test
    fun `fetchDiscontinuedProducts debe retornar solo productos descontinuados`() = runTest {
        // Dado
        val discontinuedProduct = mockProducts[0].copy(discontinued = true)
        coEvery { apiService.getDiscontinuedProducts() } returns Response.success(listOf(discontinuedProduct))

        // Cuando
        val result = repository.fetchDiscontinuedProducts()

        // Entonces
        assertNotNull(result)
        assertEquals(1, result?.size)
        assertTrue(result?.all { it.discontinued } == true)
    }

    @Test
    fun `searchProducts debe buscar por titulo`() = runTest {
        // Dado
        val query = "Teclado"
        val searchResults = listOf(mockProducts[0])
        coEvery { apiService.searchProducts(query) } returns Response.success(searchResults)

        // Cuando
        val result = repository.searchProducts(query)

        // Entonces
        assertNotNull(result)
        assertEquals(1, result?.size)
        assertTrue(result?.get(0)?.name?.contains("Teclado") == true)
    }

    @Test
    fun `getProductsByCategory debe retornar productos de una categoria`() = runTest {
        // Dado
        val categoryId = 1L
        coEvery { apiService.getProductsByCategory(categoryId) } returns Response.success(mockProducts)

        // Cuando
        val result = repository.getProductsByCategory(categoryId)

        // Entonces
        assertNotNull(result)
        assertEquals(2, result?.size)
        // Verificar que todos los productos tienen categoría
        assertTrue(result?.all { it.category != null } == true)
    }

    @Test
    fun `fetchCategories debe retornar lista de categorias`() = runTest {
        // Dado
        val categories = listOf(
            Category(id = 1, name = "Gaming"),
            Category(id = 2, name = "Oficina")
        )
        coEvery { apiService.getCategories() } returns Response.success(categories)

        // Cuando
        val result = repository.fetchCategories()

        // Entonces
        assertNotNull(result)
        assertEquals(2, result?.size)
        coVerify { apiService.getCategories() }
    }
}

