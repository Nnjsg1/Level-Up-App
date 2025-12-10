package com.example.level_up_app.data

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.level_up_app.data.model.User

object UserSession {
    // Para probar la funcionalidad de admin, inicializamos con un usuario admin
    // TODO: Cambiar esto cuando se implemente el login real con la API
    var currentUser by mutableStateOf<User?>(
        User(
            id = 1,
            name = "Admin",
            email = "admin@levelup.com",
            clave = "",
            isAdmin = true
        )
    )
        private set

    val isAdmin: Boolean
        get() = currentUser?.isAdmin ?: false

    fun login(user: User) {
        currentUser = user
    }

    fun logout() {
        currentUser = null
    }
}

