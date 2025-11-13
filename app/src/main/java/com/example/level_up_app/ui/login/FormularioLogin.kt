package com.example.level_up_app.ui.login

data class FormularioLogin(
    val email: String = "",
    val password: String = "",
    val error: String? = null,
    val isLogin: Boolean = false,
)

