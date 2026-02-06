package com.tracky.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.tracky.app.ui.screens.home.HomeScreen
import com.tracky.app.ui.screens.onboarding.OnboardingScreen
import com.tracky.app.ui.screens.goals.DailyGoalsScreen
import com.tracky.app.ui.screens.weight.WeightTrackerScreen
import com.tracky.app.ui.screens.entrydetail.EntryDetailScreen
import com.tracky.app.ui.screens.saved.SavedEntriesScreen
import com.tracky.app.ui.screens.settings.SettingsScreen
import com.tracky.app.ui.screens.summary.SummaryScreen

/**
 * Navigation routes
 */
object TrackyRoutes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val DAILY_GOALS = "daily_goals"
    const val WEIGHT_TRACKER = "weight_tracker"
    const val ENTRY_DETAIL = "entry_detail/{entryId}/{entryType}"
    const val SAVED_ENTRIES = "saved_entries"
    const val SETTINGS = "settings"
    const val SUMMARY = "summary"

    fun entryDetail(entryId: Long, entryType: String) = "entry_detail/$entryId/$entryType"
}

@Composable
fun TrackyNavHost(
    navController: NavHostController,
    startDestination: String = TrackyRoutes.HOME
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding flow
        composable(TrackyRoutes.ONBOARDING) {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate(TrackyRoutes.HOME) {
                        popUpTo(TrackyRoutes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        // Home / Day Dashboard
        composable(TrackyRoutes.HOME) {
            HomeScreen(
                onNavigateToGoals = {
                    navController.navigate(TrackyRoutes.DAILY_GOALS)
                },
                onNavigateToWeight = {
                    navController.navigate(TrackyRoutes.WEIGHT_TRACKER)
                },
                onNavigateToSavedEntries = {
                    navController.navigate(TrackyRoutes.SAVED_ENTRIES)
                },
                onNavigateToSettings = {
                    navController.navigate(TrackyRoutes.SETTINGS)
                },
                onNavigateToEntryDetail = { entryId, entryType ->
                    navController.navigate(TrackyRoutes.entryDetail(entryId, entryType))
                },
                onNavigateToSummary = {
                    navController.navigate(TrackyRoutes.SUMMARY)
                }
            )
        }

        // Daily Goals
        composable(TrackyRoutes.DAILY_GOALS) {
            DailyGoalsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Weight Tracker
        composable(TrackyRoutes.WEIGHT_TRACKER) {
            WeightTrackerScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Summary
        composable(TrackyRoutes.SUMMARY) {
            SummaryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Entry Detail
        composable(
            route = TrackyRoutes.ENTRY_DETAIL,
            arguments = listOf(
                navArgument("entryId") { type = NavType.LongType },
                navArgument("entryType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getLong("entryId") ?: 0L
            val entryType = backStackEntry.arguments?.getString("entryType") ?: "food"
            
            EntryDetailScreen(
                entryId = entryId,
                entryType = entryType,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEntryDeleted = {
                    navController.popBackStack()
                }
            )
        }

        // Saved Entries
        composable(TrackyRoutes.SAVED_ENTRIES) {
            SavedEntriesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Settings
        composable(TrackyRoutes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onResetComplete = {
                    // Navigate to onboarding after reset
                    navController.navigate(TrackyRoutes.ONBOARDING) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
