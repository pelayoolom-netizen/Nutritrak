package com.example.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NutritionGoals
import com.example.data.model.SmartGoalsCalculator
import com.example.data.model.UserPhysicalProfile
import com.example.ui.theme.CarbsColor
import com.example.ui.theme.FatColor
import com.example.ui.theme.ProteinColor

enum class FitnessGoal(val title: String, val desc: String, val icon: ImageVector) {
    LOSE_WEIGHT("Perder Grasa", "Déficit moderado con alta ingesta de proteína", Icons.Default.Scale),
    MAINTAIN("Mantener Peso", "Balance energético para rendimiento y bienestar", Icons.Default.Spa),
    BUILD_MUSCLE("Ganar Músculo", "Superávit controlado con proteína óptima", Icons.Default.FitnessCenter),
    HEALTHIER("Comer Saludable", "Enfoque en nutrientes y comida real", Icons.Default.DirectionsRun)
}

enum class ActivityLevel(val title: String, val factor: Double) {
    SEDENTARY("Sedentario (trabajo de oficina)", 1.2),
    LIGHT("Ligero (1-2 entrenamientos/sem)", 1.375),
    MODERATE("Moderado (3-5 entrenamientos/sem)", 1.55),
    VERY_ACTIVE("Muy Activo (entrenamiento intenso)", 1.725)
}

@Composable
fun OnboardingScreen(
    onFinish: (NutritionGoals, Double?) -> Unit
) {
    var step by remember { mutableIntStateOf(0) }
    var selectedGoal by remember { mutableStateOf(FitnessGoal.LOSE_WEIGHT) }
    var activityLevel by remember { mutableStateOf(ActivityLevel.MODERATE) }

    var gender by remember { mutableStateOf("Hombre") }
    var ageStr by remember { mutableStateOf("28") }
    var heightStr by remember { mutableStateOf("175") }
    var weightStr by remember { mutableStateOf("75") }

    // Utiliza el calculador de SmartGoalsModel
    val calculatedGoals = remember(selectedGoal, activityLevel, gender, ageStr, heightStr, weightStr) {
        val age = ageStr.toIntOrNull() ?: 28
        val height = heightStr.toDoubleOrNull() ?: 175.0
        val weight = weightStr.toDoubleOrNull() ?: 75.0

        val smartGoal = when (selectedGoal) {
            FitnessGoal.LOSE_WEIGHT -> com.example.data.model.GoalObjective.LOSE_WEIGHT
            FitnessGoal.MAINTAIN -> com.example.data.model.GoalObjective.MAINTAIN
            FitnessGoal.BUILD_MUSCLE -> com.example.data.model.GoalObjective.GAIN_MUSCLE
            FitnessGoal.HEALTHIER -> com.example.data.model.GoalObjective.EAT_HEALTHY
        }

        val smartActivity = when (activityLevel) {
            ActivityLevel.SEDENTARY -> com.example.data.model.UserActivityLevel.SEDENTARY
            ActivityLevel.LIGHT -> com.example.data.model.UserActivityLevel.LIGHT
            ActivityLevel.MODERATE -> com.example.data.model.UserActivityLevel.MODERATE
            ActivityLevel.VERY_ACTIVE -> com.example.data.model.UserActivityLevel.VERY_ACTIVE
        }

        val profile = UserPhysicalProfile(
            gender = if (gender == "Hombre") com.example.data.model.Gender.MALE else com.example.data.model.Gender.FEMALE,
            age = age,
            weightKg = weight,
            heightCm = height,
            activityLevel = smartActivity,
            objective = smartGoal
        )
        SmartGoalsCalculator.calculate(profile)
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header con botón de Omitir
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Configuración Micron",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                TextButton(
                    onClick = {
                        onFinish(NutritionGoals(2100, 140, 240, 65), null)
                    },
                    modifier = Modifier.testTag("onboarding_skip_button")
                ) {
                    Text("Omitir")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step Content
            AnimatedContent(targetState = step, label = "OnboardingStep") { currentStep ->
                when (currentStep) {
                    0 -> {
                        // Paso 1: Objetivo
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "¿Cuál es tu objetivo nutricional?",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Ajustaremos tus calorías y macronutrientes recomendados en base a tu meta.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            FitnessGoal.entries.forEach { goal ->
                                val isSelected = selectedGoal == goal
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                                    ),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .clickable { selectedGoal = goal }
                                        .testTag("goal_option_${goal.name.lowercase()}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = goal.icon,
                                            contentDescription = null,
                                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = goal.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = goal.desc,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Paso 2: Datos Corporales y Actividad
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Tus datos físicos",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Permiten calcular tu metabolismo basal y gasto calórico con precisión científica.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = gender == "Hombre",
                                    onClick = { gender = "Hombre" },
                                    label = { Text("Hombre") },
                                    modifier = Modifier.weight(1f)
                                )
                                FilterChip(
                                    selected = gender == "Mujer",
                                    onClick = { gender = "Mujer" },
                                    label = { Text("Mujer") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = ageStr,
                                    onValueChange = { ageStr = it },
                                    label = { Text("Edad") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = heightStr,
                                    onValueChange = { heightStr = it },
                                    label = { Text("Altura (cm)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = weightStr,
                                    onValueChange = { weightStr = it },
                                    label = { Text("Peso (kg)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text("Nivel de actividad:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                            Spacer(modifier = Modifier.height(6.dp))

                            ActivityLevel.entries.forEach { lvl ->
                                val isSelected = activityLevel == lvl
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { activityLevel = lvl }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = lvl.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        // Paso 3: Recomendaciones Calculadas
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Tu Plan Nutricional Personalizado",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Diseñado para ${selectedGoal.title.lowercase()}.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Card(
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("OBJETIVO DIARIO DE CALORÍAS", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "${calculatedGoals.dailyCalories} kcal",
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("${calculatedGoals.dailyProtein}g", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = ProteinColor)
                                            Text("Proteínas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("${calculatedGoals.dailyCarbs}g", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = CarbsColor)
                                            Text("Carbos", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("${calculatedGoals.dailyFat}g", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = FatColor)
                                            Text("Grasas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Podrás ajustar estos objetivos en cualquier momento desde tu Perfil.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Navigation CTA Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (step > 0) {
                    TextButton(
                        onClick = { step-- },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Atrás")
                    }
                }

                Button(
                    onClick = {
                        if (step < 2) {
                            step++
                        } else {
                            val initialWeight = weightStr.toDoubleOrNull()
                            onFinish(calculatedGoals, initialWeight)
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(if (step > 0) 2f else 1f)
                        .height(50.dp)
                        .testTag("onboarding_next_button")
                ) {
                    Text(
                        text = if (step == 2) "Comenzar" else "Continuar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (step == 2) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null
                    )
                }
            }
        }
    }
}
