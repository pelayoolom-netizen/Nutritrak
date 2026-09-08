package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun EditFoodItemDialog(
    initialItem: FoodItem,
    onDismiss: () -> Unit,
    onConfirm: (FoodItem) -> Unit
) {
    var name by remember { mutableStateOf(initialItem.name) }
    var quantityStr by remember { mutableStateOf(initialItem.quantity.toString()) }
    var unit by remember { mutableStateOf(initialItem.unit) }
    var caloriesStr by remember { mutableStateOf(initialItem.calories.toString()) }
    var proteinStr by remember { mutableStateOf(initialItem.protein.toString()) }
    var carbsStr by remember { mutableStateOf(initialItem.carbs.toString()) }
    var fatStr by remember { mutableStateOf(initialItem.fat.toString()) }
    var selectedMealType by remember { mutableStateOf(initialItem.mealType) }

    val baseQuantity = remember { initialItem.quantity }
    val baseCalories = remember { initialItem.calories }
    val baseProtein = remember { initialItem.protein }
    val baseCarbs = remember { initialItem.carbs }
    val baseFat = remember { initialItem.fat }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Editar Alimento",
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
                    onValueChange = { name = it },
                    label = { Text("Nombre del alimento") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = quantityStr,
                        onValueChange = { newQtyStr ->
                            quantityStr = newQtyStr
                            val newQty = newQtyStr.toDoubleOrNull()
                            if (newQty != null && baseQuantity > 0) {
                                val ratio = newQty / baseQuantity
                                caloriesStr = (baseCalories * ratio).toInt().toString()
                                proteinStr = Math.round(baseProtein * ratio * 10.0 / 10.0).toString()
                                carbsStr = Math.round(baseCarbs * ratio * 10.0 / 10.0).toString()
                                fatStr = Math.round(baseFat * ratio * 10.0 / 10.0).toString()
                            }
                        },
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

                OutlinedTextField(
                    value = caloriesStr,
                    onValueChange = { caloriesStr = it },
                    label = { Text("Calorías (kcal)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = initialItem.copy(
                        mealType = selectedMealType,
                        name = name.trim().ifEmpty { initialItem.name },
                        quantity = quantityStr.toDoubleOrNull() ?: initialItem.quantity,
                        unit = unit.trim().ifEmpty { initialItem.unit },
                        calories = caloriesStr.toDoubleOrNull() ?: initialItem.calories,
                        protein = proteinStr.toDoubleOrNull() ?: initialItem.protein,
                        carbs = carbsStr.toDoubleOrNull() ?: initialItem.carbs,
                        fat = fatStr.toDoubleOrNull() ?: initialItem.fat
                    )
                    onConfirm(updated)
                },
                modifier = Modifier.testTag("confirm_edit_food_button")
            ) {
                Text("Actualizar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
