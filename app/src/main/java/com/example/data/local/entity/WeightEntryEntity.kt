package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.WeightEntry

@Entity(tableName = "weight_entries")
data class WeightEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val weightKg: Double,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): WeightEntry = WeightEntry(
        id = id,
        date = date,
        weightKg = weightKg,
        note = note,
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(entry: WeightEntry): WeightEntryEntity = WeightEntryEntity(
            id = entry.id,
            date = entry.date,
            weightKg = entry.weightKg,
            note = entry.note,
            timestamp = entry.timestamp
        )
    }
}
