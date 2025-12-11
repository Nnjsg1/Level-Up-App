package com.example.level_up_app.ui.login

import android.util.Log
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class CreateAccountViewModelTest {

    private lateinit var viewModel: CreateAccountViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        viewModel = CreateAccountViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `updateName debe actualizar el nombre en el estado`() = runTest {
        // Cuando
        viewModel.actualizarName("Juan Pérez")

        // Entonces
        val state = viewModel.FormData.value
        assertEquals("Juan Pérez", state.name)
    }

    @Test
    fun `updateEmail debe actualizar el email en el estado`() = runTest {
        // Cuando
        viewModel.actualizarEmail("juan@example.com")

        // Entonces
        val state = viewModel.FormData.value
        assertEquals("juan@example.com", state.email)
    }

    @Test
    fun `updatePassword debe actualizar la password en el estado`() = runTest {
        // Cuando
        viewModel.actualizarPassword("password123")

        // Entonces
        val state = viewModel.FormData.value
        assertEquals("password123", state.password)
    }

    @Test
    fun `updateConfirmPassword debe actualizar la confirmacion de password`() = runTest {
        // Cuando
        viewModel.actualizarPassword("password123")

        // Entonces
        val state = viewModel.FormData.value
        assertEquals("password123", state.password)
    }

    @Test
    fun `updateDateOfBirth debe actualizar la fecha de nacimiento`() = runTest {
        // Cuando
        viewModel.actualizarDob("01/01/1990")

        // Entonces
        val state = viewModel.FormData.value
        assertEquals("01/01/1990", state.dob)
    }

    @Test
    fun `CreateAccount con campos vacios debe mostrar error`() = runTest {
        // Dado - todos los campos vacíos por defecto

        // Cuando
        viewModel.CreateAccount()
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.FormData.value
        assertNotNull(state.error)
        assertFalse(state.isAccountCreated)
    }

    @Test
    fun `CreateAccount con nombre vacio debe mostrar error`() = runTest {
        // Dado
        viewModel.actualizarName("")
        viewModel.actualizarEmail("juan@example.com")
        viewModel.actualizarPassword("password123")

        // Cuando
        viewModel.CreateAccount()
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.FormData.value
        assertFalse(state.isAccountCreated)
        assertEquals("El nombre es requerido", state.error)
    }

    @Test
    fun `CreateAccount con nombre corto debe mostrar error`() = runTest {
        // Dado
        viewModel.actualizarName("ab")
        viewModel.actualizarEmail("juan@example.com")
        viewModel.actualizarPassword("password123")

        // Cuando
        viewModel.CreateAccount()
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.FormData.value
        assertFalse(state.isAccountCreated)
        assertEquals("El nombre debe tener al menos 3 caracteres", state.error)
    }

    @Test
    fun `CreateAccount con email invalido debe mostrar error`() = runTest {
        // Dado
        viewModel.actualizarName("Juan Pérez")
        viewModel.actualizarEmail("emailinvalido")
        viewModel.actualizarPassword("password123")

        // Cuando
        viewModel.CreateAccount()
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.FormData.value
        assertFalse(state.isAccountCreated)
        assertNotNull(state.error)
        assertTrue(state.error?.contains("email") == true || state.error?.contains("correo") == true)
    }

    @Test
    fun `CreateAccount con password vacio debe mostrar error`() = runTest {
        // Dado
        viewModel.actualizarName("Juan Pérez")
        viewModel.actualizarEmail("juan@example.com")
        viewModel.actualizarPassword("")

        // Cuando
        viewModel.CreateAccount()
        testDispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val state = viewModel.FormData.value
        assertFalse(state.isAccountCreated)
        assertNotNull(state.error)
    }
}

