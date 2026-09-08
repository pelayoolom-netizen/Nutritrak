package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.FavoriteFoodDao
import com.example.data.local.dao.MealDao
import com.example.data.local.dao.WeightDao
import com.example.data.local.entity.FavoriteFoodEntity
import com.example.data.local.entity.MealItemEntity
import com.example.data.local.entity.WeightEntryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        MealItemEntity::class,
        FavoriteFoodEntity::class,
        WeightEntryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun favoriteFoodDao(): FavoriteFoodDao
    abstract fun weightDao(): WeightDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nutritrack_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialFoods(database.favoriteFoodDao(), database.weightDao())
                    }
                }
            }
        }

        private suspend fun populateInitialFoods(favoriteDao: FavoriteFoodDao, weightDao: WeightDao) {
            val starterFoods = listOf(
                FavoriteFoodEntity(name = "Oatmeal with Berries", defaultQuantity = 200.0, unit = "g", calories = 220.0, protein = 8.0, carbs = 42.0, fat = 3.5, isFavorite = true),
                FavoriteFoodEntity(name = "Boiled Eggs (2 large)", defaultQuantity = 100.0, unit = "g", calories = 144.0, protein = 12.6, carbs = 0.8, fat = 9.8, isFavorite = true),
                FavoriteFoodEntity(name = "Grilled Chicken Breast", defaultQuantity = 150.0, unit = "g", calories = 248.0, protein = 46.0, carbs = 0.0, fat = 5.0, isFavorite = true),
                FavoriteFoodEntity(name = "Brown Jasmine Rice", defaultQuantity = 180.0, unit = "g", calories = 216.0, protein = 4.5, carbs = 46.0, fat = 1.8, isFavorite = true),
                FavoriteFoodEntity(name = "Avocado (Half)", defaultQuantity = 80.0, unit = "g", calories = 130.0, protein = 1.6, carbs = 6.8, fat = 11.8, isFavorite = true),
                FavoriteFoodEntity(name = "Greek Yogurt (Plain)", defaultQuantity = 170.0, unit = "g", calories = 100.0, protein = 17.0, carbs = 6.0, fat = 0.7, isFavorite = true),
                FavoriteFoodEntity(name = "Atlantic Salmon Fillet", defaultQuantity = 170.0, unit = "g", calories = 350.0, protein = 34.0, carbs = 0.0, fat = 22.0, isFavorite = true),
                FavoriteFoodEntity(name = "Sweet Potato", defaultQuantity = 150.0, unit = "g", calories = 130.0, protein = 2.4, carbs = 30.0, fat = 0.2, isFavorite = false),
                FavoriteFoodEntity(name = "Mixed Raw Nuts", defaultQuantity = 30.0, unit = "g", calories = 185.0, protein = 5.0, carbs = 6.0, fat = 16.0, isFavorite = true),
                FavoriteFoodEntity(name = "Crisp Apple", defaultQuantity = 180.0, unit = "g", calories = 95.0, protein = 0.5, carbs = 25.0, fat = 0.3, isFavorite = false)
            )
            for (food in starterFoods) {
                favoriteDao.insertOrUpdate(food)
            }

            // Starter weight entry for initial baseline
            weightDao.insert(
                WeightEntryEntity(
                    date = com.example.data.model.currentDateString(),
                    weightKg = 72.5,
                    note = "Initial baseline"
                )
            )
        }
    }
}
