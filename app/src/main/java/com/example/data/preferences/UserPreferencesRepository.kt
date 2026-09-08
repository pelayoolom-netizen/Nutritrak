package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.NutritionGoals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val GOAL_CALORIES = intPreferencesKey("goal_calories")
        val GOAL_PROTEIN = intPreferencesKey("goal_protein")
        val GOAL_CARBS = intPreferencesKey("goal_carbs")
        val GOAL_FAT = intPreferencesKey("goal_fat")
        val THEME_MODE = stringPreferencesKey("theme_mode") // "SYSTEM", "LIGHT", "DARK"
        val REMINDERS_ENABLED = booleanPreferencesKey("reminders_enabled")
        val UNIT_SYSTEM = stringPreferencesKey("unit_system") // "METRIC", "IMPERIAL"
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
    }

    val nutritionGoals: Flow<NutritionGoals> = context.dataStore.data.map { preferences ->
        NutritionGoals(
            dailyCalories = preferences[PreferencesKeys.GOAL_CALORIES] ?: 2200,
            dailyProtein = preferences[PreferencesKeys.GOAL_PROTEIN] ?: 140,
            dailyCarbs = preferences[PreferencesKeys.GOAL_CARBS] ?: 250,
            dailyFat = preferences[PreferencesKeys.GOAL_FAT] ?: 70
        )
    }

    val themeMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.THEME_MODE] ?: "SYSTEM"
    }

    val remindersEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.REMINDERS_ENABLED] ?: false
    }

    val unitSystem: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.UNIT_SYSTEM] ?: "METRIC"
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun updateGoals(goals: NutritionGoals) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.GOAL_CALORIES] = goals.dailyCalories
            preferences[PreferencesKeys.GOAL_PROTEIN] = goals.dailyProtein
            preferences[PreferencesKeys.GOAL_CARBS] = goals.dailyCarbs
            preferences[PreferencesKeys.GOAL_FAT] = goals.dailyFat
        }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode
        }
    }

    suspend fun setRemindersEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.REMINDERS_ENABLED] = enabled
        }
    }

    suspend fun setUnitSystem(system: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.UNIT_SYSTEM] = system
        }
    }

    suspend fun resetGoals() {
        updateGoals(NutritionGoals(2200, 140, 250, 70))
    }
}
