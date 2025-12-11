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
    fun `cargar productos debe actualizar estado con lista de productos exitosamente`() = runTest {
        // Dado
        coEvery { productRepository.fetchProducts() } returns mockProductsList

        // Cuando
        viewModel.loadAllProducts()
        advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.productsList.size)
        assertEquals("Teclado Mecánico", state.productsList[0].name)
        assertEquals(89.99, state.productsList[0].price, 0.01)
        assertNull(state.error)
    }

    @Test
    fun `cargar productos debe mostrar error cuando el repositorio retorna null`() = runTest {
        // Dado
        coEvery { productRepository.fetchProducts() } returns null

        // Cuando
        viewModel.loadAllProducts()
        advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.productsList.isEmpty())
        assertEquals("Error al cargar productos", state.error)
    }

    @Test
    fun `cargar categorias debe actualizar estado con las categorias`() = runTest {
        // Dado
        coEvery { productRepository.fetchCategories() } returns mockCategories

        // Cuando
        viewModel.loadCategories()
        advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertEquals(3, state.categories.size)
        assertEquals("Gaming", state.categories[0].name)
        assertEquals("Oficina", state.categories[1].name)
    }

    @Test
    fun `descontinuar producto debe marcarlo y mostrar mensaje de exito`() = runTest {
        // Dado
        val productId = 1L
        val discontinuedProduct = mockProductsList[0].copy(discontinued = true)
        coEvery { productRepository.discontinueProduct(productId) } returns discontinuedProduct
        coEvery { productRepository.fetchProducts() } returns emptyList()

        // Cuando
        viewModel.discontinueProduct(productId)
        advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Producto descontinuado. Se eliminará automáticamente de carritos y favoritos", state.successMessage)
        assertFalse(state.showDiscontinueDialog)
        assertNull(state.productToDiscontinue)
        coVerify { productRepository.discontinueProduct(productId) }
    }

    @Test
    fun `descontinuar producto debe mostrar error cuando falla`() = runTest {
        // Dado
        val productId = 1L
        coEvery { productRepository.discontinueProduct(productId) } returns null

        // Cuando
        viewModel.discontinueProduct(productId)
        advanceUntilIdle()

        // Entonces
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Error al descontinuar el producto", state.error)
    }

    @Test
    fun `mostrar dialogo de descontinuar debe actualizar estado correctamente`() {
        // Dado
        val product = mockProductsList[0]

        // Cuando
        viewModel.showDiscontinueDialog(product)

        // Entonces
        val state = viewModel.uiState.value
        assertTrue(state.showDiscontinueDialog)
        assertEquals(product, state.productToDiscontinue)
    }

    @Test
    fun `ocultar dialogo de descontinuar debe limpiar el estado`() {
        // Dado
        viewModel.showDiscontinueDialog(mockProductsList[0])

        // Cuando
        viewModel.hideDiscontinueDialog()

        // Entonces
        val state = viewModel.uiState.value
        assertFalse(state.showDiscontinueDialog)
        assertNull(state.productToDiscontinue)
    }

    @Test
    fun `crear producto debe crearlo y llamar al callback de exito`() = runTest {
        // Dado
        val newProduct = mockProductsList[0]
        var onSuccessCalled = false
        coEvery { productRepository.createProduct(any()) } returns newProduct
        coEvery { productRepository.fetchProducts() } returns listOf(newProduct)

        // Cuando
        viewModel.createProduct(newProduct) { onSuccessCalled = true }
        advanceUntilIdle()

        // Entonces
        assertTrue(onSuccessCalled)
        val state = viewModel.uiState.value
        assertEquals("Producto creado correctamente", state.successMessage)
        coVerify { productRepository.createProduct(newProduct) }
    }

    @Test
    fun `crear producto debe mostrar error cuando falla la creacion`() = runTest {
        // Dado
        val newProduct = mockProductsList[0]
        var onSuccessCalled = false
        coEvery { productRepository.createProduct(any()) } returns null

        // Cuando
        viewModel.createProduct(newProduct) { onSuccessCalled = true }
        advanceUntilIdle()

        // Entonces
        assertFalse(onSuccessCalled)
        val state = viewModel.uiState.value
        assertEquals("Error al crear el producto", state.error)
    }

    @Test
    fun `actualizar producto debe actualizarlo y llamar al callback de exito`() = runTest {
        // Dado
        val productId = 1L
        val updatedProduct = mockProductsList[0].copy(name = "Teclado Actualizado")
        var onSuccessCalled = false
        coEvery { productRepository.updateProduct(productId, any()) } returns updatedProduct
        coEvery { productRepository.fetchProducts() } returns listOf(updatedProduct)

        // Cuando
        viewModel.updateProduct(productId, updatedProduct) { onSuccessCalled = true }
        advanceUntilIdle()

        // Entonces
        assertTrue(onSuccessCalled)
        val state = viewModel.uiState.value
        assertEquals("Producto actualizado correctamente", state.successMessage)
        coVerify { productRepository.updateProduct(productId, updatedProduct) }
    }

    @Test
    fun `limpiar mensajes debe eliminar mensajes de exito y error`() {
        // Dado
        val field = AdminProductsViewModel::class.java.getDeclaredField("_uiState")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val stateFlow = field.get(viewModel) as kotlinx.coroutines.flow.MutableStateFlow<AdminProductsState>
        stateFlow.value = AdminProductsState(
            successMessage = "Success",
            error = "Error"
        )

        // Cuando
        viewModel.clearMessages()

        // Entonces
        val state = viewModel.uiState.value
        assertNull(state.successMessage)
        assertNull(state.error)
    }
}

