package com.example.level_up_app.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.level_up_app.data.Product
import com.example.level_up_app.data.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormDialog(
    product: Product?,
    categories: List<Category>,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var price by remember { mutableStateOf(product?.price?.toString() ?: "") }
    var stock by remember { mutableStateOf(product?.stock?.toString() ?: "") }
    var imageUrl by remember { mutableStateOf(product?.imageUrl ?: "") }
    var selectedCategory by remember { mutableStateOf(product?.category) }
    var expanded by remember { mutableStateOf(false) }

    val isEditing = product != null
    val title = if (isEditing) "Editar Producto" else "Nuevo Producto"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                HorizontalDivider()

                // Formulario
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Campo Nombre
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre del producto *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Campo Descripción
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )

                    // Campo Precio
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Precio *") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        prefix = { Text("$") }
                    )

                    // Campo Stock
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it },
                        label = { Text("Stock *") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        suffix = { Text("unidades") }
                    )

                    // Selector de Categoría
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory?.name ?: "Seleccionar categoría",
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Categoría") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        selectedCategory = category
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Campo URL de imagen
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = { Text("URL de imagen") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("https://ejemplo.com/imagen.jpg") }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar")
                        }

                        Button(
                            onClick = {
                                val priceValue = price.toDoubleOrNull() ?: 0.0
                                val stockValue = stock.toIntOrNull() ?: 0

                                val newProduct = Product(
                                    id = product?.id ?: 0L,
                                    name = name.trim(),
                                    description = description.trim(),
                                    price = priceValue,
                                    stock = stockValue,
                                    imageUrl = imageUrl.trim(),
                                    category = selectedCategory
                                )
                                onSave(newProduct)
                            },
                            modifier = Modifier.weight(1f),
                            enabled = name.isNotBlank() &&
                                     price.toDoubleOrNull() != null &&
                                     stock.toIntOrNull() != null
                        ) {
                            Text(if (isEditing) "Actualizar" else "Crear")
                        }
                    }
                }
            }
        }
    }
}
