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
import com.tracky.app.domain.model.ExerciseIntensity
import com.tracky.app.domain.model.ExerciseItem
import com.tracky.app.domain.model.FoodEntry
import com.tracky.app.domain.model.FoodItem
import com.tracky.app.domain.model.Provenance
import com.tracky.app.domain.model.ProvenanceSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

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

    // ─────────────────────────────────────────────────────────────────────────────
    // Food Entry Actions
    // ─────────────────────────────────────────────────────────────────────────────

    fun addFoodItem(name: String, quantity: Float, unit: String) {
        viewModelScope.launch {
            try {
                val currentEntry = _uiState.value.foodEntry ?: return@launch
                
                // Resolve the new item
                val result = foodsRepository.resolveFood(name, quantity, unit)
                val newItem = when (result) {
                    is ResolvedFoodResult.Success -> result.foodItem.copy(displayOrder = currentEntry.items.size)
                    else -> FoodItem(
                        name = name,
                        matchedName = null,
                        quantity = quantity,
                        unit = unit,
                        calories = 0,
                        carbsG = 0f,
                        proteinG = 0f,
                        fatG = 0f,
                        provenance = Provenance(ProvenanceSource.UNRESOLVED, null, 0f),
                        displayOrder = currentEntry.items.size
                    )
                }
                
                // Add to list and update entry
                val updatedItems = currentEntry.items + newItem
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
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateFoodEntry(entry: FoodEntry) {
        viewModelScope.launch {
            try {
                // Reanalyze items if needed
                val reanalyzedItems = mutableListOf<FoodItem>()
                // Simple optimization: only re-resolve if content changed would require keeping track of original state precisely.
                // For now, consistent with previous approach, we check differences. But simpler to just saving changes if passed.

                // Logic: If the passed entry has changes, we should use it.
                // The Caller (UI) modifies the entry. If we want to support re-resolution on edit, we need to know WHICH item changed.
                // For this implementation, we assume basic property updates align with UI edits.
                // If the user edits "Quantity" in the UI, we might want to trigger a re-calc of that item's macros.
                
                // Let's implement a smart update: Check each item against DB version. If Name/Qty/Unit diff -> Re-resolve.
                val dbEntry = loggingRepository.getFoodEntryById(entry.id)
                val dbItems = dbEntry?.items ?: emptyList()
                
                val finalItems = entry.items.mapIndexed { index, item ->
                    val dbItem = dbItems.getOrNull(index) // Assuming order preserved
                    if (dbItem == null || 
                        dbItem.name != item.name || 
                        dbItem.quantity != item.quantity || 
                        dbItem.unit != item.unit) {
                        // Changed or New -> Resolve
                        val result = foodsRepository.resolveFood(item.name, item.quantity, item.unit)
                        when (result) {
                             is ResolvedFoodResult.Success -> result.foodItem.copy(id = item.id, displayOrder = index)
                             else -> item.copy(displayOrder = index) // Keep user edit if resolve fails
                        }
                    } else {
                        // No change, keep existing (preserving macros)
                        item
                    }
                }

                val reanalyzedEntry = entry.copy(
                    items = finalItems,
                    totalCalories = finalItems.sumOf { it.calories },
                    totalCarbsG = finalItems.sumOf { it.carbsG.toDouble() }.toFloat(),
                    totalProteinG = finalItems.sumOf { it.proteinG.toDouble() }.toFloat(),
                    totalFatG = finalItems.sumOf { it.fatG.toDouble() }.toFloat(),
                    updatedAt = System.currentTimeMillis()
                )
                
                loggingRepository.updateFoodEntry(reanalyzedEntry)
                _uiState.update { it.copy(foodEntry = reanalyzedEntry) }

            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun deleteFoodItem(item: FoodItem) {
        viewModelScope.launch {
            try {
                val currentEntry = uiState.value.foodEntry ?: return@launch
                val updatedItems = currentEntry.items.filter { it.id != item.id || (item.id == 0L && it !== item) }
                
                if (updatedItems.isEmpty()) {
                    loggingRepository.deleteFoodEntry(currentEntry.id)
                    _uiState.update { it.copy(entryDeleted = true) }
                } else {
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

    // ─────────────────────────────────────────────────────────────────────────────
    // Exercise Entry Actions
    // ─────────────────────────────────────────────────────────────────────────────

    fun addExerciseItem(activityName: String, durationMinutes: Int, intensity: ExerciseIntensity = ExerciseIntensity.MODERATE) {
        viewModelScope.launch {
            try {
                val currentEntry = _uiState.value.exerciseEntry ?: return@launch
                val profile = profileRepository.getProfileOnce()
                val userWeightKg = profile?.currentWeightKg ?: 70f
                
                // Resolve
                val newItem = try {
                    val response = backendApi.resolveExercise(
                        ResolveExerciseRequest(
                            activity = activityName,
                            durationMinutes = durationMinutes,
                            userWeightKg = userWeightKg
                        )
                    )
                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        ExerciseItem(
                            id = 0,
                            activityName = activityName,
                            durationMinutes = durationMinutes,
                            metValue = body.metValue ?: 0f,
                            caloriesBurned = body.caloriesBurned ?: 0,
                            intensity = intensity,
                            provenance = Provenance(ProvenanceSource.DATASET, null, 1f),
                            displayOrder = currentEntry.items.size
                        )
                    } else {
                        // Fallback
                         ExerciseItem(
                            id = 0,
                            activityName = activityName,
                            durationMinutes = durationMinutes,
                            metValue = 0f,
                            caloriesBurned = 0,
                            intensity = intensity,
                            provenance = Provenance(ProvenanceSource.UNRESOLVED, null, 0f),
                            displayOrder = currentEntry.items.size
                        )
                    }
                } catch (e: Exception) {
                     ExerciseItem(
                        id = 0,
                        activityName = activityName,
                        durationMinutes = durationMinutes,
                        metValue = 0f,
                        caloriesBurned = 0,
                        intensity = intensity,
                        provenance = Provenance(ProvenanceSource.UNRESOLVED, null, 0f),
                        displayOrder = currentEntry.items.size
                    )
                }

                val updatedItems = currentEntry.items + newItem
                val updatedEntry = currentEntry.copy(
                    items = updatedItems,
                    totalCalories = updatedItems.sumOf { it.caloriesBurned },
                    totalDurationMinutes = updatedItems.sumOf { it.durationMinutes },
                    updatedAt = System.currentTimeMillis()
                )
                
                loggingRepository.updateExerciseEntry(updatedEntry)
                _uiState.update { it.copy(exerciseEntry = updatedEntry) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateExerciseEntry(entry: ExerciseEntry) {
        viewModelScope.launch {
            try {
                val dbEntry = loggingRepository.getExerciseEntryById(entry.id)
                val dbItems = dbEntry?.items ?: emptyList()
                val profile = profileRepository.getProfileOnce()
                val userWeightKg = profile?.currentWeightKg ?: 70f
                
                // Parallel resolve for changed items
                val finalItems = coroutineScope {
                    entry.items.mapIndexed { index, item ->
                        async {
                            val dbItem = dbItems.getOrNull(index)
                            if (dbItem == null || 
                                dbItem.activityName != item.activityName || 
                                dbItem.durationMinutes != item.durationMinutes) {
                                
                                try {
                                    val response = backendApi.resolveExercise(
                                        ResolveExerciseRequest(
                                            activity = item.activityName,
                                            durationMinutes = item.durationMinutes,
                                            userWeightKg = userWeightKg
                                        )
                                    )
                                    if (response.isSuccessful && response.body() != null) {
                                        val body = response.body()!!
                                        item.copy(
                                            id = item.id, // Keep ID if existing
                                            caloriesBurned = body.caloriesBurned ?: item.caloriesBurned,
                                            metValue = body.metValue ?: item.metValue,
                                            displayOrder = index
                                        )
                                    } else {
                                        item.copy(displayOrder = index)
                                    }
                                } catch (e: Exception) {
                                    item.copy(displayOrder = index)
                                }
                            } else {
                                item
                            }
                        }
                    }.awaitAll()
                }

                val reanalyzedEntry = entry.copy(
                    items = finalItems,
                    totalCalories = finalItems.sumOf { it.caloriesBurned },
                    totalDurationMinutes = finalItems.sumOf { it.durationMinutes },
                    updatedAt = System.currentTimeMillis()
                )

                loggingRepository.updateExerciseEntry(reanalyzedEntry)
                _uiState.update { it.copy(exerciseEntry = reanalyzedEntry) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteExerciseItem(item: ExerciseItem) {
        viewModelScope.launch {
            try {
                val currentEntry = uiState.value.exerciseEntry ?: return@launch
                val updatedItems = currentEntry.items.filter { it.id != item.id || (item.id == 0L && it !== item) }
                
                if (updatedItems.isEmpty()) {
                    loggingRepository.deleteExerciseEntry(currentEntry.id)
                    _uiState.update { it.copy(entryDeleted = true) }
                } else {
                    val updatedEntry = currentEntry.copy(
                        items = updatedItems,
                        totalCalories = updatedItems.sumOf { it.caloriesBurned },
                        totalDurationMinutes = updatedItems.sumOf { it.durationMinutes },
                        updatedAt = System.currentTimeMillis()
                    )
                    loggingRepository.updateExerciseEntry(updatedEntry)
                    _uiState.update { it.copy(exerciseEntry = updatedEntry) }
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
                _uiState.update { it.copy(entryDeleted = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Common Actions
    // ─────────────────────────────────────────────────────────────────────────────

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
                        // Updated to save list of items
                        val exerciseJson = json.encodeToString(
                            SavedExerciseDataJson(
                                items = entry.items.map { 
                                    SavedExerciseItemJson(
                                        activityName = it.activityName,
                                        durationMinutes = it.durationMinutes,
                                        metValue = it.metValue
                                    )
                                }
                            )
                        )

                        savedEntryDao.insert(
                            SavedEntryEntity(
                                name = name,
                                entryType = "exercise",
                                entryDataJson = exerciseJson,
                                totalCalories = entry.totalCalories,
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
    val items: List<SavedExerciseItemJson>
)

@kotlinx.serialization.Serializable
private data class SavedExerciseItemJson(
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
