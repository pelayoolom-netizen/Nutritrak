package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.FoodItem
import com.example.data.model.MealType

@Entity(tableName = "meal_items")
data class MealItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val mealType: String,
    val name: String,
    val quantity: Double,
    val unit: String,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val isAiEstimated: Boolean = false,
    val photoUri: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): FoodItem = FoodItem(
        id = id,
        date = date,
        mealType = MealType.fromString(mealType),
        name = name,
        quantity = quantity,
        unit = unit,
        calories = calories,
        protein = protein,
        carbs = carbs,
        fat = fat,
        isAiEstimated = isAiEstimated,
        photoUri = photoUri,
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(item: FoodItem): MealItemEntity = MealItemEntity(
            id = item.id,
            date = item.date,
            mealType = item.mealType.name,
            name = item.name,
            quantity = item.quantity,
            unit = item.unit,
            calories = item.calories,
            protein = item.protein,
            carbs = item.carbs,
            fat = item.fat,
            isAiEstimated = item.isAiEstimated,
            photoUri = item.photoUri,
            timestamp = item.timestamp
        )
    }
}
