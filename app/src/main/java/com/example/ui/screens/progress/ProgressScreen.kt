package com.example.ui.screens.progress

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.DailyCalorieBarChart
import com.example.ui.components.WeightProgressLineChart
import com.example.ui.theme.CalorieColor
import com.example.ui.theme.CarbsColor
import com.example.ui.theme.FatColor
import com.example.ui.theme.ProteinColor
import com.example.ui.viewmodel.NutriTrackViewModel
import com.example.ui.viewmodel.ProgressTimeframe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    viewModel: NutriTrackViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.progressStatistics.collectAsStateWithLifecycle()
    val timeframe by viewModel.selectedTimeframe.collectAsStateWithLifecycle()
    val goals by viewModel.nutritionGoals.collectAsStateWithLifecycle()
    val weightEntries by viewModel.weightEntries.collectAsStateWithLifecycle()
    val latestWeight by viewModel.latestWeight.collectAsStateWithLifecycle()

    var showWeightDialog by remember { mutableStateOf(false) }
    var showWeightHistoryDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Progreso y Estadísticas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    FilledTonalButton(
                        onClick = { showWeightDialog = true },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("log_weight_action_button")
                    ) {
                        Icon(Icons.Default.MonitorWeight, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Registrar Peso")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Timeframe Segmented Switcher
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ProgressTimeframe.entries.forEach { tf ->
                        FilterChip(
                            selected = timeframe == tf,
                            onClick = { viewModel.setTimeframe(tf) },
                            label = { Text(tf.label, fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Key Progress Metrics Grid
            item {
                val isDark = isSystemInDarkTheme()
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, if (isDark) Color(0x1FFFFFFF) else Color(0x0A000000)),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "PROMEDIOS DEL PERÍODO (${timeframe.label})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MetricStatBox(
                                label = "Media Calorías",
                                value = "${stats.averageCalories.toInt()} kcal",
                                color = CalorieColor,
                                modifier = Modifier.weight(1f)
                            )
                            MetricStatBox(
                                label = "Cumplimiento",
                                value = "${stats.goalAdherencePercentage}%",
                                color = Color(0xFF10B981),
                                modifier = Modifier.weight(1f)
                            )
                            MetricStatBox(
                                label = "Días Activo",
                                value = "${stats.daysLoggedCount} d",
                                color = Color(0xFF6366F1),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MetricStatBox(
                                label = "Media Proteína",
                                value = "${stats.averageProtein.toInt()}g",
                                color = ProteinColor,
                                modifier = Modifier.weight(1f)
                            )
                            MetricStatBox(
                                label = "Media Carbos",
                                value = "${stats.averageCarbs.toInt()}g",
                                color = CarbsColor,
                                modifier = Modifier.weight(1f)
                            )
                            MetricStatBox(
                                label = "Media Grasa",
                                value = "${stats.averageFat.toInt()}g",
                                color = FatColor,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Interactive Daily Calorie Bar Chart
            item {
                DailyCalorieBarChart(
                    points = stats.dailyPoints,
                    targetCalories = goals.dailyCalories
                )
            }

            // Weight Tracking Chart
            item {
                WeightProgressLineChart(entries = weightEntries)
            }

            // Weight History & Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Registros de Peso (${weightEntries.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { showWeightHistoryDialog = true }) {
                        Text("Gestionar")
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Weight Logging Dialog
    if (showWeightDialog) {
        var weightInput by remember { mutableStateOf(latestWeight?.weightKg?.toString() ?: "70.0") }
        var noteInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showWeightDialog = false },
            title = { Text("Registrar Peso Corporal", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Peso (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("weight_input_field")
                    )
                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        label = { Text("Nota (opcional, ej. en ayunas)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val w = weightInput.toDoubleOrNull()
                        if (w != null && w > 0) {
                            viewModel.logWeight(w, noteInput.trim())
                            showWeightDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_log_weight_button")
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWeightDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // Manage Weight History Dialog
    if (showWeightHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showWeightHistoryDialog = false },
            title = { Text("Historial de Pesajes", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.height(240.dp)) {
                    items(weightEntries.size) { idx ->
                        val entry = weightEntries[weightEntries.size - 1 - idx]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${entry.weightKg} kg",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${entry.date}${if (entry.note.isNotEmpty()) " • ${entry.note}" else ""}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { viewModel.deleteWeight(entry.id) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showWeightHistoryDialog = false }) { Text("Listo") }
            }
        )
    }
}

@Composable
private fun MetricStatBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
