package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.FavoriteFoodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteFoodDao {
    @Query("SELECT * FROM favorite_foods WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteFoods(): Flow<List<FavoriteFoodEntity>>

    @Query("SELECT * FROM favorite_foods ORDER BY lastUsedTimestamp DESC LIMIT :limit")
    fun getRecentFoods(limit: Int = 20): Flow<List<FavoriteFoodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(food: FavoriteFoodEntity): Long

    @Update
    suspend fun update(food: FavoriteFoodEntity)

    @Query("DELETE FROM favorite_foods WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM favorite_foods WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun findByName(name: String): FavoriteFoodEntity?

    @Query("DELETE FROM favorite_foods")
    suspend fun clearAll()
}
