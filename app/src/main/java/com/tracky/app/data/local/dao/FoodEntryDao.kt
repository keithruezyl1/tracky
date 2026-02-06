package com.tracky.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.tracky.app.data.local.entity.FoodEntryEntity
import com.tracky.app.data.local.entity.FoodItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodEntryDao {
    
    // ─────────────────────────────────────────────────────────────────────────
    // Food Entries
    // ─────────────────────────────────────────────────────────────────────────
    
    @Query("SELECT * FROM food_entries WHERE date = :date ORDER BY timestamp DESC")
    fun getEntriesForDate(date: String): Flow<List<FoodEntryEntity>>
    
    @Query("SELECT * FROM food_entries WHERE date = :date ORDER BY timestamp DESC")
    suspend fun getEntriesForDateOnce(date: String): List<FoodEntryEntity>
    
    @Query("SELECT * FROM food_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): FoodEntryEntity?
    
    @Query("SELECT * FROM food_entries WHERE id = :id")
    fun getEntryByIdFlow(id: Long): Flow<FoodEntryEntity?>
    
    @Query("""
        SELECT SUM(totalCalories) FROM food_entries 
        WHERE date = :date
    """)
    fun getTotalCaloriesForDate(date: String): Flow<Int?>
    
    @Query("""
        SELECT SUM(totalCarbsG) as carbs, SUM(totalProteinG) as protein, SUM(totalFatG) as fat
        FROM food_entries WHERE date = :date
    """)
    fun getMacroTotalsForDate(date: String): Flow<MacroTotals?>

    // Range Queries
    @Query("""
        SELECT SUM(totalCalories) FROM food_entries 
        WHERE date >= :startDate AND date <= :endDate
    """)
    fun getTotalCaloriesBetween(startDate: String, endDate: String): Flow<Int?>

    @Query("""
        SELECT SUM(totalCarbsG) as carbs, SUM(totalProteinG) as protein, SUM(totalFatG) as fat
        FROM food_entries WHERE date >= :startDate AND date <= :endDate
    """)
    fun getMacroTotalsBetween(startDate: String, endDate: String): Flow<MacroTotals?>
    
    @Insert
    suspend fun insert(entry: FoodEntryEntity): Long
    
    @Update
    suspend fun update(entry: FoodEntryEntity)
    
    @Delete
    suspend fun delete(entry: FoodEntryEntity)
    
    @Query("DELETE FROM food_entries WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    // ─────────────────────────────────────────────────────────────────────────
    // Food Items
    // ─────────────────────────────────────────────────────────────────────────
    
    @Query("SELECT * FROM food_items WHERE foodEntryId = :entryId ORDER BY displayOrder")
    fun getItemsForEntry(entryId: Long): Flow<List<FoodItemEntity>>
    
    @Query("SELECT * FROM food_items WHERE foodEntryId = :entryId ORDER BY displayOrder")
    suspend fun getItemsForEntryOnce(entryId: Long): List<FoodItemEntity>
    
    @Insert
    suspend fun insertItem(item: FoodItemEntity): Long
    
    @Insert
    suspend fun insertItems(items: List<FoodItemEntity>)
    
    @Update
    suspend fun updateItem(item: FoodItemEntity)
    
    @Delete
    suspend fun deleteItem(item: FoodItemEntity)
    
    @Query("DELETE FROM food_items WHERE foodEntryId = :entryId")
    suspend fun deleteItemsForEntry(entryId: Long)
    
    // ─────────────────────────────────────────────────────────────────────────
    // Transactions
    // ─────────────────────────────────────────────────────────────────────────
    
    @Transaction
    suspend fun insertEntryWithItems(entry: FoodEntryEntity, items: List<FoodItemEntity>): Long {
        val entryId = insert(entry)
        val itemsWithEntryId = items.map { it.copy(foodEntryId = entryId) }
        insertItems(itemsWithEntryId)
        return entryId
    }
    
    @Transaction
    suspend fun deleteEntryWithItems(entryId: Long) {
        deleteItemsForEntry(entryId)
        deleteById(entryId)
    }
}

/**
 * Data class for macro totals query result
 */
data class MacroTotals(
    val carbs: Float?,
    val protein: Float?,
    val fat: Float?
)
