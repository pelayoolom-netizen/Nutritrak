package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.FoodItem
import com.example.data.model.MealType
import com.example.ui.theme.CarbsColor
import com.example.ui.theme.FatColor
import com.example.ui.theme.ProteinColor

@Composable
fun ManualFoodEntryDialog(
    initialMealType: MealType,
    onDismiss: () -> Unit,
    onSave: (FoodItem, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantityStr by remember { mutableStateOf("100") }
    var unit by remember { mutableStateOf("g") }
    var caloriesStr by remember { mutableStateOf("") }
    var proteinStr by remember { mutableStateOf("") }
    var carbsStr by remember { mutableStateOf("") }
    var fatStr by remember { mutableStateOf("") }
    var selectedMealType by remember { mutableStateOf(initialMealType) }
    var saveAsFavorite by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Registrar Alimento",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Selector de categoría de comida
                Text(
                    text = "Momento del día",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    MealType.entries.forEach { type ->
                        FilterChip(
                            selected = selectedMealType == type,
                            onClick = { selectedMealType = type },
                            label = { Text(type.displayName, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    label = { Text("Nombre del alimento *") },
                    placeholder = { Text("Ej. Pechuga de pollo, Tortilla...") },
                    isError = nameError,
                    supportingText = if (nameError) { { Text("El nombre es obligatorio") } } else null,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("manual_food_name_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = quantityStr,
                        onValueChange = { quantityStr = it },
                        label = { Text("Cantidad") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unidad") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = caloriesStr,
                        onValueChange = { caloriesStr = it },
                        label = { Text("Calorías (kcal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("manual_food_calories_input")
                    )
                }

                // Macro helper chip
                AssistChip(
                    onClick = {
                        val p = proteinStr.toDoubleOrNull() ?: 0.0
                        val c = carbsStr.toDoubleOrNull() ?: 0.0
                        val f = fatStr.toDoubleOrNull() ?: 0.0
                        val calculated = (p * 4) + (c * 4) + (f * 9)
                        if (calculated > 0) {
                            caloriesStr = calculated.toInt().toString()
                        }
                    },
                    leadingIcon = { Icon(Icons.Default.Calculate, contentDescription = null) },
                    label = { Text("Calcular calorías según macros") }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = proteinStr,
                        onValueChange = { proteinStr = it },
                        label = { Text("Proteínas (g)", color = ProteinColor) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = carbsStr,
                        onValueChange = { carbsStr = it },
                        label = { Text("Carbos (g)", color = CarbsColor) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = fatStr,
                        onValueChange = { fatStr = it },
                        label = { Text("Grasas (g)", color = FatColor) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = saveAsFavorite,
                        onCheckedChange = { saveAsFavorite = it }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Guardar en favoritos para acceso rápido",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                        return@Button
                    }
                    val qty = quantityStr.toDoubleOrNull() ?: 100.0
                    val cal = caloriesStr.toDoubleOrNull() ?: run {
                        val p = proteinStr.toDoubleOrNull() ?: 0.0
                        val c = carbsStr.toDoubleOrNull() ?: 0.0
                        val f = fatStr.toDoubleOrNull() ?: 0.0
                        (p * 4) + (c * 4) + (f * 9)
                    }
                    val item = FoodItem(
                        mealType = selectedMealType,
                        name = name.trim(),
                        quantity = qty,
                        unit = unit.trim().ifEmpty { "g" },
                        calories = cal,
                        protein = proteinStr.toDoubleOrNull() ?: 0.0,
                        carbs = carbsStr.toDoubleOrNull() ?: 0.0,
                        fat = fatStr.toDoubleOrNull() ?: 0.0,
                        isAiEstimated = false
                    )
                    onSave(item, saveAsFavorite)
                },
                modifier = Modifier.testTag("save_manual_food_button")
            ) {
                Text("Guardar alimento")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
