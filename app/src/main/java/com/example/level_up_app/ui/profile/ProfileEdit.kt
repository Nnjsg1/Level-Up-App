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
import com.example.level_up_app.utils.SessionManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

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
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val currentUser = remember { sessionManager.getUser() }

    val viewModel = remember { ProfileEditViewModel() }
    val uiState by viewModel.uiState.collectAsState()

    var permisoCamara by remember { mutableStateOf(false) }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var pendingImageUri by remember { mutableStateOf<Uri?>(null) }

    // Inicializar el nombre desde el usuario actual
    LaunchedEffect(currentUser) {
        currentUser?.let {
            viewModel.setInitialData(it.name)
        }
    }

    // Manejar éxito en la actualización
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            snackbarHostState.showSnackbar("Cambios guardados exitosamente")
            viewModel.resetSuccess()
            onBack()
        }
    }

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

        Text(text = "NOMBRE", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = uiState.name,
            onValueChange = { viewModel.updateName(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            enabled = !uiState.isLoading,
            singleLine = true
        )

        Text(text = "EMAIL (no se puede editar)", modifier = Modifier.padding(top = 12.dp), style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = currentUser?.email ?: "",
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            enabled = false,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Text(text = "NUEVA CONTRASEÑA", modifier = Modifier.padding(top = 12.dp), style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = uiState.clave,
            onValueChange = { viewModel.updateClave(it) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            enabled = !uiState.isLoading,
            singleLine = true,
            placeholder = { Text("Ingresa tu nueva contraseña") }
        )

        // Mostrar error si existe
        uiState.error?.let { errorMsg ->
            Text(
                text = errorMsg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    scope.launch {
                        // Guardar imagen de perfil
                        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        val editor = prefs.edit()
                        if (profileImageUri != null) {
                            editor.putString(KEY_PROFILE_IMAGE, profileImageUri.toString())
                        } else {
                            editor.remove(KEY_PROFILE_IMAGE)
                        }
                        editor.apply()

                        // Guardar cambios del usuario en el backend
                        currentUser?.let { user ->
                            viewModel.saveChanges(user.id) { updatedUser ->
                                // Actualizar la sesión con los nuevos datos
                                sessionManager.saveSession(updatedUser)
                            }
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(text = "Guardar Cambios")
                }
            }

            OutlinedButton(
                onClick = { onBack() },
                modifier = Modifier.weight(1f),
                enabled = !uiState.isLoading
            ) {
                Text(text = "Cancelar")
            }
        }
    }
}