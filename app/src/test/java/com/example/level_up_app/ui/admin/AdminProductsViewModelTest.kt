package com.example.level_up_app.ui.admin

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.level_up_app.data.Category
import com.example.level_up_app.data.Product
import com.example.level_up_app.data.ProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class AdminProductsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var productRepository: ProductRepository
    private lateinit var viewModel: AdminProductsViewModel

    // Datos de prueba
    private val mockCategory = Category(id = 1, name = "Gaming")

    private val mockProductsList = listOf(
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
            description = "Mouse con sensor óptico",
            price = 49.99,
            imageUrl = "mouse.jpg",
            stock = 25,
            currency = "USD",
            category = mockCategory
        )
    )

    private val mockCategories = listOf(
        Category(id = 1, name = "Gaming"),
        Category(id = 2, name = "Oficina"),
        Category(id = 3, name = "Accesorios")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock Android Log
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        productRepository = mockk(relaxed = true)

        // Configure mocks for init block
        coEvery { productRepository.fetchProducts() } returns emptyList()
        coEvery { productRepository.fetchCategories() } returns emptyList()

        viewModel = AdminProductsViewModel(productRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadAllProducts should update state with products list on success`() = runTest {
        // Given
        coEvery { productRepository.fetchProducts() } returns mockProductsList

        // When
        viewModel.loadAllProducts()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.productsList.size)
        assertEquals("Teclado Mecánico", state.productsList[0].name)
        assertEquals(89.99, state.productsList[0].price, 0.01)
        assertNull(state.error)
    }

    @Test
    fun `loadAllProducts should show error when repository returns null`() = runTest {
        // Given
        coEvery { productRepository.fetchProducts() } returns null

        // When
        viewModel.loadAllProducts()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.productsList.isEmpty())
        assertEquals("Error al cargar productos", state.error)
    }

    @Test
    fun `loadCategories should update state with categories`() = runTest {
        // Given
        coEvery { productRepository.fetchCategories() } returns mockCategories

        // When
        viewModel.loadCategories()
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(3, state.categories.size)
        assertEquals("Gaming", state.categories[0].name)
        assertEquals("Oficina", state.categories[1].name)
    }

    @Test
    fun `deleteProduct should remove product and show success message`() = runTest {
        // Given
        val productId = 1L
        coEvery { productRepository.deleteProduct(productId) } returns true
        coEvery { productRepository.fetchProducts() } returns emptyList()

        // When
        viewModel.deleteProduct(productId)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Producto eliminado correctamente", state.successMessage)
        assertFalse(state.showDeleteDialog)
        assertNull(state.productToDelete)
        coVerify { productRepository.deleteProduct(productId) }
    }

    @Test
    fun `deleteProduct should show error when deletion fails`() = runTest {
        // Given
        val productId = 1L
        coEvery { productRepository.deleteProduct(productId) } returns false

        // When
        viewModel.deleteProduct(productId)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Error al eliminar el producto", state.error)
    }

    @Test
    fun `showDeleteDialog should update state correctly`() {
        // Given
        val product = mockProductsList[0]

        // When
        viewModel.showDeleteDialog(product)

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.showDeleteDialog)
        assertEquals(product, state.productToDelete)
    }

    @Test
    fun `hideDeleteDialog should clear dialog state`() {
        // Given
        viewModel.showDeleteDialog(mockProductsList[0])

        // When
        viewModel.hideDeleteDialog()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.showDeleteDialog)
        assertNull(state.productToDelete)
    }

    @Test
    fun `createProduct should create product and call onSuccess`() = runTest {
        // Given
        val newProduct = mockProductsList[0]
        var onSuccessCalled = false
        coEvery { productRepository.createProduct(any()) } returns newProduct
        coEvery { productRepository.fetchProducts() } returns listOf(newProduct)

        // When
        viewModel.createProduct(newProduct) { onSuccessCalled = true }
        advanceUntilIdle()

        // Then
        assertTrue(onSuccessCalled)
        val state = viewModel.uiState.value
        assertEquals("Producto creado correctamente", state.successMessage)
        coVerify { productRepository.createProduct(newProduct) }
    }

    @Test
    fun `createProduct should show error when creation fails`() = runTest {
        // Given
        val newProduct = mockProductsList[0]
        var onSuccessCalled = false
        coEvery { productRepository.createProduct(any()) } returns null

        // When
        viewModel.createProduct(newProduct) { onSuccessCalled = true }
        advanceUntilIdle()

        // Then
        assertFalse(onSuccessCalled)
        val state = viewModel.uiState.value
        assertEquals("Error al crear el producto", state.error)
    }

    @Test
    fun `updateProduct should update product and call onSuccess`() = runTest {
        // Given
        val productId = 1L
        val updatedProduct = mockProductsList[0].copy(name = "Teclado Actualizado")
        var onSuccessCalled = false
        coEvery { productRepository.updateProduct(productId, any()) } returns updatedProduct
        coEvery { productRepository.fetchProducts() } returns listOf(updatedProduct)

        // When
        viewModel.updateProduct(productId, updatedProduct) { onSuccessCalled = true }
        advanceUntilIdle()

        // Then
        assertTrue(onSuccessCalled)
        val state = viewModel.uiState.value
        assertEquals("Producto actualizado correctamente", state.successMessage)
        coVerify { productRepository.updateProduct(productId, updatedProduct) }
    }

    @Test
    fun `clearMessages should clear success and error messages`() {
        // Given
        val field = AdminProductsViewModel::class.java.getDeclaredField("_uiState")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val stateFlow = field.get(viewModel) as kotlinx.coroutines.flow.MutableStateFlow<AdminProductsState>
        stateFlow.value = AdminProductsState(
            successMessage = "Success",
            error = "Error"
        )

        // When
        viewModel.clearMessages()

        // Then
        val state = viewModel.uiState.value
        assertNull(state.successMessage)
        assertNull(state.error)
    }
}

