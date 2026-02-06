package com.tracky.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.tracky.app.data.local.entity.ExerciseEntryEntity
import com.tracky.app.data.local.entity.ExerciseItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseEntryDao {
    
    @Transaction
    @Query("SELECT * FROM exercise_entries WHERE date = :date ORDER BY timestamp DESC")
    fun getEntriesForDate(date: String): Flow<List<ExerciseEntryWithItems>>
    
    @Transaction
    @Query("SELECT * FROM exercise_entries WHERE date = :date ORDER BY timestamp DESC")
    suspend fun getEntriesForDateOnce(date: String): List<ExerciseEntryWithItems>
    
    @Transaction
    @Query("SELECT * FROM exercise_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): ExerciseEntryWithItems?
    
    @Transaction
    @Query("SELECT * FROM exercise_entries WHERE id = :id")
    fun getEntryByIdFlow(id: Long): Flow<ExerciseEntryWithItems?>
    
    @Query("SELECT SUM(totalCalories) FROM exercise_entries WHERE date = :date")
    fun getTotalCaloriesBurnedForDate(date: String): Flow<Float?>

    @Query("SELECT SUM(totalCalories) FROM exercise_entries WHERE date >= :startDate AND date <= :endDate")
    fun getTotalCaloriesBurnedBetween(startDate: String, endDate: String): Flow<Float?>
    
    @Insert
    suspend fun insert(entry: ExerciseEntryEntity): Long
    
    @Insert
    suspend fun insertItems(items: List<ExerciseItemEntity>)

    @Query("DELETE FROM exercise_items WHERE entryId = :entryId")
    suspend fun deleteItemsForEntry(entryId: Long)
    
    @Update
    suspend fun update(entry: ExerciseEntryEntity)
    
    @Delete
    suspend fun delete(entry: ExerciseEntryEntity)
    
    @Query("DELETE FROM exercise_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM exercise_items WHERE id = :itemId")
    suspend fun deleteItemById(itemId: Long)
}
