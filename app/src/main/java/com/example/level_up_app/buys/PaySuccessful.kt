package com.example.level_up_app.buys

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PayResultScreen(
    isSuccess: Boolean,
    onRetry: () -> Unit,
    onGoHome: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
            Text(
                text = if (isSuccess) "Pago confirmado" else "Pago rechazado",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            if (isSuccess) {
                Button(onClick = onGoHome, modifier = Modifier.padding(top = 16.dp)) {
                    Text("Volver al menú principal")
                }
            } else {
                Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
                    Text("Reintentar pago")
                }
            }
        }
    }
}

@Composable
fun PaySuccessfuln() {
    PayResultScreen(
        isSuccess = true,
        onRetry = {},
        onGoHome = {}
    )
}