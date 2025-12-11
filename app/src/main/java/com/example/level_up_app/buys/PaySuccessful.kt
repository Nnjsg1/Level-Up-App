package com.example.level_up_app.buys

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.level_up_app.data.PokemonRepository
import kotlinx.coroutines.launch

@Composable
fun PayResultScreen(
    isSuccess: Boolean,
    onRetry: () -> Unit,
    onGoHome: () -> Unit
) {
    var pokemonName by remember { mutableStateOf<String?>(null) }
    var pokemonImageUrl by remember { mutableStateOf<String?>(null) }
    var isLoadingPokemon by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Cargar pokemon aleatorio cuando el pago es exitoso
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            isLoadingPokemon = true
            scope.launch {
                PokemonRepository.getRandomPokemon().fold(
                    onSuccess = { pokemon ->
                        pokemonName = PokemonRepository.formatPokemonName(pokemon.name)
                        pokemonImageUrl = pokemon.sprites?.front_default
                        isLoadingPokemon = false
                    },
                    onFailure = {
                        pokemonName = "Pikachu" // Pokemon por defecto en caso de error
                        isLoadingPokemon = false
                    }
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = if (isSuccess) "✓ Pago confirmado" else "✗ Pago rechazado",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isSuccess) {
                // Card con información del pokemon
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(20.dp)
                    ) {
                        if (isLoadingPokemon) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(60.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Asignando repartidor...",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            // Imagen del pokemon
                            if (pokemonImageUrl != null) {
                                AsyncImage(
                                    model = pokemonImageUrl,
                                    contentDescription = "Pokemon $pokemonName",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Fit
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }

                            Text(
                                text = "Su pedido está siendo entregado por",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = pokemonName ?: "Cargando...",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onGoHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "Volver al menú principal",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                Button(
                    onClick = onRetry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "Reintentar pago",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun PaySuccessfuln() {
    PayResultScreen(
        isSuccess = true,
        onRetry = {},
        onGoHome = {}
    )
}