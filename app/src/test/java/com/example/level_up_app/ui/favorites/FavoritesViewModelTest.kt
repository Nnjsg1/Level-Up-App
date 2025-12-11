package com.example.level_up_app.ui.favorites

import android.util.Log
import com.example.level_up_app.data.*
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class FavoritesViewModelTest {

    private lateinit var viewModel: FavoritesViewModel
    private lateinit var favoritesRepository: FavoritesRepository
    private lateinit var productRepository: ProductRepository
    private val testDispatcher = StandardTestDispatcher()

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

    private val mockFavorites = listOf(
        Favorite(userId = 1, productId = 1L),
        Favorite(userId = 1, productId = 2L)
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        favoritesRepository = mockk(relaxed = true)
        productRepository = mockk(relaxed = true)

        viewModel = FavoritesViewModel(favoritesRepository, productRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `loadFavorites debe cargar solo productos activos y eliminar descontinuados`() = runTest {
        // Dado
        val userId = 1L
        coEvery { favoritesRepository.getFavoritesByUser(userId) } returns mockFavorites
        coEvery { productRepository.getProductById(1L) } returns mockProduct
        coEvery { productRepository.getProductById(2L) } returns discontinuedProduct
        coEvery { favoritesRepository.removeFromFavorites(userId, 2L) } returns true

        // Cuando
        viewModel.loadFavorites(userId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(1, state.favoriteProducts.size) // Solo el producto activo
        assertEquals(1, state.favoriteIds.size)
        assertTrue(state.favoriteProducts.all { !it.discontinued })
        coVerify { favoritesRepository.removeFromFavorites(userId, 2L) }
    }

    @Test
    fun `loadFavorites debe manejar error al cargar`() = runTest {
        // Dado
        val userId = 1L
        coEvery { favoritesRepository.getFavoritesByUser(userId) } returns null

        // Cuando
        viewModel.loadFavorites(userId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Error al cargar favoritos", state.error)
        assertTrue(state.favoriteProducts.isEmpty())
    }

    @Test
    fun `loadFavorites debe manejar excepciones`() = runTest {
        // Dado
        val userId = 1L
        coEvery { favoritesRepository.getFavoritesByUser(userId) } throws Exception("Network error")

        // Cuando
        viewModel.loadFavorites(userId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertTrue(state.error!!.contains("Error de conexión"))
    }

    @Test
    fun `addToFavorites debe agregar producto exitosamente`() = runTest {
        // Dado
        val userId = 1L
        val favorite = Favorite(userId = userId.toInt(), productId = mockProduct.id)
        coEvery { favoritesRepository.addToFavorites(userId, mockProduct.id) } returns favorite

        // Cuando
        viewModel.addToFavorites(userId, mockProduct)
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertEquals(1, state.favoriteProducts.size)
        assertTrue(state.favoriteIds.contains(mockProduct.id))
        assertEquals("Agregado a favoritos", state.successMessage)
    }

    @Test
    fun `addToFavorites debe manejar error al agregar`() = runTest {
        // Dado
        val userId = 1L
        coEvery { favoritesRepository.addToFavorites(userId, mockProduct.id) } returns null

        // Cuando
        viewModel.addToFavorites(userId, mockProduct)
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertEquals("No se pudo agregar a favoritos", state.error)
    }

    @Test
    fun `removeFromFavorites debe eliminar producto exitosamente`() = runTest {
        // Dado
        val userId = 1L
        coEvery { favoritesRepository.getFavoritesByUser(userId) } returns mockFavorites
        coEvery { productRepository.getProductById(1L) } returns mockProduct
        coEvery { productRepository.getProductById(2L) } returns discontinuedProduct
        coEvery { favoritesRepository.removeFromFavorites(userId, any()) } returns true

        viewModel.loadFavorites(userId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Cuando
        viewModel.removeFromFavorites(userId, mockProduct.id)
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertEquals(0, state.favoriteProducts.size)
        assertFalse(state.favoriteIds.contains(mockProduct.id))
        assertEquals("Eliminado de favoritos", state.successMessage)
    }

    @Test
    fun `removeFromFavorites debe manejar error al eliminar`() = runTest {
        // Dado
        val userId = 1L
        coEvery { favoritesRepository.removeFromFavorites(userId, mockProduct.id) } returns false

        // Cuando
        viewModel.removeFromFavorites(userId, mockProduct.id)
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertEquals("No se pudo eliminar de favoritos", state.error)
    }

    @Test
    fun `isFavorite debe retornar true si el producto esta en favoritos`() = runTest {
        // Dado
        val userId = 1L
        coEvery { favoritesRepository.getFavoritesByUser(userId) } returns mockFavorites
        coEvery { productRepository.getProductById(1L) } returns mockProduct
        coEvery { productRepository.getProductById(2L) } returns discontinuedProduct
        coEvery { favoritesRepository.removeFromFavorites(any(), any()) } returns true

        viewModel.loadFavorites(userId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Cuando
        val result = viewModel.isFavorite(mockProduct.id)

        // Entonces
        assertTrue(result)
    }

    @Test
    fun `isFavorite debe retornar false si el producto no esta en favoritos`() = runTest {
        // Dado
        val userId = 1L
        coEvery { favoritesRepository.getFavoritesByUser(userId) } returns emptyList()

        viewModel.loadFavorites(userId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Cuando
        val result = viewModel.isFavorite(999L)

        // Entonces
        assertFalse(result)
    }

    @Test
    fun `clearMessages debe limpiar mensajes de error y exito`() = runTest {
        // Dado
        val userId = 1L
        coEvery { favoritesRepository.addToFavorites(userId, mockProduct.id) } returns null
        viewModel.addToFavorites(userId, mockProduct)
        testDispatcher.scheduler.advanceUntilIdle()

        // Cuando
        viewModel.clearMessages()

        // Entonces
        val state = viewModel.uiState.value
        assertNull(state.error)
        assertNull(state.successMessage)
    }
}

