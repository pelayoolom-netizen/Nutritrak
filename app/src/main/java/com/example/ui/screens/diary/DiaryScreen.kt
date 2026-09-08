package com.example.ui.screens.diary

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.FoodItem
import com.example.data.model.MealType
import com.example.data.model.formatDateDisplay
import com.example.ui.components.MealCard
import com.example.ui.screens.home.DateSwitcher
import com.example.ui.theme.CarbsColor
import com.example.ui.theme.FatColor
import com.example.ui.theme.ProteinColor
import com.example.ui.viewmodel.NutriTrackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(
    viewModel: NutriTrackViewModel,
    onOpenAddFood: (MealType) -> Unit,
    onEditFoodItem: (FoodItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val summary by viewModel.dailySummary.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val totalFoodCount = summary.meals.sumOf { it.items.size }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Diario de Comidas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            val shareText = buildString {
                                appendLine("Micron — Diario de Nutrición (${formatDateDisplay(selectedDate)})")
                                appendLine("Calorías totales: ${summary.totalCalories.toInt()} / ${summary.goalCalories} kcal")
                                appendLine("Proteínas: ${summary.totalProtein.toInt()}g | Carbos: ${summary.totalCarbs.toInt()}g | Grasas: ${summary.totalFat.toInt()}g")
                                appendLine("----------------------------------------")
                                summary.meals.forEach { meal ->
                                    if (meal.items.isNotEmpty()) {
                                        appendLine("${meal.mealType.displayName.uppercase()} (${meal.totalCalories.toInt()} kcal):")
                                        meal.items.forEach { item ->
                                            appendLine(" • ${item.name} - ${item.quantity.toInt()} ${item.unit}: ${item.calories.toInt()} kcal (P:${item.protein.toInt()}g C:${item.carbs.toInt()}g G:${item.fat.toInt()}g)")
                                        }
                                        appendLine()
                                    }
                                }
                            }
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Diario Micron - $selectedDate")
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Compartir Diario"))
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir Diario")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onOpenAddFood(MealType.LUNCH) },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("diary_fab_add_food")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir alimento")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date Switcher
            item {
                DateSwitcher(
                    displayDate = formatDateDisplay(selectedDate),
                    onPrevious = { viewModel.goToPreviousDay() },
                    onNext = { viewModel.goToNextDay() },
                    onToday = { viewModel.goToToday() }
                )
            }

            // Daily Totals Summary Banner
            item {
                val isDark = isSystemInDarkTheme()
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, if (isDark) Color(0x1FFFFFFF) else Color(0x0A000000)),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTALES DEL DÍA",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$totalFoodCount alimentos",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "${summary.totalCalories.toInt()} kcal",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Objetivo: ${summary.goalCalories} kcal",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${summary.totalProtein.toInt()}g",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = ProteinColor
                                    )
                                    Text(
                                        text = "Proteína",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${summary.totalCarbs.toInt()}g",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = CarbsColor
                                    )
                                    Text(
                                        text = "Carbos",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${summary.totalFat.toInt()}g",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = FatColor
                                    )
                                    Text(
                                        text = "Grasa",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Meals Grouped Cards
            summary.meals.forEach { mealGroup ->
                item(key = "diary_${mealGroup.mealType.name}") {
                    MealCard(
                        mealGroup = mealGroup,
                        onAddClick = { onOpenAddFood(mealGroup.mealType) },
                        onEditItem = onEditFoodItem,
                        onDeleteItem = { viewModel.deleteFoodItem(it) },
                        onDuplicateItem = { viewModel.duplicateFoodItem(it) },
                        onMoveItem = { item, newType -> viewModel.moveFoodItemMealType(item, newType) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}
