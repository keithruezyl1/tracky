package com.tracky.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tracky.app.data.local.dao.ChatMessageDao
import com.tracky.app.data.local.dao.DailyGoalDao
import com.tracky.app.data.local.dao.ExerciseEntryDao
import com.tracky.app.data.local.dao.FoodEntryDao
import com.tracky.app.data.local.dao.FoodsDatasetDao
import com.tracky.app.data.local.dao.SavedEntryDao
import com.tracky.app.data.local.dao.UserProfileDao
import com.tracky.app.data.local.dao.WeightEntryDao
import com.tracky.app.data.local.entity.ChatMessageEntity
import com.tracky.app.data.local.entity.DailyGoalEntity
import com.tracky.app.data.local.entity.ExerciseEntryEntity
import com.tracky.app.data.local.entity.ExerciseItemEntity
import com.tracky.app.data.local.entity.FoodEntryEntity
import com.tracky.app.data.local.entity.FoodItemEntity
import com.tracky.app.data.local.entity.FoodsDatasetEntity
import com.tracky.app.data.local.entity.FoodsFtsEntity
import com.tracky.app.data.local.entity.SavedEntryEntity
import com.tracky.app.data.local.entity.SynonymEntity
import com.tracky.app.data.local.entity.UserProfileEntity
import com.tracky.app.data.local.entity.WeightEntryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfileEntity::class,
        DailyGoalEntity::class,
        FoodEntryEntity::class,
        FoodItemEntity::class,
        ExerciseEntryEntity::class,
        ExerciseItemEntity::class,
        WeightEntryEntity::class,
        SavedEntryEntity::class,
        ChatMessageEntity::class,
        FoodsDatasetEntity::class,
        FoodsFtsEntity::class,
        SynonymEntity::class
    ],
    version = 4,
    exportSchema = true
)
abstract class TrackyDatabase : RoomDatabase() {
    
    abstract fun userProfileDao(): UserProfileDao
    abstract fun dailyGoalDao(): DailyGoalDao
    abstract fun foodEntryDao(): FoodEntryDao
    abstract fun exerciseEntryDao(): ExerciseEntryDao
    abstract fun weightEntryDao(): WeightEntryDao
    abstract fun savedEntryDao(): SavedEntryDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun foodsDatasetDao(): FoodsDatasetDao

    /**
     * Clear all user data but keep the foods dataset.
     * Used for app reset functionality.
     */
    suspend fun clearAllUserData() {
        withTransaction {
            try {
                // Use raw queries to delete from all user tables
                val db = openHelper.writableDatabase
                db.execSQL("DELETE FROM user_profile")
                db.execSQL("DELETE FROM daily_goals")
                db.execSQL("DELETE FROM food_entries")
                db.execSQL("DELETE FROM food_items")
                db.execSQL("DELETE FROM exercise_entries")
                db.execSQL("DELETE FROM exercise_items")
                db.execSQL("DELETE FROM weight_entries")
                db.execSQL("DELETE FROM saved_entries")
                db.execSQL("DELETE FROM chat_messages")
                // Note: We don't delete foods_dataset or synonyms as those are static data
            } catch (e: Exception) {
                // Log error but don't throw - allow reset to continue
                android.util.Log.e("TrackyDatabase", "Error clearing user data", e)
            }
        }
    }
    
    companion object {
        const val DATABASE_NAME = "tracky_database"
        
        @Volatile
        private var INSTANCE: TrackyDatabase? = null
        
        fun getInstance(context: Context): TrackyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrackyDatabase::class.java,
                    DATABASE_NAME
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Force clear database instance and delete database file.
         * Used when clearing app data to ensure fresh start.
         */
        fun clearInstance(context: Context) {
            synchronized(this) {
                INSTANCE?.close()
                INSTANCE = null
                context.applicationContext.deleteDatabase(DATABASE_NAME)
            }
        }
    }
    
    /**
     * Callback to populate initial data
     */
    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            
            // Pre-populate foods dataset with common foods
            CoroutineScope(Dispatchers.IO).launch {
                INSTANCE?.let { database ->
                    populateInitialFoodsDataset(database.foodsDatasetDao())
                }
            }
        }
    }
}

/**
 * Populate initial foods dataset with common foods.
 * This provides dataset-first resolution for common items.
 */
private suspend fun populateInitialFoodsDataset(dao: FoodsDatasetDao) {
    val commonFoods = listOf(
        // Fruits
        FoodsDatasetEntity(
            name = "Apple",
            category = "fruits",
            servingSize = 182f,
            servingUnit = "g",
            caloriesPerServing = 95,
            carbsPerServingG = 25f,
            proteinPerServingG = 0.5f,
            fatPerServingG = 0.3f,
            fiberPerServingG = 4.4f,
            sugarPerServingG = 19f,
            sodiumPerServingMg = 2f,
            usdaFdcId = 171688,
            dataSource = "usda_fdc"
        ),
        FoodsDatasetEntity(
            name = "Banana",
            category = "fruits",
            servingSize = 118f,
            servingUnit = "g",
            caloriesPerServing = 105,
            carbsPerServingG = 27f,
            proteinPerServingG = 1.3f,
            fatPerServingG = 0.4f,
            fiberPerServingG = 3.1f,
            sugarPerServingG = 14f,
            sodiumPerServingMg = 1f,
            usdaFdcId = 173944,
            dataSource = "usda_fdc"
        ),
        FoodsDatasetEntity(
            name = "Orange",
            category = "fruits",
            servingSize = 131f,
            servingUnit = "g",
            caloriesPerServing = 62,
            carbsPerServingG = 15f,
            proteinPerServingG = 1.2f,
            fatPerServingG = 0.2f,
            fiberPerServingG = 3.1f,
            sugarPerServingG = 12f,
            sodiumPerServingMg = 0f,
            usdaFdcId = 169097,
            dataSource = "usda_fdc"
        ),
        
        // Proteins
        FoodsDatasetEntity(
            name = "Chicken Breast",
            category = "proteins",
            servingSize = 100f,
            servingUnit = "g",
            caloriesPerServing = 165,
            carbsPerServingG = 0f,
            proteinPerServingG = 31f,
            fatPerServingG = 3.6f,
            fiberPerServingG = 0f,
            sugarPerServingG = 0f,
            sodiumPerServingMg = 74f,
            usdaFdcId = 171077,
            dataSource = "usda_fdc"
        ),
        FoodsDatasetEntity(
            name = "Salmon",
            category = "proteins",
            servingSize = 100f,
            servingUnit = "g",
            caloriesPerServing = 208,
            carbsPerServingG = 0f,
            proteinPerServingG = 20f,
            fatPerServingG = 13f,
            fiberPerServingG = 0f,
            sugarPerServingG = 0f,
            sodiumPerServingMg = 59f,
            usdaFdcId = 175167,
            dataSource = "usda_fdc"
        ),
        FoodsDatasetEntity(
            name = "Egg",
            category = "proteins",
            servingSize = 50f,
            servingUnit = "g",
            caloriesPerServing = 72,
            carbsPerServingG = 0.4f,
            proteinPerServingG = 6.3f,
            fatPerServingG = 4.8f,
            fiberPerServingG = 0f,
            sugarPerServingG = 0.2f,
            sodiumPerServingMg = 71f,
            usdaFdcId = 171287,
            dataSource = "usda_fdc"
        ),
        FoodsDatasetEntity(
            name = "Ground Beef",
            category = "proteins",
            servingSize = 100f,
            servingUnit = "g",
            caloriesPerServing = 250,
            carbsPerServingG = 0f,
            proteinPerServingG = 26f,
            fatPerServingG = 15f,
            fiberPerServingG = 0f,
            sugarPerServingG = 0f,
            sodiumPerServingMg = 75f,
            usdaFdcId = 174036,
            dataSource = "usda_fdc"
        ),
        
        // Vegetables
        FoodsDatasetEntity(
            name = "Broccoli",
            category = "vegetables",
            servingSize = 91f,
            servingUnit = "g",
            caloriesPerServing = 31,
            carbsPerServingG = 6f,
            proteinPerServingG = 2.5f,
            fatPerServingG = 0.3f,
            fiberPerServingG = 2.4f,
            sugarPerServingG = 1.5f,
            sodiumPerServingMg = 30f,
            usdaFdcId = 170379,
            dataSource = "usda_fdc"
        ),
        FoodsDatasetEntity(
            name = "Spinach",
            category = "vegetables",
            servingSize = 30f,
            servingUnit = "g",
            caloriesPerServing = 7,
            carbsPerServingG = 1.1f,
            proteinPerServingG = 0.9f,
            fatPerServingG = 0.1f,
            fiberPerServingG = 0.7f,
            sugarPerServingG = 0.1f,
            sodiumPerServingMg = 24f,
            usdaFdcId = 168462,
            dataSource = "usda_fdc"
        ),
        FoodsDatasetEntity(
            name = "Carrot",
            category = "vegetables",
            servingSize = 61f,
            servingUnit = "g",
            caloriesPerServing = 25,
            carbsPerServingG = 6f,
            proteinPerServingG = 0.6f,
            fatPerServingG = 0.1f,
            fiberPerServingG = 1.7f,
            sugarPerServingG = 2.9f,
            sodiumPerServingMg = 42f,
            usdaFdcId = 170393,
            dataSource = "usda_fdc"
        ),
        
        // Grains
        FoodsDatasetEntity(
            name = "White Rice",
            category = "grains",
            servingSize = 158f,
            servingUnit = "g",
            caloriesPerServing = 206,
            carbsPerServingG = 45f,
            proteinPerServingG = 4.3f,
            fatPerServingG = 0.4f,
            fiberPerServingG = 0.6f,
            sugarPerServingG = 0f,
            sodiumPerServingMg = 2f,
            usdaFdcId = 169756,
            dataSource = "usda_fdc"
        ),
        FoodsDatasetEntity(
            name = "Brown Rice",
            category = "grains",
            servingSize = 195f,
            servingUnit = "g",
            caloriesPerServing = 216,
            carbsPerServingG = 45f,
            proteinPerServingG = 5f,
            fatPerServingG = 1.8f,
            fiberPerServingG = 3.5f,
            sugarPerServingG = 0.7f,
            sodiumPerServingMg = 10f,
            usdaFdcId = 169704,
            dataSource = "usda_fdc"
        ),
        FoodsDatasetEntity(
            name = "Oatmeal",
            category = "grains",
            servingSize = 234f,
            servingUnit = "g",
            caloriesPerServing = 158,
            carbsPerServingG = 27f,
            proteinPerServingG = 6f,
            fatPerServingG = 3.2f,
            fiberPerServingG = 4f,
            sugarPerServingG = 1.1f,
            sodiumPerServingMg = 115f,
            usdaFdcId = 173904,
            dataSource = "usda_fdc"
        ),
        FoodsDatasetEntity(
            name = "Bread",
            category = "grains",
            servingSize = 25f,
            servingUnit = "g",
            caloriesPerServing = 67,
            carbsPerServingG = 13f,
            proteinPerServingG = 2.3f,
            fatPerServingG = 0.8f,
            fiberPerServingG = 0.6f,
            sugarPerServingG = 1.4f,
            sodiumPerServingMg = 130f,
            usdaFdcId = 172686,
            dataSource = "usda_fdc"
        ),
        
        // Dairy
        FoodsDatasetEntity(
            name = "Milk",
            category = "dairy",
            servingSize = 244f,
            servingUnit = "ml",
            caloriesPerServing = 149,
            carbsPerServingG = 12f,
            proteinPerServingG = 8f,
            fatPerServingG = 8f,
            fiberPerServingG = 0f,
            sugarPerServingG = 12f,
            sodiumPerServingMg = 105f,
            usdaFdcId = 171265,
            dataSource = "usda_fdc"
        ),
        FoodsDatasetEntity(
            name = "Greek Yogurt",
            category = "dairy",
            servingSize = 170f,
            servingUnit = "g",
            caloriesPerServing = 100,
            carbsPerServingG = 6f,
            proteinPerServingG = 17f,
            fatPerServingG = 0.7f,
            fiberPerServingG = 0f,
            sugarPerServingG = 5f,
            sodiumPerServingMg = 65f,
            usdaFdcId = 170903,
            dataSource = "usda_fdc"
        ),
        FoodsDatasetEntity(
            name = "Cheddar Cheese",
            category = "dairy",
            servingSize = 28f,
            servingUnit = "g",
            caloriesPerServing = 113,
            carbsPerServingG = 0.4f,
            proteinPerServingG = 7f,
            fatPerServingG = 9.3f,
            fiberPerServingG = 0f,
            sugarPerServingG = 0.1f,
            sodiumPerServingMg = 174f,
            usdaFdcId = 173414,
            dataSource = "usda_fdc"
        )
    )
    
    dao.insertAll(commonFoods)
    
    // Add common synonyms
    val synonyms = listOf(
        // Apple synonyms
        SynonymEntity(synonym = "fuji apple", foodDatasetId = 1),
        SynonymEntity(synonym = "gala apple", foodDatasetId = 1),
        SynonymEntity(synonym = "granny smith", foodDatasetId = 1),
        
        // Chicken synonyms
        SynonymEntity(synonym = "chicken", foodDatasetId = 4),
        SynonymEntity(synonym = "grilled chicken", foodDatasetId = 4),
        SynonymEntity(synonym = "baked chicken", foodDatasetId = 4),
        
        // Egg synonyms
        SynonymEntity(synonym = "eggs", foodDatasetId = 6),
        SynonymEntity(synonym = "fried egg", foodDatasetId = 6),
        SynonymEntity(synonym = "boiled egg", foodDatasetId = 6),
        SynonymEntity(synonym = "scrambled eggs", foodDatasetId = 6),
        
        // Rice synonyms
        SynonymEntity(synonym = "rice", foodDatasetId = 11),
        SynonymEntity(synonym = "steamed rice", foodDatasetId = 11),
        
        // Milk synonyms
        SynonymEntity(synonym = "whole milk", foodDatasetId = 15),
        SynonymEntity(synonym = "cow milk", foodDatasetId = 15),
        
        // Yogurt synonyms
        SynonymEntity(synonym = "yogurt", foodDatasetId = 16),
        SynonymEntity(synonym = "plain yogurt", foodDatasetId = 16)
    )
    
    dao.insertSynonyms(synonyms)
}
