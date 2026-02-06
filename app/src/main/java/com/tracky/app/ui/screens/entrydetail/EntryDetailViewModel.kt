package com.tracky.app.ui.screens.entrydetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracky.app.data.local.dao.SavedEntryDao
import com.tracky.app.data.local.entity.SavedEntryEntity
import com.tracky.app.data.remote.TrackyBackendApi
import com.tracky.app.data.remote.dto.ResolveExerciseRequest
import com.tracky.app.data.repository.FoodsRepository
import com.tracky.app.data.repository.LoggingRepository
import com.tracky.app.data.repository.ProfileRepository
import com.tracky.app.data.repository.ResolvedFoodResult
import com.tracky.app.domain.model.ExerciseEntry
import com.tracky.app.domain.model.FoodEntry
import com.tracky.app.domain.model.FoodItem
import com.tracky.app.domain.model.Provenance
import com.tracky.app.domain.model.ProvenanceSource
import com.tracky.app.domain.model.SavedFoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class EntryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loggingRepository: LoggingRepository,
    private val savedEntryDao: SavedEntryDao,
    private val foodsRepository: FoodsRepository,
    private val backendApi: TrackyBackendApi,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val json = Json { encodeDefaults = true }

    private val entryId: Long = savedStateHandle["entryId"] ?: 0L
    private val entryType: String = savedStateHandle["entryType"] ?: "food"

    private val _uiState = MutableStateFlow(EntryDetailUiState())
    val uiState: StateFlow<EntryDetailUiState> = _uiState.asStateFlow()

    init {
        loadEntry()
    }

    private fun loadEntry() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                if (entryType == "food") {
                    val entry = loggingRepository.getFoodEntryById(entryId)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            foodEntry = entry
                        )
                    }
                } else {
                    val entry = loggingRepository.getExerciseEntryById(entryId)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            exerciseEntry = entry
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun updateFoodEntry(entry: FoodEntry) {
        viewModelScope.launch {
            try {
                val originalEntry = _uiState.value.foodEntry
                
                // Check if content changed (item names, quantities, or units)
                val contentChanged = originalEntry?.items?.zip(entry.items)?.any { (old, new) ->
                    old.name != new.name || old.quantity != new.quantity || old.unit != new.unit
                } ?: true
                
                if (contentChanged && entry.items.isNotEmpty()) {
                    // Reanalyze - re-resolve each item
                    val reanalyzedItems = mutableListOf<FoodItem>()
                    for ((index, item) in entry.items.withIndex()) {
                        val result = foodsRepository.resolveFood(item.name, item.quantity, item.unit)
                        val reanalyzedItem = when (result) {
                            is ResolvedFoodResult.Success -> result.foodItem.copy(displayOrder = index)
                            else -> item.copy(
                                provenance = Provenance(ProvenanceSource.USER_OVERRIDE, null, 0f),
                                displayOrder = index
                            )
                        }
                        reanalyzedItems.add(reanalyzedItem)
                    }
                    
                    // Calculate new totals
                    val reanalyzedEntry = entry.copy(
                        items = reanalyzedItems,
                        totalCalories = reanalyzedItems.sumOf { it.calories },
                        totalCarbsG = reanalyzedItems.sumOf { it.carbsG.toDouble() }.toFloat(),
                        totalProteinG = reanalyzedItems.sumOf { it.proteinG.toDouble() }.toFloat(),
                        totalFatG = reanalyzedItems.sumOf { it.fatG.toDouble() }.toFloat(),
                        updatedAt = System.currentTimeMillis()
                    )
                    
                    loggingRepository.updateFoodEntry(reanalyzedEntry)
                    _uiState.update { it.copy(foodEntry = reanalyzedEntry) }
                } else {
                    // No content change, just save
                    loggingRepository.updateFoodEntry(entry)
                    _uiState.update { it.copy(foodEntry = entry) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateExerciseEntry(entry: ExerciseEntry) {
        viewModelScope.launch {
            try {
                val originalEntry = _uiState.value.exerciseEntry
                
                // Check if content changed (activity name or duration)
                val contentChanged = originalEntry?.let {
                    it.activityName != entry.activityName || it.durationMinutes != entry.durationMinutes
                } ?: true
                
                if (contentChanged) {
                    // Reanalyze - re-resolve exercise calories via backend
                    val profile = profileRepository.getProfileOnce()
                    val userWeightKg = profile?.currentWeightKg ?: 70f
                    
                    val response = backendApi.resolveExercise(
                        ResolveExerciseRequest(
                            activity = entry.activityName,
                            durationMinutes = entry.durationMinutes,
                            userWeightKg = userWeightKg
                        )
                    )
                    
                    val reanalyzedEntry = if (response.isSuccessful && response.body()?.resolved == true) {
                        val resolved = response.body()!!
                        entry.copy(
                            caloriesBurned = resolved.caloriesBurned ?: entry.caloriesBurned,
                            metValue = resolved.metValue ?: entry.metValue,
                            userWeightKg = userWeightKg,
                            updatedAt = System.currentTimeMillis()
                        )
                    } else {
                        entry.copy(updatedAt = System.currentTimeMillis())
                    }
                    
                    loggingRepository.updateExerciseEntry(reanalyzedEntry)
                    _uiState.update { it.copy(exerciseEntry = reanalyzedEntry) }
                } else {
                    // No content change, just save
                    loggingRepository.updateExerciseEntry(entry)
                    _uiState.update { it.copy(exerciseEntry = entry) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteEntry() {
        viewModelScope.launch {
            try {
                if (entryType == "food") {
                    loggingRepository.deleteFoodEntry(entryId)
                } else {
                    loggingRepository.deleteExerciseEntry(entryId)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteFoodItem(item: FoodItem) {
        viewModelScope.launch {
            try {
                val currentEntry = uiState.value.foodEntry ?: return@launch
                
                // Remove the item
                val updatedItems = currentEntry.items.filter { 
                    it.id != item.id && it.displayOrder != item.displayOrder 
                }
                
                if (updatedItems.isEmpty()) {
                    // If no items left, delete the whole entry
                    loggingRepository.deleteFoodEntry(currentEntry.id)
                    // Signal that entry was deleted so UI can navigate back
                    _uiState.update { it.copy(entryDeleted = true) }
                } else {
                    // Recalculate totals
                    val updatedEntry = currentEntry.copy(
                        items = updatedItems,
                        totalCalories = updatedItems.sumOf { it.calories },
                        totalCarbsG = updatedItems.sumOf { it.carbsG.toDouble() }.toFloat(),
                        totalProteinG = updatedItems.sumOf { it.proteinG.toDouble() }.toFloat(),
                        totalFatG = updatedItems.sumOf { it.fatG.toDouble() }.toFloat(),
                        updatedAt = System.currentTimeMillis()
                    )
                    loggingRepository.updateFoodEntry(updatedEntry)
                    _uiState.update { it.copy(foodEntry = updatedEntry) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun saveAsTemplate(name: String) {
        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()

                when {
                    uiState.value.foodEntry != null -> {
                        val entry = uiState.value.foodEntry!!
                        val itemsJson = json.encodeToString(
                            SavedFoodDataJson(
                                items = entry.items.map { item ->
                                    SavedFoodItemJson(
                                        name = item.name,
                                        quantity = item.quantity,
                                        unit = item.unit,
                                        calories = item.calories,
                                        carbsG = item.carbsG,
                                        proteinG = item.proteinG,
                                        fatG = item.fatG
                                    )
                                }
                            )
                        )

                        savedEntryDao.insert(
                            SavedEntryEntity(
                                name = name,
                                entryType = "food",
                                entryDataJson = itemsJson,
                                totalCalories = entry.totalCalories,
                                useCount = 0,
                                lastUsedAt = null,
                                createdAt = now,
                                updatedAt = now
                            )
                        )
                    }
                    uiState.value.exerciseEntry != null -> {
                        val entry = uiState.value.exerciseEntry!!
                        val exerciseJson = json.encodeToString(
                            SavedExerciseDataJson(
                                activityName = entry.activityName,
                                durationMinutes = entry.durationMinutes,
                                metValue = entry.metValue
                            )
                        )

                        savedEntryDao.insert(
                            SavedEntryEntity(
                                name = name,
                                entryType = "exercise",
                                entryDataJson = exerciseJson,
                                totalCalories = entry.caloriesBurned,
                                useCount = 0,
                                lastUsedAt = null,
                                createdAt = now,
                                updatedAt = now
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateDateTime(date: String, time: String) {
        viewModelScope.launch {
            try {
                when {
                    uiState.value.foodEntry != null -> {
                        val updated = uiState.value.foodEntry!!.copy(
                            date = date,
                            time = time,
                            updatedAt = System.currentTimeMillis()
                        )
                        loggingRepository.updateFoodEntry(updated)
                        _uiState.update { it.copy(foodEntry = updated) }
                    }
                    uiState.value.exerciseEntry != null -> {
                        val updated = uiState.value.exerciseEntry!!.copy(
                            date = date,
                            time = time,
                            updatedAt = System.currentTimeMillis()
                        )
                        loggingRepository.updateExerciseEntry(updated)
                        _uiState.update { it.copy(exerciseEntry = updated) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}

@kotlinx.serialization.Serializable
private data class SavedFoodDataJson(
    val items: List<SavedFoodItemJson>
)

@kotlinx.serialization.Serializable
private data class SavedFoodItemJson(
    val name: String,
    val quantity: Float,
    val unit: String,
    val calories: Int,
    val carbsG: Float,
    val proteinG: Float,
    val fatG: Float
)

@kotlinx.serialization.Serializable
private data class SavedExerciseDataJson(
    val activityName: String,
    val durationMinutes: Int,
    val metValue: Float
)

data class EntryDetailUiState(
    val isLoading: Boolean = true,
    val foodEntry: FoodEntry? = null,
    val exerciseEntry: ExerciseEntry? = null,
    val error: String? = null,
    val entryDeleted: Boolean = false
)
