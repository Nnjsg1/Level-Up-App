package com.example.level_up_app.data

import androidx.compose.runtime.mutableStateListOf
import java.util.UUID

object ProductRepository {
    private val _products = mutableStateListOf<Product>()
    val products: List<Product> get() = _products

    init {
        // Inicializar productos predefinidos
        _products.addAll(
            listOf(
                Product(
                    id = UUID.randomUUID().toString(),
                    name = "PlayStation 5",
                    description = "Consola de última generación con gráficos 4K y velocidad de carga ultrarrápida gracias a su SSD personalizado.",
                    price = 499990.0,
                    imageUrl = "https://images.unsplash.com/photo-1606813907291-d86efa9b94db?w=500",
                    category = "Consolas",
                    stock = 15
                ),
                Product(
                    id = UUID.randomUUID().toString(),
                    name = "Xbox Series X",
                    description = "La consola Xbox más potente de la historia con compatibilidad con miles de juegos de generaciones anteriores.",
                    price = 499990.0,
                    imageUrl = "https://images.unsplash.com/photo-1621259182978-fbf93132d53d?w=500",
                    category = "Consolas",
                    stock = 12
                ),
                Product(
                    id = UUID.randomUUID().toString(),
                    name = "Nintendo Switch OLED",
                    description = "Consola híbrida con pantalla OLED de 7 pulgadas, perfecta para jugar en casa o en movimiento.",
                    price = 349990.0,
                    imageUrl = "https://d16c9dlthokxv6.cloudfront.net/catalog/product/cache/e83b319fe15d087a014efa16f11c0f36/c/o/consola_oled.png",
                    category = "Consolas",
                    stock = 20
                ),
                Product(
                    id = UUID.randomUUID().toString(),
                    name = "The Legend of Zelda: Tears of the Kingdom",
                    description = "Secuela épica de Breath of the Wild. Explora Hyrule en una aventura inolvidable.",
                    price = 69990.0,
                    imageUrl = "https://media.vandal.net/m/74464/the-legend-of-zelda-tears-of-the-kingdom-202291410341410_1.jpg",
                    category = "Videojuegos",
                    stock = 50
                ),
                Product(
                    id = UUID.randomUUID().toString(),
                    name = "God of War Ragnarök",
                    description = "Acompaña a Kratos y Atreus en su épica batalla contra los dioses nórdicos.",
                    price = 30000.0,
                    imageUrl = "https://rimage.ripley.cl/home.ripley/Attachment/WOP/1/2000393144785/image1-2000393144785",
                    category = "Videojuegos",
                    stock = 35
                ),
                Product(
                    id = UUID.randomUUID().toString(),
                    name = "FIFA 24",
                    description = "La experiencia de fútbol más realista con HyperMotion V y todos los modos de juego que amas.",
                    price = 29990.0,
                    imageUrl = "https://i5.walmartimages.com/seo/EA-SPORTS-FC-24-Playstation-4-FIFA-24-Video-Game_c98bbb3c-137d-42df-91bb-dd3b25e19be0.07c374f830d31e015e4ae98a28b13508.jpeg",
                    category = "Videojuegos",
                    stock = 40
                ),
                Product(
                    id = UUID.randomUUID().toString(),
                    name = "Control DualSense",
                    description = "Control inalámbrico para PS5 con retroalimentación háptica y gatillos adaptativos.",
                    price = 69990.0,
                    imageUrl = "https://images.unsplash.com/photo-1592840496694-26d035b52b48?w=500",
                    category = "Accesorios",
                    stock = 30
                ),
                Product(
                    id = UUID.randomUUID().toString(),
                    name = "Auriculares Gaming RGB",
                    description = "Auriculares con sonido envolvente 7.1, micrófono retráctil y iluminación RGB personalizable.",
                    price = 89990.0,
                    imageUrl = "https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=500",
                    category = "Accesorios",
                    stock = 25
                ),
                Product(
                    id = UUID.randomUUID().toString(),
                    name = "Tarjeta Regalo $50",
                    description = "Tarjeta regalo digital para canjear en PlayStation Store, Xbox Store o Nintendo eShop.",
                    price = 50000.0,
                    imageUrl = "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEghZRT9Ui70NublpzusoJm5Vqb_8Lg2ON-NN8qs7l53VyBwpNknwxGt2deHHEAtY6xDLcfVpZXcLlbj-5yob9LQu-lYY0RtsDaGAo3Ed4a1kpqhKEoNz2i9Sh56pLmcH_pU_wqvka7QXJTT/s1600/TarjetasRegalo.png",
                    category = "Tarjetas Regalo",
                    stock = 100
                ),
                Product(
                    id = UUID.randomUUID().toString(),
                    name = "Carat Umamusume",
                    description = "Coins especiales del juego Umamusume Pretty Derby para comprar artículos exclusivos.",
                    price = 20000.0,
                    imageUrl = "https://gametora.com/images/umamusume/items/item_icon_00043.png",
                    category = "Otros",
                    stock = 8
                ),
                Product(
                    id = UUID.randomUUID().toString(),
                    name = "Hollow Knight Silksong",
                    description = "Secuela del famoso juego Hollow Knight. Explora nuevos reinos y enfrenta nuevos desafíos junto a Hornet.",
                    price = 20000.0,
                    imageUrl = "https://static.wikia.nocookie.net/hollowknight/images/1/13/Silksong_cover.jpg/revision/latest?cb=20190214093718",
                    category = "Videojuegos",
                    stock = 8
                ),
                Product(
                    id = UUID.randomUUID().toString(),
                    name = "Silla Gaming Pro",
                    description = "Silla ergonómica con soporte lumbar ajustable, reposabrazos 4D y respaldo reclinable hasta 180°.",
                    price = 299990.0,
                    imageUrl = "https://tododescuento.cl/wp-content/uploads/2024/02/Silla_gamer_xzone_negra_3.jpg",
                    category = "Mobiliario",
                    stock = 8
                ),
                Product(
                    id = UUID.randomUUID().toString(),
                    name = "Teclado Mecánico RGB",
                    description = "Teclado mecánico con switches Blue, iluminación RGB por tecla y reposamuñecas magnético.",
                    price = 129990.0,
                    imageUrl = "https://kyrios.cl/cdn/shop/files/TBD0605828003.jpg?v=1744343716",
                    category = "Accesorios",
                    stock = 18
                ),
                Product(
                    id = UUID.randomUUID().toString(),
                    name = "Mouse Gaming 16000 DPI",
                    description = "Mouse ergonómico con sensor óptico de 16000 DPI, 8 botones programables y peso ajustable.",
                    price = 59990.0,
                    imageUrl = "https://images.unsplash.com/photo-1527814050087-3793815479db?w=500",
                    category = "Accesorios",
                    stock = 22
                )
            )
        )
    }
}

