package com.example.level_up_app.buys

import android.app.Activity
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

// PayScreen: muestra la UI de pago y recibe un NFC.
// - onCancel: Si el usuario apreta Cancelar compra se volvera a la ventana de inicio.
// - onSuccess: callback que se ejecuta cuando se detecta cualquier tag NFC (No se valida el tipo de tarjeta usada) y debe llevar a la pantalla de pago exitoso.
@Composable
fun PayScreen(
    onCancel: () -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // Estado para mostrar si el modo lector quedó activado o no
    var readerActivated by remember { mutableStateOf(false) }
    // Estado para el último tag detectado (hex)
    var lastTagId by remember { mutableStateOf("") }

    // Obtener el adaptador NFC del dispositivo (puede ser null si no tiene NFC)
    val nfcAdapter = NfcAdapter.getDefaultAdapter(context)

    // Helper para convertir bytes a hex
    fun bytesToHex(bytes: ByteArray?): String {
        if (bytes == null) return ""
        return bytes.joinToString(":") { String.format("%02X", it) }
    }

    // Registramos un ReaderCallback; el enable/disable se hace en onResume/onPause del lifecycle
    DisposableEffect(activity) {
         // Define el callback que se ejecuta cuando llega un tag NFC
        val readerCallback = object : NfcAdapter.ReaderCallback {
            override fun onTagDiscovered(tag: Tag?) {
                Log.d("PayScreen", "NFC tag discovered: $tag")
                val idHex = bytesToHex(tag?.id)
                Log.d("PayScreen", "Tag id: $idHex")
                // Esto se ejecuta en un hilo binder; necesitamos volver al hilo UI para ejecutar el callback
                activity?.runOnUiThread {
                    // Usamos lastTagId vacío como indicador de que aún no manejamos una lectura
                    if (lastTagId.isEmpty()) {
                        lastTagId = idHex
                        Log.d("PayScreen", "Handling first tag: $idHex")
                        // Llamar al callback de éxito (no se realizan validaciones)
                        onSuccess()
                    } else {
                        Log.d("PayScreen", "Tag discovered but already handled: $idHex")
                    }
                }
            }
        }

        // Observador de ciclo de vida para activar/desactivar el modo lector en resume/pause
        val lifecycleOwner = activity as? ComponentActivity
        val observer = LifecycleEventObserver { _, event ->
            if (nfcAdapter == null || activity == null) return@LifecycleEventObserver

            val flags = NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_NFC_B or
                    NfcAdapter.FLAG_READER_NFC_F or
                    NfcAdapter.FLAG_READER_NFC_V or
                    NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK or
                    NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS

            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    try {
                        // Intentamos primero invocar enableReaderMode sobre la instancia nfcAdapter
                        val method = nfcAdapter.javaClass.getMethod(
                            "enableReaderMode",
                            Activity::class.java,
                            NfcAdapter.ReaderCallback::class.java,
                            Int::class.javaPrimitiveType,
                            Bundle::class.java
                        )
                        method.invoke(nfcAdapter, activity, readerCallback, flags, null)
                        readerActivated = true
                        Log.d("PayScreen", "enableReaderMode invoked on nfcAdapter instance")
                    } catch (ex1: Exception) {
                        // Si falla, intentamos el método de Activity (firma: enableReaderMode(ReaderCallback, int, Bundle))
                        try {
                            val method2 = activity.javaClass.getMethod(
                                "enableReaderMode",
                                NfcAdapter.ReaderCallback::class.java,
                                Int::class.javaPrimitiveType,
                                Bundle::class.java
                            )
                            method2.invoke(activity, readerCallback, flags, null)
                            readerActivated = true
                            Log.d("PayScreen", "enableReaderMode invoked on activity instance")
                        } catch (ex2: Exception) {
                            readerActivated = false
                            Log.w("PayScreen", "enableReaderMode failed both ways: ${ex1.message} | ${ex2.message}")
                        }
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    try {
                        // intentamos desactivar en nfcAdapter
                        val method = nfcAdapter.javaClass.getMethod("disableReaderMode", Activity::class.java)
                        method.invoke(nfcAdapter, activity)
                        Log.d("PayScreen", "disableReaderMode invoked on nfcAdapter instance")
                    } catch (ex1: Exception) {
                        try {
                            // intentamos método de activity: disableReaderMode()
                            val method2 = activity.javaClass.getMethod("disableReaderMode")
                            method2.invoke(activity)
                            Log.d("PayScreen", "disableReaderMode invoked on activity instance")
                        } catch (ex2: Exception) {
                            Log.w("PayScreen", "disableReaderMode failed both ways: ${ex1.message} | ${ex2.message}")
                        }
                    }
                    readerActivated = false
                }
                else -> {}
            }
        }

        // Registrar el observer si tenemos lifecycle
        if (lifecycleOwner != null) {
            lifecycleOwner.lifecycle.addObserver(observer)
        }

        onDispose {
            // Quitamos el observer y apagamos el lector si está activo
            if (lifecycleOwner != null) {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
            if (activity != null && nfcAdapter != null) {
                try {
                    val method = nfcAdapter.javaClass.getMethod("disableReaderMode", Activity::class.java)
                    method.invoke(nfcAdapter, activity)
                } catch (ex: Exception) {
                    Log.w("PayScreen", "onDispose disableReaderMode failed: ${ex.message}")
                }
            }
            // reset flag para futuras entradas si regresas a la pantalla
            readerActivated = false
            lastTagId = ""
         }
     }

    // Interfaz simple: mensaje centrado y botón "Cancelar Pago" debajo
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Acerque su Tarjeta", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            // Mensajes de diagnóstico para ayudar a identificar por qué no lee
            if (nfcAdapter == null) {
                Text(text = "NFC no disponible en este dispositivo", modifier = Modifier.padding(top = 12.dp))
            } else if (!nfcAdapter.isEnabled) {
                Text(text = "NFC desactivado: actívalo en Ajustes", modifier = Modifier.padding(top = 12.dp))
            } else if (!readerActivated) {
                Text(text = "No se pudo activar modo lector (intenta reiniciar la app)", modifier = Modifier.padding(top = 12.dp))
            } else {
                Text(text = "Lector NFC activo: acerque un tag", modifier = Modifier.padding(top = 12.dp))
            }

            // Mostrar último tag detectado
            if (lastTagId.isNotEmpty()) {
                Text(text = "Último tag: $lastTagId", modifier = Modifier.padding(top = 8.dp))
            }

            // Espacio
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(16.dp))

            // Botón cancelar
            Button(
                onClick = { onCancel() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Cancelar Pago")
            }

            // Botón para simular lectura (útil en emulador o si el modo lector no funciona)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(8.dp))
            Button(onClick = { onSuccess() }, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Pagar con Efectivo")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PayScreenPreview() {
    // preview con callbacks vacíos
    PayScreen(onCancel = {}, onSuccess = {})
}