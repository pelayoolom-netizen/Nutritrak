package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.WeightEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {
    @Query("SELECT * FROM weight_entries ORDER BY date ASC, timestamp ASC")
    fun getAllEntries(): Flow<List<WeightEntryEntity>>

    @Query("SELECT * FROM weight_entries ORDER BY date DESC, timestamp DESC LIMIT 1")
    fun getLatestEntry(): Flow<WeightEntryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: WeightEntryEntity): Long

    @Query("DELETE FROM weight_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM weight_entries")
    suspend fun clearAll()
}
