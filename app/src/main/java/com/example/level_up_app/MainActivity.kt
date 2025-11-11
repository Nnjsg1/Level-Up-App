package com.example.level_up_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.lifecycle.ViewModelProvider
import com.example.level_up_app.ui.login.LoginScreen
import com.example.level_up_app.ui.login.LoginViewModel
import com.example.level_up_app.ui.login.CreateAccountScreen
import com.example.level_up_app.ui.login.RememberPassScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import com.example.level_up_app.ui.theme.LevelUpAppTheme

sealed class Screen {
    object Login : Screen()
    object CreateAccount : Screen()
    object RememberPass : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

       
        val loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        setContent {
            LevelUpAppTheme {

                var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

                when (currentScreen) {
                    is Screen.Login -> LoginScreen(
                        loginViewModel,
                        onNavigateToCreateAccount = { currentScreen = Screen.CreateAccount },
                        onNavigateToRememberPass = { currentScreen = Screen.RememberPass }
                    )
                    is Screen.CreateAccount -> CreateAccountScreen(
                        onCreate = { name, email, password, dob ->

                            currentScreen = Screen.Login
                        },
                        onBackToLogin = { currentScreen = Screen.Login }
                    )
                    is Screen.RememberPass -> RememberPassScreen(
                        onBack = { currentScreen = Screen.Login }
                    )
                }
            }
        }
    }
}
