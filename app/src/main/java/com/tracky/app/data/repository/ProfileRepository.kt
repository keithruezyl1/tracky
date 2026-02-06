package com.tracky.app.data.repository

import com.tracky.app.data.local.dao.UserProfileDao
import com.tracky.app.data.local.preferences.UserPreferencesDataStore
import com.tracky.app.data.mapper.toDomain
import com.tracky.app.data.mapper.toEntity
import com.tracky.app.domain.model.UnitPreference
import com.tracky.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val preferencesDataStore: UserPreferencesDataStore
) {
    /**
     * Get user profile as Flow
     */
    fun getProfile(): Flow<UserProfile?> {
        return userProfileDao.getProfile().map { it?.toDomain() }
    }

    /**
     * Get user profile once (suspend)
     */
    suspend fun getProfileOnce(): UserProfile? {
        return userProfileDao.getProfileOnce()?.toDomain()
    }

    /**
     * Save or update user profile
     */
    suspend fun saveProfile(profile: UserProfile) {
        userProfileDao.insertOrUpdate(profile.toEntity())
    }

    /**
     * Update weight and BMI
     */
    suspend fun updateWeight(weightKg: Float, heightCm: Float) {
        val bmi = UserProfile.calculateBmi(weightKg, heightCm)
        userProfileDao.updateWeight(weightKg, bmi)
    }

    /**
     * Check if profile exists (for onboarding check)
     */
    suspend fun hasProfile(): Boolean {
        return userProfileDao.hasProfile()
    }

    /**
     * Get unit preference
     */
    fun getUnitPreference(): Flow<UnitPreference> {
        return preferencesDataStore.unitPreference
    }

    /**
     * Set unit preference
     */
    suspend fun setUnitPreference(preference: UnitPreference) {
        preferencesDataStore.setUnitPreference(preference)
        // Also update the DB entity so that domain objects returned by getProfile() reflect the change
        val currentProfile = userProfileDao.getProfileOnce()
        if (currentProfile != null) {
            userProfileDao.insertOrUpdate(currentProfile.copy(unitPreference = preference.value))
        }
    }

    /**
     * Check if onboarding completed
     */
    fun hasCompletedOnboarding(): Flow<Boolean> {
        return preferencesDataStore.hasCompletedOnboarding
    }

    /**
     * Mark onboarding as completed
     */
    suspend fun setOnboardingCompleted(completed: Boolean) {
        preferencesDataStore.setOnboardingCompleted(completed)
    }
}
