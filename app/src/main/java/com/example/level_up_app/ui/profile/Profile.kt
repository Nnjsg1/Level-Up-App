package com.example.level_up_app.ui.profile

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter

private const val PREFS_NAME = "profile_prefs"
private const val KEY_PROFILE_IMAGE = "profile_image_uri"

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    name: String = "",
    email: String = "",
    password: String = "",
    onEditClicked: () -> Unit = {}
) {
    val context = LocalContext.current
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }


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
                        contentDescription = "Avatar",
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

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "NOMBRE")
        Text(
            text = name,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(text = "EMAIL", modifier = Modifier.padding(top = 12.dp))
        Text(
            text = email,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(text = "CONTRASEÑA", modifier = Modifier.padding(top = 12.dp))
        Text(
            text = if (password.isEmpty()) "" else "••••••••",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Button(
            onClick = onEditClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text(text = "Editar")
        }
    }
}