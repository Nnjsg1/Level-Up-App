package com.example.level_up_app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.level_up_app.data.model.User
import com.example.level_up_app.ui.viewmodel.UserUiState
import com.example.level_up_app.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAdminScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: UserViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showEditDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var showDeactivateDialog by remember { mutableStateOf(false) }
    var showActivateDialog by remember { mutableStateOf(false) }
    var filterActive by remember { mutableStateOf<Boolean?>(null) } // null = todos

    // Cargar usuarios al iniciar la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    // Mostrar snackbar cuando hay mensaje
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administración de Usuarios") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    // Filtro: Todos
                    IconButton(
                        onClick = {
                            filterActive = null
                            viewModel.loadUsers()
                        }
                    ) {
                        Icon(
                            Icons.Default.People,
                            "Todos",
                            tint = if (filterActive == null)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                    // Filtro: Activos
                    IconButton(
                        onClick = {
                            filterActive = true
                            viewModel.loadActiveUsers()
                        }
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            "Activos",
                            tint = if (filterActive == true)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                    // Filtro: Inactivos
                    IconButton(
                        onClick = {
                            filterActive = false
                            viewModel.loadInactiveUsers()
                        }
                    ) {
                        Icon(
                            Icons.Default.Block,
                            "Inactivos",
                            tint = if (filterActive == false)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                    // Recargar
                    IconButton(onClick = {
                        when (filterActive) {
                            null -> viewModel.loadUsers()
                            true -> viewModel.loadActiveUsers()
                            false -> viewModel.loadInactiveUsers()
                        }
                    }) {
                        Icon(Icons.Default.Refresh, "Recargar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val s = state) {
                is UserUiState.Idle -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is UserUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is UserUiState.Success -> {
                    if (s.users.isEmpty()) {
                        Text(
                            "No hay usuarios registrados",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(s.users) { user ->
                                UserListItem(
                                    user = user,
                                    onEdit = {
                                        selectedUser = user
                                        showEditDialog = true
                                    },
                                    onDeactivate = {
                                        selectedUser = user
                                        showDeactivateDialog = true
                                    },
                                    onActivate = {
                                        selectedUser = user
                                        showActivateDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
                is UserUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Error: ${s.message}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadUsers() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }

    // Diálogo de edición
    if (showEditDialog && selectedUser != null) {
        EditUserDialog(
            user = selectedUser!!,
            onDismiss = {
                showEditDialog = false
                selectedUser = null
            },
            onConfirm = { name, email, password, isAdmin, active ->
                selectedUser?.id?.let { id ->
                    viewModel.updateUser(id, name, email, password, isAdmin, active)
                }
                showEditDialog = false
                selectedUser = null
            },
            viewModel = viewModel
        )
    }

    // Diálogo de confirmación de desactivación
    if (showDeactivateDialog && selectedUser != null) {
        AlertDialog(
            onDismissRequest = {
                showDeactivateDialog = false
                selectedUser = null
            },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Desactivar usuario") },
            text = {
                Text(
                    "¿Desactivar a ${selectedUser!!.name}? El usuario no podrá iniciar sesión pero se conservarán sus datos."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedUser?.id?.let { viewModel.deactivateUser(it) }
                        showDeactivateDialog = false
                        selectedUser = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Desactivar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeactivateDialog = false
                        selectedUser = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo de confirmación de activación
    if (showActivateDialog && selectedUser != null) {
        AlertDialog(
            onDismissRequest = {
                showActivateDialog = false
                selectedUser = null
            },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("Activar usuario") },
            text = {
                Text(
                    "¿Reactivar a ${selectedUser!!.name}? El usuario podrá iniciar sesión nuevamente."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedUser?.id?.let { viewModel.activateUser(it) }
                        showActivateDialog = false
                        selectedUser = null
                    }
                ) {
                    Text("Activar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showActivateDialog = false
                        selectedUser = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun UserListItem(
    user: User,
    onEdit: () -> Unit,
    onDeactivate: () -> Unit,
    onActivate: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (user.active) 1f else 0.7f),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (user.active)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header con nombre y badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = if (user.active)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (user.active)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Badge Admin
                    if (user.isAdmin) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Text("ADMIN", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    // Badge de estado ACTIVO/INACTIVO
                    Badge(
                        containerColor = if (user.active)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = if (user.active) "ACTIVO" else "INACTIVO",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Email
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Editar
                OutlinedButton(
                    onClick = onEdit
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Editar")
                }

                // Botón Desactivar o Activar según estado
                if (user.active) {
                    // Usuario activo → Mostrar botón DESACTIVAR
                    Button(
                        onClick = onDeactivate,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Desactivar")
                    }
                } else {
                    // Usuario inactivo → Mostrar botón ACTIVAR
                    Button(
                        onClick = onActivate,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Activar")
                    }
                }
            }
        }
    }
}

@Composable
fun EditUserDialog(
    user: User,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Boolean, Boolean) -> Unit,
    viewModel: UserViewModel
) {
    var name by remember { mutableStateOf(user.name) }
    var email by remember { mutableStateOf(user.email) }
    var password by remember { mutableStateOf(user.clave) }
    var isAdmin by remember { mutableStateOf(user.isAdmin) }
    var active by remember { mutableStateOf(user.active) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Usuario") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = if (!viewModel.validateName(it)) {
                            "El nombre debe tener al menos 3 caracteres"
                        } else null
                    },
                    label = { Text("Nombre") },
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = if (!viewModel.validateEmail(it)) {
                            "Email inválido"
                        } else null
                    },
                    label = { Text("Email") },
                    isError = emailError != null,
                    supportingText = emailError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider()

                // Checkbox: Usuario activo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = active,
                        onCheckedChange = { active = it }
                    )
                    Column {
                        Text(
                            text = "Usuario activo",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = if (active) "Puede iniciar sesión" else "No puede iniciar sesión",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Checkbox: Administrador
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isAdmin,
                        onCheckedChange = { isAdmin = it }
                    )
                    Text("Usuario administrador")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (viewModel.validateName(name) && viewModel.validateEmail(email)) {
                        onConfirm(name, email, password, isAdmin, active)
                    }
                },
                enabled = nameError == null && emailError == null
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun UserListScreen(viewModel: UserViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadUsers() }

    when (val s = state) {
        is UserUiState.Idle -> Text("Cargando usuarios...")
        is UserUiState.Loading -> CircularProgressIndicator()
        is UserUiState.Success -> {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(s.users) { u ->
                    Text("${u.name} - ${u.email}")
                }
            }
        }
        is UserUiState.Error -> Text("Error: ${s.message}")
    }
}

@Composable
fun CreateUserScreen(viewModel: UserViewModel = viewModel()) {
    val name = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val clave = remember { mutableStateOf("") }
    val isAdmin = remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Nombre") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = email.value, onValueChange = { email.value = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = clave.value,
            onValueChange = { clave.value = it },
            label = { Text("Clave") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isAdmin.value,
                onCheckedChange = { isAdmin.value = it }
            )
            Text("Administrador")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.addUser(name.value, email.value, clave.value, isAdmin.value) }) {
            Text("Crear usuario")
        }
    }
}






