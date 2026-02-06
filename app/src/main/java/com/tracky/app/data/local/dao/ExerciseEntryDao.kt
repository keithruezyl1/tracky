package com.tracky.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tracky.app.data.local.entity.ExerciseEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseEntryDao {
    
    @Query("SELECT * FROM exercise_entries WHERE date = :date ORDER BY timestamp DESC")
    fun getEntriesForDate(date: String): Flow<List<ExerciseEntryEntity>>
    
    @Query("SELECT * FROM exercise_entries WHERE date = :date ORDER BY timestamp DESC")
    suspend fun getEntriesForDateOnce(date: String): List<ExerciseEntryEntity>
    
    @Query("SELECT * FROM exercise_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): ExerciseEntryEntity?
    
    @Query("SELECT * FROM exercise_entries WHERE id = :id")
    fun getEntryByIdFlow(id: Long): Flow<ExerciseEntryEntity?>
    
    @Query("SELECT SUM(caloriesBurned) FROM exercise_entries WHERE date = :date")
    fun getTotalCaloriesBurnedForDate(date: String): Flow<Int?>

    @Query("SELECT SUM(caloriesBurned) FROM exercise_entries WHERE date >= :startDate AND date <= :endDate")
    fun getTotalCaloriesBurnedBetween(startDate: String, endDate: String): Flow<Int?>
    
    @Insert
    suspend fun insert(entry: ExerciseEntryEntity): Long
    
    @Update
    suspend fun update(entry: ExerciseEntryEntity)
    
    @Delete
    suspend fun delete(entry: ExerciseEntryEntity)
    
    @Query("DELETE FROM exercise_entries WHERE id = :id")
    suspend fun deleteById(id: Long)
}
