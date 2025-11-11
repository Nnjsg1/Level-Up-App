package com.example.level_up_app.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.level_up_app.R

@Composable
fun Fondo(){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(R.drawable.fondo),
            contentDescription = "pastel",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize())
        Surface(
            modifier = Modifier.fillMaxSize().padding(25.dp),
            color = Color.Blue.copy(alpha = 0.0f)
        ) {
            Text("Hola Mundo",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyLarge)
        }
    }
}