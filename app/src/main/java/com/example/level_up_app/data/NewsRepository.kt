package com.example.level_up_app.data

import androidx.compose.runtime.mutableStateListOf
import java.util.UUID

object NewsRepository {
    private val _newsList = mutableStateListOf<News>()
    val newsList: List<News> get() = _newsList

    init {
        // Inicializar noticias predefinidas
        _newsList.addAll(
            listOf(
                News(
                    id = UUID.randomUUID().toString(),
                    title = "Inédito: Se encuentra primer gatuno streamer en el mundo",
                    summary = "Un gato sorprende al mundo haciendo streams de videojuegos con miles de espectadores.",
                    content = "En un hecho sin precedentes, la comunidad gamer ha descubierto al primer gato streamer profesional del mundo. Este felino, conocido como 'MichiGamer', ha capturado la atención de miles de espectadores con sus peculiares sesiones de streaming.\n\nEl gato, que reside con su dueño streamer, ha demostrado tener habilidades únicas al interactuar con juegos en pantalla, especialmente aquellos que involucran movimientos rápidos y objetos en movimiento. Los espectadores quedan fascinados viendo cómo el felino intenta 'cazar' los elementos en la pantalla.\n\nLa comunidad ha respondido de manera abrumadoramente positiva, con donaciones y suscripciones que han convertido a este gato en una verdadera celebridad del streaming. Expertos en comportamiento animal señalan que esta es una forma innovadora de entretenimiento que combina el amor por las mascotas con la cultura gamer.",
                    imageUrl = "https://images.unsplash.com/photo-1574158622682-e40e69881006?w=500",
                    videoPath = "gatogamer", // Nombre del video en res/raw sin extensión
                    date = "12 Nov 2025",
                    category = "Curiosidades"
                ),
                News(
                    id = UUID.randomUUID().toString(),
                    title = "Nintendo anuncia nueva consola para 2026",
                    summary = "La compañía japonesa confirma el desarrollo de la sucesora de Nintendo Switch.",
                    content = "Nintendo ha confirmado oficialmente que está trabajando en una nueva consola que llegará al mercado en 2026. Aunque la compañía ha mantenido los detalles técnicos en secreto, fuentes cercanas sugieren que la nueva consola tendrá capacidades mejoradas significativamente en comparación con la Switch actual.\n\nSegún declaraciones del presidente de Nintendo, la nueva consola mantendrá la filosofía híbrida que hizo tan exitosa a la Switch, permitiendo jugar tanto en modo portátil como conectada al televisor. Sin embargo, se espera que incorpore tecnología de última generación para competir con las consolas de Sony y Microsoft.\n\nLos analistas de la industria predicen que esta nueva consola podría incluir soporte para ray tracing, mayor resolución en modo dock, y una biblioteca retrocompatible con juegos de Switch. Los fanáticos ya están especulando sobre los posibles títulos de lanzamiento, con muchos esperando una nueva entrega de la saga Zelda o Mario.\n\nLa comunidad gamer ha recibido la noticia con gran entusiasmo, y las acciones de Nintendo han experimentado un aumento significativo tras el anuncio.",
                    imageUrl = "https://images.unsplash.com/photo-1578303512597-81e6cc155b3e?w=500",
                    videoPath = "",
                    date = "10 Nov 2025",
                    category = "Hardware"
                )
            )
        )
    }

    fun addNews(news: News) {
        _newsList.add(news)
    }

    fun removeNews(newsId: String) {
        _newsList.removeAll { it.id == newsId }
    }
}

