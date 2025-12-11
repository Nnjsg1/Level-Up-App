package com.example.level_up_app.data

data class PokemonResponse(
    val name: String,
    val id: Int,
    val sprites: Sprites? = null
)

data class Sprites(
    val front_default: String? = null
)

