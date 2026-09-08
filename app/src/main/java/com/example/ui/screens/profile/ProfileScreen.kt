package com.example.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GoalObjective
import com.example.data.model.NutritionGoals
import com.example.data.model.SmartGoalsCalculator
import com.example.data.model.UserActivityLevel
import com.example.data.model.UserPhysicalProfile
import com.example.ui.theme.CarbsColor
import com.example.ui.theme.FatColor
import com.example.ui.theme.ProteinColor
import com.example.ui.viewmodel.NutriTrackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: NutriTrackViewModel,
    modifier: Modifier = Modifier
) {
    val goals by viewModel.nutritionGoals.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val remindersEnabled by viewModel.remindersEnabled.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    var showGoalsDialog by remember { mutableStateOf(false) }
    var showSmartGoalsDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Perfil y Ajustes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
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
            // Daily Targets Card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, if (isDark) Color(0x1FFFFFFF) else Color(0x0A000000)),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "OBJETIVOS NUTRICIONALES DIARIOS",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row {
                                IconButton(
                                    onClick = { showSmartGoalsDialog = true },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Calculate, contentDescription = "Calculadora Inteligente", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(
                                    onClick = { showGoalsDialog = true },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("edit_goals_button")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar objetivos", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "${goals.dailyCalories} kcal",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Macro targets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "${goals.dailyProtein}g",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = ProteinColor
                                )
                                Text(
                                    text = "Proteínas (${((goals.dailyProtein * 4.0 / goals.dailyCalories) * 100).toInt()}%)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Column {
                                Text(
                                    text = "${goals.dailyCarbs}g",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = CarbsColor
                                )
                                Text(
                                    text = "Carbos (${((goals.dailyCarbs * 4.0 / goals.dailyCalories) * 100).toInt()}%)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Column {
                                Text(
                                    text = "${goals.dailyFat}g",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = FatColor
                                )
                                Text(
                                    text = "Grasas (${((goals.dailyFat * 9.0 / goals.dailyCalories) * 100).toInt()}%)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // AI Vision Engine Info Card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    ),
                    border = BorderStroke(1.dp, if (isDark) Color(0x1FFFFFFF) else Color(0x0A000000)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Motor de Visión IA Gemini",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Detección multimodal de ingredientes, porciones y macros.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // App Preferences Card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, if (isDark) Color(0x1FFFFFFF) else Color(0x0A000000)),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "PREFERENCIAS DE LA APP",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dark Mode Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (themeMode == "DARK") Icons.Default.DarkMode else Icons.Default.LightMode,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Tema visual", fontWeight = FontWeight.SemiBold)
                                    Text(
                                        text = when (themeMode) {
                                            "DARK" -> "Modo oscuro"
                                            "LIGHT" -> "Modo claro"
                                            else -> "Predeterminado del sistema"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                TextButton(onClick = { viewModel.setThemeMode("SYSTEM") }) {
                                    Text("Auto", style = MaterialTheme.typography.labelSmall)
                                }
                                TextButton(onClick = { viewModel.setThemeMode("LIGHT") }) {
                                    Text("Claro", style = MaterialTheme.typography.labelSmall)
                                }
                                TextButton(onClick = { viewModel.setThemeMode("DARK") }) {
                                    Text("Oscuro", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        )

                        // Reminders
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Recordatorios de comidas", fontWeight = FontWeight.SemiBold)
                                    Text("Aviso diario para registrar comidas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Switch(
                                checked = remindersEnabled,
                                onCheckedChange = { viewModel.setRemindersEnabled(it) }
                            )
                        }
                    }
                }
            }

            // Privacy & Data Storage Card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, if (isDark) Color(0x1FFFFFFF) else Color(0x0A000000)),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF10B981))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Privacidad y Almacenamiento Local",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Tus registros, alimentos, fotos y peso se guardan exclusivamente en tu dispositivo usando base de datos Room local. No se requiere cuenta de terceros.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(
                                onClick = {
                                    viewModel.resetGoals()
                                    Toast.makeText(context, "Objetivos restaurados por defecto", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Default.RestartAlt, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Restaurar")
                            }

                            TextButton(
                                onClick = { showClearDataDialog = true },
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Icon(Icons.Default.DeleteForever, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Borrar Datos")
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Edit Goals Dialog
    if (showGoalsDialog) {
        var calInput by remember { mutableStateOf(goals.dailyCalories.toString()) }
        var protInput by remember { mutableStateOf(goals.dailyProtein.toString()) }
        var carbInput by remember { mutableStateOf(goals.dailyCarbs.toString()) }
        var fatInput by remember { mutableStateOf(goals.dailyFat.toString()) }

        AlertDialog(
            onDismissRequest = { showGoalsDialog = false },
            title = { Text("Establecer Objetivos Nutricionales", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = calInput,
                        onValueChange = { calInput = it },
                        label = { Text("Calorías diarias (kcal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("goal_calories_input")
                    )

                    OutlinedTextField(
                        value = protInput,
                        onValueChange = { protInput = it },
                        label = { Text("Proteínas diarias (g)", color = ProteinColor) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = carbInput,
                        onValueChange = { carbInput = it },
                        label = { Text("Carbohidratos diarios (g)", color = CarbsColor) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = fatInput,
                        onValueChange = { fatInput = it },
                        label = { Text("Grasas diarias (g)", color = FatColor) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newGoals = NutritionGoals(
                            dailyCalories = calInput.toIntOrNull() ?: goals.dailyCalories,
                            dailyProtein = protInput.toIntOrNull() ?: goals.dailyProtein,
                            dailyCarbs = carbInput.toIntOrNull() ?: goals.dailyCarbs,
                            dailyFat = fatInput.toIntOrNull() ?: goals.dailyFat
                        )
                        viewModel.updateGoals(newGoals)
                        showGoalsDialog = false
                    },
                    modifier = Modifier.testTag("save_goals_button")
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoalsDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // Smart Goals Dialog (Mifflin-St Jeor)
    if (showSmartGoalsDialog) {
        var weightInput by remember { mutableStateOf("75") }
        var heightInput by remember { mutableStateOf("175") }
        var ageInput by remember { mutableStateOf("28") }
        var isMale by remember { mutableStateOf(true) }
        var selectedObjective by remember { mutableStateOf(GoalObjective.LOSE_WEIGHT) }

        AlertDialog(
            onDismissRequest = { showSmartGoalsDialog = false },
            title = { Text("Calculadora de Metas Inteligente", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Calcula automáticamente tus calorías y macronutrientes usando la fórmula Mifflin-St Jeor.", style = MaterialTheme.typography.bodySmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(onClick = { isMale = true }, modifier = Modifier.weight(1f)) {
                            Text(if (isMale) "✓ Hombre" else "Hombre")
                        }
                        FilledTonalButton(onClick = { isMale = false }, modifier = Modifier.weight(1f)) {
                            Text(if (!isMale) "✓ Mujer" else "Mujer")
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = weightInput, onValueChange = { weightInput = it }, label = { Text("Peso (kg)") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = heightInput, onValueChange = { heightInput = it }, label = { Text("Altura (cm)") }, modifier = Modifier.weight(1f))
                    }
                    OutlinedTextField(value = ageInput, onValueChange = { ageInput = it }, label = { Text("Edad (años)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val profile = UserPhysicalProfile(
                            gender = if (isMale) com.example.data.model.Gender.MALE else com.example.data.model.Gender.FEMALE,
                            age = ageInput.toIntOrNull() ?: 28,
                            weightKg = weightInput.toDoubleOrNull() ?: 75.0,
                            heightCm = heightInput.toDoubleOrNull() ?: 175.0,
                            activityLevel = UserActivityLevel.MODERATE,
                            objective = selectedObjective
                        )
                        val calculated = SmartGoalsCalculator.calculate(profile)
                        viewModel.updateGoals(calculated)
                        showSmartGoalsDialog = false
                        Toast.makeText(context, "Objetivos calculados actualizados", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Aplicar Cálculo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSmartGoalsDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // Clear Data Confirmation Dialog
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("¿Eliminar todos los datos?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) },
            text = {
                Text("Se eliminarán de forma permanente todas las comidas registradas, alimentos personalizados y pesos de la base de datos local. Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDataDialog = false
                        Toast.makeText(context, "Todos los datos han sido borrados", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Borrar Todo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) { Text("Cancelar") }
            }
        )
    }
}
