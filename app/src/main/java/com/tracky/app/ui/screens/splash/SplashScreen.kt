package com.tracky.app.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tracky.app.R
import com.tracky.app.ui.components.TrackyBodySmall
import com.tracky.app.ui.components.TrackyLoadingIndicator
import com.tracky.app.ui.components.TrackyScreenTitle
import com.tracky.app.ui.components.TrackyScreenTitle
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyTokens

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val navigationState by viewModel.navigationState.collectAsState()

    LaunchedEffect(navigationState) {
        when (navigationState) {
            SplashNavigationState.NavigateToOnboarding -> onNavigateToOnboarding()
            SplashNavigationState.NavigateToHome -> onNavigateToHome()
            SplashNavigationState.Loading -> { /* Wait */ }
            SplashNavigationState.NoInternet -> { /* Stay on splash with error */ }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TrackyColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.tracky_logo),
                contentDescription = "Tracky Logo",
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(24.dp))
            )

             // Text and Loading Indicator removed as per request for cleaner splash
        }
    }
}
