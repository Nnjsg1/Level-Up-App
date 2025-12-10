package com.example.level_up_app.ui.login

import com.example.level_up_app.data.User

data class FormularioLogin(
    val email: String = "",
    val password: String = "",
    val error: String? = null,
    val isLogin: Boolean = false,
    val isLoading: Boolean = false,
    val user: User? = null
)

