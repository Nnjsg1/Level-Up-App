// ============================================
// EJEMPLO DE BACKEND CON NODE.JS + EXPRESS
// ============================================
// Este es un ejemplo simple de cómo debería verse tu backend
// para que funcione con la app Android

const express = require('express');
const cors = require('cors');
const app = express();

// Middleware
app.use(cors());
app.use(express.json());

// ============================================
// SIMULACIÓN DE BASE DE DATOS
// ============================================
// En tu caso real, esto vendría de MySQL, MongoDB, etc.
const usuarios = [
    {
        id: "1",
        email: "admin@test.com",
        password: "12345", // En producción, usa bcrypt para hashear
        name: "Admin Test",
        createdAt: "2024-01-01"
    },
    {
        id: "2",
        email: "usuario@test.com",
        password: "password",
        name: "Usuario Test",
        createdAt: "2024-01-02"
    }
];

const productos = [
    {
        id: "1",
        name: "Producto 1",
        description: "Descripción del producto 1",
        price: 29.99,
        stock: 10,
        category: "Categoría A",
        imageUrl: "https://via.placeholder.com/300"
    }
];

// ============================================
// ENDPOINTS DE AUTENTICACIÓN
// ============================================

// POST /api/login
app.post('/api/login', (req, res) => {
    const { email, password } = req.body;

    console.log('Login attempt:', { email, password });

    // Buscar usuario
    const usuario = usuarios.find(u => u.email === email);

    if (!usuario) {
        return res.status(404).json({
            success: false,
            message: "Usuario no encontrado"
        });
    }

    // Verificar contraseña
    if (usuario.password !== password) {
        return res.status(401).json({
            success: false,
            message: "Email o contraseña incorrectos"
        });
    }

    // Login exitoso
    res.json({
        success: true,
        message: "Login exitoso",
        user: {
            id: usuario.id,
            email: usuario.email,
            name: usuario.name,
            createdAt: usuario.createdAt
        },
        token: "jwt_token_ejemplo_12345" // Genera un JWT real en producción
    });
});

// POST /api/register
app.post('/api/register', (req, res) => {
    const { email, password, name } = req.body;

    // Verificar si el email ya existe
    const existe = usuarios.find(u => u.email === email);
    if (existe) {
        return res.status(400).json({
            success: false,
            message: "Email ya registrado"
        });
    }

    // Crear nuevo usuario
    const nuevoUsuario = {
        id: String(usuarios.length + 1),
        email,
        password, // Hashear en producción
        name,
        createdAt: new Date().toISOString()
    };

    usuarios.push(nuevoUsuario);

    res.status(201).json({
        success: true,
        message: "Usuario registrado exitosamente",
        user: {
            id: nuevoUsuario.id,
            email: nuevoUsuario.email,
            name: nuevoUsuario.name,
            createdAt: nuevoUsuario.createdAt
        }
    });
});

// ============================================
// ENDPOINTS DE USUARIOS
// ============================================

// GET /api/users - Obtener todos los usuarios
app.get('/api/users', (req, res) => {
    // No enviar las contraseñas
    const usuariosSinPassword = usuarios.map(u => ({
        id: u.id,
        email: u.email,
        name: u.name,
        createdAt: u.createdAt
    }));
    res.json(usuariosSinPassword);
});

// GET /api/users/:id - Obtener un usuario por ID
app.get('/api/users/:id', (req, res) => {
    const usuario = usuarios.find(u => u.id === req.params.id);
    if (!usuario) {
        return res.status(404).json({ message: "Usuario no encontrado" });
    }

    res.json({
        id: usuario.id,
        email: usuario.email,
        name: usuario.name,
        createdAt: usuario.createdAt
    });
});

// PUT /api/users/:id - Actualizar usuario
app.put('/api/users/:id', (req, res) => {
    const index = usuarios.findIndex(u => u.id === req.params.id);
    if (index === -1) {
        return res.status(404).json({ message: "Usuario no encontrado" });
    }

    usuarios[index] = { ...usuarios[index], ...req.body };
    res.json(usuarios[index]);
});

// DELETE /api/users/:id - Eliminar usuario
app.delete('/api/users/:id', (req, res) => {
    const index = usuarios.findIndex(u => u.id === req.params.id);
    if (index === -1) {
        return res.status(404).json({ message: "Usuario no encontrado" });
    }

    usuarios.splice(index, 1);
    res.status(204).send();
});

// ============================================
// ENDPOINTS DE PRODUCTOS
// ============================================

// GET /api/products - Obtener todos los productos
app.get('/api/products', (req, res) => {
    res.json(productos);
});

// GET /api/products/:id - Obtener un producto por ID
app.get('/api/products/:id', (req, res) => {
    const producto = productos.find(p => p.id === req.params.id);
    if (!producto) {
        return res.status(404).json({ message: "Producto no encontrado" });
    }
    res.json(producto);
});

// POST /api/products - Crear producto
app.post('/api/products', (req, res) => {
    const nuevoProducto = {
        id: String(productos.length + 1),
        ...req.body
    };
    productos.push(nuevoProducto);
    res.status(201).json(nuevoProducto);
});

// PUT /api/products/:id - Actualizar producto
app.put('/api/products/:id', (req, res) => {
    const index = productos.findIndex(p => p.id === req.params.id);
    if (index === -1) {
        return res.status(404).json({ message: "Producto no encontrado" });
    }

    productos[index] = { ...productos[index], ...req.body };
    res.json(productos[index]);
});

// DELETE /api/products/:id - Eliminar producto
app.delete('/api/products/:id', (req, res) => {
    const index = productos.findIndex(p => p.id === req.params.id);
    if (index === -1) {
        return res.status(404).json({ message: "Producto no encontrado" });
    }

    productos.splice(index, 1);
    res.status(204).send();
});

// ============================================
// INICIAR SERVIDOR
// ============================================

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`🚀 Servidor corriendo en http://localhost:${PORT}`);
    console.log(`📝 Usuarios de prueba:`);
    usuarios.forEach(u => {
        console.log(`   - ${u.email} / ${u.password}`);
    });
});

// ============================================
// PARA CORRER ESTE SERVIDOR:
// ============================================
// 1. npm init -y
// 2. npm install express cors
// 3. node server.js
// ============================================

