package com.example.level_up_app.ui.login

import android.util.Log
import com.example.level_up_app.data.LoginResponse
import com.example.level_up_app.data.User
import com.example.level_up_app.data.UserRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private lateinit var userRepository: UserRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    private val mockUser = User(
        id = 1L,
        name = "Test User",
        email = "test@example.com",
        clave = "password123",
        isAdmin = false,
        createdAt = "2024-01-01"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        userRepository = mockk(relaxed = true)

        // Crear el ViewModel
        viewModel = LoginViewModel()

        // Inyectar el mock INMEDIATAMENTE usando reflexión
        try {
            val field = LoginViewModel::class.java.getDeclaredField("userRepository")
            field.isAccessible = true
            field.set(viewModel, userRepository)
        } catch (e: Exception) {
            Log.e("LoginViewModelTest", "Error inyectando repositorio: ${e.message}")
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `actualizarEmail debe actualizar el email en el estado`() = runTest {
        // Cuando
        viewModel.actualizarEmail("test@example.com")

        // Entonces
        val state = viewModel.FormData.value
        assertEquals("test@example.com", state.email)
    }

    @Test
    fun `actualizarPassword debe actualizar la password en el estado`() = runTest {
        // Cuando
        viewModel.actualizarPassword("password123")

        // Entonces
        val state = viewModel.FormData.value
        assertEquals("password123", state.password)
    }

    @Test
    fun `Login con email vacio debe mostrar error`() = runTest {
        // Dado
        viewModel.actualizarEmail("")
        viewModel.actualizarPassword("password123")

        // Cuando
        viewModel.Login()

        // Entonces
        val state = viewModel.FormData.value
        assertEquals("Por favor ingresa tu email", state.error)
        assertFalse(state.isLogin)
    }

    @Test
    fun `Login con email invalido debe mostrar error`() = runTest {
        // Dado - Email con más de 6 caracteres pero sin @ (formato inválido)
        viewModel.actualizarEmail("abcdefgh")
        viewModel.actualizarPassword("password123")

        // Cuando
        viewModel.Login()

        // Entonces
        val state = viewModel.FormData.value
        assertEquals("Email inválido", state.error)
    }

    @Test
    fun `Login con email sin formato correcto debe mostrar error`() = runTest {
        // Dado - Email sin @
        viewModel.actualizarEmail("testexample.com")
        viewModel.actualizarPassword("password123")

        // Cuando
        viewModel.Login()

        // Entonces
        val state = viewModel.FormData.value
        assertEquals("Email inválido", state.error)
    }

    @Test
    fun `Login con password vacio debe mostrar error`() = runTest {
        // Dado
        viewModel.actualizarEmail("test@example.com")
        viewModel.actualizarPassword("")

        // Cuando
        viewModel.Login()

        // Entonces
        val state = viewModel.FormData.value
        assertEquals("Por favor ingresa tu contraseña", state.error)
    }

    @Test
    fun `Login con password corto debe mostrar error`() = runTest {
        // Dado
        viewModel.actualizarEmail("test@example.com")
        viewModel.actualizarPassword("1234")

        // Cuando
        viewModel.Login()

        // Entonces
        val state = viewModel.FormData.value
        assertEquals("La contraseña debe tener al menos 5 caracteres", state.error)
    }

    @Test
    fun `Login exitoso debe actualizar el estado correctamente`() = runTest {
        // Dado
        viewModel.actualizarEmail("test@example.com")
        viewModel.actualizarPassword("password123")

        val successResponse = LoginResponse(
            success = true,
            message = "Login exitoso",
            user = mockUser
        )
        coEvery { userRepository.login(any(), any()) } returns successResponse

        // Cuando
        viewModel.Login()

        // Entonces
        val state = viewModel.FormData.value
        assertTrue(state.isLogin)
        assertNull(state.error)
        assertFalse(state.isLoading)
        assertEquals(mockUser, state.user)
    }

    @Test
    fun `Login fallido debe mostrar mensaje de error`() = runTest {
        // Dado
        viewModel.actualizarEmail("test@example.com")
        viewModel.actualizarPassword("wrongpassword")

        val failureResponse = LoginResponse(
            success = false,
            message = "Credenciales incorrectas",
            user = null
        )
        coEvery { userRepository.login(any(), any()) } returns failureResponse

        // Cuando
        viewModel.Login()

        // Entonces
        val state = viewModel.FormData.value
        assertFalse(state.isLogin)
        assertEquals("Credenciales incorrectas", state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun `Login con excepcion debe mostrar error de conexion`() = runTest {
        // Dado
        viewModel.actualizarEmail("test@example.com")
        viewModel.actualizarPassword("password123")

        coEvery { userRepository.login(any(), any()) } throws Exception("Network error")

        // Cuando
        viewModel.Login()

        // Entonces
        val state = viewModel.FormData.value
        assertFalse(state.isLogin)
        assertNotNull(state.error)
        assertTrue(state.error!!.contains("Error de conexión"))
        assertFalse(state.isLoading)
    }

    @Test
    fun `limpiarEstado debe resetear el formulario`() = runTest {
        // Dado
        viewModel.actualizarEmail("test@example.com")
        viewModel.actualizarPassword("password123")

        // Cuando
        viewModel.limpiarEstado()

        // Entonces
        val state = viewModel.FormData.value
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertNull(state.error)
        assertFalse(state.isLogin)
        assertFalse(state.isLoading)
        assertNull(state.user)
    }
}

