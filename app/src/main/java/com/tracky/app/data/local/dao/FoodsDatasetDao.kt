package com.tracky.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tracky.app.data.local.entity.FoodsDatasetEntity
import com.tracky.app.data.local.entity.SynonymEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodsDatasetDao {
    
    // ─────────────────────────────────────────────────────────────────────────
    // Full-Text Search
    // ─────────────────────────────────────────────────────────────────────────
    
    /**
     * Search foods using FTS
     */
    @Query("""
        SELECT foods_dataset.* FROM foods_dataset
        JOIN foods_fts ON foods_dataset.rowid = foods_fts.rowid
        WHERE foods_fts MATCH :query || '*'
        LIMIT :limit
    """)
    suspend fun searchFoods(query: String, limit: Int = 20): List<FoodsDatasetEntity>
    
    /**
     * Search with category filter
     */
    @Query("""
        SELECT foods_dataset.* FROM foods_dataset
        JOIN foods_fts ON foods_dataset.rowid = foods_fts.rowid
        WHERE foods_fts MATCH :query || '*' AND foods_dataset.category = :category
        LIMIT :limit
    """)
    suspend fun searchFoodsInCategory(
        query: String, 
        category: String, 
        limit: Int = 20
    ): List<FoodsDatasetEntity>
    
    // ─────────────────────────────────────────────────────────────────────────
    // Direct Queries
    // ─────────────────────────────────────────────────────────────────────────
    
    @Query("SELECT * FROM foods_dataset WHERE id = :id")
    suspend fun getFoodById(id: Long): FoodsDatasetEntity?
    
    @Query("SELECT * FROM foods_dataset WHERE usdaFdcId = :fdcId")
    suspend fun getFoodByFdcId(fdcId: Int): FoodsDatasetEntity?
    
    @Query("SELECT * FROM foods_dataset WHERE name LIKE '%' || :name || '%' LIMIT :limit")
    suspend fun searchByName(name: String, limit: Int = 20): List<FoodsDatasetEntity>
    
    @Query("SELECT * FROM foods_dataset WHERE category = :category ORDER BY name LIMIT :limit")
    fun getFoodsByCategory(category: String, limit: Int = 100): Flow<List<FoodsDatasetEntity>>
    
    @Query("SELECT DISTINCT category FROM foods_dataset WHERE category IS NOT NULL ORDER BY category")
    fun getAllCategories(): Flow<List<String>>
    
    // ─────────────────────────────────────────────────────────────────────────
    // Insert/Update
    // ─────────────────────────────────────────────────────────────────────────
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(food: FoodsDatasetEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(foods: List<FoodsDatasetEntity>)
    
    @Query("SELECT COUNT(*) FROM foods_dataset")
    suspend fun getCount(): Int
    
    // ─────────────────────────────────────────────────────────────────────────
    // Synonyms
    // ─────────────────────────────────────────────────────────────────────────
    
    @Query("""
        SELECT foods_dataset.* FROM foods_dataset
        JOIN food_synonyms ON foods_dataset.id = food_synonyms.foodDatasetId
        WHERE food_synonyms.synonym LIKE '%' || :synonym || '%'
        LIMIT :limit
    """)
    suspend fun searchBySynonym(synonym: String, limit: Int = 20): List<FoodsDatasetEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSynonym(synonym: SynonymEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSynonyms(synonyms: List<SynonymEntity>)
    
    @Query("SELECT * FROM food_synonyms WHERE foodDatasetId = :foodId")
    suspend fun getSynonymsForFood(foodId: Long): List<SynonymEntity>
}
