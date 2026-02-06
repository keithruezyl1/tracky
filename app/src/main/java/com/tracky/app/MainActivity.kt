package com.tracky.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.tracky.app.data.repository.ProfileRepository
import com.tracky.app.ui.navigation.TrackyNavHost
import com.tracky.app.ui.navigation.TrackyRoutes
import com.tracky.app.ui.theme.TrackyTheme
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.tracky.app.worker.NotificationScheduler
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var profileRepository: ProfileRepository
    
    @Inject
    lateinit var preferencesDataStore: com.tracky.app.data.local.preferences.UserPreferencesDataStore
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Install splash screen (if using androidx core splash, but we are just switching theme manually)
        // Note: Android 12+ usually handles this automatically with windowSplashScreen... attributes
        // but it's good practice to set theme back to normal in onCreate
        setTheme(R.style.Theme_Tracky)
        
        enableEdgeToEdge()
        
        // Schedule daily notifications
        NotificationScheduler.scheduleDailyReminders(this)
        
        setContent {
            val darkModeEnabled by preferencesDataStore.darkModeEnabled
                .collectAsState(initial = false)

            // Request notification permission on Android 13+
            val context = LocalContext.current
            var hasNotificationPermission by remember {
                mutableStateOf(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    } else true
                )
            }
            
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { hasNotificationPermission = it }
            )
            
            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            
            TrackyTheme(useDarkTheme = darkModeEnabled) {
                // Collect onboarding state as nullable to detect loading
                val hasCompletedOnboarding by profileRepository.hasCompletedOnboarding()
                    .collectAsState(initial = null)
                
                // Only render navigation when we know the state
                if (hasCompletedOnboarding != null) {
                    val navController = rememberNavController()
                    val startDestination = if (hasCompletedOnboarding == true) {
                        TrackyRoutes.HOME
                    } else {
                        TrackyRoutes.ONBOARDING
                    }
                    
                    Surface(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        TrackyNavHost(
                            navController = navController,
                            startDestination = startDestination
                        )
                    }
                } else {
                    // Show a blank surface while loading availability state
                    // This prevents the "flash" of the wrong screen
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = androidx.compose.material3.MaterialTheme.colorScheme.background
                    ) {
                        // Optional: Add a subtle loading indicator if it takes too long,
                        // but for DataStore it should be near-instant.
                    }
                }
            }
        }
    }
}
