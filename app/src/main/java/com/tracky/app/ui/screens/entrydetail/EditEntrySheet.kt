package com.tracky.app.ui.screens.entrydetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.tracky.app.domain.model.ExerciseEntry
import com.tracky.app.domain.model.FoodEntry
import com.tracky.app.domain.model.FoodItem
import com.tracky.app.ui.components.TrackyBodySmall
import com.tracky.app.ui.components.TrackyBodyText
import com.tracky.app.ui.components.TrackyBottomSheet
import com.tracky.app.ui.components.TrackyCard
import com.tracky.app.ui.components.TrackyDivider
import com.tracky.app.ui.components.TrackyInput
import com.tracky.app.ui.components.TrackyNumberInput
import com.tracky.app.ui.components.TrackySheetActions
import com.tracky.app.ui.components.BadgeStyle
import com.tracky.app.ui.components.TrackyBadge
import com.tracky.app.ui.components.TrackyChip
import com.tracky.app.ui.components.TrackySelect
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyTokens

/**
 * Edit Food Entry Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFoodEntrySheet(
    entry: FoodEntry,
    onDismiss: () -> Unit,
    onSave: (FoodEntry) -> Unit
) {
    var editedItems by remember { mutableStateOf(entry.items.toMutableList()) }

    fun sentenceCase(text: String): String {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return trimmed
        val lower = trimmed.lowercase()
        return lower.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    TrackyBottomSheet(
        onDismissRequest = onDismiss,
        title = null
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            // Custom Title Row with Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = TrackyTokens.Spacing.M),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                androidx.compose.material3.Text(
                    text = "Edit Food Entry",
                    style = com.tracky.app.ui.theme.TrackyTypography.HeadlineMedium,
                    color = TrackyColors.TextPrimary
                )
                
                if (editedItems.any { it.isManualMacros }) {
                    TrackyBadge(
                        text = "Manual Edit",
                        style = BadgeStyle.WARNING,
                        compact = true
                    )
                }
            }

            // Edit each item
            editedItems.forEachIndexed { index, item ->
                EditFoodItemCard(
                    item = item,
                    onItemChanged = { updatedItem ->
                        editedItems = editedItems.toMutableList().apply {
                            this[index] = updatedItem
                        }
                    }
                )
                if (index < editedItems.size - 1) {
                    Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))
                }
            }

            // Totals
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
            TrackyCard {
                val totalCalories = editedItems.map { it.calories }.sum()
                val totalCarbs = editedItems.map { it.carbsG.toDouble() }.sum().toFloat()
                val totalProtein = editedItems.map { it.proteinG.toDouble() }.sum().toFloat()
                val totalFat = editedItems.map { it.fatG.toDouble() }.sum().toFloat()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TrackyBodyText(text = "Total Calories")
                    TrackyBodyText(
                        text = "${totalCalories.toInt()} kcal",
                        color = TrackyTokens.Colors.BrandPrimary
                    )
                }
                TrackyDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TrackyBodySmall(text = "Carbs")
                    TrackyBodySmall(text = "${totalCarbs.toInt()}g")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TrackyBodySmall(text = "Protein")
                    TrackyBodySmall(text = "${totalProtein.toInt()}g")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TrackyBodySmall(text = "Fat")
                    TrackyBodySmall(text = "${totalFat.toInt()}g")
                }
            }

            TrackySheetActions(
                primaryText = "Save Changes",
                onPrimaryClick = {
                    val totalCalories = editedItems.map { it.calories }.sum()
                    val totalCarbs = editedItems.map { it.carbsG.toDouble() }.sum().toFloat()
                    val totalProtein = editedItems.map { it.proteinG.toDouble() }.sum().toFloat()
                    val totalFat = editedItems.map { it.fatG.toDouble() }.sum().toFloat()

                    val normalizedItems = editedItems.map { it.copy(name = sentenceCase(it.name)) }

                    onSave(
                        entry.copy(
                            items = normalizedItems,
                            totalCalories = totalCalories,
                            totalCarbsG = totalCarbs,
                            totalProteinG = totalProtein,
                            totalFatG = totalFat,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                },
                primaryEnabled = editedItems.isNotEmpty()
            )
        }
    }
}

@Composable
private fun EditFoodItemCard(
    item: FoodItem,
    onItemChanged: (FoodItem) -> Unit
) {
    // Keep local buffer for text fields to allow typing invalid numbers temporarily
    var quantityText by remember { mutableStateOf(item.quantity.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() }) }
    var caloriesText by remember { mutableStateOf(kotlin.math.round(item.calories).toInt().toString()) }
    var carbsText by remember { mutableStateOf(kotlin.math.round(item.carbsG).toInt().toString()) }
    var proteinText by remember { mutableStateOf(kotlin.math.round(item.proteinG).toInt().toString()) }
    var fatText by remember { mutableStateOf(kotlin.math.round(item.fatG).toInt().toString()) }
    
    // Ratios per 1 unit of quantity (based on initial state or current if possible)
    // We update these only when the user manually changes a macro, so that subsequent quantity changes respect the new ratio
    // But if we just edit quantity, we use the EXISTING ratio.
    fun getRatio(value: Float, qty: Float): Float = if (qty > 0) value / qty else 0f
    
    // Sync local state when item changes externally
    androidx.compose.runtime.LaunchedEffect(item.quantity) {
        if (quantityText.toFloatOrNull() != item.quantity) quantityText = item.quantity.toString()
    }
    androidx.compose.runtime.LaunchedEffect(item.calories) {
        if (caloriesText.toFloatOrNull() != item.calories) caloriesText = kotlin.math.round(item.calories).toInt().toString()
    }
    androidx.compose.runtime.LaunchedEffect(item.carbsG) {
        if (carbsText.toFloatOrNull() != item.carbsG) carbsText = kotlin.math.round(item.carbsG).toInt().toString()
    }
    androidx.compose.runtime.LaunchedEffect(item.proteinG) {
        if (proteinText.toFloatOrNull() != item.proteinG) proteinText = kotlin.math.round(item.proteinG).toInt().toString()
    }
    androidx.compose.runtime.LaunchedEffect(item.fatG) {
        if (fatText.toFloatOrNull() != item.fatG) fatText = kotlin.math.round(item.fatG).toInt().toString()
    }

    fun emitChange(
        newName: String = item.name,
        newQuantity: Float = item.quantity,
        newUnit: String = item.unit,
        newCalories: Float = item.calories,
        newCarbs: Float = item.carbsG,
        newProtein: Float = item.proteinG,
        newFat: Float = item.fatG
    ) {
        onItemChanged(
            item.copy(
                name = newName,
                quantity = newQuantity,
                unit = newUnit,
                calories = newCalories,
                carbsG = newCarbs,
                proteinG = newProtein,
                fatG = newFat,
                isManualMacros = true // Mark as manual edit
            )
        )
    }

    TrackyCard {
        TrackyInput(
            value = item.name,
            onValueChange = {
                emitChange(newName = it)
            },
            label = "Food Name",
            placeholder = "Enter food name"
        )

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.S)
        ) {
            TrackyNumberInput(
                value = quantityText,
                onValueChange = {
                    quantityText = it
                    val q = it.toFloatOrNull()
                    if (q != null) {
                        // Recalculate macros based on current ratios
                        // We use the item's CURRENT values to determine ratio before updating quantity
                        val oldQty = item.quantity
                        val ratioCals = getRatio(item.calories, oldQty)
                        val ratioCarbs = getRatio(item.carbsG, oldQty)
                        val ratioProt = getRatio(item.proteinG, oldQty)
                        val ratioFat = getRatio(item.fatG, oldQty)

                        emitChange(
                            newQuantity = q,
                            newCalories = ratioCals * q,
                            newCarbs = ratioCarbs * q,
                            newProtein = ratioProt * q,
                            newFat = ratioFat * q
                        )
                    }
                },
                label = "Quantity",
                modifier = Modifier.weight(1f)
            )
            
            val allowedUnits = listOf(
                "serving", "piece", "cup", "oz", "g", "ml", 
                "tbsp", "tsp", "bottle", "can", "package", 
                "slice", "bowl", "glass"
            )
            
            TrackySelect(
                value = item.unit,
                options = allowedUnits,
                onValueChange = {
                    emitChange(newUnit = it)
                },
                label = "Unit",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

        TrackyNumberInput(
            value = caloriesText,
            onValueChange = {
                caloriesText = it
                it.toFloatOrNull()?.let { c -> emitChange(newCalories = c) }
            },
            label = "Calories",
            suffix = "kcal",
            allowDecimal = true
        )

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.S)
        ) {
            TrackyNumberInput(
                value = carbsText,
                onValueChange = {
                    carbsText = it
                    it.toFloatOrNull()?.let { c -> emitChange(newCarbs = c) }
                },
                label = "Carbs",
                suffix = "g",
                modifier = Modifier.weight(1f)
            )
            TrackyNumberInput(
                value = proteinText,
                onValueChange = {
                    proteinText = it
                    it.toFloatOrNull()?.let { p -> emitChange(newProtein = p) }
                },
                label = "Protein",
                suffix = "g",
                modifier = Modifier.weight(1f)
            )
            TrackyNumberInput(
                value = fatText,
                onValueChange = {
                    fatText = it
                    it.toFloatOrNull()?.let { f -> emitChange(newFat = f) }
                },
                label = "Fat",
                suffix = "g",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Edit Exercise Entry Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExerciseEntrySheet(
    entry: ExerciseEntry,
    onDismiss: () -> Unit,
    onSave: (ExerciseEntry) -> Unit
) {
    var editedItems by remember { mutableStateOf(entry.items.toMutableList()) }

    fun sentenceCase(text: String): String {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return trimmed
        val lower = trimmed.lowercase()
        return lower.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    TrackyBottomSheet(
        onDismissRequest = onDismiss,
        title = null
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            // Custom Title Row with Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = TrackyTokens.Spacing.M),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                androidx.compose.material3.Text(
                    text = "Edit Exercise Entry",
                    style = com.tracky.app.ui.theme.TrackyTypography.HeadlineMedium,
                    color = TrackyColors.TextPrimary
                )
                
                if (editedItems.any { it.isManual }) {
                    TrackyBadge(
                        text = "Manual Edit",
                        style = BadgeStyle.WARNING,
                        compact = true
                    )
                }
            }

            // Edit each item
            editedItems.forEachIndexed { index, item ->
                EditExerciseItemCard(
                    item = item,
                    userWeightKg = entry.userWeightKg,
                    onItemChanged = { updatedItem ->
                        editedItems = editedItems.toMutableList().apply {
                            this[index] = updatedItem
                        }
                    }
                )
                if (index < editedItems.size - 1) {
                    Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))
                }
            }

            // Totals
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
            TrackyCard {
                val totalCalories = editedItems.map { it.caloriesBurned }.sum()
                val totalDuration = editedItems.sumOf { it.durationMinutes }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TrackyBodyText(text = "Total Duration")
                    TrackyBodyText(text = "$totalDuration min")
                }
                TrackyDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TrackyBodyText(text = "Total Calories Burned")
                    TrackyBodyText(
                        text = "${totalCalories.toInt()} kcal",
                        color = TrackyTokens.Colors.Success
                    )
                }
            }

            TrackySheetActions(
                primaryText = "Save Changes",
                onPrimaryClick = {
                    val totalCalories = editedItems.map { it.caloriesBurned }.sum()
                    val totalDuration = editedItems.sumOf { it.durationMinutes }

                    val normalizedItems = editedItems.map { 
                        it.copy(activityName = sentenceCase(it.activityName)) 
                    }

                    onSave(
                        entry.copy(
                            items = normalizedItems,
                            totalCalories = totalCalories,
                            totalDurationMinutes = totalDuration,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                },
                primaryEnabled = editedItems.isNotEmpty()
            )
        }
    }
}

@Composable
private fun EditExerciseItemCard(
    item: com.tracky.app.domain.model.ExerciseItem,
    userWeightKg: Float,
    onItemChanged: (com.tracky.app.domain.model.ExerciseItem) -> Unit
) {
    // Keep local buffer for text fields to allow typing invalid numbers temporarily
    var durationText by remember { mutableStateOf(item.durationMinutes.toString()) }
    var caloriesText by remember { mutableStateOf(kotlin.math.round(item.caloriesBurned).toInt().toString()) }
    
    // Sync local state when external item changes (e.g. from recalculations triggered by other fields)
    // We only update if the new value is different enough to avoid fighting the user's typing
    androidx.compose.runtime.LaunchedEffect(item.durationMinutes) {
        if (durationText.toFloatOrNull() != item.durationMinutes.toFloat()) {
            durationText = item.durationMinutes.toString()
        }
    }
    androidx.compose.runtime.LaunchedEffect(item.caloriesBurned) {
        if (caloriesText.toFloatOrNull() != item.caloriesBurned) {
            caloriesText = kotlin.math.round(item.caloriesBurned).toInt().toString()
        }
    }

    fun calculateCalories(met: Float, weight: Float, minutes: Int): Float {
        return (met * 3.5f * weight * minutes) / 200f
    }

    fun adjustMetForIntensity(currentMet: Float, oldIntensity: com.tracky.app.domain.model.ExerciseIntensity?, newIntensity: com.tracky.app.domain.model.ExerciseIntensity): Float {
        if (oldIntensity == newIntensity) return currentMet
        if (currentMet <= 0f) return 0f // Can't adjust known 0

        // Heuristic factors relative to Moderate
        // Low: ~0.7x, Moderate: 1.0x, High: ~1.4x
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

    fun emitChange(
        newActivity: String = item.activityName,
        newDuration: Int = item.durationMinutes,
        newCalories: Float = item.caloriesBurned,
        newIntensity: com.tracky.app.domain.model.ExerciseIntensity? = item.intensity,
        newMet: Float = item.metValue
    ) {
        onItemChanged(
            item.copy(
                activityName = newActivity,
                durationMinutes = newDuration,
                caloriesBurned = newCalories,
                intensity = newIntensity,
                metValue = newMet,
                isManual = true
            )
        )
    }

    TrackyCard {
        TrackyInput(
            value = item.activityName,
            onValueChange = { emitChange(newActivity = it) },
            label = "Activity Name",
            placeholder = "Enter activity name"
        )

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

        // Intensity Selector
        TrackyBodySmall(text = "Intensity", modifier = Modifier.padding(bottom = TrackyTokens.Spacing.XS))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.XS)
        ) {
            com.tracky.app.domain.model.ExerciseIntensity.entries.forEach { level ->
                TrackyChip(
                    label = level.value.replaceFirstChar { it.uppercase() },
                    selected = item.intensity == level,
                    onClick = {
                        val newMet = adjustMetForIntensity(item.metValue, item.intensity, level)
                        // If we have met and weight, recalculate calories
                        val newCals = if (newMet > 0 && userWeightKg > 0) {
                            calculateCalories(newMet, userWeightKg, item.durationMinutes)
                        } else item.caloriesBurned
                        
                        emitChange(newIntensity = level, newMet = newMet, newCalories = newCals)
                    },
                    compact = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.S)
        ) {
            TrackyNumberInput(
                value = durationText,
                onValueChange = {
                    durationText = it
                    val d = it.toIntOrNull()
                    if (d != null) {
                        // Recalculate calories if we have MET and Weight
                        val newCals = if (item.metValue > 0 && userWeightKg > 0) {
                            calculateCalories(item.metValue, userWeightKg, d)
                        } else item.caloriesBurned
                        emitChange(newDuration = d, newCalories = newCals)
                    }
                },
                label = "Duration",
                modifier = Modifier.weight(1f),
                allowDecimal = false
            )
            
            TrackySelect(
                value = "min",
                options = listOf("min", "hours"),
                onValueChange = { /* Handle if unit changes in model later */ },
                label = "Unit",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

        TrackyNumberInput(
             value = caloriesText,
             onValueChange = {
                 caloriesText = it
                 val c = it.toFloatOrNull()
                 if (c != null) {
                    emitChange(newCalories = c) 
                 }
             },
             label = "Calories Burned",
             suffix = "kcal",
             allowDecimal = true
        )
    }
}

