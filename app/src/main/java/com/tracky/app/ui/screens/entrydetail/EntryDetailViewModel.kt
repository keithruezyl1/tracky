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
    private val profileRepository: ProfileRepository,
    private val weightRepository: com.tracky.app.data.repository.WeightRepository,
    private val soundManager: com.tracky.app.ui.sound.SoundManager,
    private val hapticManager: com.tracky.app.ui.haptics.HapticManager,
    private val canonicalKeyGenerator: com.tracky.app.domain.resolver.CanonicalKeyGenerator
) : ViewModel() {

    private val entryId: Long = savedStateHandle.get<Long>("entryId") ?: -1L
    private val entryType: String = savedStateHandle.get<String>("entryType") ?: "food"

    private val _uiState = MutableStateFlow(EntryDetailUiState())
    val uiState: StateFlow<EntryDetailUiState> = _uiState.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true }

    init {
        loadEntry()
    }

    private fun loadEntry() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                if (entryType == "food") {
                    val entry = loggingRepository.getFoodEntryById(entryId)
                    _uiState.update { it.copy(foodEntry = entry, isLoading = false) }
                } else {
                    val entry = loggingRepository.getExerciseEntryById(entryId)
                    _uiState.update { it.copy(exerciseEntry = entry, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private suspend fun getCurrentWeight(): Float {
        return profileRepository.getProfileOnce()?.currentWeightKg ?: 70f
    }

    fun updateFoodEntry(entry: FoodEntry) {
        viewModelScope.launch {
            try {
                // When user manually edits an entry (via Edit sheet), 
                // we should preserve their changes as-is instead of re-resolving.
                // Re-resolution is only for AI analysis updates, not manual edits.
                
                val originalEntry = loggingRepository.getFoodEntryById(entry.id)
                val originalItems = originalEntry?.items ?: emptyList()

                val updatedItems = entry.items.map { item ->
                    val originalItem = originalItems.find { it.id == item.id }
                    
                    // Check if critical fields have changed
                    val hasChanged = originalItem == null || // New item
                        originalItem.name != item.name ||
                        originalItem.quantity != item.quantity ||
                        originalItem.unit != item.unit ||
                        originalItem.calories != item.calories ||
                        originalItem.carbsG != item.carbsG ||
                        originalItem.proteinG != item.proteinG ||
                        originalItem.fatG != item.fatG

                    if (hasChanged) {
                        item.copy(
                            provenance = item.provenance.copy(
                                source = ProvenanceSource.USER_OVERRIDE,
                                confidence = 1.0f
                            ),
                            // Generate/Update canonical key for exact future matching
                            canonicalKey = canonicalKeyGenerator.generate(item.name)
                        )
                    } else {
                        // Preserve original provenance if untouched
                        item
                    }
                }
            
                // Recalculate totals from items
                val totalCalories = updatedItems.sumOf { it.calories.toDouble() }.toFloat()
                val totalCarbs = updatedItems.sumOf { it.carbsG.toDouble() }.toFloat()
                val totalProtein = updatedItems.sumOf { it.proteinG.toDouble() }.toFloat()
                val totalFat = updatedItems.sumOf { it.fatG.toDouble() }.toFloat()
                
                val updatedEntry = entry.copy(
                    items = updatedItems,
                    totalCalories = totalCalories,
                    totalCarbsG = totalCarbs,
                    totalProteinG = totalProtein,
                    totalFatG = totalFat,
                    updatedAt = System.currentTimeMillis()
                )
                    
                loggingRepository.updateFoodEntry(updatedEntry)
                _uiState.update { it.copy(foodEntry = updatedEntry) }

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
                    soundManager.playCrumple()
                    hapticManager.vibrateSoft()
                    _uiState.update { it.copy(entryDeleted = true) }
                } else {
                    val updatedEntry = currentEntry.copy(
                        items = updatedItems,
                        totalCalories = updatedItems.map { it.calories }.sum(),
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

    fun addFoodItem(name: String, quantity: Float, unit: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val currentEntry = uiState.value.foodEntry ?: return@launch
                
                // Resolve the new item
                val result = foodsRepository.resolveFood(name, quantity, unit)
                
                val newItem = when (result) {
                    is ResolvedFoodResult.Success -> result.foodItem.copy(
                        id = 0,
                        displayOrder = currentEntry.items.size
                    )
                    ResolvedFoodResult.NotFound, is ResolvedFoodResult.Error -> {
                        // Fallback handling if repository fails (though repo usually handles fallback)
                        FoodItem(
                            id = 0,
                            name = name,
                            matchedName = null,
                            quantity = quantity,
                            unit = unit,
                            calories = 0f,
                            carbsG = 0f,
                            proteinG = 0f,
                            fatG = 0f,
                            provenance = Provenance(ProvenanceSource.UNRESOLVED, null, 0f),
                            displayOrder = currentEntry.items.size,
                            canonicalKey = canonicalKeyGenerator.generate(name)
                        )
                    }
                }

                val updatedItems = currentEntry.items + newItem
                
                // Recalculate totals
                val totalCalories = updatedItems.sumOf { it.calories.toDouble() }.toFloat()
                val totalCarbs = updatedItems.sumOf { it.carbsG.toDouble() }.toFloat()
                val totalProtein = updatedItems.sumOf { it.proteinG.toDouble() }.toFloat()
                val totalFat = updatedItems.sumOf { it.fatG.toDouble() }.toFloat()

                val updatedEntry = currentEntry.copy(
                    items = updatedItems,
                    totalCalories = totalCalories,
                    totalCarbsG = totalCarbs,
                    totalProteinG = totalProtein,
                    totalFatG = totalFat,
                    updatedAt = System.currentTimeMillis()
                )

                loggingRepository.updateFoodEntry(updatedEntry)
                _uiState.update { it.copy(foodEntry = updatedEntry, isLoading = false) }

            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
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
                val userWeightKg = getCurrentWeight()
                
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
                            caloriesBurned = body.caloriesBurned?.toFloat() ?: 0f,
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
                            caloriesBurned = 0f,
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
                        caloriesBurned = 0f,
                        intensity = intensity,
                        provenance = Provenance(ProvenanceSource.UNRESOLVED, null, 0f),
                        displayOrder = currentEntry.items.size
                    )
                }

                val updatedItems = currentEntry.items + newItem
                val updatedEntry = currentEntry.copy(
                    items = updatedItems,
                    totalCalories = updatedItems.map { it.caloriesBurned }.sum(),
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
                val originalEntry = loggingRepository.getExerciseEntryById(entry.id)
                val originalItems = originalEntry?.items ?: emptyList()
                
                val updatedItems = entry.items.map { item ->
                    val originalItem = originalItems.find { it.id == item.id }
                    
                    // Check if critical fields have changed
                    val hasChanged = originalItem == null || // New item
                        originalItem.activityName != item.activityName ||
                        originalItem.durationMinutes != item.durationMinutes ||
                        originalItem.caloriesBurned != item.caloriesBurned

                    if (hasChanged) {
                        item.copy(
                            provenance = item.provenance.copy(
                                source = ProvenanceSource.USER_OVERRIDE,
                                confidence = 1.0f
                            )
                        )
                    } else {
                        item
                    }
                }

                val updatedEntry = entry.copy(
                    items = updatedItems,
                    totalCalories = updatedItems.map { it.caloriesBurned }.sum(),
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

    fun deleteExerciseItem(item: ExerciseItem) {
        viewModelScope.launch {
            try {
                val currentEntry = uiState.value.exerciseEntry ?: return@launch
                val updatedItems = currentEntry.items.filter { it.id != item.id || (item.id == 0L && it !== item) }
                
                if (updatedItems.isEmpty()) {
                    loggingRepository.deleteExerciseEntry(currentEntry.id)
                    soundManager.playCrumple()
                    hapticManager.vibrateSoft()
                    _uiState.update { it.copy(entryDeleted = true) }
                } else {
                    val updatedEntry = currentEntry.copy(
                        items = updatedItems,
                        totalCalories = updatedItems.map { it.caloriesBurned }.sum(),
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
                soundManager.playCrumple()
                hapticManager.vibrateSoft()
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
    val calories: Float,
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
