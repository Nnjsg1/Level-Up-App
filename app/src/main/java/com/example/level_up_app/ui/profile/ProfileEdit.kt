package com.example.level_up_app.ui.profile

import android.Manifest
import android.net.Uri
import android.os.Environment
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import java.io.File

private const val PREFS_NAME = "profile_prefs"
private const val KEY_PROFILE_IMAGE = "profile_image_uri"

@Composable
fun ProfileEditScreen(
    modifier: Modifier = Modifier,
    initialName: String = "",
    initialEmail: String = "",
    initialPassword: String = "",
    onSave: (name: String, email: String, password: String) -> Unit = { _, _, _ -> },
    onBack: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf(initialName) }
    var email by remember { mutableStateOf(initialEmail) }
    var password by remember { mutableStateOf(initialPassword) }


    val context = LocalContext.current
    var permisoCamara by remember { mutableStateOf(false) }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    var pendingImageUri by remember { mutableStateOf<Uri?>(null) }


    val permisoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        permisoCamara = granted
    }


    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {

            profileImageUri = pendingImageUri
        }

        pendingImageUri = null
    }


    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.getString(KEY_PROFILE_IMAGE, null)?.let { saved ->
            profileImageUri = Uri.parse(saved)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Surface(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(120.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (profileImageUri != null) {
                    val painter = rememberAsyncImagePainter(model = profileImageUri)
                    Image(
                        painter = painter,
                        contentDescription = "Profile image",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar placeholder",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))


        Button(onClick = {

            if (!permisoCamara) {
                permisoLauncher.launch(Manifest.permission.CAMERA)
                return@Button
            }


            val picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val fotoFile: File = try {
                File.createTempFile("profile_${System.currentTimeMillis()}", ".jpg", picturesDir)
            } catch (_: Exception) {

                File.createTempFile("profile_${System.currentTimeMillis()}", ".jpg", context.filesDir)
            }

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", fotoFile)
            pendingImageUri = uri
            takePictureLauncher.launch(uri)
        }) {
            Text(text = "Cambiar foto")
        }

        Spacer(modifier = Modifier.height(16.dp))

        SnackbarHost(hostState = snackbarHostState)

        Text(text = "NOMBRE")
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Text(text = "EMAIL", modifier = Modifier.padding(top = 12.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Text(text = "CONTRASEÑA", modifier = Modifier.padding(top = 12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),

            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    scope.launch {

                        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        val editor = prefs.edit()
                        if (profileImageUri != null) {
                            editor.putString(KEY_PROFILE_IMAGE, profileImageUri.toString())
                        } else {
                            editor.remove(KEY_PROFILE_IMAGE)
                        }
                        editor.apply()

                        snackbarHostState.showSnackbar("Guardado")
                        onSave(name, email, password)
                        onBack()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Guardar Cambios")
            }

            OutlinedButton(
                onClick = { onBack() },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Descartar Cambios")
            }
        }
    }
}