package com.example.level_up_app.ui.catalog

import android.util.Log
import com.example.level_up_app.data.Category
import com.example.level_up_app.data.Product
import com.example.level_up_app.data.ProductRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class CatalogViewModelTest {

    private lateinit var productRepository: ProductRepository
    private val testDispatcher = StandardTestDispatcher()

    private val mockCategory = Category(id = 1, name = "Gaming")

    private val mockProducts = listOf(
        Product(
            id = 1,
            name = "Teclado Mecánico",
            description = "Teclado gaming RGB",
            price = 89.99,
            imageUrl = "teclado.jpg",
            stock = 15,
            discontinued = false,
            category = mockCategory
        ),
        Product(
            id = 2,
            name = "Mouse Gamer",
            description = "Mouse óptico",
            price = 49.99,
            imageUrl = "mouse.jpg",
            stock = 25,
            discontinued = false,
            category = mockCategory
        ),
        Product(
            id = 3,
            name = "Monitor Descontinuado",
            description = "Monitor antiguo",
            price = 199.99,
            imageUrl = "monitor.jpg",
            stock = 0,
            discontinued = true,
            category = mockCategory
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        productRepository = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `loadProducts debe cargar solo productos activos exitosamente`() = runTest {
        // Dado
        coEvery { productRepository.fetchProducts() } returns mockProducts

        // Cuando
        val viewModel = CatalogViewModel()
        // Inyectar el mock usando reflexión
        val field = CatalogViewModel::class.java.getDeclaredField("productRepository")
        field.isAccessible = true
        field.set(viewModel, productRepository)

        viewModel.loadProducts()
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(2, state.products.size) // Solo 2 productos activos
        assertTrue(state.products.all { !it.discontinued })
    }

    @Test
    fun `loadProducts debe manejar error de conexion`() = runTest {
        // Dado
        coEvery { productRepository.fetchProducts() } throws Exception("Network error")

        // Cuando
        val viewModel = CatalogViewModel()
        val field = CatalogViewModel::class.java.getDeclaredField("productRepository")
        field.isAccessible = true
        field.set(viewModel, productRepository)

        viewModel.loadProducts()
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertTrue(state.error!!.contains("Error de conexión"))
    }

    @Test
    fun `loadProducts debe manejar respuesta nula del repositorio`() = runTest {
        // Dado
        coEvery { productRepository.fetchProducts() } returns null

        // Cuando
        val viewModel = CatalogViewModel()
        val field = CatalogViewModel::class.java.getDeclaredField("productRepository")
        field.isAccessible = true
        field.set(viewModel, productRepository)

        viewModel.loadProducts()
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Error al cargar productos", state.error)
        assertTrue(state.products.isEmpty())
    }

    @Test
    fun `searchProducts con query vacio debe mostrar todos los productos`() = runTest {
        // Dado
        coEvery { productRepository.fetchProducts() } returns mockProducts

        val viewModel = CatalogViewModel()
        val field = CatalogViewModel::class.java.getDeclaredField("productRepository")
        field.isAccessible = true
        field.set(viewModel, productRepository)

        viewModel.loadProducts()
        testDispatcher.scheduler.advanceUntilIdle()

        // Cuando
        viewModel.searchProducts("")
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertEquals(2, state.products.size) // Solo productos activos
        assertEquals("", state.searchQuery)
    }

    @Test
    fun `searchProducts debe filtrar productos descontinuados de los resultados`() = runTest {
        // Dado
        coEvery { productRepository.fetchProducts() } returns mockProducts
        coEvery { productRepository.searchProducts("Mouse") } returns listOf(mockProducts[1], mockProducts[2])

        val viewModel = CatalogViewModel()
        val field = CatalogViewModel::class.java.getDeclaredField("productRepository")
        field.isAccessible = true
        field.set(viewModel, productRepository)

        viewModel.loadProducts()
        testDispatcher.scheduler.advanceUntilIdle()

        // Cuando
        viewModel.searchProducts("Mouse")
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertEquals(1, state.products.size) // Solo el Mouse activo
        assertEquals("Mouse", state.searchQuery)
        assertTrue(state.products.all { !it.discontinued })
    }

    @Test
    fun `searchProducts debe buscar localmente si falla el backend`() = runTest {
        // Dado
        coEvery { productRepository.fetchProducts() } returns mockProducts
        coEvery { productRepository.searchProducts(any()) } returns null

        val viewModel = CatalogViewModel()
        val field = CatalogViewModel::class.java.getDeclaredField("productRepository")
        field.isAccessible = true
        field.set(viewModel, productRepository)

        viewModel.loadProducts()
        testDispatcher.scheduler.advanceUntilIdle()

        // Cuando
        viewModel.searchProducts("Teclado")
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertEquals(1, state.products.size)
        assertEquals("Teclado Mecánico", state.products[0].name)
    }

    @Test
    fun `filterByCategory debe filtrar productos descontinuados`() = runTest {
        // Dado
        coEvery { productRepository.fetchProducts() } returns mockProducts
        coEvery { productRepository.getProductsByCategory(1L) } returns mockProducts

        val viewModel = CatalogViewModel()
        val field = CatalogViewModel::class.java.getDeclaredField("productRepository")
        field.isAccessible = true
        field.set(viewModel, productRepository)

        viewModel.loadProducts()
        testDispatcher.scheduler.advanceUntilIdle()

        // Cuando
        viewModel.filterByCategory(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertEquals(2, state.products.size) // Solo productos activos
        assertTrue(state.products.all { !it.discontinued })
    }

    @Test
    fun `filterByCategory debe manejar excepciones`() = runTest {
        // Dado
        coEvery { productRepository.fetchProducts() } returns mockProducts
        coEvery { productRepository.getProductsByCategory(any()) } throws Exception("Error")

        val viewModel = CatalogViewModel()
        val field = CatalogViewModel::class.java.getDeclaredField("productRepository")
        field.isAccessible = true
        field.set(viewModel, productRepository)

        viewModel.loadProducts()
        testDispatcher.scheduler.advanceUntilIdle()

        // Cuando
        viewModel.filterByCategory(1L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
    }
}

