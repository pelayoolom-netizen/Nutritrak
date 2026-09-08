package com.example.data.repository

import com.example.data.local.dao.FavoriteFoodDao
import com.example.data.local.dao.MealDao
import com.example.data.local.dao.WeightDao
import com.example.data.local.entity.FavoriteFoodEntity
import com.example.data.local.entity.MealItemEntity
import com.example.data.local.entity.WeightEntryEntity
import com.example.data.model.DailyNutritionSummary
import com.example.data.model.FoodItem
import com.example.data.model.MealGroup
import com.example.data.model.MealType
import com.example.data.model.NutritionGoals
import com.example.data.model.WeightEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class NutritionRepository(
    private val mealDao: MealDao,
    private val favoriteFoodDao: FavoriteFoodDao,
    private val weightDao: WeightDao
) {
    fun getDailySummary(date: String, goalsFlow: Flow<NutritionGoals>): Flow<DailyNutritionSummary> {
        return combine(mealDao.getItemsForDate(date), goalsFlow) { entities, goals ->
            val items = entities.map { it.toDomain() }
            val groups = MealType.entries.map { type ->
                val mealItems = items.filter { it.mealType == type }
                MealGroup(mealType = type, items = mealItems)
            }

            DailyNutritionSummary(
                date = date,
                totalCalories = items.sumOf { it.calories },
                totalProtein = items.sumOf { it.protein },
                totalCarbs = items.sumOf { it.carbs },
                totalFat = items.sumOf { it.fat },
                goalCalories = goals.dailyCalories,
                goalProtein = goals.dailyProtein,
                goalCarbs = goals.dailyCarbs,
                goalFat = goals.dailyFat,
                meals = groups
            )
        }
    }

    fun getAllItems(): Flow<List<FoodItem>> {
        return mealDao.getAllItems().map { list -> list.map { it.toDomain() } }
    }

    fun getItemsBetweenDates(startDate: String, endDate: String): Flow<List<FoodItem>> {
        return mealDao.getItemsBetweenDates(startDate, endDate).map { list -> list.map { it.toDomain() } }
    }

    suspend fun saveFoodItem(item: FoodItem): Long {
        val id = mealDao.insertItem(MealItemEntity.fromDomain(item))
        // Also update or add to recent foods
        val existing = favoriteFoodDao.findByName(item.name)
        if (existing != null) {
            favoriteFoodDao.update(
                existing.copy(
                    lastUsedTimestamp = System.currentTimeMillis(),
                    calories = item.calories,
                    protein = item.protein,
                    carbs = item.carbs,
                    fat = item.fat,
                    defaultQuantity = item.quantity,
                    unit = item.unit
                )
            )
        } else {
            favoriteFoodDao.insertOrUpdate(
                FavoriteFoodEntity(
                    name = item.name,
                    defaultQuantity = item.quantity,
                    unit = item.unit,
                    calories = item.calories,
                    protein = item.protein,
                    carbs = item.carbs,
                    fat = item.fat,
                    isFavorite = false,
                    lastUsedTimestamp = System.currentTimeMillis()
                )
            )
        }
        return id
    }

    suspend fun saveFoodItems(items: List<FoodItem>): List<Long> {
        val entities = items.map { MealItemEntity.fromDomain(it) }
        val ids = mealDao.insertItems(entities)
        for (item in items) {
            val existing = favoriteFoodDao.findByName(item.name)
            if (existing != null) {
                favoriteFoodDao.update(existing.copy(lastUsedTimestamp = System.currentTimeMillis()))
            } else {
                favoriteFoodDao.insertOrUpdate(
                    FavoriteFoodEntity(
                        name = item.name,
                        defaultQuantity = item.quantity,
                        unit = item.unit,
                        calories = item.calories,
                        protein = item.protein,
                        carbs = item.carbs,
                        fat = item.fat,
                        isFavorite = false,
                        lastUsedTimestamp = System.currentTimeMillis()
                    )
                )
            }
        }
        return ids
    }

    suspend fun updateFoodItem(item: FoodItem) {
        mealDao.updateItem(MealItemEntity.fromDomain(item))
    }

    suspend fun deleteFoodItem(id: Long) {
        mealDao.deleteItemById(id)
    }

    // Favorites & Recents
    fun getFavoriteFoods(): Flow<List<FavoriteFoodEntity>> = favoriteFoodDao.getFavoriteFoods()
    fun getRecentFoods(limit: Int = 20): Flow<List<FavoriteFoodEntity>> = favoriteFoodDao.getRecentFoods(limit)

    suspend fun toggleFavorite(food: FavoriteFoodEntity) {
        favoriteFoodDao.update(food.copy(isFavorite = !food.isFavorite))
    }

    suspend fun addCustomFavorite(food: FavoriteFoodEntity): Long {
        return favoriteFoodDao.insertOrUpdate(food)
    }

    suspend fun deleteFavorite(id: Long) {
        favoriteFoodDao.deleteById(id)
    }

    // Weight
    fun getAllWeightEntries(): Flow<List<WeightEntry>> = weightDao.getAllEntries().map { list ->
        list.map { it.toDomain() }
    }

    fun getLatestWeight(): Flow<WeightEntry?> = weightDao.getLatestEntry().map { it?.toDomain() }

    suspend fun saveWeight(entry: WeightEntry): Long {
        return weightDao.insert(WeightEntryEntity.fromDomain(entry))
    }

    suspend fun deleteWeight(id: Long) {
        weightDao.deleteById(id)
    }

    suspend fun clearAllData() {
        mealDao.clearAll()
        weightDao.clearAll()
        favoriteFoodDao.clearAll()
    }
}
