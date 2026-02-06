package com.tracky.app.ui.screens.home

import com.tracky.app.ui.utils.toSmartString

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.tracky.app.ui.components.DayStatus
import com.tracky.app.ui.components.TrackyBodySmall
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
import com.tracky.app.ui.components.TrackyCircularMacroProgress
import com.tracky.app.ui.components.TrackyMacrosRow
import com.tracky.app.ui.components.TrackyScreenTitle
import com.tracky.app.ui.components.TrackySheetActions
import com.tracky.app.ui.components.SwipeableRow
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyTokens
import com.tracky.app.ui.theme.TrackyTypography
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToGoals: () -> Unit,
    onNavigateToWeight: () -> Unit,
    onNavigateToSavedEntries: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToEntryDetail: (Long, String) -> Unit,
    onNavigateToSummary: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val draftState by viewModel.draftState.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
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

    // Show error from uiState
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    // Show error from draftState
    LaunchedEffect(draftState) {
        if (draftState is DraftState.Error) {
            val error = (draftState as DraftState.Error).message
            snackbarHostState.showSnackbar(error)
        }
    }

    // Gallery picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val base64 = uriToBase64(context, it)
            base64?.let { encoded ->
                viewModel.logAutoFromImage(encoded)
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
                viewModel.logAutoFromImage(base64)
            },
            onGallerySelected = { uri ->
                showCamera = false
                val base64 = uriToBase64(context, uri)
                base64?.let { viewModel.logAutoFromImage(it) }
            },
            onDismiss = { showCamera = false }
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(TrackyColors.Background)
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
                    Row {
                        IconButton(onClick = onNavigateToWeight) {
                            Icon(
                                Icons.Outlined.MonitorWeight,
                                contentDescription = "Weight Tracker",
                                tint = TrackyColors.TextSecondary
                            )
                        }
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(
                                Icons.Outlined.Settings,
                                contentDescription = "Settings",
                                tint = TrackyColors.TextSecondary
                            )
                        }
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
                            if (summary.foodCalories > (summary.goal?.calorieGoalKcal ?: currentGoal?.calorieGoalKcal ?: 2000f)) {
                                DayStatus.FAILURE
                            } else if (summary.foodCalories > 0) {
                                DayStatus.SUCCESS
                            } else {
                                DayStatus.NONE
                            }
                        } ?: DayStatus.NONE

                        TrackyDayChip(
                            dayLetter = date.dayOfWeek.name.take(1),
                            dateNumber = date.dayOfMonth.toString(),
                            selected = isSelected,
                            isToday = isToday,
                            onClick = { viewModel.selectDate(date) },
                            status = dayStatus
                        )
                    }
                }

                Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

                // Track swipe for day navigation
                var swipeOffset by remember { mutableStateOf(0f) }
                val swipeThreshold = 100f

                // Main content with swipe gesture for day navigation
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragEnd = {
                                    if (swipeOffset > swipeThreshold) {
                                        // Swipe right -> previous day
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.selectPreviousDay()
                                    } else if (swipeOffset < -swipeThreshold) {
                                        // Swipe left -> next day
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.selectNextDay()
                                    }
                                    swipeOffset = 0f
                                },
                                onHorizontalDrag = { _, dragAmount ->
                                    swipeOffset += dragAmount
                                }
                            )
                        }
                ) {

                    // Calories card - daily goal progress
                    item {
                        val dayGoal = uiState.currentSummary?.goal
                        Box(modifier = Modifier.padding(horizontal = TrackyTokens.Spacing.M)) {
                            CaloriesCard(
                                foodCalories = uiState.currentSummary?.foodCalories ?: 0f,
                                exerciseCalories = uiState.currentSummary?.exerciseCalories ?: 0f,
                                goalCalories = dayGoal?.calorieGoalKcal ?: currentGoal?.calorieGoalKcal ?: 2000f,
                                onEditGoals = onNavigateToGoals
                            )
                        }
                    }

                    // Macros card - use currentGoal as fallback when day-specific goal is null
                    item {
                        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
                        val dayGoal = uiState.currentSummary?.goal
                        Box(modifier = Modifier.padding(horizontal = TrackyTokens.Spacing.M)) {
                            MacrosCard(
                                carbsConsumed = uiState.currentSummary?.carbsConsumedG ?: 0f,
                                carbsTarget = dayGoal?.carbsTargetG ?: currentGoal?.carbsTargetG ?: 0f,
                                proteinConsumed = uiState.currentSummary?.proteinConsumedG ?: 0f,
                                proteinTarget = dayGoal?.proteinTargetG ?: currentGoal?.proteinTargetG ?: 0f,
                                fatConsumed = uiState.currentSummary?.fatConsumedG ?: 0f,
                                fatTarget = dayGoal?.fatTargetG ?: currentGoal?.fatTargetG ?: 0f
                            )
                        }
                    }

                    // Chat thread
                    val threadVisibleMessages = chatMessages.filter { it.role != MessageRole.USER }

                    if (threadVisibleMessages.isNotEmpty()) {
                        items(threadVisibleMessages, key = { it.id }) { message ->
                            Box(modifier = Modifier.padding(horizontal = TrackyTokens.Spacing.M)) {
                                SwipeableChatMessageRow(
                                    message = message,
                                    onDelete = { viewModel.deleteChatMessage(message.id) },
                                    onClick = {
                                        // Only allow editing system confirmed messages with linked food entry
                                        if (message.messageType == ChatMessageType.SYSTEM_CONFIRMED &&
                                            message.linkedFoodEntryId != null) {
                                            editingMessage = message
                                            editText = message.content ?: ""
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // Draft card (if drafting)
                    item {
                        AnimatedVisibility(
                            visible = draftState is DraftState.Drafting,
                            enter = fadeIn(
                                tween(200, easing = FastOutSlowInEasing)
                            ) + slideInVertically(
                                tween(200, easing = FastOutSlowInEasing)
                            ) + scaleIn(
                                tween(200, easing = FastOutSlowInEasing),
                                initialScale = 0.92f
                            ),
                            exit = fadeOut() + slideOutVertically()
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
                                Box(modifier = Modifier.padding(horizontal = TrackyTokens.Spacing.M)) {
                                    TrackyDraftingState()
                                }
                            }
                        }
                    }

                    // Draft confirmation card
                    item {
                        when (val state = draftState) {
                            is DraftState.FoodDraft -> {
                                Column {
                                    Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
                                    Box(modifier = Modifier.padding(horizontal = TrackyTokens.Spacing.M)) {
                                        FoodDraftCard(
                                            draft = state.data,
                                            onConfirm = { viewModel.confirmFoodDraft(state.data) },
                                            onCancel = viewModel::cancelDraft,
                                            onItemClick = { index -> editingFoodIndex = index }
                                        )
                                    }
                                }
                            }
                            is DraftState.ExerciseDraft -> {
                                Column {
                                    Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
                                    Box(modifier = Modifier.padding(horizontal = TrackyTokens.Spacing.M)) {
                                        ExerciseDraftCard(
                                            draft = state.data,
                                            onConfirm = { viewModel.confirmExerciseDraft(state.data) },
                                            onCancel = viewModel::cancelDraft,
                                            onItemClick = { index -> editingExerciseIndex = index }
                                        )
                                    }
                                }
                            }
                            else -> {}
                        }
                    }

                    // Food entries with swipe-to-delete
                    uiState.currentSummary?.foodEntries?.let { entries ->
                        if (entries.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
                                Box(modifier = Modifier.padding(horizontal = TrackyTokens.Spacing.M)) {
                                    TrackyCardTitle(text = "Food")
                                }
                                Spacer(modifier = Modifier.height(TrackyTokens.Spacing.XS))
                            }
                            items(entries, key = { it.id }) { entry ->
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

                    // Exercise entries with swipe-to-delete
                    uiState.currentSummary?.exerciseEntries?.let { entries ->
                        if (entries.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
                                Box(modifier = Modifier.padding(horizontal = TrackyTokens.Spacing.M)) {
                                    TrackyCardTitle(text = "Exercise")
                                }
                                Spacer(modifier = Modifier.height(TrackyTokens.Spacing.XS))
                            }
                            items(entries, key = { it.id }) { entry ->
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

                    // Empty state for no entries - hide if entries exist OR if drafting
                    val hasEntries = (uiState.currentSummary?.foodEntries?.isNotEmpty() == true) ||
                                    (uiState.currentSummary?.exerciseEntries?.isNotEmpty() == true)
                    val isDrafting = draftState is DraftState.Drafting ||
                                    draftState is DraftState.FoodDraft ||
                                    draftState is DraftState.ExerciseDraft
                    val emptyStateVisibleMessages = chatMessages.filter { it.role != MessageRole.USER }

                    if (!hasEntries && !isDrafting && emptyStateVisibleMessages.isEmpty()) {
                        item {
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

                    // Bottom padding for composer
                    item {
                        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.XXXL))
                    }
                }

                // Composer bar - AI auto-detects food vs exercise
                ComposerBar(
                    inputText = uiState.inputText,
                    onInputChange = viewModel::updateInputText,
                    onSend = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.logAutoFromText(uiState.inputText)
                    },
                    onCameraClick = { showCamera = true },
                    onGalleryClick = { galleryLauncher.launch("image/*") }
                )
            }

            // Snackbar for errors
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
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

    // Edit Food Draft Item Sheet
    editingFoodIndex?.let { index ->
        val draft = (draftState as? DraftState.FoodDraft)?.data
        if (draft != null && index in draft.items.indices) {
            val item = draft.items[index]
            EditFoodDraftItemSheet(
                item = item,
                onDismiss = { editingFoodIndex = null },
                onSave = { name, quantity, unit ->
                    viewModel.updateFoodDraftItem(0L, index, name, quantity, unit)
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
                onDismiss = { editingExerciseIndex = null },
                onSave = { activity, duration ->
                    viewModel.updateExerciseDraftItem(0L, index, activity, duration)
                    editingExerciseIndex = null
                }
            )
        } else {
            editingExerciseIndex = null
        }
    }
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
    TrackyCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TrackyCardTitle(text = "Calories")
            TrackyChip(
                label = "Edit Goals",
                selected = false,
                onClick = onEditGoals,
                compact = true
            )
        }
        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
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
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onItemClick: (Int) -> Unit
) {
    TrackyCard {
        TrackyCardTitle(text = "Confirm Food Entry")
        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

        draft.items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(index) }
                    .padding(vertical = TrackyTokens.Spacing.XXS),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TrackyBodyText(text = "${item.quantity.toSmartString()} ${item.unit} ${item.name}")
                TrackyBodySmall(text = "${item.calories.toInt()} kcal")
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

@Composable
private fun ExerciseDraftCard(
    draft: DraftData.ExerciseDraft,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onItemClick: (Int) -> Unit
) {
    TrackyCard {
        TrackyCardTitle(text = "Confirm Exercise Entry")
        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

        draft.items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(index) }
                    .padding(vertical = TrackyTokens.Spacing.XXS),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TrackyBodyText(text = item.activity)
                TrackyBodySmall(text = "${item.durationMinutes} min")
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.Restaurant,
                    contentDescription = null,
                    tint = TrackyColors.BrandPrimary
                )
                Spacer(modifier = Modifier.width(TrackyTokens.Spacing.S))
                Column {
                    TrackyBodyText(
                        text = entry.items.firstOrNull()?.name ?: "Food entry",
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.FitnessCenter,
                    contentDescription = null,
                    tint = TrackyColors.Success
                )
                Spacer(modifier = Modifier.width(TrackyTokens.Spacing.S))
                Column {
                    val firstItem = entry.items.firstOrNull()?.activityName ?: "Exercise"
                    TrackyBodyText(
                        text = firstItem.lowercase().replaceFirstChar { it.uppercase() },
                        maxLines = 1
                    )
                    
                    if (entry.items.size > 1) {
                         TrackyBodySmall(
                            text = "+${entry.items.size - 1} more items",
                            color = TrackyColors.TextTertiary
                        )
                    } else {
                        TrackyBodySmall(
                            text = "${entry.totalDurationMinutes} min",
                            color = TrackyColors.TextTertiary
                        )
                    }
                }
            }
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
                    Icons.Outlined.Send,
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
private fun EditFoodDraftItemSheet(
    item: com.tracky.app.domain.model.DraftFoodItem,
    onDismiss: () -> Unit,
    onSave: (String, Double, String) -> Unit
) {
    var name by remember { mutableStateOf(item.name) }
    var quantity by remember { mutableStateOf(item.quantity.toString()) }
    var unit by remember { mutableStateOf(item.unit) }

    TrackyBottomSheet(
        onDismissRequest = onDismiss,
        title = "Edit Item"
    ) {
        Column {
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
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = "Quantity",
                        placeholder = "1.0"
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    TrackyInput(
                        value = unit,
                        onValueChange = { unit = it },
                        label = "Unit",
                        placeholder = "serving"
                    )
                }
            }
            TrackySheetActions(
                primaryText = "Save",
                onPrimaryClick = {
                    val q = quantity.toDoubleOrNull() ?: 1.0
                    onSave(name, q, unit)
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
    onDismiss: () -> Unit,
    onSave: (String, Int) -> Unit
) {
    var activity by remember { mutableStateOf(item.activity) }
    var duration by remember { mutableStateOf(item.durationMinutes.toString()) }

    TrackyBottomSheet(
        onDismissRequest = onDismiss,
        title = "Edit Exercise"
    ) {
        Column {
            TrackyInput(
                value = activity,
                onValueChange = { activity = it },
                label = "Activity",
                placeholder = "Running, Walking, etc."
            )
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
            com.tracky.app.ui.components.TrackyNumberInput(
                value = duration,
                onValueChange = { duration = it },
                label = "Duration (minutes)",
                placeholder = "30",
                allowDecimal = false
            )
            
            TrackySheetActions(
                primaryText = "Save",
                onPrimaryClick = {
                    val d = duration.toIntOrNull() ?: 30
                    onSave(activity, d)
                },
                secondaryText = "Cancel",
                onSecondaryClick = onDismiss
            )
        }
    }
}
