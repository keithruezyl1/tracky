package com.tracky.app.ui.screens.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracky.app.data.local.dao.SavedEntryDao
import com.tracky.app.data.local.entity.SavedEntryEntity
import com.tracky.app.data.repository.LoggingRepository
import com.tracky.app.data.repository.ProfileRepository
import com.tracky.app.domain.model.ExerciseEntry
import com.tracky.app.domain.model.ExerciseIntensity
import com.tracky.app.domain.model.ExerciseItem
import com.tracky.app.domain.model.FoodEntry
import com.tracky.app.domain.model.FoodItem
import com.tracky.app.domain.model.Provenance
import com.tracky.app.domain.model.ProvenanceSource
import com.tracky.app.domain.model.SavedEntry
import com.tracky.app.domain.model.SavedEntryData
import com.tracky.app.domain.model.SavedEntryType
import com.tracky.app.domain.model.SavedExerciseItem
import com.tracky.app.domain.model.SavedFoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class SavedEntriesViewModel @Inject constructor(
    private val savedEntryDao: SavedEntryDao,
    private val loggingRepository: LoggingRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedEntriesUiState())
    val uiState: StateFlow<SavedEntriesUiState> = _uiState.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true }

    init {
        loadSavedEntries()
    }

    private fun loadSavedEntries() {
        viewModelScope.launch {
            savedEntryDao.getAllEntries().collect { entities ->
                val entries = entities.map { it.toDomain() }
                _uiState.update { it.copy(entries = entries) }
            }
        }
    }

    fun useSavedEntry(entry: SavedEntry) {
        viewModelScope.launch {
            try {
                // Increment use count
                savedEntryDao.incrementUseCount(entry.id, System.currentTimeMillis())
                val now = Clock.System.now()
                val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
                val timestamp = now.toEpochMilliseconds()

                // Create a new entry from the saved template
                when (entry.entryType) {
                    SavedEntryType.FOOD -> {
                        val foodData = entry.data as? SavedEntryData.FoodData
                        if (foodData != null) {
                            val foodEntry = FoodEntry(
                                date = localDateTime.date.toString(),
                                time = localDateTime.time.toString(),
                                timestamp = timestamp,
                                totalCalories = foodData.items.map { it.calories }.sum(),
                                totalCarbsG = foodData.items.sumOf { it.carbsG.toDouble() }.toFloat(),
                                totalProteinG = foodData.items.sumOf { it.proteinG.toDouble() }.toFloat(),
                                totalFatG = foodData.items.sumOf { it.fatG.toDouble() }.toFloat(),
                                analysisNarrative = "Logged from saved entry: ${entry.name}",
                                photoPath = null,
                                originalInput = "Saved: ${entry.name}",
                                items = foodData.items.mapIndexed { index, item ->
                                    FoodItem(
                                        name = item.name,
                                        matchedName = null,
                                        quantity = item.quantity,
                                        unit = item.unit,
                                        calories = item.calories,
                                        carbsG = item.carbsG,
                                        proteinG = item.proteinG,
                                        fatG = item.fatG,
                                        provenance = Provenance(
                                            source = ProvenanceSource.USER_OVERRIDE,
                                            sourceId = "saved_${entry.id}",
                                            confidence = 1f
                                        ),
                                        displayOrder = index
                                    )
                                },
                                createdAt = timestamp,
                                updatedAt = timestamp
                            )

                            loggingRepository.saveFoodEntry(foodEntry)
                            _uiState.update { it.copy(message = "Added ${entry.name} to today's log") }
                        }
                    }
                    SavedEntryType.EXERCISE -> {
                        val exerciseData = entry.data as? SavedEntryData.ExerciseData
                        if (exerciseData != null) {
                            val profile = profileRepository.getProfileOnce()
                            val userWeightKg = profile?.currentWeightKg ?: 70f
                            
                            val exerciseItems = exerciseData.items.mapIndexed { index, item ->
                                // Calculate calories: MET * Weight (kg) * Duration (hr)
                                val durationHours = item.durationMinutes / 60f
                                val calories = (item.metValue * userWeightKg * durationHours)
                                
                                ExerciseItem(
                                    activityName = item.activityName,
                                    durationMinutes = item.durationMinutes,
                                    metValue = item.metValue,
                                    caloriesBurned = calories,
                                    intensity = ExerciseIntensity.MODERATE, // Defaulting, stored templates didn't have intensity before
                                    provenance = Provenance(
                                        source = ProvenanceSource.USER_OVERRIDE,
                                        sourceId = "saved_${entry.id}",
                                        confidence = 1f
                                    ),
                                    displayOrder = index
                                )
                            }
                            
                            val exerciseEntry = ExerciseEntry(
                                date = localDateTime.date.toString(),
                                time = localDateTime.time.toString(),
                                timestamp = timestamp,
                                items = exerciseItems,
                                totalCalories = exerciseItems.map { it.caloriesBurned }.sum(),
                                totalDurationMinutes = exerciseItems.sumOf { it.durationMinutes },
                                userWeightKg = userWeightKg,
                                originalInput = "Saved: ${entry.name}",
                                createdAt = timestamp,
                                updatedAt = timestamp
                            )
                            
                            loggingRepository.saveExerciseEntry(exerciseEntry)
                            _uiState.update { it.copy(message = "Added ${entry.name} to today's log") }
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteSavedEntry(id: Long) {
        viewModelScope.launch {
            savedEntryDao.deleteById(id)
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    private fun SavedEntryEntity.toDomain(): SavedEntry {
        // Parse JSON data based on entry type
        val data = try {
            if (entryType == SavedEntryType.FOOD.value) {
                val parsed = json.decodeFromString<SavedFoodDataJson>(entryDataJson)
                SavedEntryData.FoodData(
                    items = parsed.items.map { item ->
                        SavedFoodItem(
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
            } else {
                 // Try to decode as list (new format) first
                try {
                    val parsed = json.decodeFromString<SavedExerciseDataJson>(entryDataJson)
                    SavedEntryData.ExerciseData(
                        items = parsed.items.map { item ->
                            SavedExerciseItem(
                                activityName = item.activityName,
                                durationMinutes = item.durationMinutes,
                                metValue = item.metValue
                            )
                        }
                    )
                } catch (e: Exception) {
                    // Fallback to old format (single item) if it was saved before update
                    // or if I messed up the JSON structure update logic
                    // Although simple destructive migration was used so old data is gone from DB?
                    // "fallbackToDestructiveMigration" typically clears DB on version mismatch.
                    // So we might not have old data. But good to be safe.
                    // Actually, if we cleared DB, we have NO old data.
                    SavedEntryData.ExerciseData(emptyList())
                }
            }
        } catch (e: Exception) {
            if (entryType == SavedEntryType.FOOD.value) {
                SavedEntryData.FoodData(emptyList())
            } else {
                SavedEntryData.ExerciseData(emptyList())
            }
        }

        return SavedEntry(
            id = id,
            name = name,
            entryType = SavedEntryType.fromValue(entryType),
            data = data,
            totalCalories = totalCalories,
            useCount = useCount,
            lastUsedAt = lastUsedAt,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
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

data class SavedEntriesUiState(
    val entries: List<SavedEntry> = emptyList(),
    val message: String? = null,
    val error: String? = null
)
