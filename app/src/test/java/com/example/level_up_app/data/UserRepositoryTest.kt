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

class UserRepositoryTest {

    private lateinit var apiService: ApiService
    private lateinit var repository: UserRepository

    private val mockUser = User(
        id = 1L,
        name = "Test User",
        email = "test@example.com",
        clave = "password123",
        isAdmin = false,
        createdAt = "2024-01-01"
    )

    private val mockUsers = listOf(
        mockUser,
        User(
            id = 2L,
            name = "Admin User",
            email = "admin@example.com",
            clave = "admin123",
            isAdmin = true,
            createdAt = "2024-01-01"
        )
    )

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        apiService = mockk(relaxed = true)
        repository = UserRepository()

        // Inject mock API service
        val field = UserRepository::class.java.getDeclaredField("apiService")
        field.isAccessible = true
        field.set(repository, apiService)
    }

    @Test
    fun `login exitoso debe retornar LoginResponse con success true`() = runTest {
        // Dado
        val email = "test@example.com"
        val clave = "password123"
        val successResponse = LoginResponse(
            success = true,
            message = "Login exitoso",
            user = mockUser
        )

        coEvery { apiService.login(any()) } returns Response.success(successResponse)

        // Cuando
        val result = repository.login(email, clave)

        // Entonces
        assertTrue(result.success)
        assertEquals("Login exitoso", result.message)
        assertNotNull(result.user)
        assertEquals(mockUser.email, result.user?.email)
    }

    @Test
    fun `login con credenciales incorrectas debe retornar error 401`() = runTest {
        // Dado
        val email = "test@example.com"
        val clave = "wrongpassword"

        coEvery { apiService.login(any()) } returns Response.error(
            401,
            "Unauthorized".toResponseBody()
        )

        // Cuando
        val result = repository.login(email, clave)

        // Entonces
        assertFalse(result.success)
        assertEquals("Email o contraseña incorrectos", result.message)
        assertNull(result.user)
    }

    @Test
    fun `login con usuario no encontrado debe retornar error 404`() = runTest {
        // Dado
        val email = "noexiste@example.com"
        val clave = "password123"

        coEvery { apiService.login(any()) } returns Response.error(
            404,
            "Not found".toResponseBody()
        )

        // Cuando
        val result = repository.login(email, clave)

        // Entonces
        assertFalse(result.success)
        assertEquals("Usuario no encontrado", result.message)
    }

    @Test
    fun `login con error del servidor debe retornar error 500`() = runTest {
        // Dado
        val email = "test@example.com"
        val clave = "password123"

        coEvery { apiService.login(any()) } returns Response.error(
            500,
            "Server error".toResponseBody()
        )

        // Cuando
        val result = repository.login(email, clave)

        // Entonces
        assertFalse(result.success)
        assertEquals("Error en el servidor", result.message)
    }

    @Test
    fun `login con excepcion de red debe manejar error`() = runTest {
        // Dado
        val email = "test@example.com"
        val clave = "password123"

        coEvery { apiService.login(any()) } throws Exception("Network error")

        // Cuando
        val result = repository.login(email, clave)

        // Entonces
        assertFalse(result.success)
        assertTrue(result.message.contains("Error de conexión"))
    }

    @Test
    fun `register exitoso debe retornar LoginResponse con success true`() = runTest {
        // Dado
        val newUser = mockUser.copy(id = 0L)
        val successResponse = LoginResponse(
            success = true,
            message = "Usuario registrado",
            user = mockUser
        )

        coEvery { apiService.register(newUser) } returns Response.success(successResponse)

        // Cuando
        val result = repository.register(newUser)

        // Entonces
        assertTrue(result.success)
        assertNotNull(result.user)
    }

    @Test
    fun `register con email ya existente debe retornar error 400`() = runTest {
        // Dado
        val newUser = mockUser.copy(id = 0L)

        coEvery { apiService.register(newUser) } returns Response.error(
            400,
            "Bad request".toResponseBody()
        )

        // Cuando
        val result = repository.register(newUser)

        // Entonces
        assertFalse(result.success)
        assertEquals("Email ya registrado", result.message)
    }

    @Test
    fun `fetchUsers debe retornar lista de usuarios exitosamente`() = runTest {
        // Dado
        coEvery { apiService.getUsers() } returns Response.success(mockUsers)

        // Cuando
        val result = repository.fetchUsers()

        // Entonces
        assertNotNull(result)
        assertEquals(2, result?.size)
    }

    @Test
    fun `fetchUsers debe retornar lista vacia cuando hay error`() = runTest {
        // Dado
        coEvery { apiService.getUsers() } returns Response.error(
            403,
            "Forbidden".toResponseBody()
        )

        // Cuando
        val result = repository.fetchUsers()

        // Entonces
        assertNotNull(result)
        assertTrue(result?.isEmpty() == true)
    }

    @Test
    fun `fetchUsers debe retornar null cuando hay excepcion`() = runTest {
        // Dado
        coEvery { apiService.getUsers() } throws Exception("Network error")

        // Cuando
        val result = repository.fetchUsers()

        // Entonces
        assertNull(result)
    }

    @Test
    fun `getUserById debe retornar usuario exitosamente`() = runTest {
        // Dado
        val userId = "1"
        coEvery { apiService.getUserById(userId) } returns Response.success(mockUser)

        // Cuando
        val result = repository.getUserById(userId)

        // Entonces
        assertNotNull(result)
        assertEquals(mockUser.id, result?.id)
        assertEquals(mockUser.email, result?.email)
    }

    @Test
    fun `updateUser debe actualizar usuario exitosamente`() = runTest {
        // Dado
        val userId = 1L
        val newName = "Updated Name"
        val newClave = "newpassword123"

        coEvery { apiService.getUserById(userId.toString()) } returns Response.success(mockUser)
        val updatedUser = mockUser.copy(name = newName, clave = newClave)
        coEvery { apiService.updateUser(userId.toString(), updatedUser) } returns Response.success(updatedUser)

        // Cuando
        val result = repository.updateUser(userId, newName, newClave)

        // Entonces
        assertTrue(result.success)
        assertEquals("Usuario actualizado correctamente", result.message)
        assertEquals(newName, result.user?.name)
    }

    @Test
    fun `updateUser debe manejar error cuando no se encuentra el usuario`() = runTest {
        // Dado
        val userId = 1L
        val newName = "Updated Name"
        val newClave = "newpassword123"

        coEvery { apiService.getUserById(userId.toString()) } returns Response.error(
            404,
            "Not found".toResponseBody()
        )

        // Cuando
        val result = repository.updateUser(userId, newName, newClave)

        // Entonces
        assertFalse(result.success)
        assertEquals("No se pudo obtener datos del usuario", result.message)
    }
}

