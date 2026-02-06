package com.tracky.app.domain.usecase

import com.tracky.app.data.remote.TrackyBackendApi
import com.tracky.app.data.remote.dto.LogAutoRequest
import com.tracky.app.data.remote.dto.LogExerciseRequest
import com.tracky.app.data.remote.dto.LogFoodRequest
import com.tracky.app.data.remote.dto.ResolveExerciseRequest
import com.tracky.app.data.repository.FoodsRepository
import com.tracky.app.data.repository.LoggingRepository
import com.tracky.app.data.repository.ProfileRepository
import com.tracky.app.data.repository.ResolvedFoodResult
import com.tracky.app.domain.model.DraftData
import com.tracky.app.domain.model.DraftFoodItem
import com.tracky.app.domain.model.DraftStatus
import com.tracky.app.domain.model.ExerciseEntry
import com.tracky.app.domain.model.ExerciseIntensity
import com.tracky.app.domain.model.FoodEntry
import com.tracky.app.domain.model.FoodItem
import com.tracky.app.domain.model.Provenance
import com.tracky.app.domain.model.ProvenanceSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Draft Logging Interactor
 * 
 * Implements the AI drafting pipeline with dataset-first resolution.
 * Enforces that:
 * - Gemini only parses, never invents nutrition values
 * - Local dataset is checked first
 * - USDA is fallback via backend proxy
 * - User must confirm drafts before persistence (no silent logging)
 */
@Singleton
class DraftLoggingInteractor @Inject constructor(
    private val backendApi: TrackyBackendApi,
    private val foodsRepository: FoodsRepository,
    private val loggingRepository: LoggingRepository,
    private val profileRepository: ProfileRepository
) {

    private fun sentenceCase(text: String): String {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return trimmed
        val lower = trimmed.lowercase()
        return lower.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
    private val _draftState = MutableStateFlow<DraftState>(DraftState.Idle)
    val draftState: StateFlow<DraftState> = _draftState.asStateFlow()

    // ─────────────────────────────────────────────────────────────────────────
    // Auto-Detect Drafting (AI determines if food/exercise/mixed/none)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Auto-detect and draft from text - AI determines if input is food, exercise, mixed, or none
     */
    suspend fun draftAutoFromText(text: String): DraftResult {
        _draftState.value = DraftState.Drafting

        val profile = profileRepository.getProfileOnce()
        val userWeightKg = profile?.currentWeightKg ?: 70f

        return try {
            // Call the auto-detect endpoint
            val autoResponse = backendApi.logAuto(
                LogAutoRequest(text = text, userWeightKg = userWeightKg)
            )

            if (!autoResponse.isSuccessful || autoResponse.body() == null) {
                _draftState.value = DraftState.Error("Failed to analyze input")
                return DraftResult.Error("Failed to analyze input")
            }

            val parsed = autoResponse.body()!!

            when (parsed.entry_type) {
                "food" -> {
                    // Route to food drafting with the parsed items
                    if (parsed.food_items.isEmpty()) {
                        _draftState.value = DraftState.Error("No food items detected")
                        return DraftResult.Error("No food items detected")
                    }
                    // Resolve food items
                    resolveFoodItemsAndCreateDraft(parsed.food_items, parsed.narrative, text)
                }
                "exercise" -> {
                    // Route to exercise drafting
                    // Route to exercise drafting
                    if (parsed.exercises.isEmpty()) {
                        _draftState.value = DraftState.Error("No exercise details detected")
                        return DraftResult.Error("No exercise details detected")
                    }
                    resolveExercisesAndCreateDraft(parsed.exercises, userWeightKg, text)
                }
                "mixed" -> {
                    // For mixed, handle both
                    // We prioritize adding everything to the draft
                    // But current UI might only show one draft type active at a time?
                    // Actually, Multi-modal draft is not fully supported in UI yet (it switches input mode).
                    // BUT per new requirements we should validly parse both.
                    // For now, if we have food, we draft food. If we have exercises, we draft exercises.
                    // If BOTH, we might need a composite state or just pick one.
                    // Existing logic prioritizes food. We will stick to that but ensure exercise logic is ready.
                    if (parsed.food_items.isNotEmpty()) {
                        resolveFoodItemsAndCreateDraft(parsed.food_items, parsed.narrative, text)
                    } else if (parsed.exercises.isNotEmpty()) {
                        resolveExercisesAndCreateDraft(parsed.exercises, userWeightKg, text)
                    } else {
                        _draftState.value = DraftState.Error("Unable to parse input")
                        DraftResult.Error("Unable to parse input")
                    }
                }
                else -> {
                    // "none" - not food or exercise
                    _draftState.value = DraftState.Error("Input doesn't appear to be food or exercise")
                    DraftResult.Error("Input doesn't appear to be food or exercise. Try describing what you ate or what exercise you did.")
                }
            }
        } catch (e: Exception) {
            _draftState.value = DraftState.Error(e.message ?: "Unknown error")
            DraftResult.Error(e.message ?: "Unknown error")
        }
    }

    private suspend fun resolveFoodItemsAndCreateDraft(
        items: List<com.tracky.app.data.remote.dto.ParsedFoodItemDto>,
        narrative: String?,
        originalInput: String
    ): DraftResult {
        val resolvedItems = mutableListOf<DraftFoodItem>()

        for (item in items) {
            val result = foodsRepository.resolveFood(
                name = item.name,
                quantity = item.quantity.toFloat(),
                unit = item.unit
            )

            val draftItem = when (result) {
                is ResolvedFoodResult.Success -> {
                    DraftFoodItem(
                        name = item.name,
                        matchedName = result.foodItem.matchedName,
                        quantity = item.quantity.toDouble(),
                        unit = item.unit,
                        calories = result.foodItem.calories,
                        carbsG = result.foodItem.carbsG,
                        proteinG = result.foodItem.proteinG,
                        fatG = result.foodItem.fatG,
                        provenance = result.foodItem.provenance,
                        resolved = true
                    )
                }
                else -> {
                    DraftFoodItem(
                        name = item.name,
                        matchedName = null,
                        quantity = item.quantity.toDouble(),
                        unit = item.unit,
                        calories = 0,
                        carbsG = 0f,
                        proteinG = 0f,
                        fatG = 0f,
                        provenance = Provenance(ProvenanceSource.USER_OVERRIDE, null, 0f),
                        resolved = false
                    )
                }
            }
            resolvedItems.add(draftItem)
        }

        val draftData = DraftData.FoodDraft(
            items = resolvedItems,
            totalCalories = resolvedItems.sumOf { it.calories },
            totalCarbsG = resolvedItems.sumOf { it.carbsG.toDouble() }.toFloat(),
            totalProteinG = resolvedItems.sumOf { it.proteinG.toDouble() }.toFloat(),
            totalFatG = resolvedItems.sumOf { it.fatG.toDouble() }.toFloat(),
            narrative = narrative
        )

        _draftState.value = DraftState.FoodDraft(draftData, DraftStatus.NEEDS_CONFIRMATION, originalInput)
        return DraftResult.Success(draftData)
    }

    private suspend fun resolveExercisesAndCreateDraft(
        exercises: List<com.tracky.app.data.remote.dto.ParsedExerciseDto>,
        userWeightKg: Float,
        originalInput: String
    ): DraftResult {
        val resolvedItems = mutableListOf<com.tracky.app.domain.model.DraftExerciseItem>()

        for (exercise in exercises) {
            val resolveResponse = backendApi.resolveExercise(
                ResolveExerciseRequest(
                    activity = exercise.activity,
                    durationMinutes = exercise.durationMinutes,
                    userWeightKg = userWeightKg
                )
            )

            if (resolveResponse.isSuccessful && resolveResponse.body() != null) {
                val resolved = resolveResponse.body()!!
                resolvedItems.add(
                    com.tracky.app.domain.model.DraftExerciseItem(
                        activity = exercise.activity,
                        durationMinutes = exercise.durationMinutes,
                        metValue = resolved.metValue ?: 0f,
                        caloriesBurned = resolved.caloriesBurned ?: 0,
                        intensity = exercise.intensity?.let { ExerciseIntensity.fromValue(it) } ?: ExerciseIntensity.MODERATE,
                        resolved = resolved.resolved
                    )
                )
            } else {
                 resolvedItems.add(
                    com.tracky.app.domain.model.DraftExerciseItem(
                        activity = exercise.activity,
                        durationMinutes = exercise.durationMinutes,
                        metValue = 0f,
                        caloriesBurned = 0,
                        intensity = exercise.intensity?.let { ExerciseIntensity.fromValue(it) } ?: ExerciseIntensity.MODERATE,
                        resolved = false
                    )
                )
            }
        }

        val draftData = DraftData.ExerciseDraft(
            items = resolvedItems,
            totalCalories = resolvedItems.sumOf { it.caloriesBurned },
            totalDurationMinutes = resolvedItems.sumOf { it.durationMinutes }
        )

        // If any item is unresolved, we might need confirmation or manual entry
        val status = if (resolvedItems.all { it.resolved }) {
            DraftStatus.NEEDS_CONFIRMATION
        } else {
            DraftStatus.NEEDS_CONFIRMATION
        }

        _draftState.value = DraftState.ExerciseDraft(draftData, status, originalInput)
        return DraftResult.Success(draftData)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Food Drafting
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Start drafting a food entry from text
     */
    suspend fun draftFoodFromText(text: String): DraftResult {
        _draftState.value = DraftState.Drafting

        val profile = profileRepository.getProfileOnce()
        val userWeightKg = profile?.currentWeightKg ?: 70f

        return try {
            // Step 1: Call Gemini to parse text into structured items
            val parseResponse = backendApi.logFood(
                LogFoodRequest(text = text, userWeightKg = userWeightKg)
            )

            if (!parseResponse.isSuccessful || parseResponse.body() == null) {
                _draftState.value = DraftState.Error("Failed to parse food")
                return DraftResult.Error("Failed to parse food input")
            }

            val parsed = parseResponse.body()!!
            if (parsed.items.isEmpty()) {
                _draftState.value = DraftState.Error("No food items detected")
                return DraftResult.Error("No food items detected in input")
            }

            // Step 2: Resolve each item using dataset-first approach
            val resolvedItems = mutableListOf<DraftFoodItem>()
            
            for ((index, item) in parsed.items.withIndex()) {
                val result = foodsRepository.resolveFood(
                    name = item.name,
                    quantity = item.quantity.toFloat(),
                    unit = item.unit
                )

                val draftItem = when (result) {
                    is ResolvedFoodResult.Success -> {
                        DraftFoodItem(
                            name = item.name,
                            matchedName = result.foodItem.matchedName,
                            quantity = item.quantity.toDouble(),
                            unit = item.unit,
                            calories = result.foodItem.calories,
                            carbsG = result.foodItem.carbsG,
                            proteinG = result.foodItem.proteinG,
                            fatG = result.foodItem.fatG,
                            provenance = result.foodItem.provenance,
                            resolved = true
                        )
                    }
                    is ResolvedFoodResult.NotFound -> {
                        DraftFoodItem(
                            name = item.name,
                            matchedName = null,
                            quantity = item.quantity.toDouble(),
                            unit = item.unit,
                            calories = 0,
                            carbsG = 0f,
                            proteinG = 0f,
                            fatG = 0f,
                            provenance = Provenance(
                                source = ProvenanceSource.USER_OVERRIDE,
                                sourceId = null,
                                confidence = 0f
                            ),
                            resolved = false
                        )
                    }
                    is ResolvedFoodResult.Error -> {
                        DraftFoodItem(
                            name = item.name,
                            matchedName = null,
                            quantity = item.quantity.toDouble(),
                            unit = item.unit,
                            calories = 0,
                            carbsG = 0f,
                            proteinG = 0f,
                            fatG = 0f,
                            provenance = Provenance(
                                source = ProvenanceSource.USER_OVERRIDE,
                                sourceId = null,
                                confidence = 0f
                            ),
                            resolved = false
                        )
                    }
                }
                resolvedItems.add(draftItem)
            }

            // Step 3: Calculate totals
            val totalCalories = resolvedItems.sumOf { it.calories }
            val totalCarbs = resolvedItems.sumOf { it.carbsG.toDouble() }.toFloat()
            val totalProtein = resolvedItems.sumOf { it.proteinG.toDouble() }.toFloat()
            val totalFat = resolvedItems.sumOf { it.fatG.toDouble() }.toFloat()

            val draftData = DraftData.FoodDraft(
                items = resolvedItems,
                totalCalories = totalCalories,
                totalCarbsG = totalCarbs,
                totalProteinG = totalProtein,
                totalFatG = totalFat,
                narrative = parsed.narrative
            )

            // Step 4: Check if needs clarification
            val hasUnresolved = resolvedItems.any { !it.resolved }
            val status = if (hasUnresolved) {
                DraftStatus.NEEDS_CONFIRMATION
            } else {
                DraftStatus.NEEDS_CONFIRMATION // Always require confirmation per PRD
            }

            _draftState.value = DraftState.FoodDraft(draftData, status, text)
            DraftResult.Success(draftData)

        } catch (e: Exception) {
            _draftState.value = DraftState.Error(e.message ?: "Unknown error")
            DraftResult.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Start drafting a food entry from photo
     */
    suspend fun draftFoodFromImage(imageBase64: String): DraftResult {
        _draftState.value = DraftState.Drafting

        val profile = profileRepository.getProfileOnce()
        val userWeightKg = profile?.currentWeightKg ?: 70f

        return try {
            val parseResponse = backendApi.logFood(
                LogFoodRequest(imageBase64 = imageBase64, userWeightKg = userWeightKg)
            )

            if (!parseResponse.isSuccessful || parseResponse.body() == null) {
                _draftState.value = DraftState.Error("Failed to analyze image")
                return DraftResult.Error("Failed to analyze food image")
            }

            // Same resolution flow as text
            val parsed = parseResponse.body()!!
            if (parsed.items.isEmpty()) {
                _draftState.value = DraftState.Error("No food items detected in image")
                return DraftResult.Error("No food items detected in image")
            }

            // Resolve items (same as text flow)
            val resolvedItems = parsed.items.map { item ->
                when (val result = foodsRepository.resolveFood(item.name, item.quantity.toFloat(), item.unit)) {
                    is ResolvedFoodResult.Success -> DraftFoodItem(
                        name = item.name,
                        matchedName = result.foodItem.matchedName,
                        quantity = item.quantity.toDouble(),
                        unit = item.unit,
                        calories = result.foodItem.calories,
                        carbsG = result.foodItem.carbsG,
                        proteinG = result.foodItem.proteinG,
                        fatG = result.foodItem.fatG,
                        provenance = result.foodItem.provenance,
                        resolved = true
                    )
                    else -> DraftFoodItem(
                        name = item.name,
                        matchedName = null,
                        quantity = item.quantity.toDouble(),
                        unit = item.unit,
                        calories = 0,
                        carbsG = 0f,
                        proteinG = 0f,
                        fatG = 0f,
                        provenance = Provenance(ProvenanceSource.USER_OVERRIDE, null, 0f),
                        resolved = false
                    )
                }
            }

            val draftData = DraftData.FoodDraft(
                items = resolvedItems,
                totalCalories = resolvedItems.sumOf { it.calories },
                totalCarbsG = resolvedItems.sumOf { it.carbsG.toDouble() }.toFloat(),
                totalProteinG = resolvedItems.sumOf { it.proteinG.toDouble() }.toFloat(),
                totalFatG = resolvedItems.sumOf { it.fatG.toDouble() }.toFloat(),
                narrative = parsed.narrative
            )

            _draftState.value = DraftState.FoodDraft(draftData, DraftStatus.NEEDS_CONFIRMATION, null)
            DraftResult.Success(draftData)

        } catch (e: Exception) {
            _draftState.value = DraftState.Error(e.message ?: "Unknown error")
            DraftResult.Error(e.message ?: "Unknown error")
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Exercise Drafting
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Draft an exercise entry from text
     */
    suspend fun draftExerciseFromText(text: String): DraftResult {
        _draftState.value = DraftState.Drafting

        val profile = profileRepository.getProfileOnce()
        val userWeightKg = profile?.currentWeightKg ?: 70f

        return try {
            // Step 1: Parse exercise via Gemini
            val parseResponse = backendApi.logExercise(
                LogExerciseRequest(text = text, userWeightKg = userWeightKg)
            )

            if (!parseResponse.isSuccessful || parseResponse.body() == null) {
                _draftState.value = DraftState.Error("Failed to parse exercise")
                return DraftResult.Error("Failed to parse exercise input")
            }

            val parsed = parseResponse.body()!!

            // Step 2: Resolve exercises to calories via MET
            // We reuse the list resolver
            resolveExercisesAndCreateDraft(parsed.exercises, userWeightKg, text)

        } catch (e: Exception) {
            _draftState.value = DraftState.Error(e.message ?: "Unknown error")
            DraftResult.Error(e.message ?: "Unknown error")
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Confirmation & Persistence
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Confirm and persist a food draft
     * This is the ONLY way to persist entries - enforces no silent logging
     */
    suspend fun confirmFoodDraft(draft: DraftData.FoodDraft, targetDate: LocalDate): ConfirmResult {
        val now = Clock.System.now()
        val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
        val timestamp = now.toEpochMilliseconds()

        val entry = FoodEntry(
            date = targetDate.toString(),
            time = localDateTime.time.toString(),
            timestamp = timestamp,
            totalCalories = draft.totalCalories,
            totalCarbsG = draft.totalCarbsG,
            totalProteinG = draft.totalProteinG,
            totalFatG = draft.totalFatG,
            analysisNarrative = draft.narrative,
            photoPath = null,
            originalInput = (_draftState.value as? DraftState.FoodDraft)?.originalInput,
            items = draft.items.mapIndexed { index, item ->
                FoodItem(
                    name = sentenceCase(item.name),
                    matchedName = item.matchedName,
                    quantity = item.quantity.toFloat(),
                    unit = item.unit,
                    calories = item.calories,
                    carbsG = item.carbsG,
                    proteinG = item.proteinG,
                    fatG = item.fatG,
                    provenance = item.provenance,
                    displayOrder = index
                )
            },
            createdAt = timestamp,
            updatedAt = timestamp
        )

        return try {
            val entryId = loggingRepository.saveFoodEntry(entry)
            _draftState.value = DraftState.Confirmed(entryId, isFood = true)
            ConfirmResult.Success(entryId)
        } catch (e: Exception) {
            ConfirmResult.Error(e.message ?: "Failed to save entry")
        }
    }

    /**
     * Confirm and persist an exercise draft
     */
    /**
     * Confirm and persist an exercise draft (multi)
     */
    suspend fun confirmExerciseDraft(draft: DraftData.ExerciseDraft, targetDate: LocalDate): ConfirmResult {
        val now = Clock.System.now()
        val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
        val timestamp = now.toEpochMilliseconds()

        val profile = profileRepository.getProfileOnce()
        val userWeight = profile?.currentWeightKg ?: 70f

        var lastEntryId = 0L

        return try {
            draft.items.forEach { item ->
                val entry = ExerciseEntry(
                    date = targetDate.toString(),
                    time = localDateTime.time.toString(),
                    timestamp = timestamp,
                    activityName = item.activity,
                    durationMinutes = item.durationMinutes,
                    metValue = item.metValue,
                    userWeightKg = userWeight,
                    caloriesBurned = item.caloriesBurned,
                    intensity = item.intensity,
                    originalInput = (_draftState.value as? DraftState.ExerciseDraft)?.originalInput,
                    provenance = Provenance(
                        source = ProvenanceSource.DATASET,
                        sourceId = "met_compendium",
                        confidence = 0.9f
                    ),
                    createdAt = timestamp,
                    updatedAt = timestamp
                )
                lastEntryId = loggingRepository.saveExerciseEntry(entry)
            }
            
            _draftState.value = DraftState.Confirmed(lastEntryId, isFood = false)
            ConfirmResult.Success(lastEntryId)
        } catch (e: Exception) {
            ConfirmResult.Error(e.message ?: "Failed to save entry")
        }
    }

    /**
     * Cancel current draft
     */
    fun cancelDraft() {
        _draftState.value = DraftState.Idle
    }

    /**
     * Update a specific food draft item
     */
    suspend fun updateFoodDraftItem(index: Int, name: String, quantity: Double, unit: String) {
        val currentState = _draftState.value
        if (currentState !is DraftState.FoodDraft) return

        val currentItems = currentState.data.items.toMutableList()
        if (index !in currentItems.indices) return

        // Re-resolve the item
        val result = foodsRepository.resolveFood(name, quantity.toFloat(), unit)
        
        val newItem = when (result) {
            is ResolvedFoodResult.Success -> {
                DraftFoodItem(
                    name = name,
                    matchedName = result.foodItem.matchedName,
                    quantity = quantity,
                    unit = unit,
                    calories = result.foodItem.calories,
                    carbsG = result.foodItem.carbsG,
                    proteinG = result.foodItem.proteinG,
                    fatG = result.foodItem.fatG,
                    provenance = result.foodItem.provenance,
                    resolved = true
                )
            }
            else -> {
                DraftFoodItem(
                    name = name,
                    matchedName = null,
                    quantity = quantity,
                    unit = unit,
                    calories = 0,
                    carbsG = 0f,
                    proteinG = 0f,
                    fatG = 0f,
                    provenance = Provenance(ProvenanceSource.USER_OVERRIDE, null, 0f),
                    resolved = false
                )
            }
        }

        currentItems[index] = newItem

        val newData = currentState.data.copy(
            items = currentItems,
            totalCalories = currentItems.sumOf { it.calories },
            totalCarbsG = currentItems.sumOf { it.carbsG.toDouble() }.toFloat(),
            totalProteinG = currentItems.sumOf { it.proteinG.toDouble() }.toFloat(),
            totalFatG = currentItems.sumOf { it.fatG.toDouble() }.toFloat()
        )

        _draftState.value = currentState.copy(data = newData)
    }

    /**
     * Update a specific exercise draft item
     */
    suspend fun updateExerciseDraftItem(index: Int, activity: String, durationMinutes: Int) {
        val currentState = _draftState.value
        if (currentState !is DraftState.ExerciseDraft) return

        val currentItems = currentState.data.items.toMutableList()
        if (index !in currentItems.indices) return

        val profile = profileRepository.getProfileOnce()
        val userWeightKg = profile?.currentWeightKg ?: 70f

        val resolveResponse = backendApi.resolveExercise(
            ResolveExerciseRequest(
                activity = activity,
                durationMinutes = durationMinutes,
                userWeightKg = userWeightKg
            )
        )

        val newItem = if (resolveResponse.isSuccessful && resolveResponse.body() != null) {
            val resolved = resolveResponse.body()!!
            com.tracky.app.domain.model.DraftExerciseItem(
                activity = activity,
                durationMinutes = durationMinutes,
                metValue = resolved.metValue ?: 0f,
                caloriesBurned = resolved.caloriesBurned ?: 0,
                intensity = ExerciseIntensity.fromValue(resolved.source ?: "moderate") ?: ExerciseIntensity.MODERATE,
                resolved = resolved.resolved
            )
        } else {
            com.tracky.app.domain.model.DraftExerciseItem(
                activity = activity,
                durationMinutes = durationMinutes,
                metValue = 0f,
                caloriesBurned = 0,
                intensity = ExerciseIntensity.MODERATE,
                resolved = false
            )
        }

        currentItems[index] = newItem

        val newData = currentState.data.copy(
            items = currentItems,
            totalCalories = currentItems.sumOf { it.caloriesBurned },
            totalDurationMinutes = currentItems.sumOf { it.durationMinutes }
        )

        _draftState.value = currentState.copy(data = newData)
    }

    /**
     * Reset to idle state
     */
    fun reset() {
        _draftState.value = DraftState.Idle
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// State Types
// ─────────────────────────────────────────────────────────────────────────────

sealed class DraftState {
    data object Idle : DraftState()
    data object Drafting : DraftState()
    data class FoodDraft(
        val data: DraftData.FoodDraft,
        val status: DraftStatus,
        val originalInput: String?
    ) : DraftState()
    data class ExerciseDraft(
        val data: DraftData.ExerciseDraft,
        val status: DraftStatus,
        val originalInput: String?
    ) : DraftState()
    data class Confirmed(val entryId: Long, val isFood: Boolean) : DraftState()
    data class Error(val message: String) : DraftState()
}

sealed class DraftResult {
    data class Success(val data: DraftData) : DraftResult()
    data class Error(val message: String) : DraftResult()
}

sealed class ConfirmResult {
    data class Success(val entryId: Long) : ConfirmResult()
    data class Error(val message: String) : ConfirmResult()
}
