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
        title = "Edit Food Entry"
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
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
                val totalCalories = editedItems.sumOf { it.calories }
                val totalCarbs = editedItems.sumOf { it.carbsG.toDouble() }.toFloat()
                val totalProtein = editedItems.sumOf { it.proteinG.toDouble() }.toFloat()
                val totalFat = editedItems.sumOf { it.fatG.toDouble() }.toFloat()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TrackyBodyText(text = "Total Calories")
                    TrackyBodyText(
                        text = "$totalCalories kcal",
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
                    val totalCalories = editedItems.sumOf { it.calories }
                    val totalCarbs = editedItems.sumOf { it.carbsG.toDouble() }.toFloat()
                    val totalProtein = editedItems.sumOf { it.proteinG.toDouble() }.toFloat()
                    val totalFat = editedItems.sumOf { it.fatG.toDouble() }.toFloat()

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
    var name by remember { mutableStateOf(item.name) }
    var quantity by remember { mutableStateOf(item.quantity.toString()) }
    var unit by remember { mutableStateOf(item.unit) }
    var calories by remember { mutableStateOf(item.calories.toString()) }
    var carbs by remember { mutableStateOf(item.carbsG.toString()) }
    var protein by remember { mutableStateOf(item.proteinG.toString()) }
    var fat by remember { mutableStateOf(item.fatG.toString()) }

    fun emitChange(
        newName: String = name,
        newQuantity: Float = quantity.toFloatOrNull() ?: item.quantity,
        newUnit: String = unit,
        newCalories: Int = calories.toIntOrNull() ?: item.calories,
        newCarbs: Float = carbs.toFloatOrNull() ?: item.carbsG,
        newProtein: Float = protein.toFloatOrNull() ?: item.proteinG,
        newFat: Float = fat.toFloatOrNull() ?: item.fatG
    ) {
        onItemChanged(
            item.copy(
                name = newName,
                quantity = newQuantity,
                unit = newUnit,
                calories = newCalories,
                carbsG = newCarbs,
                proteinG = newProtein,
                fatG = newFat
            )
        )
    }

    TrackyCard {
        TrackyInput(
            value = name,
            onValueChange = {
                name = it
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
                value = quantity,
                onValueChange = {
                    quantity = it
                    it.toFloatOrNull()?.let { q -> emitChange(newQuantity = q) }
                },
                label = "Quantity",
                modifier = Modifier.weight(1f)
            )
            TrackyInput(
                value = unit,
                onValueChange = {
                    unit = it
                    emitChange(newUnit = it)
                },
                label = "Unit",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

        TrackyNumberInput(
            value = calories,
            onValueChange = {
                calories = it
                it.toIntOrNull()?.let { c -> emitChange(newCalories = c) }
            },
            label = "Calories",
            suffix = "kcal",
            allowDecimal = false
        )

        Spacer(modifier = Modifier.height(TrackyTokens.Spacing.S))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(TrackyTokens.Spacing.S)
        ) {
            TrackyNumberInput(
                value = carbs,
                onValueChange = {
                    carbs = it
                    it.toFloatOrNull()?.let { c -> emitChange(newCarbs = c) }
                },
                label = "Carbs",
                suffix = "g",
                modifier = Modifier.weight(1f)
            )
            TrackyNumberInput(
                value = protein,
                onValueChange = {
                    protein = it
                    it.toFloatOrNull()?.let { p -> emitChange(newProtein = p) }
                },
                label = "Protein",
                suffix = "g",
                modifier = Modifier.weight(1f)
            )
            TrackyNumberInput(
                value = fat,
                onValueChange = {
                    fat = it
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
    var activityName by remember { mutableStateOf(entry.activityName) }
    var durationMinutes by remember { mutableStateOf(entry.durationMinutes.toString()) }
    var caloriesBurned by remember { mutableStateOf(entry.caloriesBurned.toString()) }

    TrackyBottomSheet(
        onDismissRequest = onDismiss,
        title = "Edit Exercise Entry"
    ) {
        Column {
            TrackyInput(
                value = activityName,
                onValueChange = { activityName = it },
                label = "Activity",
                placeholder = "Enter activity name"
            )

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

            TrackyNumberInput(
                value = durationMinutes,
                onValueChange = { durationMinutes = it },
                label = "Duration",
                suffix = "min",
                allowDecimal = false
            )

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

            TrackyNumberInput(
                value = caloriesBurned,
                onValueChange = { caloriesBurned = it },
                label = "Calories Burned",
                suffix = "kcal",
                allowDecimal = false
            )

            TrackySheetActions(
                primaryText = "Save Changes",
                onPrimaryClick = {
                    onSave(
                        entry.copy(
                            activityName = activityName,
                            durationMinutes = durationMinutes.toIntOrNull() ?: entry.durationMinutes,
                            caloriesBurned = caloriesBurned.toIntOrNull() ?: entry.caloriesBurned,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                },
                primaryEnabled = activityName.isNotBlank() &&
                        durationMinutes.toIntOrNull() != null &&
                        caloriesBurned.toIntOrNull() != null
            )
        }
    }
}
