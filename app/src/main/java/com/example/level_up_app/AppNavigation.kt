package com.example.level_up_app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.level_up_app.ui.login.LoginScreen
import com.example.level_up_app.ui.login.LoginViewModel

@Composable
fun AppNavigation(loginViewModel: LoginViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(loginViewModel) { 
                navController.navigate("main") { // Navigate to main screen
                    popUpTo("login") { inclusive = true } // Pop login screen off back stack
                }
            }
        }
        composable("main") {
            MainScreen()
        }
    }
}