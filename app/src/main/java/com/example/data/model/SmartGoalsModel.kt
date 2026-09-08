package com.example.data.model

enum class GoalType(val displayName: String, val calorieDeltaFactor: Double) {
    MAINTAIN("Mantener peso", 1.0),
    BUILD_MUSCLE("Ganar masa muscular", 1.10),
    GAIN_WEIGHT("Ganar peso", 1.15),
    LOSE_FAT("Perder grasa", 0.82),
    DEFINITION("Definición", 0.85)
}

enum class GoalObjective(val displayName: String) {
    LOSE_WEIGHT("Perder peso/grasa"),
    MAINTAIN("Mantener peso"),
    GAIN_MUSCLE("Ganar masa muscular"),
    EAT_HEALTHY("Comer saludable")
}

enum class UserActivityLevel(val displayName: String, val multiplier: Double) {
    SEDENTARY("Sedentario (poco o ningún ejercicio)", 1.2),
    LIGHT("Ligero (ejercicio 1-3 días/semana)", 1.375),
    MODERATE("Moderado (ejercicio 3-5 días/semana)", 1.55),
    ACTIVE("Activo (ejercicio 5-6 días/semana)", 1.725),
    VERY_ACTIVE("Extremadamente activo (atleta/físico intenso)", 1.9)
}

enum class ActivityLevel(val displayName: String, val multiplier: Double) {
    SEDENTARY("Sedentario (poco o ningún ejercicio)", 1.2),
    LIGHT("Ligero (ejercicio 1-3 días/semana)", 1.375),
    MODERATE("Moderado (ejercicio 3-5 días/semana)", 1.55),
    ACTIVE("Muy activo (ejercicio 6-7 días/semana)", 1.725),
    VERY_ACTIVE("Extremadamente activo (atleta/físico intenso)", 1.9)
}

enum class Gender(val displayName: String) {
    MALE("Hombre"),
    FEMALE("Mujer")
}

data class UserPhysicalProfile(
    val gender: Gender = Gender.MALE,
    val age: Int = 28,
    val weightKg: Double = 75.0,
    val heightCm: Double = 175.0,
    val activityLevel: UserActivityLevel = UserActivityLevel.MODERATE,
    val objective: GoalObjective = GoalObjective.LOSE_WEIGHT
)

object SmartGoalsCalculator {
    fun calculate(profile: UserPhysicalProfile): NutritionGoals {
        val bmr = if (profile.gender == Gender.MALE) {
            10.0 * profile.weightKg + 6.25 * profile.heightCm - 5.0 * profile.age + 5.0
        } else {
            10.0 * profile.weightKg + 6.25 * profile.heightCm - 5.0 * profile.age - 161.0
        }

        val tdee = bmr * profile.activityLevel.multiplier

        val targetCalories = when (profile.objective) {
            GoalObjective.LOSE_WEIGHT -> (tdee * 0.82).toInt()
            GoalObjective.MAINTAIN, GoalObjective.EAT_HEALTHY -> tdee.toInt()
            GoalObjective.GAIN_MUSCLE -> (tdee * 1.10).toInt()
        }.coerceIn(1200, 4500)

        val proteinMultiplier = when (profile.objective) {
            GoalObjective.LOSE_WEIGHT -> 2.2
            GoalObjective.GAIN_MUSCLE -> 2.0
            GoalObjective.MAINTAIN, GoalObjective.EAT_HEALTHY -> 1.8
        }
        val protein = (profile.weightKg * proteinMultiplier).toInt().coerceIn(60, 280)
        val fat = (profile.weightKg * 0.95).toInt().coerceIn(40, 140)
        val carbCalories = (targetCalories - (protein * 4) - (fat * 9)).coerceAtLeast(200)
        val carbs = (carbCalories / 4).coerceIn(60, 600)

        return NutritionGoals(
            dailyCalories = targetCalories,
            dailyProtein = protein,
            dailyCarbs = carbs,
            dailyFat = fat
        )
    }
}

data class UserProfile(
    val name: String = "Usuario",
    val age: Int = 26,
    val gender: Gender = Gender.MALE,
    val heightCm: Double = 175.0,
    val weightKg: Double = 70.0,
    val targetWeightKg: Double = 68.0,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val goalType: GoalType = GoalType.MAINTAIN,
    val isAutoCalculateGoals: Boolean = true
) {
    fun calculateSmartGoals(): NutritionGoals {
        val bmr = if (gender == Gender.MALE) {
            10.0 * weightKg + 6.25 * heightCm - 5.0 * age + 5.0
        } else {
            10.0 * weightKg + 6.25 * heightCm - 5.0 * age - 161.0
        }

        val tdee = bmr * activityLevel.multiplier
        val targetCalories = (tdee * goalType.calorieDeltaFactor).toInt().coerceIn(1200, 4500)

        val proteinGramsPerKg = when (goalType) {
            GoalType.DEFINITION, GoalType.LOSE_FAT -> 2.2
            GoalType.BUILD_MUSCLE -> 2.0
            GoalType.MAINTAIN -> 1.8
            GoalType.GAIN_WEIGHT -> 1.8
        }
        val proteinGrams = (weightKg * proteinGramsPerKg).toInt().coerceIn(60, 300)
        val proteinCalories = proteinGrams * 4
        val fatGrams = (weightKg * 0.95).toInt().coerceIn(40, 150)
        val fatCalories = fatGrams * 9
        val remainingCaloriesForCarbs = (targetCalories - proteinCalories - fatCalories).coerceAtLeast(200)
        val carbsGrams = (remainingCaloriesForCarbs / 4).coerceIn(80, 600)

        return NutritionGoals(
            dailyCalories = targetCalories,
            dailyProtein = proteinGrams,
            dailyCarbs = carbsGrams,
            dailyFat = fatGrams
        )
    }
}
