package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CalAiGreen
import com.example.ui.theme.CalAiGreenLight
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CircularCalorieProgress(
    consumedCalories: Double,
    targetCalories: Int,
    remainingCalories: Double,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val progress = if (targetCalories > 0) {
        (consumedCalories / targetCalories).toFloat().coerceIn(0f, 1f)
    } else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "CalorieProgress"
    )

    val numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY) // Formats with dot, e.g. 1.245

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Circular Ring with CAL AI typography
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(210.dp)
                .testTag("circular_calorie_progress")
        ) {
            Canvas(modifier = Modifier.size(195.dp)) {
                val strokeWidth = 14.dp.toPx()

                // Subtle Background Track
                drawArc(
                    color = if (isDark) Color(0x24FFFFFF) else Color(0x14000000),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // CAL AI Vibrant Gradient Progress Arc
                if (animatedProgress > 0f) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                CalAiGreen,
                                CalAiGreenLight,
                                CalAiGreen,
                                Color(0xFF10B981),
                                CalAiGreen
                            )
                        ),
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
            }

            // CAL AI Center Typography: Big Number + "kcal consumidas"
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = numberFormat.format(consumedCalories.toInt()),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = (-1.0).sp
                )
                Text(
                    text = "kcal consumidas",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // CAL AI Quick Glance Target & Remaining Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Target Goal
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${numberFormat.format(targetCalories)} kcal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Objetivo diario",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Divider Dot
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            )

            // Remaining Calories Pill
            val isExcess = remainingCalories < 0
            val remainingColor = if (isExcess) MaterialTheme.colorScheme.error else CalAiGreen
            val remainingBg = if (isExcess) MaterialTheme.colorScheme.error.copy(alpha = 0.12f) else CalAiGreen.copy(alpha = 0.12f)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(remainingBg)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = if (isExcess) {
                        "+${numberFormat.format(Math.abs(remainingCalories.toInt()))} kcal exceso"
                    } else {
                        "${numberFormat.format(remainingCalories.toInt())} kcal restantes"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = remainingColor
                )
            }
        }
    }
}
