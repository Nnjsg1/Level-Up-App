package com.example.level_up_app.ui.login

data class FormularioCreateAccount(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val dob: String = "",
    val error: String? = null,
    val isAccountCreated: Boolean = false,
)

