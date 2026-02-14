package com.tracky.app.ui.screens.entrydetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.hilt.navigation.compose.hiltViewModel
import com.tracky.app.domain.model.ExerciseEntry
import com.tracky.app.domain.model.ExerciseItem
import com.tracky.app.domain.model.FoodEntry
import com.tracky.app.domain.model.FoodItem
import com.tracky.app.ui.components.SwipeableRow
import com.tracky.app.ui.components.TrackyBodySmall
import com.tracky.app.ui.components.TrackyBodyText
import com.tracky.app.ui.components.TrackyCard
import com.tracky.app.ui.components.TrackyCardTitle
import com.tracky.app.ui.components.TrackyCircularMacroProgress
import com.tracky.app.ui.components.TrackyDivider
import com.tracky.app.ui.components.TrackyFullScreenLoading
import com.tracky.app.ui.components.TrackyInfoCard
import com.tracky.app.ui.components.TrackyLoadingIndicator
import com.tracky.app.ui.components.TrackySectionTitle
import com.tracky.app.ui.components.TrackyTopBarWithBack
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import com.tracky.app.ui.components.TrackySelect
import com.tracky.app.ui.components.TrackyBadge
import com.tracky.app.ui.components.BadgeStyle
import com.tracky.app.ui.components.TrackyChip
import com.tracky.app.ui.components.TrackyBottomSheet
import com.tracky.app.ui.components.ProvenanceLabel
import com.tracky.app.ui.components.TrackyButtonPrimary
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryDetailScreen(
    entryId: Long,
    entryType: String,
    viewModel: EntryDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onReanalyze: (String, Long, String) -> Unit,
    onEntryDeleted: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showEditSheet by remember { mutableStateOf(false) }
    var showActionsSheet by remember { mutableStateOf(false) }
    var showDateTimeSheet by remember { mutableStateOf(false) }
    var showAddItemSheet by remember { mutableStateOf(false) }

    // Suggestion Review State
    var foodSuggestionToReview by remember { mutableStateOf<FoodItem?>(null) }
    var exerciseSuggestionToReview by remember { mutableStateOf<ExerciseItem?>(null) }

    // Navigate back when entry is deleted
    LaunchedEffect(uiState.entryDeleted) {
        if (uiState.entryDeleted) {
            onEntryDeleted()
        }
    }

    Scaffold(
        topBar = {
            val title = if (entryType == "food") {
                val items = uiState.foodEntry?.items
                if (!items.isNullOrEmpty()) {
                    val firstItem = items.first().name
                    val remainingCount = items.size - 1
                    
                    if (remainingCount > 0) {
                        buildAnnotatedString {
                            append(firstItem)
                            withStyle(SpanStyle(color = TrackyColors.TextTertiary)) {
                                append(" + $remainingCount others")
                            }
                        }
                    } else {
                        AnnotatedString(firstItem)
                    }
                } else {
                    AnnotatedString("Food Entry")
                }
            } else {
                val items = uiState.exerciseEntry?.items
                if (!items.isNullOrEmpty()) {
                    val firstItem = items.first().activityName
                    val remainingCount = items.size - 1
                    
                    if (remainingCount > 0) {
                        buildAnnotatedString {
                            append(firstItem)
                            withStyle(SpanStyle(color = TrackyColors.TextTertiary)) {
                                append(" + $remainingCount others")
                            }
                        }
                    } else {
                        AnnotatedString(firstItem)
                    }
                } else {
                    AnnotatedString("Exercise Entry")
                }
            }

            TrackyTopBarWithBack(
                title = title,
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(onClick = { showAddItemSheet = true }) {
                        Icon(
                            Icons.Outlined.Add,
                            contentDescription = "Add Item",
                            tint = TrackyColors.BrandPrimary
                        )
                    }
                    IconButton(onClick = { showActionsSheet = true }) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = "Actions",
                            tint = TrackyColors.TextSecondary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            TrackyFullScreenLoading()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(TrackyColors.Background)
                    .padding(paddingValues)
                    .padding(horizontal = TrackyTokens.Spacing.ScreenPadding)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.M)
            ) {
                Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

                when {
                    uiState.foodEntry != null -> FoodEntryDetail(
                        entry = uiState.foodEntry!!,
                        onItemDelete = viewModel::deleteFoodItem,
                        onReviewClick = { foodSuggestionToReview = it }
                    )
                    uiState.exerciseEntry != null -> ExerciseEntryDetail(
                        entry = uiState.exerciseEntry!!,
                        onItemDelete = viewModel::deleteExerciseItem,
                        onReviewClick = { exerciseSuggestionToReview = it }
                    )
                }

                Spacer(modifier = Modifier.height(TrackyTokens.Spacing.L))
            }
        }
    }
    
    // Review Suggestions Sheets
    if (foodSuggestionToReview != null) {
        ReviewSuggestionDialog(
            originalFood = foodSuggestionToReview,
            suggestionFood = foodSuggestionToReview?.pendingSuggestion,
            onDismiss = { foodSuggestionToReview = null },
            onApply = {
                foodSuggestionToReview?.let { viewModel.applyFoodSuggestion(it) }
                foodSuggestionToReview = null
            },
            onDiscard = {
                foodSuggestionToReview?.let { viewModel.discardFoodSuggestion(it) }
                foodSuggestionToReview = null
            }
        )
    }
    
    if (exerciseSuggestionToReview != null) {
        ReviewSuggestionDialog(
            originalExercise = exerciseSuggestionToReview,
            suggestionExercise = exerciseSuggestionToReview?.pendingSuggestion,
            onDismiss = { exerciseSuggestionToReview = null },
            onApply = {
                 exerciseSuggestionToReview?.let { viewModel.applyExerciseSuggestion(it) }
                 exerciseSuggestionToReview = null
            },
            onDiscard = {
                 exerciseSuggestionToReview?.let { viewModel.discardExerciseSuggestion(it) }
                 exerciseSuggestionToReview = null
            }
        )
    }

    // Actions sheet
    if (showActionsSheet) {
        EntryActionsSheet(
            onDismiss = { showActionsSheet = false },
            onEdit = { 
                 showActionsSheet = false
                 showEditSheet = true 
            },
            onReanalyze = {
                showActionsSheet = false
                val query = if (entryType == "food") {
                    uiState.foodEntry?.let { entry ->
                        entry.originalInput?.takeIf { it.isNotBlank() }
                            ?: entry.items.joinToString(", ") { "${it.quantity} ${it.unit} ${it.name}" }
                    }
                } else {
                    uiState.exerciseEntry?.let { entry ->
                        entry.originalInput?.takeIf { it.isNotBlank() }
                            ?: entry.items.joinToString(", ") { "${it.activityName} ${it.durationMinutes} min" }
                    }
                }
                
                query?.let { onReanalyze(it, entryId, entryType) }
            },
            onChangeDateTime = {
                 showActionsSheet = false
                 showDateTimeSheet = true 
            },
            onDelete = {
                viewModel.deleteEntry()
                onEntryDeleted()
            }
        )
    }

    // Edit sheets
    if (showEditSheet) {
        uiState.foodEntry?.let { entry ->
            EditFoodEntrySheet(
                entry = entry,
                onDismiss = { showEditSheet = false },
                onSave = { updatedEntry ->
                    viewModel.updateFoodEntry(updatedEntry)
                    showEditSheet = false
                }
            )
        }
        uiState.exerciseEntry?.let { entry ->
            EditExerciseEntrySheet(
                entry = entry,
                onDismiss = { showEditSheet = false },
                onSave = { updatedEntry ->
                    viewModel.updateExerciseEntry(updatedEntry)
                    showEditSheet = false
                }
            )
        }
    }

    // Add Item Sheet
    if (showAddItemSheet) {
        AddItemSheet(
            entryType = entryType,
            onDismiss = { showAddItemSheet = false },
            onAddFood = { name, quantity, unit ->
                viewModel.addFoodItem(name, quantity, unit)
                showAddItemSheet = false
            },
            onAddExercise = { activity, duration ->
                viewModel.addExerciseItem(activity, duration)
                showAddItemSheet = false
            }
        )
    }



    // Change date/time sheet
    if (showDateTimeSheet) {
        val currentDate = uiState.foodEntry?.date ?: uiState.exerciseEntry?.date ?: ""
        val currentTime = uiState.foodEntry?.time ?: uiState.exerciseEntry?.time ?: ""

        ChangeDateTimeSheet(
            currentDate = currentDate,
            currentTime = currentTime,
            onDismiss = { showDateTimeSheet = false },
            onSave = { date, time ->
                viewModel.updateDateTime(date, time)
                showDateTimeSheet = false
            }
        )
    }
}

@Composable
private fun FoodEntryDetail(
    entry: FoodEntry,
    onItemDelete: (FoodItem) -> Unit,
    onReviewClick: (FoodItem) -> Unit
) {
    // Summary card
    TrackyCard {
        TrackyCardTitle(text = "Summary")
        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TrackyBodyText(text = "Total Calories")
            TrackyBodyText(
                text = "${entry.totalCalories} kcal",
                color = TrackyColors.BrandPrimary
            )
        }

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

        // Circular Macros
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TrackyCircularMacroProgress(
                label = "Carbs",
                consumed = entry.totalCarbsG,
                target = entry.totalCarbsG, // Show full circle
                color = TrackyColors.Success, // Green
                showPercentage = false
            )
            TrackyCircularMacroProgress(
                label = "Protein",
                consumed = entry.totalProteinG,
                target = entry.totalProteinG,
                color = TrackyColors.Warning, // Orange
                showPercentage = false
            )
            TrackyCircularMacroProgress(
                label = "Fat",
                consumed = entry.totalFatG,
                target = entry.totalFatG,
                color = Color(0xFFFFD60A), // Yellow
                showPercentage = false
            )
        }
    }

    // Items breakdown
    TrackySectionTitle(text = "Items")

    entry.items.forEach { item ->
        SwipeableRow(onDelete = { onItemDelete(item) }) {
            FoodItemRow(item = item, onReviewClick = onReviewClick)
        }
    }

    // Metadata
    TrackyCard {
        TrackyCardTitle(text = "Details")
        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

        DetailRow("Date", entry.date)
        DetailRow("Time", entry.time.take(5))

        entry.originalInput?.let { input ->
            if (input.isNotEmpty()) {
                DetailRow("Original Input", input)
            }
        }
    }
}

@Composable
private fun ExerciseEntryDetail(
    entry: ExerciseEntry,
    onItemDelete: (ExerciseItem) -> Unit,
    onReviewClick: (ExerciseItem) -> Unit
) {
    // Summary card
    TrackyCard {
        TrackyCardTitle(text = "Summary")
        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TrackyBodyText(text = "Total Duration")
            TrackyBodyText(text = "${entry.totalDurationMinutes} min")
        }

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))
        TrackyDivider()
        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TrackyBodyText(text = "Total Calories Burned")
            TrackyBodyText(
                text = "${entry.totalCalories} kcal",
                color = TrackyColors.Success
            )
        }
    }

    // Items breakdown
    TrackySectionTitle(text = "Exercises")

    entry.items.forEach { item ->
        SwipeableRow(onDelete = { onItemDelete(item) }) {
            ExerciseItemRow(item = item, onReviewClick = onReviewClick)
        }
    }

    // Metadata
    TrackyCard {
        TrackyCardTitle(text = "Details")
        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

        DetailRow("Date", entry.date)
        DetailRow("Time", entry.time.take(5))
        
        entry.originalInput?.let { input ->
             if (input.isNotEmpty()) {
                DetailRow("Original Input", input)
            }
        }
    }
}

@Composable
private fun FoodItemRow(
    item: FoodItem,
    onReviewClick: (FoodItem) -> Unit
) {
    TrackyCard(
        modifier = Modifier.clickable { 
            if (item.pendingSuggestion != null) onReviewClick(item)
        }
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    TrackyBodyText(text = item.name)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.XS)
                    ) {
                        TrackyBodySmall(
                            text = "${item.quantity} ${item.unit}",
                            color = TrackyColors.TextSecondary
                        )
                        ProvenanceLabel(source = item.provenance.source)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    if (item.isAnalyzing) {
                        TrackyLoadingIndicator(size = 16.dp, strokeWidth = 2.dp)
                    } else {
                        TrackyBodyText(text = "${item.calories} kcal")
                        TrackyBodySmall(
                            text = "C: ${item.carbsG.toInt()}g P: ${item.proteinG.toInt()}g F: ${item.fatG.toInt()}g",
                            color = TrackyColors.TextTertiary
                        )
                    }
                }
            }
            
            if (item.pendingSuggestion != null) {
                Spacer(modifier = Modifier.height(TrackyTokens.Spacing.XS))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TrackyButtonPrimary(
                        text = "Review Update",
                        onClick = { onReviewClick(item) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun ExerciseItemRow(
    item: ExerciseItem,
    onReviewClick: (ExerciseItem) -> Unit
) {
    TrackyCard(
        modifier = Modifier.clickable { 
            if (item.pendingSuggestion != null) onReviewClick(item)
        }
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    TrackyBodyText(text = item.activityName)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.XS)
                    ) {
                        item.intensity?.let { intensity ->
                            TrackyBodySmall(
                                text = intensity.value.replaceFirstChar { it.uppercase() },
                                color = TrackyColors.TextSecondary
                            )
                        }
                        ProvenanceLabel(source = item.provenance.source)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    if (item.isAnalyzing) {
                         TrackyLoadingIndicator(size = 16.dp, strokeWidth = 2.dp)
                    } else {
                        TrackyBodyText(text = "${item.caloriesBurned} kcal")
                        TrackyBodySmall(
                            text = "${item.durationMinutes} min",
                            color = TrackyColors.TextTertiary
                        )
                    }
                }
            }
             if (item.pendingSuggestion != null) {
                Spacer(modifier = Modifier.height(TrackyTokens.Spacing.XS))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TrackyButtonPrimary(
                        text = "Review Update",
                        onClick = { onReviewClick(item) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewSuggestionDialog(
    originalFood: FoodItem? = null,
    suggestionFood: FoodItem? = null,
    originalExercise: ExerciseItem? = null,
    suggestionExercise: ExerciseItem? = null,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onDiscard: () -> Unit
) {
    TrackyBottomSheet(
        onDismissRequest = onDismiss,
        title = "Review Update"
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.M)
        ) {
            TrackyBodyText(text = "The AI has found a more accurate match based on your description. Would you like to update? This will replace your manual edits.")
            
            TrackyCard {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TrackyBodySmall(text = "Current", color = TrackyTokens.Colors.TextSecondary)
                    TrackyBodySmall(text = "New", color = TrackyTokens.Colors.BrandPrimary)
                }
                TrackyDivider(modifier = Modifier.padding(vertical = TrackyTokens.Spacing.XS))
                
                if (originalFood != null && suggestionFood != null) {
                    DiffRow("Calories", "${originalFood.calories} kcal", "${suggestionFood.calories} kcal")
                    DiffRow("Carbs", "${originalFood.carbsG}g", "${suggestionFood.carbsG}g")
                    DiffRow("Protein", "${originalFood.proteinG}g", "${suggestionFood.proteinG}g")
                    DiffRow("Fat", "${originalFood.fatG}g", "${suggestionFood.fatG}g")
                }
                if (originalExercise != null && suggestionExercise != null) {
                     DiffRow("Calories", "${originalExercise.caloriesBurned} kcal", "${suggestionExercise.caloriesBurned} kcal")
                     if (originalExercise.metValue != suggestionExercise.metValue) {
                         DiffRow("MET", "${originalExercise.metValue}", "${suggestionExercise.metValue}")
                     }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.S)
            ) {
                 Button(
                    onClick = onDiscard,
                    modifier = Modifier.weight(1f),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = TrackyTokens.Colors.Surface,
                        contentColor = TrackyTokens.Colors.Error
                    )
                ) {
                    Text("Discard")
                }
                Button(
                    onClick = onApply,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Update")
                }
            }
        }
    }
}

@Composable
fun DiffRow(label: String, oldVal: String, newVal: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TrackyBodyText(text = label, modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.weight(2f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
             TrackyBodyText(text = oldVal, color = TrackyTokens.Colors.TextSecondary)
             Icon(
                 imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowForward,
                 contentDescription = null,
                 modifier = Modifier.size(16.dp),
                 tint = TrackyTokens.Colors.TextTertiary
             )
             TrackyBodyText(text = newVal, color = TrackyTokens.Colors.BrandPrimary)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemSheet(
    entryType: String,
    onDismiss: () -> Unit,
    onAddFood: (name: String, quantity: Float, unit: String) -> Unit,
    onAddExercise: (activity: String, duration: Int) -> Unit
) {
     val sheetState = rememberModalBottomSheetState()
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = TrackyColors.Surface
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = TrackyTokens.Spacing.ScreenPadding)
                .padding(bottom = TrackyTokens.Spacing.XL),
            verticalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.M)
        ) {
            TrackyCardTitle(text = "Add Item")

            if (entryType == "food") {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name (e.g. Banana)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.S)) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantity") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    
                    val commonUnits = listOf("serving", "g", "oz", "cup", "ml", "tbsp", "tsp")
                    TrackySelect(
                        value = unit,
                        options = commonUnits,
                        onValueChange = { unit = it },
                        label = "Unit",
                        modifier = Modifier.weight(1f)
                    )
                }
                Button(
                    onClick = {
                        val qty = quantity.toFloatOrNull() ?: 0f
                        if (name.isNotEmpty() && qty > 0) {
                            onAddFood(name, qty, unit)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add")
                }
            } else {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Activity (e.g. Running)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.S)) {
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("Duration") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    
                    TrackySelect(
                        value = unit.ifEmpty { "min" },
                        options = listOf("min", "hours"),
                        onValueChange = { unit = it },
                        label = "Unit",
                        modifier = Modifier.weight(1f)
                    )
                }
                Button(
                    onClick = {
                        val dur = duration.toIntOrNull() ?: 0
                        if (name.isNotEmpty() && dur > 0) {
                            onAddExercise(name, dur)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add")
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = TrackyTokens.Spacing.XXS),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TrackyBodySmall(
            text = label,
            color = TrackyColors.TextTertiary
        )
        TrackyBodySmall(text = value)
    }
}
