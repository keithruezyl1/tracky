package com.tracky.app.data.local.entity

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Foods Dataset Entity
 * 
 * Local food nutrition database for dataset-first resolution.
 * Pre-populated with common foods.
 */
@Entity(
    tableName = "foods_dataset",
    indices = [
        Index(value = ["name"]),
        Index(value = ["category"])
    ]
)
data class FoodsDatasetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * Food name
     */
    val name: String,
    
    /**
     * Food category (e.g., "fruits", "vegetables", "meat", "dairy")
     */
    val category: String?,
    
    /**
     * Serving size value
     */
    val servingSize: Float,
    
    /**
     * Serving unit (g, oz, cup, piece, etc.)
     */
    val servingUnit: String,
    
    /**
     * Calories per serving
     */
    val caloriesPerServing: Int,
    
    /**
     * Carbs per serving in grams
     */
    val carbsPerServingG: Float,
    
    /**
     * Protein per serving in grams
     */
    val proteinPerServingG: Float,
    
    /**
     * Fat per serving in grams
     */
    val fatPerServingG: Float,
    
    /**
     * Fiber per serving in grams (optional)
     */
    val fiberPerServingG: Float?,
    
    /**
     * Sugar per serving in grams (optional)
     */
    val sugarPerServingG: Float?,
    
    /**
     * Sodium per serving in mg (optional)
     */
    val sodiumPerServingMg: Float?,
    
    /**
     * USDA FDC ID if this food was sourced from USDA
     */
    val usdaFdcId: Int?,
    
    /**
     * Data source: "usda_fdc", "manual", "imported"
     */
    val dataSource: String
)

/**
 * Foods FTS Entity
 * 
 * Full-text search index for foods dataset.
 * Enables fast fuzzy search on food names.
 */
@Fts4(contentEntity = FoodsDatasetEntity::class)
@Entity(tableName = "foods_fts")
data class FoodsFtsEntity(
    val name: String,
    val category: String?
)

/**
 * Synonym Entity
 * 
 * Maps common food synonyms/aliases to dataset foods.
 * Improves search matching.
 */
@Entity(
    tableName = "food_synonyms",
    indices = [
        Index(value = ["synonym"]),
        Index(value = ["foodDatasetId"])
    ]
)
data class SynonymEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * The synonym/alias text
     */
    val synonym: String,
    
    /**
     * Foreign key to FoodsDatasetEntity
     */
    val foodDatasetId: Long
)
