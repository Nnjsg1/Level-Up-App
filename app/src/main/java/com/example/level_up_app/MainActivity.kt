package com.example.level_up_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.level_up_app.ui.login.LoginViewModel
import com.example.level_up_app.ui.theme.LevelUpAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        setContent {
            LevelUpAppTheme {
                AppNavigation(loginViewModel)
            }
        }
    }
}
