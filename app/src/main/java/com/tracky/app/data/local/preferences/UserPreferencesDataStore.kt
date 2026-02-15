package com.tracky.app.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tracky.app.domain.model.UnitPreference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "tracky_preferences"
)

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    // ─────────────────────────────────────────────────────────────────────────
    // Preference Keys
    // ─────────────────────────────────────────────────────────────────────────

    private object Keys {
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        val UNIT_PREFERENCE = stringPreferencesKey("unit_preference")
        val STORE_PHOTOS_LOCALLY = booleanPreferencesKey("store_photos_locally")
        val TIMEZONE = stringPreferencesKey("timezone")
        val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
        val STREAK_STATE_JSON = stringPreferencesKey("streak_state_json")
        val LAST_GRACE_DATE = stringPreferencesKey("last_grace_date")
        val HOME_TIMEZONE = stringPreferencesKey("home_timezone")
        val STREAK_LAST_ANIMATED_COUNT = androidx.datastore.preferences.core.intPreferencesKey("streak_last_animated_count")
        val STREAK_LAST_ANIMATED_DATE = stringPreferencesKey("streak_last_animated_date")
        val REANALYZING_ENTRY_ID = androidx.datastore.preferences.core.longPreferencesKey("reanalyzing_entry_id")
        val REANALYZING_ENTRY_TYPE = stringPreferencesKey("reanalyzing_entry_type")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Re-analysis State
    // ─────────────────────────────────────────────────────────────────────────

    val reanalyzingEntryId: Flow<Long?> = dataStore.data.map { preferences ->
        preferences[Keys.REANALYZING_ENTRY_ID]
    }
    
    val reanalyzingEntryType: Flow<String?> = dataStore.data.map { preferences ->
        preferences[Keys.REANALYZING_ENTRY_TYPE]
    }

    suspend fun setReanalyzingState(id: Long?, type: String?) {
        dataStore.edit { preferences ->
            if (id == null) {
                preferences.remove(Keys.REANALYZING_ENTRY_ID)
                preferences.remove(Keys.REANALYZING_ENTRY_TYPE)
            } else {
                preferences[Keys.REANALYZING_ENTRY_ID] = id
                preferences[Keys.REANALYZING_ENTRY_TYPE] = type ?: "food"
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Onboarding State
    // ─────────────────────────────────────────────────────────────────────────

    val hasCompletedOnboarding: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.HAS_COMPLETED_ONBOARDING] ?: false
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.HAS_COMPLETED_ONBOARDING] = completed
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Unit Preference
    // ─────────────────────────────────────────────────────────────────────────

    val unitPreference: Flow<UnitPreference> = dataStore.data.map { preferences ->
        val value = preferences[Keys.UNIT_PREFERENCE] ?: UnitPreference.METRIC.value
        UnitPreference.fromValue(value)
    }

    suspend fun setUnitPreference(preference: UnitPreference) {
        dataStore.edit { preferences ->
            preferences[Keys.UNIT_PREFERENCE] = preference.value
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Photo Storage Setting
    // ─────────────────────────────────────────────────────────────────────────

    val storePhotosLocally: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.STORE_PHOTOS_LOCALLY] ?: false
    }

    suspend fun setStorePhotosLocally(store: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.STORE_PHOTOS_LOCALLY] = store
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Timezone
    // ─────────────────────────────────────────────────────────────────────────

    val timezone: Flow<String> = dataStore.data.map { preferences ->
        preferences[Keys.TIMEZONE] ?: java.util.TimeZone.getDefault().id
    }

    suspend fun setTimezone(timezone: String) {
        dataStore.edit { preferences ->
            preferences[Keys.TIMEZONE] = timezone
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Dark Mode Preference
    // ─────────────────────────────────────────────────────────────────────────

    // ─────────────────────────────────────────────────────────────────────────
    // Dark Mode Preference
    // ─────────────────────────────────────────────────────────────────────────

    val darkModeEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.DARK_MODE_ENABLED] ?: false  // Default to light mode
    }

    suspend fun setDarkModeEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.DARK_MODE_ENABLED] = enabled
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Haptics Preference
    // ─────────────────────────────────────────────────────────────────────────

    val hapticsEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.HAPTICS_ENABLED] ?: true  // Default to enabled
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.HAPTICS_ENABLED] = enabled
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Streak State
    // ─────────────────────────────────────────────────────────────────────────

    val streakStateJson: Flow<String?> = dataStore.data.map { preferences ->
        preferences[Keys.STREAK_STATE_JSON]
    }

    suspend fun setStreakStateJson(json: String) {
        dataStore.edit { preferences ->
            preferences[Keys.STREAK_STATE_JSON] = json
        }
    }

    val lastGraceDate: Flow<String?> = dataStore.data.map { preferences ->
        preferences[Keys.LAST_GRACE_DATE]
    }

    suspend fun setLastGraceDate(date: String) {
        dataStore.edit { preferences ->
            preferences[Keys.LAST_GRACE_DATE] = date
        }
    }

    val homeTimezone: Flow<String?> = dataStore.data.map { preferences ->
        preferences[Keys.HOME_TIMEZONE]
    }

    suspend fun setHomeTimezone(timezone: String) {
        dataStore.edit { preferences ->
            preferences[Keys.HOME_TIMEZONE] = timezone
        }
    }

    val streakLastAnimatedCount: Flow<Int> = dataStore.data.map { preferences ->
        preferences[Keys.STREAK_LAST_ANIMATED_COUNT] ?: -1
    }

    suspend fun setStreakLastAnimatedCount(count: Int) {
        dataStore.edit { preferences ->
            preferences[Keys.STREAK_LAST_ANIMATED_COUNT] = count
        }
    }

    val streakLastAnimatedDate: Flow<String?> = dataStore.data.map { preferences ->
        preferences[Keys.STREAK_LAST_ANIMATED_DATE]
    }

    suspend fun setStreakLastAnimatedDate(date: String) {
        dataStore.edit { preferences ->
            preferences[Keys.STREAK_LAST_ANIMATED_DATE] = date
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Notifications Preference
    // ─────────────────────────────────────────────────────────────────────────

    val notificationsEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.NOTIFICATIONS_ENABLED] ?: true // Default to true
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Reset All Preferences
    // ─────────────────────────────────────────────────────────────────────────

    suspend fun clearAllPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
