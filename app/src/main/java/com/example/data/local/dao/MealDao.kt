package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.MealItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("SELECT * FROM meal_items WHERE date = :date ORDER BY timestamp ASC")
    fun getItemsForDate(date: String): Flow<List<MealItemEntity>>

    @Query("SELECT * FROM meal_items ORDER BY timestamp DESC")
    fun getAllItems(): Flow<List<MealItemEntity>>

    @Query("SELECT * FROM meal_items WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC, timestamp ASC")
    fun getItemsBetweenDates(startDate: String, endDate: String): Flow<List<MealItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: MealItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<MealItemEntity>): List<Long>

    @Update
    suspend fun updateItem(item: MealItemEntity)

    @Query("DELETE FROM meal_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    @Query("SELECT * FROM meal_items WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: Long): MealItemEntity?

    @Query("DELETE FROM meal_items")
    suspend fun clearAll()
}
