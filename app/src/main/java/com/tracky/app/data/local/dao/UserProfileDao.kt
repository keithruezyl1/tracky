package com.tracky.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tracky.app.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getProfile(): Flow<UserProfileEntity?>
    
    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getProfileOnce(): UserProfileEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfileEntity)
    
    @Update
    suspend fun update(profile: UserProfileEntity)
    
    @Query("UPDATE user_profile SET currentWeightKg = :weight, bmi = :bmi WHERE id = 1")
    suspend fun updateWeight(weight: Float, bmi: Float)
    
    @Query("SELECT EXISTS(SELECT 1 FROM user_profile WHERE id = 1)")
    suspend fun hasProfile(): Boolean
}
