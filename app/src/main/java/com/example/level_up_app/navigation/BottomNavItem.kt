package com.example.level_up_app.navigation

sealed class BottomNavItem(val route: String, val title: String) {
    object Home : BottomNavItem("home", "Inicio")
    object Catalog : BottomNavItem("catalog", "Catálogo")
    object News : BottomNavItem("news", "Noticias")
    object Profile : BottomNavItem("profile", "Perfil")
}