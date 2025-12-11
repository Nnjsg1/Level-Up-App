# 🔐 Sistema de Autenticación con Backend - Level Up App

## ✅ Archivos Creados

Se han creado los siguientes archivos para implementar el login con tu backend:

### 📁 Modelos de Datos (`data/`)
- ✅ `User.kt` - Modelo de usuario
- ✅ `LoginRequest.kt` - Request para el login
- ✅ `LoginResponse.kt` - Response del login

### 🌐 Capa de Red (`remote/`)
- ✅ `ApiService.kt` - Interface con todos los endpoints (usuarios y productos)
- ✅ `RetrofitInstance.kt` - Configuración de Retrofit

### 💾 Repositorio (`data/`)
- ✅ `UserRepository.kt` - Maneja la lógica de autenticación

### 🔄 Archivos Actualizados
- ✅ `LoginViewModel.kt` - Conectado con el backend
- ✅ `FormularioLogin.kt` - Agregado estado de carga y usuario
- ✅ `LoginScreen.kt` - Indicador de carga en el botón
- ✅ `build.gradle.kts` - Agregada dependencia de OkHttp

---

## ⚙️ CONFIGURACIÓN NECESARIA

### 1️⃣ Configurar la URL del Backend

Abre el archivo `RetrofitInstance.kt` y cambia la URL:

```kotlin
// En: app/src/main/java/com/example/level_up_app/remote/RetrofitInstance.kt
private const val BASE_URL = "http://10.0.2.2:3000/api/"
```

**Cambia según tu caso:**
- **Emulador Android + Backend en localhost**: `http://10.0.2.2:PUERTO/api/`
- **Dispositivo físico + Backend local**: `http://TU_IP_LOCAL:PUERTO/api/`
  - Para saber tu IP local:
    - Windows: `ipconfig` en CMD (busca IPv4)
    - Ejemplo: `http://192.168.1.100:3000/api/`
- **Backend en producción**: `https://tu-backend.com/api/`

### 2️⃣ Sincronizar Gradle

Después de los cambios en `build.gradle.kts`, sincroniza el proyecto:
- Click en "Sync Now" en la barra superior
- O ejecuta: **File > Sync Project with Gradle Files**

### 3️⃣ Permisos de Internet

Asegúrate de tener permisos de internet en tu `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### 4️⃣ Configurar Network Security (Opcional para HTTP)

Si usas HTTP (no HTTPS) en desarrollo, necesitas permitirlo.

Crea: `app/src/main/res/xml/network_security_config.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
</network-security-config>
```

Y agrégalo en `AndroidManifest.xml`:

```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ...>
```

---

## 🎯 Estructura del Backend Esperada

### Endpoint de Login

**POST** `/api/login`

**Request Body:**
```json
{
  "email": "usuario@email.com",
  "password": "contraseña123"
}
```

**Response (Success - 200):**
```json
{
  "success": true,
  "message": "Login exitoso",
  "user": {
    "id": "123",
    "email": "usuario@email.com",
    "name": "Nombre Usuario",
    "createdAt": "2024-01-01"
  },
  "token": "jwt_token_aqui" // Opcional si usas JWT
}
```

**Response (Error - 401):**
```json
{
  "success": false,
  "message": "Email o contraseña incorrectos"
}
```

### Otros Endpoints Disponibles

**Registro**
- POST `/api/register`

**Usuarios**
- GET `/api/users` - Obtener todos
- GET `/api/users/{id}` - Obtener por ID
- PUT `/api/users/{id}` - Actualizar
- DELETE `/api/users/{id}` - Eliminar

**Productos**
- GET `/api/products` - Obtener todos
- GET `/api/products/{id}` - Obtener por ID
- POST `/api/products` - Crear
- PUT `/api/products/{id}` - Actualizar
- DELETE `/api/products/{id}` - Eliminar

---

## 📝 Adaptación a tu Backend

Si tu backend tiene una estructura diferente, modifica estos archivos:

### 1. Cambiar nombres de campos

Si tu backend usa nombres diferentes (por ejemplo `username` en vez de `email`):

**En `LoginRequest.kt`:**
```kotlin
data class LoginRequest(
    val username: String,  // En vez de email
    val password: String
)
```

**En `User.kt`:**
```kotlin
data class User(
    val id: String = "",
    val username: String = "",  // Agregar o cambiar campos
    val nombre: String = "",    // según tu backend
    ...
)
```

### 2. Cambiar rutas de endpoints

Si tus rutas son diferentes, modifica `ApiService.kt`:

```kotlin
@POST("auth/login")  // En vez de "login"
suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

@GET("api/usuarios")  // En vez de "users"
suspend fun getUsers(): Response<List<User>>
```

### 3. Agregar Headers de autenticación

Si necesitas enviar un token JWT en las peticiones:

**En `RetrofitInstance.kt`:**
```kotlin
private val httpClient = OkHttpClient.Builder()
    .addInterceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        chain.proceed(request)
    }
    .addInterceptor(loggingInterceptor)
    .build()
```

---

## 🧪 Cómo Probar

1. **Inicia tu backend** y asegúrate que esté corriendo
2. **Configura la URL correcta** en `RetrofitInstance.kt`
3. **Compila y ejecuta** la app
4. **Intenta hacer login** con credenciales de tu base de datos
5. **Revisa los logs** en Logcat para ver las peticiones:
   - Filtra por: `UserRepository` o `LoginViewModel`

### Ver logs de Retrofit

Los logs mostrarán toda la información de las peticiones HTTP:
```
D/OkHttp: --> POST http://10.0.2.2:3000/api/login
D/OkHttp: {"email":"test@test.com","password":"12345"}
D/OkHttp: <-- 200 OK
```

---

## 🐛 Troubleshooting

### Error: "Unable to resolve host" o "Connection refused"
- ✅ Verifica que el backend esté corriendo
- ✅ Verifica la URL en `RetrofitInstance.kt`
- ✅ Si usas emulador, usa `10.0.2.2` en vez de `localhost`
- ✅ Si usas dispositivo físico, usa la IP local de tu PC

### Error: "Cleartext HTTP traffic not permitted"
- ✅ Configura el `network_security_config.xml` (ver arriba)

### Error 401: "Unauthorized"
- ✅ Verifica las credenciales en la base de datos
- ✅ Verifica que el backend esté validando correctamente

### No aparecen los logs
- ✅ Asegúrate de tener Logcat abierto en Android Studio
- ✅ Filtra por "level_up_app" o "UserRepository"

---

## 📚 Próximos Pasos

1. ✅ **Implementar registro de usuarios** (usar `CreateAccountViewModel.kt`)
2. ✅ **Guardar token JWT** en SharedPreferences
3. ✅ **Implementar sesión persistente**
4. ✅ **Conectar productos con el backend** (similar a usuarios)
5. ✅ **Agregar refresh token**

---

## 📞 ¿Necesitas Ayuda?

Si tu backend tiene una estructura diferente, compárteme:
1. La URL de tu endpoint de login
2. Un ejemplo del JSON que envía y recibe
3. Los campos que tiene tu tabla de usuarios

Y te ayudaré a adaptar el código! 🚀

