package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_foods")
data class FavoriteFoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val defaultQuantity: Double = 100.0,
    val unit: String = "g",
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val isFavorite: Boolean = false,
    val lastUsedTimestamp: Long = System.currentTimeMillis()
)
