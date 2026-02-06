package com.tracky.app.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracky.app.data.remote.TrackyBackendApi
import com.tracky.app.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val backendApi: TrackyBackendApi
) : ViewModel() {

    private val _navigationState = MutableStateFlow<SplashNavigationState>(SplashNavigationState.Loading)
    val navigationState: StateFlow<SplashNavigationState> = _navigationState.asStateFlow()

    init {
        checkOnboardingStatus()
    }

    private fun checkOnboardingStatus() {
        viewModelScope.launch {
            // First, require network / backend health before letting the app open.
            val isBackendHealthy = try {
                val response = backendApi.healthCheck()
                response.isSuccessful
            } catch (e: Exception) {
                false
            }

            if (!isBackendHealthy) {
                _navigationState.value = SplashNavigationState.NoInternet
                return@launch
            }

            val hasCompletedOnboarding = profileRepository.hasCompletedOnboarding().first()

            _navigationState.value = if (hasCompletedOnboarding) {
                SplashNavigationState.NavigateToHome
            } else {
                SplashNavigationState.NavigateToOnboarding
            }
        }
    }
}

sealed class SplashNavigationState {
    data object Loading : SplashNavigationState()
    data object NavigateToOnboarding : SplashNavigationState()
    data object NavigateToHome : SplashNavigationState()
    data object NoInternet : SplashNavigationState()
}
