package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiAnalysisResult
import com.example.data.ai.GeminiNutritionService
import com.example.data.local.AppDatabase
import com.example.data.local.entity.FavoriteFoodEntity
import com.example.data.model.AiDetectedFood
import com.example.data.model.DailyNutritionSummary
import com.example.data.model.FoodItem
import com.example.data.model.MealType
import com.example.data.model.NutritionGoals
import com.example.data.model.WeightEntry
import com.example.data.model.currentDateString
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.repository.NutritionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

sealed class AiAnalysisUiState {
    object Idle : AiAnalysisUiState()
    data class Loading(val message: String = "Analizando tu plato con IA...") : AiAnalysisUiState()
    data class Review(
        val foods: List<AiDetectedFood>,
        val photoBitmap: Bitmap? = null,
        val photoUri: String? = null,
        val note: String = "Estimación con IA — revisa porciones e ingredientes antes de guardar.",
        val isFallback: Boolean = false
    ) : AiAnalysisUiState()
    data class Error(val message: String) : AiAnalysisUiState()
}

enum class ProgressTimeframe(val label: String, val days: Int) {
    WEEK_1("7 Días", 7),
    DAYS_30("30 Días", 30),
    DAYS_90("90 Días", 90),
    ALL_TIME("Todo", 365)
}

data class DayNutrientPoint(
    val date: String,
    val dayLabel: String,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double
)

data class ProgressStatistics(
    val timeframe: ProgressTimeframe = ProgressTimeframe.WEEK_1,
    val dailyPoints: List<DayNutrientPoint> = emptyList(),
    val averageCalories: Double = 0.0,
    val averageProtein: Double = 0.0,
    val averageCarbs: Double = 0.0,
    val averageFat: Double = 0.0,
    val goalAdherencePercentage: Int = 0,
    val calorieProgressRatio: Float = 0f,
    val daysLoggedCount: Int = 0
)

class NutriTrackViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    val repository = NutritionRepository(
        mealDao = database.mealDao(),
        favoriteFoodDao = database.favoriteFoodDao(),
        weightDao = database.weightDao()
    )
    val preferences = UserPreferencesRepository(application)
    private val aiService = GeminiNutritionService()

    // Preferences state
    val isOnboardingCompleted = preferences.isOnboardingCompleted.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val nutritionGoals: StateFlow<NutritionGoals> = preferences.nutritionGoals.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NutritionGoals()
    )

    val themeMode = preferences.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "SYSTEM"
    )

    val remindersEnabled = preferences.remindersEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    // Current selected date
    private val _selectedDate = MutableStateFlow(currentDateString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // Daily summary for selected date
    val dailySummary: StateFlow<DailyNutritionSummary> = _selectedDate
        .flatMapLatest { date -> repository.getDailySummary(date, nutritionGoals) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DailyNutritionSummary(
                date = currentDateString(),
                totalCalories = 0.0,
                totalProtein = 0.0,
                totalCarbs = 0.0,
                totalFat = 0.0,
                goalCalories = 2200,
                goalProtein = 140,
                goalCarbs = 250,
                goalFat = 70
            )
        )

    // Favorites & Recents
    val favoriteFoods = repository.getFavoriteFoods().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentFoods = repository.getRecentFoods(25).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Weight tracking
    val weightEntries = repository.getAllWeightEntries().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val latestWeight = repository.getLatestWeight().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Progress Timeframe & Stats
    private val _selectedTimeframe = MutableStateFlow(ProgressTimeframe.WEEK_1)
    val selectedTimeframe: StateFlow<ProgressTimeframe> = _selectedTimeframe.asStateFlow()

    val progressStatistics: StateFlow<ProgressStatistics> = combine(
        _selectedTimeframe,
        repository.getAllItems(),
        nutritionGoals
    ) { timeframe, allItems, goals ->
        calculateProgressStats(timeframe, allItems, goals)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProgressStatistics()
    )

    val currentStreak: StateFlow<Int> = repository.getAllItems().map { allItems ->
        calculateCurrentStreak(allItems)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 1
    )

    private fun calculateCurrentStreak(items: List<FoodItem>): Int {
        if (items.isEmpty()) return 0
        val loggedDates = items.map { it.date }.toSet()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        var streak = 0

        // Check if logged today or yesterday to continue streak
        val todayStr = sdf.format(cal.time)
        val hasToday = loggedDates.contains(todayStr)
        if (!hasToday) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = sdf.format(cal.time)
            if (!loggedDates.contains(yesterdayStr)) {
                return 0
            }
        }

        // Count consecutive days
        cal.time = Date()
        if (!hasToday) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        while (true) {
            val dStr = sdf.format(cal.time)
            if (loggedDates.contains(dStr)) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }

    // AI Analysis UI State
    private val _aiAnalysisState = MutableStateFlow<AiAnalysisUiState>(AiAnalysisUiState.Idle)
    val aiAnalysisState: StateFlow<AiAnalysisUiState> = _aiAnalysisState.asStateFlow()

    fun selectDate(date: String) {
        _selectedDate.value = date
    }

    fun goToPreviousDay() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            val cal = Calendar.getInstance()
            cal.time = sdf.parse(_selectedDate.value) ?: Date()
            cal.add(Calendar.DAY_OF_YEAR, -1)
            _selectedDate.value = sdf.format(cal.time)
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun goToNextDay() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            val cal = Calendar.getInstance()
            cal.time = sdf.parse(_selectedDate.value) ?: Date()
            cal.add(Calendar.DAY_OF_YEAR, 1)
            _selectedDate.value = sdf.format(cal.time)
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun goToToday() {
        _selectedDate.value = currentDateString()
    }

    fun setTimeframe(timeframe: ProgressTimeframe) {
        _selectedTimeframe.value = timeframe
    }

    // AI Analysis
    fun analyzePhoto(bitmap: Bitmap, photoUri: String? = null) {
        viewModelScope.launch {
            _aiAnalysisState.value = AiAnalysisUiState.Loading("Scanning image...")
            kotlinx.coroutines.delay(400)
            _aiAnalysisState.value = AiAnalysisUiState.Loading("Identifying food ingredients...")
            kotlinx.coroutines.delay(500)
            _aiAnalysisState.value = AiAnalysisUiState.Loading("Estimating portion sizes & macros...")

            val result = aiService.analyzeMealPhoto(bitmap)
            when (result) {
                is AiAnalysisResult.Success -> {
                    _aiAnalysisState.value = AiAnalysisUiState.Review(
                        foods = result.foods,
                        photoBitmap = bitmap,
                        photoUri = photoUri,
                        note = result.note,
                        isFallback = result.isFallbackEstimate
                    )
                }
                is AiAnalysisResult.Error -> {
                    _aiAnalysisState.value = AiAnalysisUiState.Error(result.message)
                }
            }
        }
    }

    fun clearAiAnalysis() {
        _aiAnalysisState.value = AiAnalysisUiState.Idle
    }

    fun updateAiFoodItem(index: Int, updated: AiDetectedFood) {
        val current = _aiAnalysisState.value as? AiAnalysisUiState.Review ?: return
        val list = current.foods.toMutableList()
        if (index in list.indices) {
            list[index] = updated
            _aiAnalysisState.value = current.copy(foods = list)
        }
    }

    fun removeAiFoodItem(index: Int) {
        val current = _aiAnalysisState.value as? AiAnalysisUiState.Review ?: return
        val list = current.foods.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            _aiAnalysisState.value = current.copy(foods = list)
        }
    }

    fun duplicateAiFoodItem(index: Int) {
        val current = _aiAnalysisState.value as? AiAnalysisUiState.Review ?: return
        val list = current.foods.toMutableList()
        if (index in list.indices) {
            list.add(index + 1, list[index])
            _aiAnalysisState.value = current.copy(foods = list)
        }
    }

    fun addAiFoodItem(food: AiDetectedFood) {
        val current = _aiAnalysisState.value as? AiAnalysisUiState.Review ?: return
        val list = current.foods.toMutableList()
        list.add(food)
        _aiAnalysisState.value = current.copy(foods = list)
    }

    fun saveAiMeal(mealType: MealType, date: String = _selectedDate.value) {
        val current = _aiAnalysisState.value as? AiAnalysisUiState.Review ?: return
        viewModelScope.launch {
            val items = current.foods.map { detected ->
                FoodItem(
                    date = date,
                    mealType = mealType,
                    name = detected.name,
                    quantity = detected.estimatedQuantity,
                    unit = detected.unit,
                    calories = detected.estimatedCalories,
                    protein = detected.estimatedProtein,
                    carbs = detected.estimatedCarbs,
                    fat = detected.estimatedFat,
                    isAiEstimated = true,
                    photoUri = current.photoUri
                )
            }
            repository.saveFoodItems(items)
            _aiAnalysisState.value = AiAnalysisUiState.Idle
        }
    }

    // Food Entry Actions
    fun logFoodItem(item: FoodItem) {
        viewModelScope.launch {
            repository.saveFoodItem(item)
        }
    }

    fun updateFoodItem(item: FoodItem) {
        viewModelScope.launch {
            repository.updateFoodItem(item)
        }
    }

    fun deleteFoodItem(id: Long) {
        viewModelScope.launch {
            repository.deleteFoodItem(id)
        }
    }

    fun duplicateFoodItem(item: FoodItem) {
        viewModelScope.launch {
            val duplicated = item.copy(id = 0, timestamp = System.currentTimeMillis())
            repository.saveFoodItem(duplicated)
        }
    }

    fun moveFoodItemMealType(item: FoodItem, newMealType: MealType) {
        viewModelScope.launch {
            repository.updateFoodItem(item.copy(mealType = newMealType))
        }
    }

    // Favorites
    fun toggleFavorite(food: FavoriteFoodEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(food)
        }
    }

    fun deleteFavorite(id: Long) {
        viewModelScope.launch {
            repository.deleteFavorite(id)
        }
    }

    // Weight Logging
    fun logWeight(weightKg: Double, note: String = "") {
        viewModelScope.launch {
            repository.saveWeight(
                WeightEntry(
                    date = _selectedDate.value,
                    weightKg = weightKg,
                    note = note
                )
            )
        }
    }

    fun deleteWeight(id: Long) {
        viewModelScope.launch {
            repository.deleteWeight(id)
        }
    }

    // Goals & Preferences
    fun updateGoals(goals: NutritionGoals) {
        viewModelScope.launch {
            preferences.updateGoals(goals)
        }
    }

    fun completeOnboarding(goals: NutritionGoals) {
        viewModelScope.launch {
            preferences.updateGoals(goals)
            preferences.setOnboardingCompleted(true)
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            preferences.setThemeMode(mode)
        }
    }

    fun setRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setRemindersEnabled(enabled)
        }
    }

    fun resetGoals() {
        viewModelScope.launch {
            preferences.resetGoals()
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }

    private fun calculateProgressStats(
        timeframe: ProgressTimeframe,
        allItems: List<FoodItem>,
        goals: NutritionGoals
    ): ProgressStatistics {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        val today = Date()

        // Generate date list for timeframe
        val dayCount = if (timeframe == ProgressTimeframe.ALL_TIME) {
            // Find earliest date or default to 30
            30
        } else {
            timeframe.days
        }

        val dayPoints = mutableListOf<DayNutrientPoint>()
        var totalCalsInPeriod = 0.0
        var totalProteinInPeriod = 0.0
        var totalCarbsInPeriod = 0.0
        var totalFatInPeriod = 0.0
        var daysWithEntries = 0
        var adheredDays = 0

        for (i in (dayCount - 1) downTo 0) {
            cal.time = today
            cal.add(Calendar.DAY_OF_YEAR, -i)
            val dStr = sdf.format(cal.time)

            val itemsForDay = allItems.filter { it.date == dStr }
            val cals = itemsForDay.sumOf { it.calories }
            val protein = itemsForDay.sumOf { it.protein }
            val carbs = itemsForDay.sumOf { it.carbs }
            val fat = itemsForDay.sumOf { it.fat }

            val dayNameFormat = SimpleDateFormat("EEE", Locale("es", "ES"))
            val label = if (i == 0) "Hoy" else dayNameFormat.format(cal.time)

            dayPoints.add(
                DayNutrientPoint(
                    date = dStr,
                    dayLabel = label,
                    calories = cals,
                    protein = protein,
                    carbs = carbs,
                    fat = fat
                )
            )

            if (cals > 0) {
                daysWithEntries++
                totalCalsInPeriod += cals
                totalProteinInPeriod += protein
                totalCarbsInPeriod += carbs
                totalFatInPeriod += fat

                // Adherence: within +/- 15% of goal calories
                val ratio = cals / goals.dailyCalories
                if (ratio in 0.85..1.15) {
                    adheredDays++
                }
            }
        }

        val denominator = if (daysWithEntries > 0) daysWithEntries else 1
        val avgCals = Math.round(totalCalsInPeriod / denominator * 10.0) / 10.0
        val avgProt = Math.round(totalProteinInPeriod / denominator * 10.0) / 10.0
        val avgCarb = Math.round(totalCarbsInPeriod / denominator * 10.0) / 10.0
        val avgFat = Math.round(totalFatInPeriod / denominator * 10.0) / 10.0
        val adherence = if (daysWithEntries > 0) ((adheredDays.toFloat() / daysWithEntries) * 100).toInt() else 100

        return ProgressStatistics(
            timeframe = timeframe,
            dailyPoints = dayPoints,
            averageCalories = avgCals,
            averageProtein = avgProt,
            averageCarbs = avgCarb,
            averageFat = avgFat,
            goalAdherencePercentage = adherence,
            calorieProgressRatio = if (goals.dailyCalories > 0) (avgCals / goals.dailyCalories).toFloat() else 0f,
            daysLoggedCount = daysWithEntries
        )
    }
}
