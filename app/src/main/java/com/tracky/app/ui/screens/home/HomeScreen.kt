package com.tracky.app.ui.screens.home

import com.tracky.app.ui.utils.toSmartString
import com.tracky.app.util.toTitleCase

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.draw.blur
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import com.tracky.app.domain.model.ChatMessage
import com.tracky.app.domain.model.ChatMessageType
import com.tracky.app.domain.model.DailySummary
import com.tracky.app.domain.model.DraftData
import com.tracky.app.domain.model.ExerciseEntry
import com.tracky.app.domain.model.FoodEntry
import com.tracky.app.domain.model.MessageRole
import com.tracky.app.domain.usecase.DraftState
import com.tracky.app.domain.model.DraftStatus
import com.tracky.app.ui.camera.CameraCaptureScreen
import com.tracky.app.ui.camera.uriToBase64
import com.tracky.app.ui.components.BadgeStyle
import com.tracky.app.ui.components.TrackyBadge
import com.tracky.app.ui.components.DayStatus
import com.tracky.app.ui.components.TrackyBodySmall
import com.tracky.app.ui.components.TrackyText
import com.tracky.app.ui.components.TrackyTextStyle
import com.tracky.app.ui.components.TrackyBodyText
import com.tracky.app.ui.components.TrackyBottomSheet
import com.tracky.app.ui.components.TrackyButtonPrimary
import com.tracky.app.ui.components.TrackyButtonSecondary
import com.tracky.app.ui.components.TrackyButtonDanger
import com.tracky.app.ui.components.TrackyCaloriesProgress
import com.tracky.app.ui.components.TrackyCard
import com.tracky.app.ui.components.TrackyCardTitle
import com.tracky.app.ui.components.TrackyChip
import com.tracky.app.ui.components.TrackyDayChip
import com.tracky.app.ui.components.TrackyDivider
import com.tracky.app.ui.components.TrackyDraftingState
import com.tracky.app.ui.components.TrackyEntryCard
import com.tracky.app.ui.components.TrackyInput
import com.tracky.app.ui.components.TrackyLoadingIndicator
import com.tracky.app.ui.components.TrackyLoadingOverlay
import com.tracky.app.ui.components.TrackyFullScreenLoading
import com.tracky.app.ui.components.TrackyCircularMacroProgress
import com.tracky.app.ui.components.TrackyMacrosRow
import com.tracky.app.ui.components.TrackyScreenTitle
import com.tracky.app.ui.components.TrackySheetActions
import com.tracky.app.ui.components.SwipeableRow
import com.tracky.app.ui.components.SuccessOverlay
import com.tracky.app.ui.components.StreakIndicator
import com.tracky.app.ui.components.StreakModalContent
import com.tracky.app.ui.components.ProvenanceLabel
import com.tracky.app.domain.model.ProvenanceSource
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyTokens
import com.tracky.app.ui.components.TrackyLoadingOverlay
import com.tracky.app.ui.components.TrackyFullScreenLoading
import com.tracky.app.ui.theme.TrackyTypography
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import kotlinx.datetime.minus
import kotlinx.datetime.daysUntil
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.format.TextStyle
import java.util.Locale

import androidx.compose.foundation.ExperimentalFoundationApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToGoals: () -> Unit,
    onNavigateToWeight: () -> Unit,
    onNavigateToSavedEntries: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToEntryDetail: (Long, String) -> Unit,
    onNavigateToSummary: () -> Unit,
    reanalyzeQuery: String? = null,
    reanalyzeId: Long? = null,
    reanalyzeType: String? = null,
    onReanalyzeConsumed: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val draftState by viewModel.draftState.collectAsState()
    val weekDraftStates by viewModel.weekDraftStates.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val weekChatMessages by viewModel.weekChatMessages.collectAsState()
    val weekSummaries by viewModel.weekSummaries.collectAsState()
    val weekDates by viewModel.weekDates.collectAsState()
    val currentGoal by viewModel.currentGoal.collectAsState()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showCamera by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var editingMessage by remember { mutableStateOf<ChatMessage?>(null) }
    var editText by remember { mutableStateOf("") }

    // Draft editing state
    var editingFoodIndex by remember { mutableStateOf<Int?>(null) }
    var editingExerciseIndex by remember { mutableStateOf<Int?>(null) }
    
    // Quick Add State
    var showQuickAdd by remember { mutableStateOf(false) }
    var quickAddType by remember { mutableStateOf<QuickAddType?>(null) }
    var captureMode by remember { mutableStateOf(CaptureMode.AUTO_LOG) }

    // Show error from uiState
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            if (error.contains("Unable to resolve host", ignoreCase = true) || 
                error.contains("UnknownHostException", ignoreCase = true)) {
                viewModel.toggleOffline(true)
            } else {
                snackbarHostState.showSnackbar(error)
            }
            viewModel.clearError()
        }
    }

    // Play pop sound and manage animation state when a draft appears
    LaunchedEffect(draftState) {
        if (draftState is DraftState.Error) {
            val error = (draftState as DraftState.Error).message
            if (error.contains("Unable to resolve host", ignoreCase = true) || 
                error.contains("UnknownHostException", ignoreCase = true)) {
                viewModel.toggleOffline(true)
            } else {
                snackbarHostState.showSnackbar(error)
            }
        }
        if (draftState is DraftState.FoodDraft || draftState is DraftState.ExerciseDraft) {
            viewModel.onDraftAppeared()
        }
    }

    // Handle re-analysis request
    LaunchedEffect(reanalyzeQuery, reanalyzeId, reanalyzeType) {
        if (reanalyzeQuery != null) {
            viewModel.logAutoFromText(reanalyzeQuery, reanalyzeId, reanalyzeType)
            onReanalyzeConsumed()
        }
    }

    // Gallery picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val base64 = uriToBase64(context, it)
            base64?.let { encoded ->
                when (captureMode) {
                    CaptureMode.AUTO_LOG -> viewModel.logAutoFromImage(encoded)
                    CaptureMode.QUICK_ADD_FOOD -> viewModel.addToDraftImage(encoded, true)
                    CaptureMode.QUICK_ADD_EXERCISE -> viewModel.addToDraftImage(encoded, false)
                }
                captureMode = CaptureMode.AUTO_LOG
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val instant = Instant.fromEpochMilliseconds(millis)
                            val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                            viewModel.selectDateFromCalendar(localDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showCamera) {
        CameraCaptureScreen(
            onImageCaptured = { base64 ->
                showCamera = false
                when (captureMode) {
                    CaptureMode.AUTO_LOG -> viewModel.logAutoFromImage(base64)
                    CaptureMode.QUICK_ADD_FOOD -> viewModel.addToDraftImage(base64, true)
                    CaptureMode.QUICK_ADD_EXERCISE -> viewModel.addToDraftImage(base64, false)
                }
                captureMode = CaptureMode.AUTO_LOG
            },
            onGallerySelected = { uri ->
                showCamera = false
                val base64 = uriToBase64(context, uri)
                base64?.let { encoded ->
                    when (captureMode) {
                        CaptureMode.AUTO_LOG -> viewModel.logAutoFromImage(encoded)
                        CaptureMode.QUICK_ADD_FOOD -> viewModel.addToDraftImage(encoded, true)
                        CaptureMode.QUICK_ADD_EXERCISE -> viewModel.addToDraftImage(encoded, false)
                    }
                    captureMode = CaptureMode.AUTO_LOG
                }
            },
            onDismiss = { showCamera = false }
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(TrackyColors.Background)
                    .then(
                        if (uiState.showSidebar && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S)
                            Modifier.blur(8.dp)
                        else Modifier
                    )
                    .statusBarsPadding()
            ) {
                // Header with dynamic date title
                val today = remember {
                    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                }
                val headerTitle = remember(selectedDate, today) {
                    if (selectedDate == today) {
                        "Today"
                    } else {
                        // Format as "Wed, Feb 4"
                        val dayOfWeek = java.time.DayOfWeek.valueOf(selectedDate.dayOfWeek.name)
                            .getDisplayName(TextStyle.SHORT, Locale.getDefault())
                        val month = java.time.Month.of(selectedDate.monthNumber)
                            .getDisplayName(TextStyle.SHORT, Locale.getDefault())
                        "$dayOfWeek, $month ${selectedDate.dayOfMonth}"
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = TrackyTokens.Spacing.ScreenPadding)
                        .padding(top = TrackyTokens.Spacing.M),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.clickable { showDatePicker = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TrackyScreenTitle(text = headerTitle)
                        Spacer(modifier = Modifier.width(TrackyTokens.Spacing.XS))
                        Icon(
                            Icons.Outlined.CalendarToday,
                            contentDescription = "Calendar",
                            tint = TrackyColors.TextTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.XS)
                    ) {
                        StreakIndicator(
                            streakInfo = uiState.streakInfo,
                            onClick = viewModel::showStreakModal,
                            shouldAnimate = uiState.shouldAnimateStreak,
                            onAnimationComplete = viewModel::onStreakAnimationComplete
                        )

                        Spacer(modifier = Modifier.size(48.dp))
                    }
                }

                // 7-day strip (centered)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = TrackyTokens.Spacing.ScreenPadding),
                    horizontalArrangement = Arrangement.spacedBy(
                        TrackyTokens.Spacing.XS,
                        Alignment.CenterHorizontally
                    )
                ) {
                    items(weekDates) { date ->
                        val isSelected = date == selectedDate
                        val isToday = date == today

                        val dayStatus = weekSummaries[date]?.let { summary: DailySummary ->
                            val goal = summary.goal?.calorieGoalKcal ?: currentGoal?.calorieGoalKcal ?: 2000f
                            // Net Calories Logic: Goal + Exercise - Food
                            // If Food > Goal + Exercise, then remaining < 0 (Over)
                            val remaining = goal + summary.exerciseCalories - summary.foodCalories

                            if (remaining < 0) {
                                DayStatus.FAILURE // Red (Over budget)
                            } else if (summary.foodCalories > 0 || summary.exerciseCalories > 0) {
                                DayStatus.SUCCESS // Green (Healthy/Within budget with logs)
                            } else {
                                DayStatus.NONE
                            }
                        } ?: DayStatus.NONE

                        TrackyDayChip(
                            dayLetter = if (date.dayOfWeek.name == "THURSDAY") "TH" else date.dayOfWeek.name.take(1),
                            dateNumber = date.dayOfMonth.toString(),
                            selected = isSelected,
                            isToday = isToday,
                            onClick = { viewModel.selectDate(date) },
                            status = dayStatus
                        )
                    }
                }

                Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

                // Fixed page count for a stable pager (approx 54 years of past history)
                // initialPageBase corresponds to 'today'. We add 1 to make it the last page.
                val totalPages = 40_000
                val initialPageBase = 20_000
                
                // Stable offset calculation, capped at today
                val initialOffset = remember { minOf(0, today.daysUntil(selectedDate)) }
                val pagerState = rememberPagerState(
                    initialPage = initialPageBase + initialOffset,
                    pageCount = { initialPageBase + 1 }
                )

                // Track animation state to prevent fighting between scroll and state updates
                var isAnimating by remember { mutableStateOf(false) }

                // 1. Sync: External Date Change (Calendar, Button) -> Pager Scroll
                LaunchedEffect(selectedDate) {
                    val daysDiff = today.daysUntil(selectedDate)
                    val targetPage = initialPageBase + daysDiff
                    
                    // Only scroll if we're not already there and not currently animating via user swipe
                    if (pagerState.currentPage != targetPage && !pagerState.isScrollInProgress) {
                        isAnimating = true
                        try {
                            pagerState.animateScrollToPage(targetPage)
                        } finally {
                            isAnimating = false
                        }
                    }
                }

                // 2. Sync: Pager Swipe (Real-time) -> Update Day List Indicator
                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }
                        .distinctUntilChanged()
                        .collect { page ->
                            // Only update selection during manual drags, not programmatic animations
                            // checking isScrollInProgress ensures we update during swipes
                            if (pagerState.isScrollInProgress && !isAnimating) {
                                val pageDate = today.plus(page - initialPageBase, DateTimeUnit.DAY)
                                // Just update the selected indicator, don't shift the window yet
                                viewModel.selectDate(pageDate)
                            }
                        }
                }

                // 3. Sync: Pager Settle -> Handle Window Boundary Crossing & Finalize Selection
                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.settledPage }
                        .distinctUntilChanged()
                        .collect { page ->
                            // When scrolling finishes (animation or swipe), ensure everything is synced
                            if (!pagerState.isScrollInProgress && !isAnimating) {
                                val pageDate = today.plus(page - initialPageBase, DateTimeUnit.DAY)
                                
                                // Ensure viewmodel is synced with final settled page
                                if (selectedDate != pageDate) {
                                    viewModel.selectDate(pageDate)
                                }
                                
                                val currentWeekDates = viewModel.weekDates.value
                                
                                // Check if the settled date is outside the current visual strip
                                if (pageDate !in currentWeekDates) {
                                    if (pageDate < currentWeekDates.first()) {
                                        viewModel.selectPreviousDay()
                                    } else if (pageDate > currentWeekDates.last()) {
                                        viewModel.selectNextDay()
                                    } else {
                                        // Extreme jump (e.g. from calendar picker far away)
                                        viewModel.selectDateFromCalendar(pageDate)
                                    }
                                }
                            }
                        }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    val date = today.plus(page - initialPageBase, DateTimeUnit.DAY)
                    val summary = weekSummaries[date]
                    val dateChatMessages = weekChatMessages[date] ?: emptyList()
                    val dateDraftState = weekDraftStates[date] ?: DraftState.Idle

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = TrackyTokens.Spacing.XXXL)
                    ) {
                        item(key = date.toString()) {
                            val dayGoal = summary?.goal
                            val totalFoodCals = summary?.foodCalories ?: 0f
                            val totalExCals = summary?.exerciseCalories ?: 0f
                            val goalCals = dayGoal?.calorieGoalKcal ?: currentGoal?.calorieGoalKcal ?: 2000f

                            Column {
                                // 1. Summary Cards
                                Box(modifier = Modifier.padding(horizontal = TrackyTokens.Spacing.M)) {
                                    CaloriesCard(
                                        foodCalories = totalFoodCals,
                                        exerciseCalories = totalExCals,
                                        goalCalories = goalCals,
                                        onEditGoals = onNavigateToGoals
                                    )
                                }

                                Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

                                Box(modifier = Modifier.padding(horizontal = TrackyTokens.Spacing.M)) {
                                    MacrosCard(
                                        carbsConsumed = summary?.carbsConsumedG ?: 0f,
                                        carbsTarget = dayGoal?.carbsTargetG ?: currentGoal?.carbsTargetG ?: 0f,
                                        proteinConsumed = summary?.proteinConsumedG ?: 0f,
                                        proteinTarget = dayGoal?.proteinTargetG ?: currentGoal?.proteinTargetG ?: 0f,
                                        fatConsumed = summary?.fatConsumedG ?: 0f,
                                        fatTarget = dayGoal?.fatTargetG ?: currentGoal?.fatTargetG ?: 0f
                                    )
                                }

                                Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

                                // 2. Chat Thread for this date
                                val threadVisibleMessages = dateChatMessages.filter { it.role != MessageRole.USER }
                                threadVisibleMessages.forEach { message ->
                                    Box(modifier = Modifier.padding(horizontal = TrackyTokens.Spacing.M)) {
                                        SwipeableChatMessageRow(
                                            message = message,
                                            onDelete = { viewModel.deleteChatMessage(message.id) },
                                            onClick = {
                                                if (message.messageType == ChatMessageType.SYSTEM_CONFIRMED &&
                                                    message.linkedFoodEntryId != null) {
                                                    editingMessage = message
                                                    editText = message.content ?: ""
                                                }
                                            }
                                        )
                                    }
                                }

                                // 3. Drafting State (Calculating...)
                                if (dateDraftState is DraftState.Drafting) {
                                    Column {
                                        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
                                        Box(modifier = Modifier.padding(horizontal = TrackyTokens.Spacing.M)) {
                                            TrackyDraftingState()
                                        }
                                    }
                                }

                                // 4. Draft Confirmation Card
                                when (dateDraftState) {
                                    is DraftState.FoodDraft -> {
                                        Column {
                                            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
                                            Box(modifier = Modifier.padding(horizontal = TrackyTokens.Spacing.M)) {
                                                FoodDraftCard(
                                                    draft = dateDraftState.data,
                                                    animate = uiState.shouldAnimateDraft,
                                                    onConfirm = { viewModel.confirmFoodDraft(dateDraftState.data) },
                                                    onCancel = viewModel::cancelDraft,
                                                    onItemClick = { index -> editingFoodIndex = index },
                                                    onAddClick = { 
                                                        quickAddType = QuickAddType.AmbulatoryFood 
                                                        showQuickAdd = true 
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    is DraftState.ExerciseDraft -> {
                                        Column {
                                            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
                                            Box(modifier = Modifier.padding(horizontal = TrackyTokens.Spacing.M)) {
                                                ExerciseDraftCard(
                                                    draft = dateDraftState.data,
                                                    animate = uiState.shouldAnimateDraft,
                                                    onConfirm = { viewModel.confirmExerciseDraft(dateDraftState.data) },
                                                    onCancel = viewModel::cancelDraft,
                                                    onItemClick = { index -> editingExerciseIndex = index },
                                                    onAddClick = { 
                                                        quickAddType = QuickAddType.AmbulatoryExercise
                                                        showQuickAdd = true 
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    else -> {}
                                }

                                // 5. Food & Exercise Entries
                                summary?.foodEntries?.let { entries ->
                                    if (entries.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
                                        Box(modifier = Modifier.padding(horizontal = TrackyTokens.Spacing.M)) {
                                            TrackyCardTitle(text = "Food")
                                        }
                                        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.XS))
                                        entries.forEach { entry ->
                                            Box(
                                                modifier = Modifier
                                                    .padding(horizontal = TrackyTokens.Spacing.M)
                                                    .padding(bottom = TrackyTokens.Spacing.DenseListSpacing)
                                            ) {
                                                SwipeableRow(
                                                    onDelete = { viewModel.deleteFoodEntry(entry.id) }
                                                ) {
                                                    FoodEntryRow(entry = entry, onClick = { onNavigateToEntryDetail(entry.id, "food") })
                                                }
                                            }
                                        }
                                    }
                                }

                                summary?.exerciseEntries?.let { entries ->
                                    if (entries.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
                                        Box(modifier = Modifier.padding(horizontal = TrackyTokens.Spacing.M)) {
                                            TrackyCardTitle(text = "Exercise")
                                        }
                                        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.XS))
                                        entries.forEach { entry ->
                                            Box(
                                                modifier = Modifier
                                                    .padding(horizontal = TrackyTokens.Spacing.M)
                                                    .padding(bottom = TrackyTokens.Spacing.DenseListSpacing)
                                            ) {
                                                SwipeableRow(
                                                    onDelete = { viewModel.deleteExerciseEntry(entry.id) }
                                                ) {
                                                    ExerciseEntryRow(entry = entry, onClick = { onNavigateToEntryDetail(entry.id, "exercise") })
                                                }
                                            }
                                        }
                                    }
                                }

                                // 6. Empty State
                                val hasEntries = (summary?.foodEntries?.isNotEmpty() == true) ||
                                                (summary?.exerciseEntries?.isNotEmpty() == true)
                                val isDrafting = dateDraftState is DraftState.Drafting ||
                                                dateDraftState is DraftState.FoodDraft ||
                                                dateDraftState is DraftState.ExerciseDraft
                                
                                if (!hasEntries && !isDrafting && threadVisibleMessages.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = TrackyTokens.Spacing.XL),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        TrackyBodySmall(
                                            text = "No logs for this day :(",
                                            color = TrackyColors.TextTertiary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                val keyboardController = LocalSoftwareKeyboardController.current
                // Composer bar - AI auto-detects food vs exercise
                ComposerBar(
                    inputText = uiState.inputText,
                    onInputChange = viewModel::updateInputText,
                    onSend = {
                        if (uiState.hapticsEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        keyboardController?.hide()
                        viewModel.logAutoFromText(uiState.inputText)
                    },
                    onCameraClick = { showCamera = true },
                    onGalleryClick = { galleryLauncher.launch("image/*") }
                )
            }

            // Streak Modal
            if (uiState.showStreakModal) {
                TrackyBottomSheet(onDismissRequest = viewModel::dismissStreakModal) {
                    StreakModalContent(
                        streakInfo = uiState.streakInfo,
                        onDismiss = viewModel::dismissStreakModal
                    )
                }
            }

            // Pill Menu Overlay (Persistent)
            AnimatedVisibility(
                visible = uiState.showSidebar,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { viewModel.toggleSidebar() }
                )
            }

            AnimatedVisibility(
                visible = uiState.showSidebar,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(top = TrackyTokens.Spacing.M, end = TrackyTokens.Spacing.M),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.S)
                    ) {
                        // The original menu button position (conceptual top row)
                        // Add some space to align with the top bar menu icon
                        Spacer(modifier = Modifier.height(48.dp))

                        PillMenuItem(
                            icon = Icons.Outlined.MonitorWeight,
                            text = "Tracking",
                            onClick = {
                                viewModel.toggleSidebar()
                                onNavigateToWeight()
                            }
                        )

                        PillMenuItem(
                            icon = Icons.Outlined.Settings,
                            text = "Settings",
                            onClick = {
                                viewModel.toggleSidebar()
                                onNavigateToSettings()
                            }
                        )

                        // Spacer remove to bring X closer to settings pill as requested

                        // Persistent Close Icon
                        IconButton(
                            onClick = viewModel::toggleSidebar,
                            modifier = Modifier
                                .size(40.dp)
                                .background(TrackyColors.Surface, CircleShape)
                        ) {
                            Icon(
                                Icons.Outlined.Close,
                                contentDescription = "Close Menu",
                                tint = TrackyColors.TextPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // Floating Hamburger Menu (Always visible, not blurred)
            val hamburgerBackgroundColor by animateColorAsState(
                targetValue = if (uiState.showSidebar) TrackyColors.Surface else Color.Transparent,
                label = "HamburgerBackground"
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = TrackyTokens.Spacing.M, end = TrackyTokens.Spacing.ScreenPadding)
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = viewModel::toggleSidebar,
                    modifier = Modifier.background(hamburgerBackgroundColor, CircleShape)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(2.dp)
                                .background(TrackyColors.TextSecondary, RoundedCornerShape(1.dp))
                        )
                        Box(
                            modifier = Modifier
                                .width(14.dp)
                                .height(2.dp)
                                .background(TrackyColors.TextSecondary, RoundedCornerShape(1.dp))
                        )
                    }
                }
            }


            // Snackbar for errors
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            // Offline Overlay
            com.tracky.app.ui.components.OfflineOverlay(
                visible = uiState.isOffline,
                onDismiss = viewModel::dismissOfflineOverlay
            )
        }
    }

    // Edit analysis sheet
    editingMessage?.let { message ->
        EditAnalysisSheet(
            content = editText,
            onContentChange = { editText = it },
            onDismiss = {
                editingMessage = null
                editText = ""
            },
            onReanalyze = {
                message.linkedFoodEntryId?.let { foodEntryId ->
                    viewModel.reanalyzeFoodEntry(foodEntryId, editText)
                }
                editingMessage = null
                editText = ""
            }
        )
    }
    
    // Quick Add Sheet
    if (showQuickAdd) {
        quickAddType?.let { type ->
            QuickAddSheet(
                type = type,
                onDismiss = { showQuickAdd = false },
                onAdd = { text ->
                    viewModel.addToDraft(text, type == QuickAddType.AmbulatoryFood)
                },
                onCameraClick = {
                    captureMode = if (type == QuickAddType.AmbulatoryFood) CaptureMode.QUICK_ADD_FOOD else CaptureMode.QUICK_ADD_EXERCISE
                    showCamera = true
                    showQuickAdd = false
                },
                onGalleryClick = {
                    captureMode = if (type == QuickAddType.AmbulatoryFood) CaptureMode.QUICK_ADD_FOOD else CaptureMode.QUICK_ADD_EXERCISE
                    galleryLauncher.launch("image/*")
                    showQuickAdd = false
                }
            )
        }
    }

    // Edit Food Draft Item Sheet
    editingFoodIndex?.let { index ->
        val draft = (draftState as? DraftState.FoodDraft)?.data
        if (draft != null && index in draft.items.indices) {
            val item = draft.items[index]
            EditFoodDraftItemSheet(
                item = item,
                onDismiss = { editingFoodIndex = null },
                onSave = { name, quantity, unit, cal, carb, prot, fat, manual ->
                    viewModel.updateFoodDraftItem(0L, index, name, quantity, unit, cal, carb, prot, fat, manual)
                    editingFoodIndex = null
                }
            )
        } else {
            editingFoodIndex = null
        }
    }

    // Edit Exercise Draft Item Sheet
    editingExerciseIndex?.let { index ->
        val draft = (draftState as? DraftState.ExerciseDraft)?.data
        if (draft != null && index in draft.items.indices) {
            val item = draft.items[index]
            EditExerciseDraftItemSheet(
                item = item,
                userWeightKg = uiState.userWeightKg,
                onDismiss = { editingExerciseIndex = null },
                onSave = { activity, duration, intensity, calories, isManual ->
                    viewModel.updateExerciseDraftItem(0L, index, activity, duration, intensity, calories, isManual)
                    editingExerciseIndex = null
                }
            )
        } else {
            editingExerciseIndex = null
        }
    }

    // Success Overlay
    val showSuccessOverlay by viewModel.showSuccessOverlay.collectAsState()
    SuccessOverlay(
        visible = showSuccessOverlay,
        onAnimationFinished = viewModel::dismissSuccessOverlay
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditAnalysisSheet(
    content: String,
    onContentChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onReanalyze: () -> Unit
) {
    TrackyBottomSheet(
        onDismissRequest = onDismiss,
        title = "Edit Food Analysis"
    ) {
        Column {
            TrackyBodySmall(
                text = "Edit the description and tap Re-analyze to update the nutritional information.",
                color = TrackyColors.TextSecondary
            )
            
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
            
            TrackyInput(
                value = content,
                onValueChange = onContentChange,
                label = "Food Description",
                placeholder = "Describe what you ate...",
                singleLine = false
            )

            TrackySheetActions(
                primaryText = "Re-analyze",
                onPrimaryClick = onReanalyze,
                primaryEnabled = content.isNotBlank(),
                secondaryText = "Cancel",
                onSecondaryClick = onDismiss
            )
        }
    }
}

@Composable
private fun CaloriesCard(
    foodCalories: Float,
    exerciseCalories: Float,
    goalCalories: Float,
    onEditGoals: () -> Unit
) {
    val percentage = if (goalCalories > 0) (foodCalories / goalCalories) * 100f else 0f
    
    // Derived from percentage to be reactive, but seeded to feel somewhat random per percentage bucket
    // We use the int value of percentage to make it stable for small fluctuations but dynamic for large changes
    val messageIndex = (percentage.toInt() + (foodCalories.toInt() % 3)).coerceAtLeast(0) % 5
    
    val message = when {
        foodCalories <= 0f -> listOf(
            "Ready to start?",
            "Ready to start your day?",
            "Log your first meal!",
            "Time to fuel up!",
            "Start your day!"
        )[messageIndex]
        percentage < 50f -> listOf(
            "Great progress!",
            "Solid start!",
            "Keep it going!",
            "Every bite counts!",
            "Building healthy habits!"
        )[messageIndex]
        percentage < 75f -> listOf(
            "Getting there!",
            "Over halfway!",
            "Doing great!",
            "Halfway there!",
            "Steady progress!"
        )[messageIndex]
        percentage < 90f -> listOf(
            "Nearly there!",
            "Almost there!",
            "Final stretch!",
            "Close to the finish!",
            "Goal in sight!"
        )[messageIndex]
        percentage <= 100f -> listOf(
            "Great job today!",
            "Nearly done!",
            "Perfect pacing!",
            "Beautifully balanced!",
            "Almost there!"
        )[messageIndex]
        else -> listOf(
            "Over the limit",
            "Over goal today",
            "A bit over—that's okay!",
            "Balance it out tomorrow",
            "Fresh start tomorrow!"
        )[messageIndex]
    }

    TrackyCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TrackyCardTitle(text = message)
            IconButton(
                onClick = onEditGoals,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Outlined.Edit,
                    contentDescription = "Edit Goals",
                    tint = TrackyColors.TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))
        TrackyCaloriesProgress(
            consumed = foodCalories,
            burned = exerciseCalories,
            goal = goalCalories
        )
    }
}

@Composable
private fun MacrosCard(
    carbsConsumed: Float,
    carbsTarget: Float,
    proteinConsumed: Float,
    proteinTarget: Float,
    fatConsumed: Float,
    fatTarget: Float
) {
    TrackyCard(onClick = {}) { // Non-clickable
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TrackyCircularMacroProgress(
                label = "Carbs",
                consumed = carbsConsumed,
                target = carbsTarget,
                color = TrackyColors.Success, // Green
                modifier = Modifier.weight(1f)
            )
            
            TrackyCircularMacroProgress(
                label = "Protein",
                consumed = proteinConsumed,
                target = proteinTarget,
                color = TrackyColors.Warning, // Red/Orange
                modifier = Modifier.weight(1f)
            )
            
            TrackyCircularMacroProgress(
                label = "Fat",
                consumed = fatConsumed,
                target = fatTarget,
                color = Color(0xFFFFD60A), // Yellow
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FoodDraftCard(
    draft: DraftData.FoodDraft,
    animate: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onItemClick: (Int) -> Unit,
    onAddClick: () -> Unit
) {
    val scale = remember { androidx.compose.animation.core.Animatable(if (animate) 0.9f else 1.0f) }
    
    LaunchedEffect(Unit) {
        if (animate) {
            scale.animateTo(
                targetValue = 1.05f,
                animationSpec = tween(150)
            )
            scale.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(100)
            )
        }
    }

//    }

    val isAnalyzing = remember(draft.items) { draft.items.any { it.isAnalyzing } }

    Box {
        TrackyCard(
            modifier = Modifier.scale(scale.value)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Restaurant,
                        contentDescription = null,
                        tint = TrackyColors.BrandPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(TrackyTokens.Spacing.XS))
                    TrackyCardTitle(text = "Confirm Food Entry")
                }
                IconButton(onClick = onAddClick, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.Outlined.Add,
                        contentDescription = "Add Item",
                        tint = TrackyColors.BrandPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

            draft.items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(index) }
                        .padding(vertical = TrackyTokens.Spacing.XXS),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = TrackyTokens.Spacing.S)) {
                        TrackyBodyText(
                            text = item.name,
                            maxLines = 1
                        )
                        TrackyBodySmall(
                            text = "${item.quantity.toSmartString()} ${item.unit}",
                            color = TrackyColors.TextSecondary,
                            maxLines = 1
                        )
                        if (item.resolved && !item.isAnalyzing) {
                            ProvenanceLabel(source = item.provenance.source)
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        if (item.isAnalyzing) {
                            TrackyLoadingIndicator(size = 16.dp, strokeWidth = 2.dp)
                        } else {
                            TrackyBodySmall(text = "${item.calories.toInt()} kcal")
                            TrackyText(
                                text = "C: ${item.carbsG.toInt()}g P: ${item.proteinG.toInt()}g F: ${item.fatG.toInt()}g",
                                style = TrackyTextStyle.LabelExtraSmall,
                                color = TrackyColors.TextTertiary
                            )
                        }
                    }
                }
            }

            TrackyDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TrackyBodyText(text = "Total")
                TrackyBodyText(
                    text = "${draft.totalCalories.toInt()} kcal",
                    color = TrackyColors.BrandPrimary
                )
            }

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.S)
            ) {
                TrackyButtonSecondary(
                    text = "Cancel",
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                )
                TrackyButtonPrimary(
                    text = "Confirm",
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        

    }
}

@Composable
private fun ExerciseDraftCard(
    draft: DraftData.ExerciseDraft,
    animate: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onItemClick: (Int) -> Unit,
    onAddClick: () -> Unit
) {
    val scale = remember { androidx.compose.animation.core.Animatable(if (animate) 0.9f else 1.0f) }

    LaunchedEffect(Unit) {
        if (animate) {
            scale.animateTo(
                targetValue = 1.05f,
                animationSpec = tween(150)
            )
            scale.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(100)
            )
        }
    }

//    }

    val isAnalyzing = remember(draft.items) { draft.items.any { it.isAnalyzing } }

    Box {
        TrackyCard(
            modifier = Modifier.scale(scale.value)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.FitnessCenter,
                        contentDescription = null,
                        tint = TrackyColors.Success,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(TrackyTokens.Spacing.XS))
                    TrackyCardTitle(text = "Confirm Exercise Entry")
                }
                IconButton(onClick = onAddClick, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.Outlined.Add,
                        contentDescription = "Add Item",
                        tint = TrackyColors.Success
                    )
                }
            }
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

            draft.items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(index) }
                        .padding(vertical = TrackyTokens.Spacing.XXS),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = TrackyTokens.Spacing.S)) {
                        TrackyBodyText(
                            text = item.activity,
                            maxLines = 1
                        )
                        TrackyBodySmall(
                            text = "${item.durationMinutes} min",
                            color = TrackyColors.TextSecondary,
                            maxLines = 1
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        if (item.isAnalyzing) {
                            TrackyLoadingIndicator(size = 16.dp, strokeWidth = 2.dp)
                        } else {
                            TrackyBodySmall(text = "${item.caloriesBurned.toInt()} kcal")
                            item.intensity.let { intensity ->
                                TrackyBodySmall(
                                    text = intensity.value.lowercase().replaceFirstChar { it.uppercase() },
                                    color = TrackyColors.TextTertiary
                                )
                            }
                        }
                    }
                }
            }

            TrackyDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TrackyBodyText(text = "Total Burned")
                TrackyBodyText(
                    text = "${draft.totalCalories.toInt()} kcal",
                    color = TrackyColors.Success
                )
            }

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.S)
            ) {
                TrackyButtonSecondary(
                    text = "Cancel",
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                )
                TrackyButtonPrimary(
                    text = "Confirm",
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        

    }
}

@Composable
private fun FoodEntryRow(
    entry: FoodEntry,
    onClick: () -> Unit
) {
    TrackyEntryCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.Restaurant,
                    contentDescription = null,
                    tint = TrackyColors.BrandPrimary
                )
                Spacer(modifier = Modifier.width(TrackyTokens.Spacing.S))
                Column {
                    TrackyBodyText(
                        text = entry.items.firstOrNull()?.name?.toTitleCase() ?: "Food entry",
                        maxLines = 1
                    )
                    if (entry.items.size > 1) {
                        TrackyBodySmall(
                            text = "+${entry.items.size - 1} more items",
                            color = TrackyColors.TextTertiary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(TrackyTokens.Spacing.S))
            Column(horizontalAlignment = Alignment.End) {
                TrackyBodyText(text = "${entry.totalCalories.toInt()} kcal")
                TrackyBodySmall(
                    text = entry.time.take(5),
                    color = TrackyColors.TextTertiary
                )
            }
        }
    }
}

@Composable
private fun ExerciseEntryRow(
    entry: ExerciseEntry,
    onClick: () -> Unit
) {
    TrackyEntryCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.FitnessCenter,
                    contentDescription = null,
                    tint = TrackyColors.Success
                )
                Spacer(modifier = Modifier.width(TrackyTokens.Spacing.S))
                Column {
                    val firstItem = entry.items.firstOrNull()?.activityName ?: "Exercise"
                    TrackyBodyText(
                        text = firstItem.toTitleCase(),
                        maxLines = 1
                    )
                    
                    if (entry.items.size > 1) {
                        TrackyBodySmall(
                            text = "+${entry.items.size - 1} more items",
                            color = TrackyColors.TextTertiary
                        )
                    } else {
                        val duration = entry.totalDurationMinutes
                        val intensity = entry.items.firstOrNull()?.intensity?.value?.replaceFirstChar { it.uppercase() } ?: "Moderate"
                        TrackyBodySmall(
                            text = "$duration min | $intensity",
                            color = TrackyColors.TextTertiary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(TrackyTokens.Spacing.S))
            Column(horizontalAlignment = Alignment.End) {
                TrackyBodyText(
                    text = "-${entry.totalCalories.toInt()} kcal",
                    color = TrackyColors.Success
                )
                TrackyBodySmall(
                    text = entry.time.take(5),
                    color = TrackyColors.TextTertiary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableFoodEntryRow(
    entry: FoodEntry,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        TrackyColors.Error,
                        RoundedCornerShape(TrackyTokens.Radii.L)
                    )
                    .padding(horizontal = TrackyTokens.Spacing.M),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = TrackyColors.TextOnPrimary
                )
            }
        },
        content = {
            FoodEntryRow(entry = entry, onClick = onClick)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableExerciseEntryRow(
    entry: ExerciseEntry,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        TrackyColors.Error,
                        RoundedCornerShape(TrackyTokens.Radii.L)
                    )
                    .padding(horizontal = TrackyTokens.Spacing.M),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = TrackyColors.TextOnPrimary
                )
            }
        },
        content = {
            ExerciseEntryRow(entry = entry, onClick = onClick)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableChatMessageRow(
    message: ChatMessage,
    onDelete: () -> Unit,
    onClick: () -> Unit = {}
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            // Delete background revealed on swipe
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        TrackyColors.Error,
                        RoundedCornerShape(TrackyTokens.Radii.M)
                    )
                    .padding(horizontal = TrackyTokens.Spacing.M),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = TrackyColors.TextOnPrimary
                )
            }
        },
        content = {
            ChatMessageRow(message = message, onClick = onClick)
        }
    )
}

@Composable
private fun ChatMessageRow(
    message: ChatMessage,
    onClick: () -> Unit = {}
) {
    val isUser = message.role == MessageRole.USER
    val isEditable = message.messageType == ChatMessageType.SYSTEM_CONFIRMED && 
                     message.linkedFoodEntryId != null

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TrackyColors.Background)
            .padding(vertical = TrackyTokens.Spacing.XXS)
            .then(
                if (isEditable) Modifier.clickable(onClick = onClick) else Modifier
            ),
        horizontalArrangement = Arrangement.Start
    ) {
        TrackyCard(
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            TrackyBodyText(
                text = message.content.orEmpty(),
                color = if (isUser) {
                    TrackyColors.TextPrimary
                } else {
                    TrackyColors.TextSecondary
                }
            )
            if (isEditable) {
                Spacer(modifier = Modifier.height(TrackyTokens.Spacing.XXS))
                TrackyBodySmall(
                    text = "Tap to edit",
                    color = TrackyColors.BrandPrimary
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ComposerBar(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    val isImeVisible = WindowInsets.isImeVisible
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isImeVisible) TrackyTokens.Spacing.XXS else 0.dp)
            .background(TrackyColors.Surface)
            .padding(TrackyTokens.Spacing.S)
    ) {
        // Input row - AI auto-detects food vs exercise
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.XS)
        ) {
            TrackyInput(
                value = inputText,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f),
                placeholder = "Send a message...",
                singleLine = true
            )

            IconButton(onClick = onCameraClick) {
                Icon(
                    Icons.Outlined.CameraAlt,
                    contentDescription = "Camera",
                    tint = TrackyColors.TextSecondary
                )
            }
            IconButton(onClick = onGalleryClick) {
                Icon(
                    Icons.Outlined.PhotoLibrary,
                    contentDescription = "Gallery",
                    tint = TrackyColors.TextSecondary
                )
            }

            IconButton(
                onClick = onSend,
                enabled = inputText.isNotBlank()
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.Send,
                    contentDescription = "Send",
                    tint = if (inputText.isNotBlank())
                        TrackyColors.BrandPrimary
                    else
                        TrackyColors.TextTertiary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickAddSheet(
    type: QuickAddType,
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    val title = if (type == QuickAddType.AmbulatoryFood) "Add Food" else "Add Exercise"
    val placeholder = if (type == QuickAddType.AmbulatoryFood) "e.g. 1 apple, 200g chicken" else "e.g. 30min running, 15 pushups"

    TrackyBottomSheet(
        onDismissRequest = onDismiss,
        title = title
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.XS)
            ) {
                TrackyInput(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = placeholder,
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onCameraClick) {
                    Icon(
                        Icons.Outlined.CameraAlt,
                        contentDescription = "Camera",
                        tint = TrackyColors.TextSecondary
                    )
                }
                IconButton(onClick = onGalleryClick) {
                    Icon(
                        Icons.Outlined.PhotoLibrary,
                        contentDescription = "Gallery",
                        tint = TrackyColors.TextSecondary
                    )
                }
            }
            
            TrackySheetActions(
                primaryText = "Add",
                onPrimaryClick = {
                    onAdd(text)
                    onDismiss()
                },
                primaryEnabled = text.isNotBlank(),
                secondaryText = "Cancel",
                onSecondaryClick = onDismiss
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditFoodDraftItemSheet(
    item: com.tracky.app.domain.model.DraftFoodItem,
    onDismiss: () -> Unit,
    onSave: (String, Double, String, Float, Float, Float, Float, Boolean) -> Unit
) {
    var name by remember { mutableStateOf(item.name) }
    var quantityText by remember { mutableStateOf(item.quantity.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() }) }
    var unit by remember { mutableStateOf(item.unit) }
    
    // Macros states
    var caloriesText by remember { mutableStateOf(kotlin.math.round(item.calories).toInt().toString()) }
    var carbsText by remember { mutableStateOf(kotlin.math.round(item.carbsG).toInt().toString()) }
    var proteinText by remember { mutableStateOf(kotlin.math.round(item.proteinG).toInt().toString()) }
    var fatText by remember { mutableStateOf(kotlin.math.round(item.fatG).toInt().toString()) }

    // Ratios for scaling
    val currentQty = item.quantity.toFloat()
    val currentCal = item.calories
    
    fun getRatio(value: Float): Float = if (currentQty > 0) value / currentQty else 0f
    fun getCalRatio(value: Float): Float = if (currentCal > 0) value / currentCal else 0f
    
    val ratioCals = remember { getRatio(item.calories) }
    val ratioCarbs = remember { getRatio(item.carbsG) }
    val ratioProt = remember { getRatio(item.proteinG) }
    val ratioFat = remember { getRatio(item.fatG) }
    
    val ratioCarbsFromCals = remember { getCalRatio(item.carbsG) }
    val ratioProtFromCals = remember { getCalRatio(item.proteinG) }
    val ratioFatFromCals = remember { getCalRatio(item.fatG) }

    // Original values for checking if manual edit badge should show
    val originalName = remember { item.name }
    val originalQty = remember { item.quantity }
    val originalUnit = remember { item.unit }
    val originalCals = remember { item.calories }
    val originalCarbs = remember { item.carbsG }
    val originalProt = remember { item.proteinG }
    val originalFat = remember { item.fatG }

    val isManual = remember(name, quantityText, unit, caloriesText, carbsText, proteinText, fatText) {
        val q = quantityText.toDoubleOrNull() ?: 0.0
        val cal = caloriesText.toFloatOrNull() ?: 0f
        val carb = carbsText.toFloatOrNull() ?: 0f
        val prot = proteinText.toFloatOrNull() ?: 0f
        val fat = fatText.toFloatOrNull() ?: 0f

        name != originalName || 
        q != originalQty || 
        unit != originalUnit ||
        kotlin.math.abs(cal - originalCals) > 1f ||
        kotlin.math.abs(carb - originalCarbs) > 0.5f ||
        kotlin.math.abs(prot - originalProt) > 0.5f ||
        kotlin.math.abs(fat - originalFat) > 0.5f
    }

    fun updateMacrosAutomatically(newQty: Double) {
        // Only auto-update macros if this is an AI estimate
        // User overrides or dataset items should NOT change ratios automatically
        if (item.provenance.source == com.tracky.app.domain.model.ProvenanceSource.AI_ESTIMATE) {
            val q = newQty.toFloat()
            caloriesText = kotlin.math.round(ratioCals * q).toInt().toString()
            carbsText = kotlin.math.round(ratioCarbs * q).toInt().toString()
            proteinText = kotlin.math.round(ratioProt * q).toInt().toString()
            fatText = kotlin.math.round(ratioFat * q).toInt().toString()
        }
    }
    
    fun updateMacrosFromCalories(newCals: Float) {
        // Only auto-update from calories if this is an AI estimate
        if (item.provenance.source == com.tracky.app.domain.model.ProvenanceSource.AI_ESTIMATE) {
            carbsText = kotlin.math.round(ratioCarbsFromCals * newCals).toInt().toString()
            proteinText = kotlin.math.round(ratioProtFromCals * newCals).toInt().toString()
            fatText = kotlin.math.round(ratioFatFromCals * newCals).toInt().toString()
        }
    }

    TrackyBottomSheet(
        onDismissRequest = onDismiss,
        title = null
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            // Header with badge
             Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = TrackyTokens.Spacing.M),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                androidx.compose.material3.Text(
                    text = "Edit Item",
                    style = com.tracky.app.ui.theme.TrackyTypography.HeadlineMedium,
                    color = TrackyColors.TextPrimary
                )
                
                if (isManual) {
                    TrackyBadge(
                        text = "Manual Edit",
                        style = BadgeStyle.WARNING,
                        compact = true
                    )
                }
            }

            TrackyInput(
                value = name,
                onValueChange = { name = it },
                label = "Name",
                placeholder = "Item name"
            )
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
            
            Row(horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.M)) {
                Box(modifier = Modifier.weight(1f)) {
                    com.tracky.app.ui.components.TrackyNumberInput(
                        value = quantityText,
                        onValueChange = { 
                            quantityText = it 
                            it.toDoubleOrNull()?.let { q -> updateMacrosAutomatically(q) }
                        },
                        label = "Quantity",
                        placeholder = "1.0",
                        allowDecimal = true
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    val commonUnits = listOf("serving", "g", "oz", "cup", "ml", "tbsp", "tsp")
                    com.tracky.app.ui.components.TrackySelect(
                        value = unit,
                        options = commonUnits,
                        onValueChange = { unit = it },
                        label = "Unit",
                        placeholder = "serving"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.L))
            TrackyCardTitle(text = "Nutritional Info (total)")
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

            com.tracky.app.ui.components.TrackyNumberInput(
                value = caloriesText,
                onValueChange = { 
                    caloriesText = it
                    it.toFloatOrNull()?.let { c -> updateMacrosFromCalories(c) }
                },
                label = "Calories",
                suffix = "kcal",
                allowDecimal = false
            )
            
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

            Row(horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.M)) {
                Box(modifier = Modifier.weight(1f)) {
                    com.tracky.app.ui.components.TrackyNumberInput(
                        value = carbsText,
                        onValueChange = { carbsText = it },
                        label = "Carbs",
                        suffix = "g",
                        allowDecimal = false
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    com.tracky.app.ui.components.TrackyNumberInput(
                        value = proteinText,
                        onValueChange = { proteinText = it },
                        label = "Protein",
                        suffix = "g",
                        allowDecimal = false
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    com.tracky.app.ui.components.TrackyNumberInput(
                        value = fatText,
                        onValueChange = { fatText = it },
                        label = "Fat",
                        suffix = "g",
                        allowDecimal = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.L))

            TrackySheetActions(
                primaryText = "Save",
                onPrimaryClick = {
                    val q = quantityText.toDoubleOrNull() ?: 1.0
                    val cal = caloriesText.toFloatOrNull() ?: 0f
                    val carb = carbsText.toFloatOrNull() ?: 0f
                    val prot = proteinText.toFloatOrNull() ?: 0f
                    val fat = fatText.toFloatOrNull() ?: 0f
                    onSave(name, q, unit, cal, carb, prot, fat, isManual)
                },
                secondaryText = "Cancel",
                onSecondaryClick = onDismiss
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditExerciseDraftItemSheet(
    item: com.tracky.app.domain.model.DraftExerciseItem,
    userWeightKg: Float,
    onDismiss: () -> Unit,
    onSave: (String, Int, com.tracky.app.domain.model.ExerciseIntensity, Float, Boolean) -> Unit
) {
    // Local state
    var activity by remember { mutableStateOf(item.activity) }
    var durationText by remember { mutableStateOf(item.durationMinutes.toString()) }
    var caloriesText by remember { mutableStateOf(kotlin.math.round(item.caloriesBurned).toInt().toString()) }
    var intensity by remember { mutableStateOf(item.intensity) }
    
    // Store original values to determine "Manual Edit" state
    // We assume the passed 'item' is the original state when sheet opens
    val originalActivity = remember { item.activity }
    val originalDuration = remember { item.durationMinutes }
    val originalCalories = remember { item.caloriesBurned }
    val originalIntensity = remember { item.intensity }

    // Derive isManual based on difference from original
    // Use a tolerance for float comparison
    val isManual = remember(activity, durationText, caloriesText, intensity) {
        val d = durationText.toIntOrNull() ?: 0
        val c = caloriesText.toFloatOrNull() ?: 0f
        
        activity != originalActivity ||
        d != originalDuration ||
        kotlin.math.abs(c - originalCalories) > 1.0f ||
        intensity != originalIntensity
    }

    // Calculation Helpers
    fun calculateCalories(met: Float, weight: Float, minutes: Int): Float {
        return (met * 3.5f * weight * minutes) / 200f
    }

    fun adjustMetForIntensity(currentMet: Float, oldIntensity: com.tracky.app.domain.model.ExerciseIntensity?, newIntensity: com.tracky.app.domain.model.ExerciseIntensity): Float {
        if (oldIntensity == newIntensity) return currentMet
        if (currentMet <= 0f) return 0f

        fun getFactor(i: com.tracky.app.domain.model.ExerciseIntensity?): Float = when(i) {
            com.tracky.app.domain.model.ExerciseIntensity.LOW -> 0.7f
            com.tracky.app.domain.model.ExerciseIntensity.MODERATE -> 1.0f
            com.tracky.app.domain.model.ExerciseIntensity.HIGH -> 1.4f
            null -> 1.0f
        }

        val oldFactor = getFactor(oldIntensity ?: com.tracky.app.domain.model.ExerciseIntensity.MODERATE)
        val newFactor = getFactor(newIntensity)
        return currentMet * (newFactor / oldFactor)
    }

    // Update handler - updates calories when Duration/Intensity changes
    fun updateCaloriesAuto() {
        val d = durationText.toIntOrNull() ?: return
        if (item.metValue > 0 && userWeightKg > 0) {
            // Adjust MET based on *current* intensity vs *item's original* MET-intensity (which draft doesn't explicitly store separately, but we assume item.metValue corresponds to item.intensity)
            // Actually item.metValue matches item.intensity.
            // But if we changed intensity from A to B, we need to adjust item.metValue.
            val adjustedMet = adjustMetForIntensity(item.metValue, item.intensity, intensity)
            val newCals = calculateCalories(adjustedMet, userWeightKg, d)
            caloriesText = kotlin.math.round(newCals).toInt().toString()
        }
    }

    TrackyBottomSheet(
        onDismissRequest = onDismiss,
        title = null
    ) {
        Column {
            // Header with badge
             Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = TrackyTokens.Spacing.M),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                androidx.compose.material3.Text(
                    text = "Edit Exercise",
                    style = com.tracky.app.ui.theme.TrackyTypography.HeadlineMedium,
                    color = TrackyColors.TextPrimary
                )
                
                if (isManual) {
                    TrackyBadge(
                        text = "Manual Edit",
                        style = BadgeStyle.WARNING,
                        compact = true
                    )
                }
            }

            TrackyInput(
                value = activity,
                onValueChange = { activity = it },
                label = "Activity",
                placeholder = "Running, Walking, etc."
            )
            
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
            
            // Intensity Selector
            TrackyBodySmall(text = "Intensity", color = TrackyColors.TextSecondary)
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.XS))
            Row(horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.XS)) {
                com.tracky.app.domain.model.ExerciseIntensity.values().forEach { level ->
                    TrackyChip(
                        label = level.name.lowercase().replaceFirstChar { it.uppercase() },
                        selected = intensity == level,
                        onClick = { 
                            intensity = level 
                            updateCaloriesAuto()
                        },
                        compact = true
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

            Row(horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.M)) {
                Box(modifier = Modifier.weight(1f)) {
                    com.tracky.app.ui.components.TrackyNumberInput(
                        value = durationText,
                        onValueChange = { 
                            durationText = it 
                            updateCaloriesAuto()
                        },
                        label = "Duration",
                        placeholder = "30",
                        allowDecimal = false
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    com.tracky.app.ui.components.TrackySelect(
                        value = "min",
                        options = listOf("min", "hours"),
                        onValueChange = { /* Handle unit change */ },
                        label = "Unit",
                        placeholder = "min"
                    )
                }
            }

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

            com.tracky.app.ui.components.TrackyNumberInput(
                value = caloriesText,
                onValueChange = { caloriesText = it }, // Manual override allowed
                label = "Calories Burned",
                suffix = "kcal",
                placeholder = "100",
                allowDecimal = false
            )
            
            TrackySheetActions(
                primaryText = "Save",
                onPrimaryClick = {
                    val d = durationText.toIntOrNull() ?: 0
                    val c = caloriesText.toFloatOrNull() ?: 0f
                    onSave(activity, d, intensity, c, isManual)
                },
                secondaryText = "Cancel",
                onSecondaryClick = onDismiss
            )
        }
    }
}
@Composable
private fun PillMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    isDanger: Boolean = false
) {
    Surface(
        onClick = onClick,
        shape = androidx.compose.foundation.shape.CircleShape,
        color = if (isDanger) TrackyColors.Error.copy(alpha = 0.1f) else TrackyColors.Surface,
        modifier = Modifier.padding(horizontal = 4.dp),
        shadowElevation = 4.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = TrackyTokens.Spacing.M, vertical = TrackyTokens.Spacing.S),
            horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.S)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDanger) TrackyColors.Error else TrackyColors.TextPrimary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = text,
                style = TrackyTypography.BodyMedium,
                color = if (isDanger) TrackyColors.Error else TrackyColors.TextPrimary
            )
        }
    }
}

enum class QuickAddType {
    AmbulatoryFood,
    AmbulatoryExercise
}

private enum class CaptureMode {
    AUTO_LOG,
    QUICK_ADD_FOOD,
    QUICK_ADD_EXERCISE
}
