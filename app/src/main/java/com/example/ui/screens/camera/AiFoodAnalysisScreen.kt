package com.example.ui.screens.camera

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiDetectedFood
import com.example.data.model.MealType
import com.example.ui.theme.CarbsColor
import com.example.ui.theme.FatColor
import com.example.ui.theme.ProteinColor
import com.example.ui.viewmodel.AiAnalysisUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiFoodAnalysisScreen(
    state: AiAnalysisUiState,
    onBack: () -> Unit,
    onSaveMeal: (MealType) -> Unit,
    onUpdateItem: (Int, AiDetectedFood) -> Unit,
    onDeleteItem: (Int) -> Unit,
    onDuplicateItem: (Int) -> Unit,
    onAddItem: (AiDetectedFood) -> Unit,
    onRetry: () -> Unit,
    onAddManuallyInstead: () -> Unit,
    initialMealType: MealType = MealType.LUNCH
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (state) {
                            is AiAnalysisUiState.Loading -> "Analizando plato..."
                            is AiAnalysisUiState.Review -> "Análisis IA de Comida"
                            is AiAnalysisUiState.Error -> "Error de Análisis"
                            else -> "Análisis de Foto"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (state) {
                is AiAnalysisUiState.Loading -> {
                    AiAnalyzingLoadingView(message = state.message)
                }
                is AiAnalysisUiState.Review -> {
                    AiAnalysisReviewView(
                        review = state,
                        initialMealType = initialMealType,
                        onSaveMeal = onSaveMeal,
                        onUpdateItem = onUpdateItem,
                        onDeleteItem = onDeleteItem,
                        onDuplicateItem = onDuplicateItem,
                        onAddItem = onAddItem
                    )
                }
                is AiAnalysisUiState.Error -> {
                    AiAnalysisErrorView(
                        message = state.message,
                        onRetry = onRetry,
                        onManual = onAddManuallyInstead,
                        onCancel = onBack
                    )
                }
                is AiAnalysisUiState.Idle -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No se ha seleccionado ninguna foto para analizar")
                    }
                }
            }
        }
    }
}

@Composable
fun AiAnalyzingLoadingView(message: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "ai_pulse")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ai_spin"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(48.dp)
                    .rotate(rotation)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "ANALIZANDO TU PLATO",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        CircularProgressIndicator(
            modifier = Modifier.size(36.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp
        )
    }
}

@Composable
fun AiAnalysisReviewView(
    review: AiAnalysisUiState.Review,
    initialMealType: MealType,
    onSaveMeal: (MealType) -> Unit,
    onUpdateItem: (Int, AiDetectedFood) -> Unit,
    onDeleteItem: (Int) -> Unit,
    onDuplicateItem: (Int) -> Unit,
    onAddItem: (AiDetectedFood) -> Unit
) {
    var selectedMealType by remember { mutableStateOf(initialMealType) }
    var editingIndex by remember { mutableStateOf<Int?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val totalCalories = review.foods.sumOf { it.estimatedCalories }
    val totalProtein = review.foods.sumOf { it.estimatedProtein }
    val totalCarbs = review.foods.sumOf { it.estimatedCarbs }
    val totalFat = review.foods.sumOf { it.estimatedFat }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Thumbnail & AI Disclaimer Card
        item {
            Spacer(modifier = Modifier.height(4.dp))
            if (review.photoBitmap != null) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Image(
                        bitmap = review.photoBitmap.asImageBitmap(),
                        contentDescription = "Foto del plato",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        // IMPORTANT AI Disclaimer Banner
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = review.note,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Detected Foods Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ALIMENTOS DETECTADOS (${review.foods.size})",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Añadir otro")
                }
            }
        }

        // Detected Food Items
        itemsIndexed(review.foods) { index, item ->
            AiDetectedFoodCard(
                item = item,
                onEdit = { editingIndex = index },
                onDelete = { onDeleteItem(index) },
                onDuplicate = { onDuplicateItem(index) }
            )
        }

        // Total Estimate Summary Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ESTIMACIÓN TOTAL",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${totalCalories.toInt()} kcal",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Proteínas: ${totalProtein.toInt()}g",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = ProteinColor
                        )
                        Text(
                            text = "Carbos: ${totalCarbs.toInt()}g",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = CarbsColor
                        )
                        Text(
                            text = "Grasas: ${totalFat.toInt()}g",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = FatColor
                        )
                    }
                }
            }
        }

        // Meal Type Selector
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Guardar en:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MealType.entries.forEach { type ->
                        FilterChip(
                            selected = selectedMealType == type,
                            onClick = { selectedMealType = type },
                            label = { Text(type.displayName) }
                        )
                    }
                }
            }
        }

        // Save Meal CTA
        item {
            Button(
                onClick = { onSaveMeal(selectedMealType) },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_ai_meal_button")
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Guardar en ${selectedMealType.displayName} (${totalCalories.toInt()} kcal)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Edit Item Dialog
    editingIndex?.let { index ->
        val item = review.foods.getOrNull(index)
        if (item != null) {
            EditDetectedFoodDialog(
                initial = item,
                onDismiss = { editingIndex = null },
                onConfirm = { updated ->
                    onUpdateItem(index, updated)
                    editingIndex = null
                }
            )
        }
    }

    // Add Item Dialog
    if (showAddDialog) {
        EditDetectedFoodDialog(
            initial = AiDetectedFood(
                name = "",
                estimatedQuantity = 100.0,
                unit = "g",
                estimatedCalories = 120.0,
                estimatedProtein = 5.0,
                estimatedCarbs = 15.0,
                estimatedFat = 2.0
            ),
            isNewItem = true,
            onDismiss = { showAddDialog = false },
            onConfirm = { added ->
                onAddItem(added)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AiDetectedFoodCard(
    item: AiDetectedFood,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${item.estimatedQuantity.toInt()} ${item.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "${item.estimatedCalories.toInt()} kcal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Proteínas: ${item.estimatedProtein.toInt()}g",
                    style = MaterialTheme.typography.bodySmall,
                    color = ProteinColor
                )
                Text(
                    text = "Carbos: ${item.estimatedCarbs.toInt()}g",
                    style = MaterialTheme.typography.bodySmall,
                    color = CarbsColor
                )
                Text(
                    text = "Grasas: ${item.estimatedFat.toInt()}g",
                    style = MaterialTheme.typography.bodySmall,
                    color = FatColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f), thickness = 0.5.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDuplicate) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Duplicar", style = MaterialTheme.typography.labelSmall)
                }
                TextButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Editar", style = MaterialTheme.typography.labelSmall)
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EditDetectedFoodDialog(
    initial: AiDetectedFood,
    isNewItem: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (AiDetectedFood) -> Unit
) {
    var name by remember { mutableStateOf(initial.name) }
    var quantityStr by remember { mutableStateOf(initial.estimatedQuantity.toString()) }
    var unit by remember { mutableStateOf(initial.unit) }
    var caloriesStr by remember { mutableStateOf(initial.estimatedCalories.toString()) }
    var proteinStr by remember { mutableStateOf(initial.estimatedProtein.toString()) }
    var carbsStr by remember { mutableStateOf(initial.estimatedCarbs.toString()) }
    var fatStr by remember { mutableStateOf(initial.estimatedFat.toString()) }

    val baseQty = remember { initial.baseQuantity.coerceAtLeast(1.0) }
    val baseCal = remember { initial.baseCalories }
    val baseProt = remember { initial.baseProtein }
    val baseCarb = remember { initial.baseCarbs }
    val baseFatVal = remember { initial.baseFat }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (isNewItem) "Añadir Alimento Detectado" else "Editar Alimento", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                        onValueChange = { newQ ->
                            quantityStr = newQ
                            val q = newQ.toDoubleOrNull()
                            if (q != null && baseQty > 0) {
                                val ratio = q / baseQty
                                caloriesStr = (Math.round(baseCal * ratio * 10.0) / 10.0).toString()
                                proteinStr = (Math.round(baseProt * ratio * 10.0) / 10.0).toString()
                                carbsStr = (Math.round(baseCarb * ratio * 10.0) / 10.0).toString()
                                fatStr = (Math.round(baseFatVal * ratio * 10.0) / 10.0).toString()
                            }
                        },
                        label = { Text("Cantidad") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unidad") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = caloriesStr,
                    onValueChange = { caloriesStr = it },
                    label = { Text("Calorías (kcal)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = proteinStr,
                        onValueChange = { proteinStr = it },
                        label = { Text("Proteínas (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = carbsStr,
                        onValueChange = { carbsStr = it },
                        label = { Text("Carbos (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = fatStr,
                        onValueChange = { fatStr = it },
                        label = { Text("Grasas (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = initial.copy(
                        name = name.trim().ifEmpty { "Alimento" },
                        estimatedQuantity = quantityStr.toDoubleOrNull() ?: initial.estimatedQuantity,
                        unit = unit.trim().ifEmpty { "g" },
                        estimatedCalories = caloriesStr.toDoubleOrNull() ?: initial.estimatedCalories,
                        estimatedProtein = proteinStr.toDoubleOrNull() ?: initial.estimatedProtein,
                        estimatedCarbs = carbsStr.toDoubleOrNull() ?: initial.estimatedCarbs,
                        estimatedFat = fatStr.toDoubleOrNull() ?: initial.estimatedFat
                    )
                    onConfirm(updated)
                }
            ) {
                Text(if (isNewItem) "Añadir" else "Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun AiAnalysisErrorView(
    message: String,
    onRetry: () -> Unit,
    onManual: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No se pudo analizar el plato",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reintentar")
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onManual,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Restaurant, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Añadir manualmente")
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(onClick = onCancel) {
            Text("Cancelar")
        }
    }
}
