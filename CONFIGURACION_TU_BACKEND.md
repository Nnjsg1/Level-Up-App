# 🎯 CONFIGURACIÓN ESPECÍFICA PARA TU BACKEND

## ✅ ARCHIVOS YA CONFIGURADOS

Todos los archivos han sido adaptados para tu backend específico:

### 🔧 Cambios Realizados:

1. **User.kt**
   - ✅ `id` cambiado a `Long` (en vez de String)
   - ✅ Campo `password` → `clave`
   - ✅ Agregado campo `isAdmin: Boolean`
   - ✅ Todos los campos coinciden con tu tabla MySQL

2. **LoginRequest.kt**
   - ✅ Campo `password` → `clave`

3. **LoginResponse.kt**
   - ✅ Eliminado campo `token` (no lo usas)

4. **ApiService.kt**
   - ✅ Ruta cambiada: `POST("login")` → `POST("auth/login")`

5. **RetrofitInstance.kt**
   - ✅ URL configurada: `http://10.0.2.2:8080/api/`
   - ✅ Puerto cambiado de 3000 → 8080

6. **UserRepository.kt**
   - ✅ Método usa `clave` en vez de `password`

---

## 🚀 CÓMO PROBAR AHORA

### 1️⃣ Asegúrate que tu Backend esté corriendo

Verifica que tu servidor Spring Boot esté activo en:
```
http://localhost:8080
```

### 2️⃣ Prueba tu backend con Postman/cURL (Opcional)

Para verificar que funciona antes de probar en la app:

**Request:**
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "clave": "admin123"
}
```

**Response esperada:**
```json
{
  "success": true,
  "message": "Login exitoso",
  "user": {
    "id": 1,
    "name": "Admin User",
    "email": "admin@example.com",
    "clave": "admin123",
    "isAdmin": true,
    "createdAt": "2025-12-10T10:30:00"
  }
}
```

### 3️⃣ Sincroniza Gradle en Android Studio

- Click en **"Sync Now"** en la barra superior
- O: **File → Sync Project with Gradle Files**

### 4️⃣ Ejecuta la App

1. Compila y ejecuta la app en el **emulador**
2. En la pantalla de login ingresa:
   - **Email:** `admin@example.com`
   - **Contraseña:** `admin123`
3. Click en "Inicio Sesión"

### 5️⃣ Revisa los Logs

En **Logcat** (Android Studio), filtra por:
- `UserRepository` - Para ver las peticiones HTTP
- `LoginViewModel` - Para ver el flujo de login

Deberías ver logs como:
```
D/OkHttp: --> POST http://10.0.2.2:8080/api/auth/login
D/OkHttp: {"email":"admin@example.com","clave":"admin123"}
D/OkHttp: <-- 200 OK
D/LoginViewModel: Login exitoso: Admin User
```

---

## 📱 Si usas DISPOSITIVO FÍSICO (no emulador)

Necesitas cambiar la URL en `RetrofitInstance.kt`:

### Paso 1: Obtén tu IP local

**Windows:**
```bash
ipconfig
# Busca: IPv4 Address
# Ejemplo: 192.168.1.100
```

### Paso 2: Cambia la URL

En `RetrofitInstance.kt`, línea 17:
```kotlin
private const val BASE_URL = "http://192.168.1.100:8080/api/"
```

### Paso 3: Asegúrate de estar en la misma red

- Tu PC y tu celular deben estar conectados a la misma red WiFi

---

## 🗄️ ESTRUCTURA DE TU BASE DE DATOS

```sql
-- Tabla: users
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    clave VARCHAR(255) NOT NULL,
    is_admin BOOLEAN DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Usuario de prueba
INSERT INTO users (name, email, clave, is_admin) 
VALUES ('Admin User', 'admin@example.com', 'admin123', 1);

INSERT INTO users (name, email, clave, is_admin) 
VALUES ('Usuario Test', 'usuario@example.com', 'password', 0);
```

---

## ⚠️ IMPORTANTE: SEGURIDAD

**Para producción deberías:**

1. **Encriptar contraseñas con BCrypt**
   ```kotlin
   // En tu backend Spring Boot
   @Bean
   fun passwordEncoder() = BCryptPasswordEncoder()
   ```

2. **Usar HTTPS** en vez de HTTP
   
3. **Implementar JWT** para sesiones
   
4. **NO enviar la contraseña** en la respuesta del login
   ```json
   // ❌ Malo (tu respuesta actual)
   "user": {
     "clave": "admin123"  // NO enviar esto
   }
   
   // ✅ Bueno
   "user": {
     "id": 1,
     "name": "Admin",
     "email": "admin@example.com",
     "isAdmin": true
     // Sin campo 'clave'
   }
   ```

---

## 🐛 TROUBLESHOOTING ESPECÍFICO

### ❌ Error: "Unable to resolve host: 10.0.2.2"
**Solución:**
- Verifica que el backend esté corriendo (`http://localhost:8080`)
- Verifica que el puerto sea 8080

### ❌ Error 404: "Not Found"
**Solución:**
- Verifica que la ruta sea correcta: `/api/auth/login`
- Verifica en tu backend que el endpoint esté registrado
- Revisa los logs del backend para ver las peticiones

### ❌ Error 500: "Internal Server Error"
**Solución:**
- Revisa los logs del backend Spring Boot
- Verifica que la base de datos MySQL esté corriendo
- Verifica las credenciales de conexión a la BD

### ❌ La app dice "Email o contraseña incorrectos"
**Solución:**
- Verifica que el usuario exista en la base de datos
- Haz un `SELECT * FROM users` para confirmar
- Verifica que el email y la clave sean exactos (case-sensitive)

### ❌ "Cleartext HTTP traffic not permitted"
**Solución:**

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

Agrega en `AndroidManifest.xml`:
```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ...>
```

---

## 📊 FLUJO COMPLETO DE TU LOGIN

```
┌─────────────────────────────────────────────────────────────┐
│ 1. Usuario ingresa email y clave en LoginScreen            │
└────────────────────┬────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. LoginViewModel valida formato (email válido, >= 5 chars)│
└────────────────────┬────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. UserRepository.login(email, clave)                       │
└────────────────────┬────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────┐
│ 4. Retrofit → POST http://10.0.2.2:8080/api/auth/login     │
│    Body: {"email":"...", "clave":"..."}                     │
└────────────────────┬────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────┐
│ 5. Tu Backend Spring Boot recibe la petición                │
│    → Busca usuario en MySQL                                 │
│    → Compara credenciales                                   │
└────────────────────┬────────────────────────────────────────┘
                     ↓
         ┌───────────┴───────────┐
         ↓                       ↓
    ✅ SUCCESS              ❌ ERROR
         ↓                       ↓
┌────────────────┐      ┌────────────────┐
│ Response 200   │      │ Response 401   │
│ {              │      │ {              │
│   success: true│      │   success:false│
│   user: {...}  │      │   message: "..." 
│ }              │      │ }              │
└────────┬───────┘      └────────┬───────┘
         ↓                       ↓
┌────────────────┐      ┌────────────────┐
│ isLogin = true │      │ Muestra error  │
│ → MainScreen   │      │ en pantalla    │
└────────────────┘      └────────────────┘
```

---

## ✅ CHECKLIST FINAL

Antes de ejecutar la app, verifica:

- [ ] Backend Spring Boot corriendo en `localhost:8080`
- [ ] Base de datos MySQL corriendo
- [ ] Usuario de prueba creado en la tabla `users`
- [ ] Gradle sincronizado en Android Studio
- [ ] Permisos de Internet en `AndroidManifest.xml`
- [ ] `network_security_config.xml` configurado (si usas HTTP)

---

## 🎯 PRÓXIMOS PASOS RECOMENDADOS

1. **Implementar registro de usuarios**
   - Endpoint: `POST /api/auth/register`
   - Adaptar `CreateAccountViewModel.kt`

2. **Guardar sesión del usuario**
   - Usar SharedPreferences
   - Guardar `user.id` y `user.name`

3. **Agregar campo de admin**
   - Mostrar opciones diferentes si `user.isAdmin == true`

4. **Conectar productos con el backend**
   - Similar a como conectamos usuarios

---

## 🆘 ¿PROBLEMAS?

Si encuentras algún error, verifica:
1. Los logs en Logcat (Android Studio)
2. Los logs del backend Spring Boot
3. Que los datos en MySQL sean correctos

¡Todo está listo para funcionar con tu backend! 🚀

