package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CarbsColor
import com.example.ui.theme.CarbsContainer
import com.example.ui.theme.FatColor
import com.example.ui.theme.FatContainer
import com.example.ui.theme.ProteinColor
import com.example.ui.theme.ProteinContainer

@Composable
fun MacroNutrientBar(
    name: String,
    current: Double,
    target: Int,
    remaining: Double,
    barColor: Color,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val progress = if (target > 0) (current / target).toFloat().coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing),
        label = "${name}Progress"
    )

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) {
                MaterialTheme.colorScheme.surface
            } else {
                Color.White
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp),
        modifier = modifier
            .border(
                width = 1.dp,
                color = if (isDark) Color(0x1FFFFFFF) else Color(0x0D000000),
                shape = RoundedCornerShape(22.dp)
            )
            .testTag("macro_card_${name.lowercase()}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Header Row: Name & remaining
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = barColor
                )
                Text(
                    text = "${remaining.toInt()}g rest.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Numbers: Big Current + Target
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "${current.toInt()}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = " / ${target}g",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 2.dp, bottom = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Ultra-minimalist sleek progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(barColor.copy(alpha = if (isDark) 0.18f else 0.12f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(barColor)
                )
            }
        }
    }
}

@Composable
fun MacroRow(
    currentProtein: Double,
    targetProtein: Int,
    remainingProtein: Double,
    currentCarbs: Double,
    targetCarbs: Int,
    remainingCarbs: Double,
    currentFat: Double,
    targetFat: Int,
    remainingFat: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MacroNutrientBar(
            name = "Proteínas",
            current = currentProtein,
            target = targetProtein,
            remaining = remainingProtein,
            barColor = ProteinColor,
            containerColor = ProteinContainer,
            modifier = Modifier.weight(1f)
        )
        MacroNutrientBar(
            name = "Carbos",
            current = currentCarbs,
            target = targetCarbs,
            remaining = remainingCarbs,
            barColor = CarbsColor,
            containerColor = CarbsContainer,
            modifier = Modifier.weight(1f)
        )
        MacroNutrientBar(
            name = "Grasas",
            current = currentFat,
            target = targetFat,
            remaining = remainingFat,
            barColor = FatColor,
            containerColor = FatContainer,
            modifier = Modifier.weight(1f)
        )
    }
}
