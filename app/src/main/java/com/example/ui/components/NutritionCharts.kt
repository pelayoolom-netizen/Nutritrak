package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WeightEntry
import com.example.ui.viewmodel.DayNutrientPoint
import java.util.Locale

@Composable
fun DailyCalorieBarChart(
    points: List<DayNutrientPoint>,
    targetCalories: Int,
    onDaySelected: ((DayNutrientPoint) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val displayPoints = remember(points) {
        if (points.size > 14) points.takeLast(14) else points
    }
    val isDark = isSystemInDarkTheme()

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, if (isDark) Color(0x1FFFFFFF) else Color(0x0A000000)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Consumo Calórico Diario",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Objetivo: $targetCalories kcal",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            val selectedPoint = selectedIndex?.let { displayPoints.getOrNull(it) }
            if (selectedPoint != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${selectedPoint.dayLabel} (${selectedPoint.date})",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${selectedPoint.calories.toInt()} kcal",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (displayPoints.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay comidas registradas en este período", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                val maxCal = remember(displayPoints, targetCalories) {
                    val maxVal = displayPoints.maxOfOrNull { it.calories } ?: targetCalories.toDouble()
                    Math.max(maxVal, targetCalories * 1.15).coerceAtLeast(100.0)
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .pointerInput(displayPoints) {
                            detectTapGestures { offset ->
                                val barSlotWidth = size.width / displayPoints.size
                                val index = (offset.x / barSlotWidth).toInt().coerceIn(0, displayPoints.lastIndex)
                                selectedIndex = index
                                onDaySelected?.invoke(displayPoints[index])
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height - 24.dp.toPx() // leave room for labels
                    val count = displayPoints.size
                    val slotWidth = width / count
                    val barWidth = (slotWidth * 0.55f).coerceIn(12.dp.toPx(), 28.dp.toPx())

                    // Target line
                    val targetY = height - ((targetCalories / maxCal) * height).toFloat()
                    drawLine(
                        color = Color(0x6610B981),
                        start = Offset(0f, targetY),
                        end = Offset(width, targetY),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f))
                    )

                    displayPoints.forEachIndexed { i, point ->
                        val centerX = (i * slotWidth) + (slotWidth / 2f)
                        val barHeight = ((point.calories / maxCal) * height).toFloat().coerceAtLeast(4f)
                        val barTop = height - barHeight
                        val isSelected = (i == selectedIndex)

                        val barBrush = if (point.calories >= targetCalories * 0.85 && point.calories <= targetCalories * 1.15) {
                            Brush.verticalGradient(listOf(Color(0xFF10B981), Color(0xFF059669)))
                        } else if (point.calories > targetCalories * 1.15) {
                            Brush.verticalGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706)))
                        } else {
                            Brush.verticalGradient(listOf(Color(0xFF60A5FA), Color(0xFF3B82F6)))
                        }

                        // Draw bar
                        drawRoundRect(
                            brush = barBrush,
                            topLeft = Offset(centerX - barWidth / 2f, barTop),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                            alpha = if (isSelected || selectedIndex == null) 1f else 0.5f
                        )

                        if (isSelected) {
                            drawRoundRect(
                                color = Color.White,
                                topLeft = Offset(centerX - barWidth / 2f - 2.dp.toPx(), barTop - 2.dp.toPx()),
                                size = Size(barWidth + 4.dp.toPx(), barHeight + 4.dp.toPx()),
                                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                    }
                }

                // X-axis day labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val step = if (displayPoints.size > 7) 2 else 1
                    displayPoints.forEachIndexed { idx, pt ->
                        if (idx % step == 0 || idx == displayPoints.lastIndex) {
                            Text(
                                text = pt.dayLabel.take(3),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (idx == selectedIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeightProgressLineChart(
    entries: List<WeightEntry>,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, if (isDark) Color(0x1FFFFFFF) else Color(0x0A000000)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val latest = entries.lastOrNull()?.weightKg
            val initial = entries.firstOrNull()?.weightKg
            val change = if (latest != null && initial != null) latest - initial else 0.0

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Evolución de Peso",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Actual: ${latest ?: "--"} kg",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (entries.size >= 2) {
                    val isLoss = change < 0
                    val changeStr = String.format(Locale.getDefault(), "%+.1f kg", change)
                    Box(
                        modifier = Modifier
                            .background(
                                if (isLoss) Color(0xFFD1FAE5) else Color(0xFFFEF3C7),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = changeStr,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isLoss) Color(0xFF065F46) else Color(0xFF92400E)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (entries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay registros de peso aún", style = MaterialTheme.typography.bodyMedium)
                }
            } else if (entries.size == 1) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Registra al menos 2 pesos para ver la curva de tendencia", style = MaterialTheme.typography.bodySmall)
                }
            } else {
                val minWeight = entries.minOf { it.weightKg } - 1.0
                val maxWeight = (entries.maxOf { it.weightKg } + 1.0).coerceAtLeast(minWeight + 2.0)

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    val width = size.width
                    val height = size.height - 20.dp.toPx()
                    val range = maxWeight - minWeight

                    val points = entries.mapIndexed { idx, item ->
                        val x = (idx.toFloat() / (entries.size - 1)) * width
                        val y = height - (((item.weightKg - minWeight) / range).toFloat() * height)
                        Offset(x, y)
                    }

                    // Fill under curve
                    val fillPath = Path().apply {
                        moveTo(points.first().x, height)
                        points.forEach { lineTo(it.x, it.y) }
                        lineTo(points.last().x, height)
                        close()
                    }

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            listOf(Color(0x336366F1), Color(0x006366F1))
                        )
                    )

                    // Line
                    val strokePath = Path().apply {
                        moveTo(points.first().x, points.first().y)
                        points.drop(1).forEach { lineTo(it.x, it.y) }
                    }

                    drawPath(
                        path = strokePath,
                        color = Color(0xFF6366F1),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Dots
                    points.forEach { pt ->
                        drawCircle(color = Color(0xFF6366F1), radius = 5.dp.toPx(), center = pt)
                        drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = pt)
                    }
                }
            }
        }
    }
}
