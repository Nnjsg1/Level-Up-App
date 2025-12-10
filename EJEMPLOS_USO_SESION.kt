// ============================================
// EJEMPLO: CÓMO USAR LOS DATOS DEL USUARIO
// DESPUÉS DEL LOGIN EXITOSO
// ============================================

package com.example.level_up_app.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.level_up_app.data.User
import com.google.gson.Gson

/**
 * Clase para gestionar la sesión del usuario
 * Guarda y recupera datos usando SharedPreferences
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    private val gson = Gson()

    companion object {
        private const val KEY_USER = "user"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_IS_ADMIN = "is_admin"
    }

    /**
     * Guardar sesión del usuario después del login
     */
    fun saveUserSession(user: User) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putLong(KEY_USER_ID, user.id)
            putString(KEY_USER_NAME, user.name)
            putString(KEY_USER_EMAIL, user.email)
            putBoolean(KEY_IS_ADMIN, user.isAdmin)
            putString(KEY_USER, gson.toJson(user))
            apply()
        }
    }

    /**
     * Obtener el usuario guardado
     */
    fun getUser(): User? {
        val userJson = prefs.getString(KEY_USER, null) ?: return null
        return gson.fromJson(userJson, User::class.java)
    }

    /**
     * Verificar si hay una sesión activa
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Obtener ID del usuario
     */
    fun getUserId(): Long {
        return prefs.getLong(KEY_USER_ID, 0)
    }

    /**
     * Obtener nombre del usuario
     */
    fun getUserName(): String {
        return prefs.getString(KEY_USER_NAME, "") ?: ""
    }

    /**
     * Obtener email del usuario
     */
    fun getUserEmail(): String {
        return prefs.getString(KEY_USER_EMAIL, "") ?: ""
    }

    /**
     * Verificar si es administrador
     */
    fun isAdmin(): Boolean {
        return prefs.getBoolean(KEY_IS_ADMIN, false)
    }

    /**
     * Cerrar sesión
     */
    fun logout() {
        prefs.edit().clear().apply()
    }
}

// ============================================
// EJEMPLO 1: GUARDAR SESIÓN EN LoginViewModel
// ============================================

class LoginViewModel(
    private val sessionManager: SessionManager // Inyectar en el constructor
) : ViewModel() {

    // ...existing code...

    fun Login() {
        viewModelScope.launch {
            // ... validaciones ...

            try {
                val response = userRepository.login(f.email, f.password)

                if (response.success && response.user != null) {
                    // ✅ GUARDAR SESIÓN
                    sessionManager.saveUserSession(response.user)

                    formuLogin.value = formuLogin.value.copy(
                        error = null,
                        isLogin = true,
                        isLoading = false,
                        user = response.user
                    )
                } else {
                    mensajeError(response.message)
                }
            } catch (e: Exception) {
                mensajeError("Error de conexión: ${e.localizedMessage}")
            }
        }
    }
}

// ============================================
// EJEMPLO 2: VERIFICAR SESIÓN AL INICIAR LA APP
// ============================================

@Composable
fun MainActivity() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // Verificar si hay sesión activa
    val isLoggedIn = remember { sessionManager.isLoggedIn() }
    val startDestination = if (isLoggedIn) "main" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") { LoginScreen() }
        composable("main") { MainScreen() }
    }
}

// ============================================
// EJEMPLO 3: MOSTRAR DATOS DEL USUARIO EN PERFIL
// ============================================

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val user = remember { sessionManager.getUser() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        user?.let {
            Text("ID: ${it.id}")
            Text("Nombre: ${it.name}")
            Text("Email: ${it.email}")
            Text("Admin: ${if (it.isAdmin) "Sí" else "No"}")
            Text("Creado: ${it.createdAt}")

            // Si es admin, mostrar opciones especiales
            if (it.isAdmin) {
                Button(onClick = { /* Ir a panel de admin */ }) {
                    Text("Panel de Administración")
                }
            }
        }
    }
}

// ============================================
// EJEMPLO 4: CERRAR SESIÓN
// ============================================

@Composable
fun LogoutButton(
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    Button(
        onClick = {
            sessionManager.logout()
            onLogout() // Navegar al login
        }
    ) {
        Text("Cerrar Sesión")
    }
}

// ============================================
// EJEMPLO 5: USAR DATOS EN PETICIONES AL BACKEND
// ============================================

class ProductRepository(private val context: Context) {

    private val apiService = RetrofitInstance.api
    private val sessionManager = SessionManager(context)

    suspend fun createProduct(product: Product): Boolean {
        return try {
            // Obtener el ID del usuario actual
            val userId = sessionManager.getUserId()

            // Agregar el userId al producto
            val productWithUser = product.copy(
                userId = userId
            )

            val response = apiService.createProduct(productWithUser)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error: ${e.message}")
            false
        }
    }
}

// ============================================
// EJEMPLO 6: AGREGAR HEADER DE AUTENTICACIÓN
// (Si tu backend usa tokens JWT en el futuro)
// ============================================

object RetrofitInstance {

    private fun getAuthInterceptor(context: Context): Interceptor {
        return Interceptor { chain ->
            val sessionManager = SessionManager(context)
            val token = sessionManager.getToken() // Agregar este método

            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()

            chain.proceed(request)
        }
    }

    fun createApi(context: Context): ApiService {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(getAuthInterceptor(context))
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

// ============================================
// EJEMPLO 7: PROTEGER RUTAS EN LA NAVEGACIÓN
// ============================================

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    NavHost(
        navController = navController,
        startDestination = if (sessionManager.isLoggedIn()) "main" else "login"
    ) {
        composable("login") {
            LoginScreen(
                onNavigateToMain = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            // Verificar sesión antes de mostrar
            if (!sessionManager.isLoggedIn()) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            } else {
                MainScreen(
                    onLogout = {
                        sessionManager.logout()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable("admin") {
            // Solo accesible para admins
            if (!sessionManager.isAdmin()) {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            } else {
                AdminPanel()
            }
        }
    }
}

// ============================================
// EJEMPLO 8: ACTUALIZAR PERFIL DE USUARIO
// ============================================

class UserRepository {

    suspend fun updateProfile(userId: Long, name: String, email: String): User? {
        return try {
            val updatedUser = User(
                id = userId,
                name = name,
                email = email,
                clave = "", // No enviar la contraseña
                isAdmin = false,
                createdAt = ""
            )

            val response = apiService.updateUser(userId.toString(), updatedUser)

            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating: ${e.message}")
            null
        }
    }
}

// ============================================
// DEPENDENCIAS NECESARIAS
// ============================================

// En build.gradle.kts, agregar:

dependencies {
    // ...existing dependencies...

    // Gson para serializar objetos
    implementation("com.google.code.gson:gson:2.10.1")
}

// ============================================
// NOTAS IMPORTANTES
// ============================================

/*
 * 1. SharedPreferences es suficiente para datos simples
 * 2. Para datos más complejos, usa Room Database
 * 3. NUNCA guardes contraseñas en SharedPreferences
 * 4. Para mayor seguridad, usa EncryptedSharedPreferences
 * 5. Limpia la sesión cuando el token expire
 * 6. Valida la sesión al iniciar la app
 * 7. Implementa refresh token para sesiones largas
 */

// ============================================
// PARA USAR SessionManager EN TU PROYECTO:
// ============================================

/*
 * 1. Crea el archivo: app/src/main/java/com/example/level_up_app/utils/SessionManager.kt
 * 2. Copia el código de SessionManager
 * 3. Agrega la dependencia de Gson en build.gradle.kts
 * 4. Úsalo en LoginViewModel para guardar la sesión
 * 5. Úsalo en MainActivity para verificar la sesión
 * 6. Úsalo en ProfileScreen para mostrar datos
 * 7. Úsalo en LogoutButton para cerrar sesión
 */

