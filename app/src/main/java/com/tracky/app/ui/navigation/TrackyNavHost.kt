package com.tracky.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import com.tracky.app.ui.screens.home.HomeScreen
import com.tracky.app.ui.screens.onboarding.OnboardingScreen
import com.tracky.app.ui.screens.goals.DailyGoalsScreen
import com.tracky.app.ui.screens.weight.WeightTrackerScreen
import com.tracky.app.ui.screens.entrydetail.EntryDetailScreen
import com.tracky.app.ui.screens.saved.SavedEntriesScreen
import com.tracky.app.ui.screens.settings.SettingsScreen
import com.tracky.app.ui.screens.summary.SummaryScreen
import com.tracky.app.ui.screens.features.FeaturesScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

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
    const val FEATURES = "features"

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
        composable(TrackyRoutes.HOME) { backStackEntry ->
            val reanalyzeQuery by backStackEntry.savedStateHandle.getStateFlow<String?>("reanalyze_query", null).collectAsState()
            val reanalyzeId by backStackEntry.savedStateHandle.getStateFlow<Long?>("reanalyze_id", null).collectAsState()
            val reanalyzeType by backStackEntry.savedStateHandle.getStateFlow<String?>("reanalyze_type", null).collectAsState()

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
                },
                reanalyzeQuery = reanalyzeQuery,
                reanalyzeId = reanalyzeId,
                reanalyzeType = reanalyzeType,
                onReanalyzeConsumed = {
                    backStackEntry.savedStateHandle.remove<String>("reanalyze_query")
                    backStackEntry.savedStateHandle.remove<Long>("reanalyze_id")
                    backStackEntry.savedStateHandle.remove<String>("reanalyze_type")
                }
            )
        }

        // Daily Goals
        composable(
            route = TrackyRoutes.DAILY_GOALS,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            DailyGoalsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Weight Tracker
        composable(
            route = TrackyRoutes.WEIGHT_TRACKER,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            WeightTrackerScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Summary
        composable(
            route = TrackyRoutes.SUMMARY,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
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
            ),
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
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
                },
                onReanalyze = { query, id, type ->
                    navController.previousBackStackEntry?.savedStateHandle?.let { handle ->
                        handle.set("reanalyze_query", query)
                        handle.set("reanalyze_id", id)
                        handle.set("reanalyze_type", type)
                    }
                    navController.popBackStack()
                }
            )
        }

        // Saved Entries
        composable(
            route = TrackyRoutes.SAVED_ENTRIES,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            SavedEntriesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Settings
        composable(
            route = TrackyRoutes.SETTINGS,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onResetComplete = {
                    // Navigate to onboarding after reset
                    navController.navigate(TrackyRoutes.ONBOARDING) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToFeatures = {
                    navController.navigate(TrackyRoutes.FEATURES)
                }
            )
        }


        // Features Screen
        composable(
            route = TrackyRoutes.FEATURES,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            FeaturesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
