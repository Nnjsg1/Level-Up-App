# Level Up App - Aplicación Móvil E-commerce

## Información del Proyecto

**Institución:** DuocUC  
**Asignatura:** Desarrollo de Aplicaciones Móviles  
**Fecha:** Diciembre 2025  
**Plataforma:** Android  

### Integrantes del Equipo

- Jesus Aranguiz
- Ignacio Bravo
- Leonardo Oñate

---

## Descripción del Proyecto

Level Up App es una aplicación móvil de comercio electrónico desarrollada para Android, especializada en productos gaming y tecnología. La aplicación permite a los usuarios navegar por un catálogo de productos, gestionar favoritos, realizar compras y administrar su perfil. Incluye un panel de administración completo para la gestión de productos, usuarios y contenido de noticias.

El proyecto implementa una arquitectura cliente-servidor, donde la aplicación móvil desarrollada en Kotlin con Jetpack Compose se comunica con un backend REST API construido en Spring Boot, utilizando MySQL como sistema de gestión de base de datos.

---

## Tecnologías Utilizadas

### Frontend (Aplicación Móvil)
- **Lenguaje:** Kotlin 1.9
- **Framework UI:** Jetpack Compose
- **Arquitectura:** MVVM (Model-View-ViewModel)
- **Gestión de Estado:** StateFlow y Coroutines
- **Inyección de Dependencias:** Manual (Repositories)
- **Navegación:** Compose Navigation
- **Consumo de API:** Retrofit 2.9.0
- **Serialización JSON:** Gson 2.10.1
- **Multimedia:** ExoPlayer para reproducción de videos
- **Testing:** JUnit 4, MockK, Coroutines Test

### Backend (Microservicios)
- **Framework:** Spring Boot 3.x
- **Lenguaje:** Java 17
- **Base de Datos:** MySQL 8.0
- **ORM:** Spring Data JPA / Hibernate
- **Arquitectura:** REST API
- **Gestión de CORS:** Configurado para desarrollo

### Herramientas de Desarrollo
- **IDE Móvil:** Android Studio Hedgehog
- **IDE Backend:** IntelliJ IDEA / Eclipse
- **Control de Versiones:** Git / GitHub
- **Gestión de Proyecto:** Trello
- **Build Tool (Android):** Gradle 8.2
- **Build Tool (Backend):** Maven

---

## Funcionalidades Implementadas

### Módulo de Autenticación
- Registro de nuevos usuarios con validación de datos
- Inicio de sesión con email y contraseña
- Gestión de sesión persistente (SessionManager)
- Cierre de sesión

### Módulo de Catálogo de Productos
- Visualización de productos con imágenes, precios y descripciones
- Filtrado por categorías
- Búsqueda de productos
- Vista detallada de producto con información completa
- Indicador de stock disponible

### Módulo de Favoritos
- Agregar productos a lista de favoritos
- Eliminar productos de favoritos
- Visualización de productos favoritos del usuario
- Sincronización con base de datos mediante API REST

### Módulo de Carrito de Compras
- Agregar productos al carrito con cantidad seleccionable
- Incrementar/decrementar cantidad de productos
- Eliminar productos individuales del carrito
- Vaciar carrito completo
- Cálculo automático de subtotales y total
- Persistencia del carrito en base de datos

### Módulo de Checkout y Órdenes
- Resumen de compra con detalle de productos
- Selección de método de pago (Tarjeta de Crédito, Débito, PayPal)
- Procesamiento de pago (simulado)
- Creación de orden en base de datos
- Generación de número de orden
- Vaciado automático del carrito después de compra exitosa
- Pantalla de confirmación de compra

### Módulo de Perfil de Usuario
- Visualización de datos del usuario
- Edición de nombre y contraseña
- Selección de foto de perfil
- Actualización de información en base de datos

### Módulo de Noticias/Blog
- Listado de artículos y noticias
- Visualización de contenido completo
- Soporte para imágenes y videos
- Categorización de noticias
- Reproductor de video integrado (ExoPlayer)

### Panel de Administración (Solo usuarios admin)
- Gestión completa de productos (CRUD)
  - Crear nuevos productos
  - Editar productos existentes
  - Eliminar productos
  - Gestión de stock y precios
- Gestión de usuarios (CRUD)
  - Listar usuarios registrados
  - Modificar información de usuarios
  - Eliminar cuentas de usuario
  - Asignar permisos de administrador
- Gestión de noticias (CRUD)
  - Crear nuevos artículos
  - Editar contenido existente
  - Eliminar artículos
  - Gestión de categorías y multimedia

### Navegación
- Bottom Navigation Bar con 4 secciones principales
- Top App Bar con opciones contextuales
- Navegación fluida entre pantallas sin errores
- Botón de retroceso funcional en todas las vistas

---

## Arquitectura de la Aplicación

### Patrón MVVM

La aplicación sigue el patrón de arquitectura Model-View-ViewModel para separar la lógica de negocio de la interfaz de usuario.

**Capas:**

1. **Model (Modelo):** Clases de datos que representan las entidades del sistema
   - `User.kt`, `Product.kt`, `Order.kt`, `News.kt`, etc.

2. **Repository (Repositorio):** Capa de acceso a datos que comunica con el backend
   - `UserRepository.kt`, `ProductRepository.kt`, `CartRepository.kt`, etc.
   - Manejo de llamadas API mediante Retrofit
   - Manejo de errores y respuestas

3. **ViewModel:** Lógica de negocio y gestión de estado
   - `CartViewModel.kt`, `CheckoutViewModel.kt`, `FavoritesViewModel.kt`, etc.
   - Uso de StateFlow para estados reactivos
   - Coroutines para operaciones asíncronas

4. **View (Vista):** Interfaz de usuario con Jetpack Compose
   - `CartScreen.kt`, `CheckoutScreen.kt`, `CatalogScreen.kt`, etc.
   - Composables reutilizables
   - Material Design 3

### Estructura de Directorios

```
app/src/main/java/com/example/level_up_app/
├── data/                    # Modelos y repositorios
│   ├── User.kt
│   ├── Product.kt
│   ├── Cart.kt
│   ├── Order.kt
│   ├── UserRepository.kt
│   ├── ProductRepository.kt
│   ├── CartRepository.kt
│   └── OrderRepository.kt
├── remote/                  # Configuración de red
│   ├── ApiService.kt
│   └── RetrofitInstance.kt
├── ui/                      # Interfaces de usuario
│   ├── cart/
│   ├── catalog/
│   ├── checkout/
│   ├── favorites/
│   ├── profile/
│   ├── news/
│   ├── admin/
│   └── menu/
├── utils/                   # Utilidades
│   └── SessionManager.kt
└── MainActivity.kt
```

---

## API REST - Endpoints Implementados

### Base URL
```
http://localhost:8080/api
```

### Autenticación
- `POST /auth/login` - Iniciar sesión
  - Body: `{ "email": "string", "clave": "string" }`
  - Response: `LoginResponse` con datos del usuario

- `POST /auth/register` - Registrar nuevo usuario
  - Body: `User` object
  - Response: `LoginResponse`

### Usuarios
- `GET /users` - Obtener todos los usuarios (admin)
- `GET /users/{id}` - Obtener usuario por ID
- `PUT /users/{id}` - Actualizar usuario
- `DELETE /users/{id}` - Eliminar usuario

### Productos
- `GET /products` - Listar todos los productos
- `GET /products/{id}` - Obtener producto por ID
- `GET /products/category/{categoryId}` - Productos por categoría
- `POST /products` - Crear producto (admin)
- `PUT /products/{id}` - Actualizar producto (admin)
- `DELETE /products/{id}` - Eliminar producto (admin)

### Categorías
- `GET /categories` - Listar categorías
- `POST /categories` - Crear categoría (admin)

### Noticias
- `GET /news` - Listar todas las noticias
- `GET /news/{id}` - Obtener noticia por ID
- `POST /news` - Crear noticia (admin)
- `PUT /news/{id}` - Actualizar noticia (admin)
- `DELETE /news/{id}` - Eliminar noticia (admin)

### Favoritos
- `GET /favorites/user/{userId}` - Favoritos del usuario
- `POST /favorites` - Agregar a favoritos
  - Body: `{ "userId": int, "productId": long }`
- `DELETE /favorites/{userId}/{productId}` - Eliminar favorito

### Carrito
- `GET /cart/{userId}` - Obtener carrito del usuario
- `POST /cart` - Agregar producto al carrito
  - Body: `{ "userId": int, "productId": long, "quantity": int }`
- `PUT /cart/{userId}/{productId}` - Actualizar cantidad
- `DELETE /cart/{userId}/{productId}` - Eliminar item del carrito
- `DELETE /cart/{userId}` - Vaciar carrito completo

### Órdenes
- `GET /orders/user/{userId}` - Órdenes del usuario
- `GET /orders/{id}` - Obtener orden por ID
- `POST /orders` - Crear nueva orden
  - Body: `CreateOrderRequest` con lista de items
- `PUT /orders/{id}` - Actualizar orden (cambiar estado)

### API Externa: PokeAPI
- **URL Base:** `https://pokeapi.co/api/v2/`
- **Endpoint utilizado:** `/pokemon/{id}`
- **Propósito:** Demostración de integración con API externa
- **Implementación:** `PokeApiService.kt`, `Pokemon.kt`, `PokemonRepository.kt`

---

## Base de Datos - Estructura

### Tablas Principales

**users**
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    clave VARCHAR(255),
    is_admin BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**products**
```sql
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255),
    description TEXT,
    price DECIMAL(10,2),
    currency VARCHAR(10),
    category_id BIGINT,
    stock INT,
    image VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
```

**cart**
```sql
CREATE TABLE cart (
    user_id BIGINT,
    product_id BIGINT,
    quantity INT,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, product_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);
```

**favorites**
```sql
CREATE TABLE favorites (
    user_id BIGINT,
    product_id BIGINT,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, product_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);
```

**orders**
```sql
CREATE TABLE orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    status VARCHAR(20) DEFAULT 'pending',
    total DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

**order_items**
```sql
CREATE TABLE order_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    product_id BIGINT,
    quantity INT,
    price DECIMAL(10,2),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);
```

**news**
```sql
CREATE TABLE news (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255),
    content TEXT,
    summary TEXT,
    image VARCHAR(255),
    thumbnail VARCHAR(255),
    category VARCHAR(100),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

---

## Pruebas Unitarias

### Framework de Testing
- JUnit 4.13.2
- MockK 1.13.8
- Kotlinx Coroutines Test 1.7.3

### Tests Implementados

**AdminNewsViewModelTest**
- Verificación de carga de noticias
- Pruebas de creación de noticias
- Pruebas de actualización de noticias
- Pruebas de eliminación de noticias
- Pruebas de selección y navegación
- Total: 8 tests

**AdminProductsViewModelTest**
- Verificación de carga de productos
- Pruebas de creación de productos
- Pruebas de actualización de productos
- Pruebas de eliminación de productos
- Pruebas de filtrado por categoría
- Validación de estados de UI
- Total: 11 tests

**ProductRepositoryTest**
- Pruebas de obtención de productos
- Pruebas de filtrado por categoría
- Pruebas de búsqueda de productos
- Pruebas de operaciones CRUD
- Manejo de errores y casos límite
- Total: 11 tests

### Ejecución de Tests

```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar tests con reporte de cobertura
./gradlew testDebugUnitTest --info

# Ver reporte de cobertura
./gradlew jacocoTestReport
```

---

## Instalación y Configuración

### Requisitos Previos

**Para la Aplicación Móvil:**
- Android Studio Hedgehog o superior
- JDK 17 o superior
- SDK de Android (API Level 26 - Android 8.0 o superior)
- Dispositivo Android físico o emulador configurado

**Para el Backend:**
- Java JDK 17 o superior
- MySQL 8.0 o superior
- Maven 3.8 o superior
- Puerto 8080 disponible

### Configuración del Backend

1. Clonar el repositorio del backend:
```bash
git clone [URL_REPOSITORIO_BACKEND]
cd [CARPETA_BACKEND]
```

2. Crear la base de datos en MySQL:
```sql
CREATE DATABASE levelup_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. Configurar credenciales en `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/levelup_db
spring.datasource.username=[TU_USUARIO]
spring.datasource.password=[TU_CONTRASEÑA]
spring.jpa.hibernate.ddl-auto=update
```

4. Ejecutar el backend:
```bash
mvn spring-boot:run
```

El servidor estará disponible en `http://localhost:8080`

### Configuración de la Aplicación Móvil

1. Clonar el repositorio:
```bash
git clone [URL_REPOSITORIO]
cd Level-Up-App
```

2. Abrir el proyecto en Android Studio

3. Sincronizar dependencias de Gradle:
   - File > Sync Project with Gradle Files

4. Configurar la URL del backend en `RetrofitInstance.kt`:
```kotlin
private const val BASE_URL = "http://10.0.2.2:8080/api/"  // Para emulador
// o
private const val BASE_URL = "http://[TU_IP]:8080/api/"    // Para dispositivo físico
```

5. Compilar el proyecto:
```bash
./gradlew build
```

6. Ejecutar en dispositivo/emulador:
   - Run > Run 'app'
   - O presionar Shift + F10

### Datos de Prueba

Usuarios predefinidos en la base de datos:

**Usuario Administrador:**
- Email: `admin@example.com`
- Contraseña: `admin123`

**Usuario Regular:**
- Email: `user@example.com`
- Contraseña: `user123`

---

## Generación de APK Firmado

### Configuración de Firma

1. Crear KeyStore:
```bash
keytool -genkey -v -keystore level-up-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias level-up-key
```

2. Crear archivo `keystore.properties` en la raíz:
```properties
storePassword=[CONTRASEÑA_KEYSTORE]
keyPassword=[CONTRASEÑA_ALIAS]
keyAlias=level-up-key
storeFile=[RUTA_COMPLETA_AL_JKS]
```

3. Configurar `build.gradle.kts` (app):
```kotlin
android {
    signingConfigs {
        create("release") {
            // Configuración de firma
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(...)
        }
    }
}
```

### Generar APK

```bash
./gradlew assembleRelease
```

El APK estará en: `app/build/outputs/apk/release/app-release.apk`

---

## Gestión del Proyecto

### Metodología de Trabajo
- Metodología Ágil con sprints de 1 semana
- Reuniones de sincronización diarias
- Revisión de código mediante Pull Requests
- Integración continua

### Control de Versiones (Git)
- Rama principal: `main`
- Ramas de desarrollo: `feature/[nombre-feature]`
- Commits descriptivos siguiendo convención:
  - `feat:` para nuevas funcionalidades
  - `fix:` para correcciones de bugs
  - `refactor:` para refactorización de código
  - `docs:` para documentación
  - `test:` para pruebas

### Gestión de Tareas (Trello)
Tablero organizado en columnas:
- Backlog
- En Progreso
- En Revisión
- Completado

Tareas asignadas a cada integrante con etiquetas de prioridad.

---

## Problemas Conocidos y Limitaciones

1. **Simulación de Pago:** El proceso de pago está simulado con un delay de 2 segundos. En producción se requiere integración con pasarela de pagos real (Stripe, PayPal, etc.)

2. **Autenticación:** El sistema de autenticación no utiliza JWT. Se recomienda implementar tokens para mayor seguridad en producción.

3. **Cifrado de Contraseñas:** Las contraseñas se almacenan en texto plano. Se debe implementar BCrypt o similar para producción.

4. **Carga de Imágenes:** Las imágenes se referencian por ruta de archivo. En producción se recomienda usar almacenamiento en la nube (AWS S3, Firebase Storage).

5. **Validación de Stock:** La validación de stock no contempla operaciones concurrentes. Se requiere implementar transacciones y locks en producción.

---


## Referencias

- Android Developers Documentation: https://developer.android.com/
- Jetpack Compose Documentation: https://developer.android.com/jetpack/compose
- Spring Boot Documentation: https://spring.io/projects/spring-boot
- Retrofit Documentation: https://square.github.io/retrofit/
- Material Design 3: https://m3.material.io/

---

## Licencia

Este proyecto es desarrollado con fines académicos para la asignatura de Desarrollo de Aplicaciones Móviles de DuocUC.

---


**Última actualización:** Diciembre 2025

---

jesus ql chupalo

