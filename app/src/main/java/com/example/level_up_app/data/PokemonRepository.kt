package com.example.level_up_app.data

import com.example.level_up_app.remote.PokeApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

object PokemonRepository {
    // La PokeAPI tiene más de 1000 pokemones, pero usaremos los primeros 898 (generaciones 1-8)
    private const val MAX_POKEMON_ID = 898

    suspend fun getRandomPokemon(): Result<PokemonResponse> = withContext(Dispatchers.IO) {
        try {
            val randomId = Random.nextInt(1, MAX_POKEMON_ID + 1)
            val pokemon = PokeApiClient.api.getPokemon(randomId)
            Result.success(pokemon)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun formatPokemonName(name: String): String {
        // Capitaliza la primera letra de cada palabra
        return name.split("-")
            .joinToString(" ") { word ->
                word.replaceFirstChar { it.uppercase() }
            }
    }
}

