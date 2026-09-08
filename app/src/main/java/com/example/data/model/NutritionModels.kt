package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class MealType(val displayName: String) {
    BREAKFAST("Desayuno"),
    LUNCH("Comida"),
    SNACK("Merienda"),
    DINNER("Cena"),
    OTHER("Otros");

    companion object {
        fun fromString(value: String): MealType {
            return entries.firstOrNull { 
                it.name.equals(value, ignoreCase = true) || 
                it.displayName.equals(value, ignoreCase = true) ||
                // backwards compatibility with previous English names
                (value.equals("Breakfast", ignoreCase = true) && it == BREAKFAST) ||
                (value.equals("Lunch", ignoreCase = true) && it == LUNCH) ||
                (value.equals("Snack", ignoreCase = true) && it == SNACK) ||
                (value.equals("Dinner", ignoreCase = true) && it == DINNER) ||
                (value.equals("Other", ignoreCase = true) && it == OTHER)
            } ?: OTHER
        }
    }
}

data class NutritionGoals(
    val dailyCalories: Int = 2200,
    val dailyProtein: Int = 140,
    val dailyCarbs: Int = 250,
    val dailyFat: Int = 70
)

data class FoodItem(
    val id: Long = 0,
    val date: String = currentDateString(),
    val mealType: MealType = MealType.LUNCH,
    val name: String,
    val quantity: Double,
    val unit: String = "g",
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val isAiEstimated: Boolean = false,
    val photoUri: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class MealGroup(
    val mealType: MealType,
    val items: List<FoodItem>,
    val totalCalories: Double = items.sumOf { it.calories },
    val totalProtein: Double = items.sumOf { it.protein },
    val totalCarbs: Double = items.sumOf { it.carbs },
    val totalFat: Double = items.sumOf { it.fat }
)

data class DailyNutritionSummary(
    val date: String,
    val totalCalories: Double,
    val totalProtein: Double,
    val totalCarbs: Double,
    val totalFat: Double,
    val goalCalories: Int,
    val goalProtein: Int,
    val goalCarbs: Int,
    val goalFat: Int,
    val meals: List<MealGroup> = emptyList()
) {
    val remainingCalories: Double get() = (goalCalories - totalCalories).coerceAtLeast(0.0)
    val remainingProtein: Double get() = (goalProtein - totalProtein).coerceAtLeast(0.0)
    val remainingCarbs: Double get() = (goalCarbs - totalCarbs).coerceAtLeast(0.0)
    val remainingFat: Double get() = (goalFat - totalFat).coerceAtLeast(0.0)

    val calorieProgress: Float get() = if (goalCalories > 0) (totalCalories / goalCalories).toFloat().coerceIn(0f, 2f) else 0f
    val proteinProgress: Float get() = if (goalProtein > 0) (totalProtein / goalProtein).toFloat().coerceIn(0f, 2f) else 0f
    val carbsProgress: Float get() = if (goalCarbs > 0) (totalCarbs / goalCarbs).toFloat().coerceIn(0f, 2f) else 0f
    val fatProgress: Float get() = if (goalFat > 0) (totalFat / goalFat).toFloat().coerceIn(0f, 2f) else 0f
}

data class WeightEntry(
    val id: Long = 0,
    val date: String,
    val weightKg: Double,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class AiDetectedFood(
    val name: String,
    val estimatedQuantity: Double,
    val unit: String = "g",
    val estimatedCalories: Double,
    val estimatedProtein: Double,
    val estimatedCarbs: Double,
    val estimatedFat: Double,
    val confidence: String = "High",
    val baseQuantity: Double = estimatedQuantity,
    val baseCalories: Double = estimatedCalories,
    val baseProtein: Double = estimatedProtein,
    val baseCarbs: Double = estimatedCarbs,
    val baseFat: Double = estimatedFat
) {
    fun copyWithRecalculatedQuantity(newQuantity: Double): AiDetectedFood {
        if (baseQuantity <= 0.0) return copy(estimatedQuantity = newQuantity)
        val ratio = newQuantity / baseQuantity
        return copy(
            estimatedQuantity = newQuantity,
            estimatedCalories = Math.round(baseCalories * ratio * 10.0) / 10.0,
            estimatedProtein = Math.round(baseProtein * ratio * 10.0) / 10.0,
            estimatedCarbs = Math.round(baseCarbs * ratio * 10.0) / 10.0,
            estimatedFat = Math.round(baseFat * ratio * 10.0) / 10.0
        )
    }
}

fun currentDateString(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date())
}

fun formatDateDisplay(dateStr: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = parser.parse(dateStr) ?: return dateStr
        val today = currentDateString()
        if (dateStr == today) return "Hoy"
        val spanishLocale = Locale("es", "ES")
        val formatter = SimpleDateFormat("EEEE, d 'de' MMMM", spanishLocale)
        val formatted = formatter.format(date)
        formatted.replaceFirstChar { if (it.isLowerCase()) it.titlecase(spanishLocale) else it.toString() }
    } catch (e: Exception) {
        dateStr
    }
}
