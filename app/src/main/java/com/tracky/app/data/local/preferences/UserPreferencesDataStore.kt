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

    val darkModeEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.DARK_MODE_ENABLED] ?: false  // Default to light mode
    }

    suspend fun setDarkModeEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.DARK_MODE_ENABLED] = enabled
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
