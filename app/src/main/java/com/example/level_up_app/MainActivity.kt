package com.example.level_up_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.lifecycle.ViewModelProvider
import com.example.level_up_app.ui.login.LoginScreen
import com.example.level_up_app.ui.login.LoginViewModel

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.level_up_app.screen.Fondo

import com.example.level_up_app.ui.theme.LevelUpAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

       
        val loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        setContent {
            LevelUpAppTheme {

                Fondo()
                LoginScreen(loginViewModel)

              

            }
        }
    }
}
