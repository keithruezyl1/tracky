package com.tracky.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracky.app.data.local.TrackyDatabase
import com.tracky.app.data.local.preferences.UserPreferencesDataStore
import com.tracky.app.data.repository.ProfileRepository
import com.tracky.app.domain.model.UnitPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val preferencesDataStore: UserPreferencesDataStore,
    private val database: TrackyDatabase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _resetComplete = MutableStateFlow(false)
    val resetComplete: StateFlow<Boolean> = _resetComplete.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            launch {
                profileRepository.getUnitPreference().collect { pref ->
                    _uiState.update { it.copy(unitPreference = pref) }
                }
            }
            launch {
                preferencesDataStore.storePhotosLocally.collect { store ->
                    _uiState.update { it.copy(storePhotosLocally = store) }
                }
            }
            launch {
                preferencesDataStore.darkModeEnabled.collect { enabled ->
                    _uiState.update { it.copy(darkModeEnabled = enabled) }
                }
            }
            launch {
                preferencesDataStore.hapticsEnabled.collect { enabled ->
                    _uiState.update { it.copy(hapticsEnabled = enabled) }
                }
            }
        }
    }

    fun setUnitPreference(preference: UnitPreference) {
        viewModelScope.launch {
            profileRepository.setUnitPreference(preference)
        }
    }

    fun setStorePhotosLocally(store: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setStorePhotosLocally(store)
        }
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setDarkModeEnabled(enabled)
        }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesDataStore.setHapticsEnabled(enabled)
        }
    }

    fun showResetConfirmation() {
        _uiState.update { it.copy(showResetConfirmation = true) }
    }

    fun hideResetConfirmation() {
        _uiState.update { it.copy(showResetConfirmation = false) }
    }

    fun resetAllData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isResetting = true) }
            
            // Cancel all active collections first
            this.coroutineContext.cancelChildren()
            
            // Clear database
            database.clearAllUserData()
            
            // Clear preferences (this will reset onboarding flag)
            preferencesDataStore.clearAllPreferences()
            
            // Small delay to ensure all operations complete
            kotlinx.coroutines.delay(100)
            
            _uiState.update { it.copy(isResetting = false, showResetConfirmation = false) }
            _resetComplete.value = true
        }
    }
}

data class SettingsUiState(
    val unitPreference: UnitPreference = UnitPreference.METRIC,
    val storePhotosLocally: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val hapticsEnabled: Boolean = true,
    val showResetConfirmation: Boolean = false,
    val isResetting: Boolean = false
)
