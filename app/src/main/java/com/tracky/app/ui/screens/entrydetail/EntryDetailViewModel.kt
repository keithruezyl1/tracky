package com.tracky.app.ui.screens.entrydetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracky.app.data.local.dao.SavedEntryDao
import com.tracky.app.data.local.entity.SavedEntryEntity
import com.tracky.app.data.remote.TrackyBackendApi
import com.tracky.app.data.remote.dto.ResolveExerciseRequest
import com.tracky.app.util.toTitleCase
import com.tracky.app.data.remote.dto.LogFoodRequest
import com.tracky.app.data.repository.FoodsRepository
import com.tracky.app.data.repository.LoggingRepository
import com.tracky.app.data.repository.ProfileRepository
import com.tracky.app.data.repository.ResolvedFoodResult
import com.tracky.app.domain.logic.NutritionConflictDetector
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
    private val canonicalKeyGenerator: com.tracky.app.domain.resolver.CanonicalKeyGenerator,
    private val nutritionConflictDetector: NutritionConflictDetector
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
                val originalEntry = loggingRepository.getFoodEntryById(entry.id)
                val originalItems = originalEntry?.items ?: emptyList()
                val itemsToReanalyze = mutableListOf<Pair<Long, Long>>() // itemId, revision

                val updatedItems = entry.items.map { item ->
                    val originalItem = originalItems.find { it.id == item.id }
                    
                    if (originalItem == null) {
                         // New item (should typically be handled by addFoodItem, but handling here for safety)
                         item
                    } else {
                         val nameChanged = originalItem.name != item.name
                         val unitChanged = originalItem.unit != item.unit
                         val macrosChanged = originalItem.calories != item.calories ||
                                            originalItem.carbsG != item.carbsG ||
                                            originalItem.proteinG != item.proteinG ||
                                            originalItem.fatG != item.fatG

                         var newItem = item
                         
                         // Rule B & C: Macros changed -> Manual
                         if (macrosChanged) {
                             newItem = newItem.copy(
                                 isManualMacros = true,
                                 provenance = newItem.provenance.copy(source = ProvenanceSource.USER_OVERRIDE)
                             )
                         }

                         // Rule A & B: Name/Unit changed -> Increment revision, trigger analysis
                         if (nameChanged || unitChanged) {
                             val newRevision = originalItem.analysisRevision + 1
                             newItem = newItem.copy(
                                 analysisRevision = newRevision,
                                 // Requirement: "Rule A — Name changed, macros NOT changed -> overwrite macros"
                                 // reset manual if name/unit changed and macros didn't change in this specific edit.
                                 isManualMacros = if (macrosChanged) true else false,
                                 pendingSuggestion = null, // Clear old suggestion
                                 isAnalyzing = true
                             )
                             itemsToReanalyze.add(newItem.id to newRevision)
                         }
                         
                         newItem.copy(
                            canonicalKey = if (nameChanged) canonicalKeyGenerator.generate(newItem.name) else newItem.canonicalKey
                         )
                    }
                }
            
                // Recalculate totals
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

                // Trigger Background Re-analysis
                itemsToReanalyze.forEach { (itemId, revision) ->
                    // Launch separate coroutines for parallel reanalysis if needed, or sequential
                    // Since reanalyzeFoodItem is suspend, calling it here will block this update loop.
                    // This is acceptable as updateFoodEntry is already in viewModelScope.launch
                    reanalyzeFoodItem(entry.id, itemId, revision)
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    private suspend fun reanalyzeFoodItem(entryId: Long, itemId: Long, revision: Long) {
            try {
                // 1. Fetch current item info to get name (snapshot)
                val currentEntry = loggingRepository.getFoodEntryById(entryId) ?: return
                val currentItem = currentEntry.items.find { it.id == itemId } ?: return
                
                // sanity check revision
                if (currentItem.analysisRevision != revision) return

                val userWeightKg = getCurrentWeight()
                
                // 2. Fetch AI Estimates via log/food
                // We construct a natural language query to get the AI to re-evaluate the item
                val query = "${currentItem.quantity} ${currentItem.unit} ${currentItem.name}"
                val logRequest = LogFoodRequest(
                    text = query,
                    imageBase64 = null,
                    userWeightKg = userWeightKg
                )
                
                var aiCalories: Float? = null
                var aiCarbs: Float? = null
                var aiProtein: Float? = null
                var aiFat: Float? = null

                try {
                    val response = backendApi.logFood(logRequest)
                    if (response.isSuccessful) {
                        val body = response.body()
                        val firstItem = body?.items?.firstOrNull()
                        if (firstItem != null) {
                            aiCalories = firstItem.calories
                            aiCarbs = firstItem.carbs
                            aiProtein = firstItem.protein
                            aiFat = firstItem.fat
                        }
                    }
                } catch (e: Exception) {
                    // Network/backend error: Proceed without AI estimates (will likely be Unresolved unless local match exists)
                    // e.printStackTrace() 
                }

                // 3. Resolve using Repository (with AI fallback data)
                val result = foodsRepository.resolveFood(
                    name = currentItem.name, 
                    quantity = currentItem.quantity, 
                    unit = currentItem.unit,
                    aiCalories = aiCalories,
                    aiCarbsG = aiCarbs,
                    aiProteinG = aiProtein,
                    aiFatG = aiFat
                )
                
                // 4. Update DB
                val freshEntry = loggingRepository.getFoodEntryById(entryId) ?: return
                val freshItem = freshEntry.items.find { it.id == itemId } ?: return

                if (freshItem.analysisRevision == revision) {
                    val newItem = if (result is ResolvedFoodResult.Success) {
                        val resolved = result.foodItem
                        
                        // Decision Matrix:
                        // Case 1: Auto (AI) source, NOT manual macros -> Overwrite
                        // Case 2: Manual (User) source OR Manual macros -> Suggest if material diff
                        
                        val isManualSource = freshItem.provenance.source == ProvenanceSource.USER_OVERRIDE
                        val isLocked = freshItem.isManualMacros || isManualSource

                        if (!isLocked) {
                            // Rule A: Overwrite
                            freshItem.copy(
                                calories = resolved.calories,
                                carbsG = resolved.carbsG,
                                proteinG = resolved.proteinG,
                                fatG = resolved.fatG,
                                matchedName = resolved.matchedName,
                                provenance = resolved.provenance,
                                isAnalyzing = false
                            )
                        } else {
                            // Rule B: Pending Suggestion (Only if material diff)
                            val hasMaterialDiff = nutritionConflictDetector.isMaterialDifference(freshItem, resolved)
                            
                            if (hasMaterialDiff) {
                                freshItem.copy(
                                    pendingSuggestion = resolved.copy(id = freshItem.id, isAnalyzing = false),
                                    isAnalyzing = false
                                )
                            } else {
                                // No material diff, just finish analyzing without changes
                                freshItem.copy(isAnalyzing = false)
                            }
                        }
                    } else {
                        // Failure: Clear isAnalyzing but keep original values
                        freshItem.copy(isAnalyzing = false)
                    }

                    val newItems = freshEntry.items.map { if (it.id == itemId) newItem else it }
                    
                    val updatedfreshEntry = freshEntry.copy(
                        items = newItems,
                        totalCalories = newItems.sumOf { it.calories.toDouble() }.toFloat(),
                        totalCarbsG = newItems.sumOf { it.carbsG.toDouble() }.toFloat(),
                        totalProteinG = newItems.sumOf { it.proteinG.toDouble() }.toFloat(),
                        totalFatG = newItems.sumOf { it.fatG.toDouble() }.toFloat(),
                        updatedAt = System.currentTimeMillis()
                    )
                    loggingRepository.updateFoodEntry(updatedfreshEntry)
                    _uiState.update { it.copy(foodEntry = updatedfreshEntry) }
                }
            } catch (e: Exception) {
                // Log error and reset (clears loading spinner)
                val cleanEntry = loggingRepository.getFoodEntryById(entryId)
                if (cleanEntry != null) {
                     val cleanItems = cleanEntry.items.map { 
                         if (it.id == itemId && it.analysisRevision == revision) it.copy(isAnalyzing = false) else it 
                     }
                     val entryWithClearedLoading = cleanEntry.copy(items = cleanItems)
                     _uiState.update { it.copy(foodEntry = entryWithClearedLoading) }
                     loggingRepository.updateFoodEntry(entryWithClearedLoading)
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

    fun addFoodItem(
        name: String, 
        quantity: Float, 
        unit: String,
        calories: Float? = null,
        carbs: Float? = null,
        protein: Float? = null,
        fat: Float? = null
    ) {
        viewModelScope.launch {
            try {
                val currentEntry = uiState.value.foodEntry ?: return@launch
                
                // If user provided macros, we skip resolution (or resolve for name matching but use user macros)
                val isManual = calories != null || carbs != null || protein != null || fat != null
                
                val newItem = FoodItem(
                    id = 0,
                    name = name.toTitleCase(),
                    matchedName = null,
                    quantity = quantity,
                    unit = unit,
                    calories = calories ?: 0f,
                    carbsG = carbs ?: 0f,
                    proteinG = protein ?: 0f,
                    fatG = fat ?: 0f,
                    provenance = if (isManual) Provenance(ProvenanceSource.USER_OVERRIDE, null, 1.0f)
                                 else Provenance(ProvenanceSource.UNRESOLVED, null, 0.5f),
                    displayOrder = currentEntry.items.size,
                    canonicalKey = canonicalKeyGenerator.generate(name),
                    isManualMacros = isManual,
                    isAnalyzing = !isManual // Set loading state immediately for auto-resolving items
                )

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

                // Update UI and DB immediately with the placeholder/manual item
                _uiState.update { it.copy(foodEntry = updatedEntry, isLoading = true) }
                loggingRepository.updateFoodEntry(updatedEntry)

                // If it's an auto-resolving item, trigger background analysis
                if (!isManual) {
                    // Fetch the saved entry to get the correctly assigned ID for the new item
                    val persistedEntry = loggingRepository.getFoodEntryById(currentEntry.id)
                    val persistedItem = persistedEntry?.items?.lastOrNull()
                    if (persistedItem != null && persistedItem.name == name) {
                        reanalyzeFoodItem(currentEntry.id, persistedItem.id, persistedItem.analysisRevision)
                    }
                }
                
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Exercise Entry Actions
    // ─────────────────────────────────────────────────────────────────────────────

    fun addExerciseItem(
        activityName: String, 
        durationMinutes: Int, 
        intensity: ExerciseIntensity? = null,
        calories: Float? = null
    ) {
        viewModelScope.launch {
            try {
                val currentEntry = _uiState.value.exerciseEntry ?: return@launch
                
                val isManual = calories != null
                
                val newItem = ExerciseItem(
                    id = 0,
                    activityName = activityName.toTitleCase(),
                    durationMinutes = durationMinutes,
                    metValue = 0f,
                    caloriesBurned = calories ?: 0f,
                    intensity = intensity,
                    provenance = if (isManual) Provenance(ProvenanceSource.USER_OVERRIDE, null, 1f)
                                 else Provenance(ProvenanceSource.UNRESOLVED, null, 0f),
                    displayOrder = currentEntry.items.size,
                    isManual = isManual,
                    isAnalyzing = !isManual
                )

                val updatedItems = currentEntry.items + newItem
                val updatedEntry = currentEntry.copy(
                    items = updatedItems,
                    totalCalories = updatedItems.map { it.caloriesBurned }.sum(),
                    totalDurationMinutes = updatedItems.sumOf { it.durationMinutes },
                    updatedAt = System.currentTimeMillis()
                )
                
                // Update UI and DB immediately
                _uiState.update { it.copy(exerciseEntry = updatedEntry, isLoading = true) }
                loggingRepository.updateExerciseEntry(updatedEntry)

                // If not manual, trigger background re-analysis
                if (!isManual) {
                    val persistedEntry = loggingRepository.getExerciseEntryById(currentEntry.id)
                    val persistedItem = persistedEntry?.items?.lastOrNull()
                    if (persistedItem != null && persistedItem.activityName == activityName) {
                        reanalyzeExerciseItem(currentEntry.id, persistedItem.id, persistedItem.analysisRevision)
                    }
                }
                
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) } // Ensure loading cleared on error
            }
        }
    }

    fun updateExerciseEntry(entry: ExerciseEntry) {
        viewModelScope.launch {
            try {
                val originalEntry = loggingRepository.getExerciseEntryById(entry.id)
                val originalItems = originalEntry?.items ?: emptyList()
                val itemsToReanalyze = mutableListOf<Pair<Long, Long>>()

                val updatedItems = entry.items.map { item ->
                    val originalItem = originalItems.find { it.id == item.id }
                    
                    if (originalItem == null) {
                         item
                    } else {
                         val activityChanged = originalItem.activityName != item.activityName
                         // Check manual changes (calories or intensity)
                         val manualChanged = originalItem.caloriesBurned != item.caloriesBurned ||
                                            originalItem.intensity != item.intensity

                         var newItem = item
                         
                         if (manualChanged) {
                             newItem = newItem.copy(
                                 isManual = true,
                                 provenance = newItem.provenance.copy(source = ProvenanceSource.USER_OVERRIDE)
                             )
                         }

                         if (activityChanged) {
                             val newRevision = originalItem.analysisRevision + 1
                             newItem = newItem.copy(
                                 analysisRevision = newRevision,
                                 // Reset manual if name changed (assume new activity = auto unless user also edited manual fields)
                                 isManual = if (manualChanged) true else false,
                                 pendingSuggestion = null,
                                 isAnalyzing = true
                             )
                             itemsToReanalyze.add(newItem.id to newRevision)
                         }
                         newItem
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

                itemsToReanalyze.forEach { (itemId, revision) ->
                    reanalyzeExerciseItem(entry.id, itemId, revision)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    private suspend fun reanalyzeExerciseItem(entryId: Long, itemId: Long, revision: Long) {
            try {
                val currentEntry = loggingRepository.getExerciseEntryById(entryId) ?: return
                val currentItem = currentEntry.items.find { it.id == itemId } ?: return
                if (currentItem.analysisRevision != revision) return

                val userWeightKg = getCurrentWeight()
                
                val activityDisplay = currentItem.activityName
                val intensitySuffix = currentItem.intensity?.let { " (${it.value})" } ?: ""
                val resolveActivity = if (currentItem.intensity == null) activityDisplay else "$activityDisplay$intensitySuffix"

                val response = backendApi.resolveExercise(
                    ResolveExerciseRequest(
                        activity = resolveActivity, 
                        durationMinutes = currentItem.durationMinutes,
                        userWeightKg = userWeightKg
                    )
                )

                val body = response.body()
                val freshEntry = loggingRepository.getExerciseEntryById(entryId) ?: return
                val freshItem = freshEntry.items.find { it.id == itemId } ?: return

                if (freshItem.analysisRevision == revision) {
                    val newItem = if (response.isSuccessful && body != null) {
                         val resolved = freshItem.copy(
                            caloriesBurned = body.caloriesBurned?.toFloat() ?: 0f,
                            metValue = body.metValue ?: 0f,
                            provenance = Provenance(ProvenanceSource.DATASET, null, 1f),
                            isAnalyzing = false
                         )

                        // Decision Matrix (Exercise)
                        val isManualSource = freshItem.provenance.source == ProvenanceSource.USER_OVERRIDE
                        val isLocked = freshItem.isManual || isManualSource

                        if (!isLocked) {
                             resolved
                        } else {
                            // Suggestion if material diff
                            val hasMaterialDiff = nutritionConflictDetector.isMaterialDifference(freshItem, resolved)
                            if (hasMaterialDiff) {
                                freshItem.copy(
                                    pendingSuggestion = resolved.copy(id = freshItem.id, isAnalyzing = false),
                                    isAnalyzing = false
                                )
                            } else {
                                freshItem.copy(isAnalyzing = false)
                            }
                        }
                    } else {
                        freshItem.copy(isAnalyzing = false)
                    }
                    
                    val newItems = freshEntry.items.map { if (it.id == itemId) newItem else it }
                    val updatedFreshEntry = freshEntry.copy(
                        items = newItems,
                        totalCalories = newItems.map { it.caloriesBurned }.sum(),
                        totalDurationMinutes = newItems.sumOf { it.durationMinutes },
                        updatedAt = System.currentTimeMillis()
                    )
                    loggingRepository.updateExerciseEntry(updatedFreshEntry)
                    _uiState.update { it.copy(exerciseEntry = updatedFreshEntry) }
                }
            } catch (e: Exception) {
                // Log error and reset (clears loading spinner)
                val cleanEntry = loggingRepository.getExerciseEntryById(entryId)
                if (cleanEntry != null) {
                    val cleanItems = cleanEntry.items.map { 
                        if (it.id == itemId && it.analysisRevision == revision) it.copy(isAnalyzing = false) else it 
                    }
                    val entryWithClearedLoading = cleanEntry.copy(items = cleanItems)
                    _uiState.update { it.copy(exerciseEntry = entryWithClearedLoading) }
                    loggingRepository.updateExerciseEntry(entryWithClearedLoading)
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

    // ─────────────────────────────────────────────────────────────────────────────
    // Pending Suggestions Actions
    // ─────────────────────────────────────────────────────────────────────────────

    fun applyFoodSuggestion(item: FoodItem) {
        viewModelScope.launch {
            val suggestion = item.pendingSuggestion ?: return@launch
            val currentEntry = uiState.value.foodEntry ?: return@launch
            
            val updatedItems = currentEntry.items.map {
                if (it.id == item.id) {
                    suggestion.copy(
                        id = item.id, // Ensure ID is preserved
                        isManualMacros = false, // Reset manual flag since we accepted auto
                        pendingSuggestion = null,
                        analysisRevision = item.analysisRevision + 1
                    )
                } else it
            }
            
            val updatedEntry = currentEntry.copy(
                items = updatedItems,
                totalCalories = updatedItems.sumOf { it.calories.toDouble() }.toFloat(),
                totalCarbsG = updatedItems.sumOf { it.carbsG.toDouble() }.toFloat(),
                totalProteinG = updatedItems.sumOf { it.proteinG.toDouble() }.toFloat(),
                totalFatG = updatedItems.sumOf { it.fatG.toDouble() }.toFloat(),
                updatedAt = System.currentTimeMillis()
            )
            
            loggingRepository.updateFoodEntry(updatedEntry)
            _uiState.update { it.copy(foodEntry = updatedEntry) }
        }
    }

    fun discardFoodSuggestion(item: FoodItem) {
        viewModelScope.launch {
            val currentEntry = uiState.value.foodEntry ?: return@launch
            
            val updatedItems = currentEntry.items.map {
                if (it.id == item.id) {
                    it.copy(pendingSuggestion = null)
                } else it
            }
            
            val updatedEntry = currentEntry.copy(items = updatedItems)
            loggingRepository.updateFoodEntry(updatedEntry)
            _uiState.update { it.copy(foodEntry = updatedEntry) }
        }
    }

    fun applyExerciseSuggestion(item: ExerciseItem) {
        viewModelScope.launch {
            val suggestion = item.pendingSuggestion ?: return@launch
            val currentEntry = uiState.value.exerciseEntry ?: return@launch
            
            val updatedItems = currentEntry.items.map {
                if (it.id == item.id) {
                    suggestion.copy(
                        id = item.id,
                        isManual = false,
                        pendingSuggestion = null,
                        analysisRevision = item.analysisRevision + 1
                    )
                } else it
            }
            
            val updatedEntry = currentEntry.copy(
                items = updatedItems,
                totalCalories = updatedItems.map { it.caloriesBurned }.sum(),
                totalDurationMinutes = updatedItems.sumOf { it.durationMinutes },
                updatedAt = System.currentTimeMillis()
            )
            
            loggingRepository.updateExerciseEntry(updatedEntry)
            _uiState.update { it.copy(exerciseEntry = updatedEntry) }
        }
    }

    fun discardExerciseSuggestion(item: ExerciseItem) {
        viewModelScope.launch {
            val currentEntry = uiState.value.exerciseEntry ?: return@launch
            
            val updatedItems = currentEntry.items.map {
                if (it.id == item.id) {
                    it.copy(pendingSuggestion = null)
                } else it
            }
            
            val updatedEntry = currentEntry.copy(items = updatedItems)
            loggingRepository.updateExerciseEntry(updatedEntry)
            _uiState.update { it.copy(exerciseEntry = updatedEntry) }
        }
    }

    fun triggerEntryReanalysis() {
        viewModelScope.launch {
            try {
                if (entryType == "food") {
                    val entry = loggingRepository.getFoodEntryById(entryId) ?: return@launch
                    val itemsToReanalyze = mutableListOf<Pair<Long, Long>>() // itemId, revision
                    
                    val updatedItems = entry.items.map { item ->
                        val newRevision = item.analysisRevision + 1
                        // Set analyzing state but DON'T change manual flags or pending suggestions yet
                        // Just prepare for re-check
                        itemsToReanalyze.add(item.id to newRevision)
                        item.copy(
                            analysisRevision = newRevision,
                            isAnalyzing = true,
                            pendingSuggestion = null // Clear old suggestions when forcing re-analysis
                        )
                    }
                    
                    val updatedEntry = entry.copy(items = updatedItems)
                    loggingRepository.updateFoodEntry(updatedEntry)
                    _uiState.update { it.copy(foodEntry = updatedEntry) }
                    
                    itemsToReanalyze.forEach { (itemId, revision) ->
                        reanalyzeFoodItem(entry.id, itemId, revision)
                    }
                    
                } else {
                    val entry = loggingRepository.getExerciseEntryById(entryId) ?: return@launch
                    val itemsToReanalyze = mutableListOf<Pair<Long, Long>>()
                    
                    val updatedItems = entry.items.map { item ->
                        val newRevision = item.analysisRevision + 1
                        itemsToReanalyze.add(item.id to newRevision)
                        item.copy(
                            analysisRevision = newRevision,
                            isAnalyzing = true,
                            pendingSuggestion = null
                        )
                    }
                    
                    val updatedEntry = entry.copy(items = updatedItems)
                    loggingRepository.updateExerciseEntry(updatedEntry)
                    _uiState.update { it.copy(exerciseEntry = updatedEntry) }
                    
                    itemsToReanalyze.forEach { (itemId, revision) ->
                        reanalyzeExerciseItem(entry.id, itemId, revision)
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
