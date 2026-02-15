package com.tracky.app.domain.usecase

import com.tracky.app.data.remote.TrackyBackendApi
import com.tracky.app.data.remote.dto.LogAutoRequest
import com.tracky.app.data.remote.dto.LogExerciseRequest
import com.tracky.app.data.remote.dto.LogFoodRequest
import com.tracky.app.data.remote.dto.ResolveExerciseRequest
import com.tracky.app.data.remote.dto.ResolveFoodRequest
import com.tracky.app.data.repository.FoodsRepository
import com.tracky.app.data.repository.LoggingRepository
import com.tracky.app.data.repository.ProfileRepository
import com.tracky.app.data.repository.ResolvedFoodResult
import com.tracky.app.domain.model.DraftData
import com.tracky.app.domain.model.DraftFoodItem
import com.tracky.app.domain.model.DraftExerciseItem
import com.tracky.app.domain.model.ExerciseEntry
import com.tracky.app.domain.model.ExerciseIntensity
import com.tracky.app.domain.model.ExerciseItem
import com.tracky.app.domain.model.FoodEntry
import com.tracky.app.domain.model.FoodItem
import com.tracky.app.domain.model.Provenance
import com.tracky.app.domain.model.ProvenanceSource
import com.tracky.app.util.toTitleCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

sealed class DraftState {
    data object Idle : DraftState()
    data class Drafting(val date: LocalDate) : DraftState()
    data class FoodDraft(val data: DraftData.FoodDraft) : DraftState()
    data class ExerciseDraft(val data: DraftData.ExerciseDraft) : DraftState()
    data class Error(val message: String) : DraftState()
}

sealed class ConfirmResult {
    data object Success : ConfirmResult()
    data class Error(val message: String) : ConfirmResult()
}

@Singleton
class DraftLoggingInteractor @Inject constructor(
    private val backendApi: TrackyBackendApi,
    private val foodsRepository: FoodsRepository,
    private val loggingRepository: LoggingRepository,
    private val profileRepository: ProfileRepository
) {

    private val _draftState = MutableStateFlow<DraftState>(DraftState.Idle)
    val draftState: StateFlow<DraftState> = _draftState.asStateFlow()

    private fun sentenceCase(text: String): String {
        return text.toTitleCase()
    }

    private fun parseIntensityFromText(text: String): ExerciseIntensity? {
        val lower = text.lowercase()
        return when {
            Regex("\\b(high|vigorous|intense|hard)\\b", RegexOption.IGNORE_CASE).containsMatchIn(text) -> ExerciseIntensity.HIGH
            Regex("\\b(moderate|medium|average)\\b", RegexOption.IGNORE_CASE).containsMatchIn(text) -> ExerciseIntensity.MODERATE
            Regex("\\b(low|light|easy|gentle)\\b", RegexOption.IGNORE_CASE).containsMatchIn(text) -> ExerciseIntensity.LOW
            else -> null
        }
    }

    suspend fun draftFoodFromText(text: String, date: LocalDate) {
        _draftState.value = DraftState.Drafting(date)
        try {
            val profile = profileRepository.getProfileOnce()
            val userWeightKg = profile?.currentWeightKg ?: 70f
            val response = backendApi.logFood(LogFoodRequest(text = text, imageBase64 = null, userWeightKg = userWeightKg))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val draftItems = body.items.map { dto ->
                        DraftFoodItem(
                            name = sentenceCase(dto.name),
                            matchedName = null,
                            quantity = dto.quantity.toDouble(),
                            unit = dto.unit,
                            calories = dto.calories ?: 0f,
                            carbsG = dto.carbs ?: 0f,
                            proteinG = dto.protein ?: 0f,
                            fatG = dto.fat ?: 0f,
                            provenance = if (dto.calories != null && dto.calories > 0f)
                                Provenance(ProvenanceSource.AI_ESTIMATE, null, dto.confidence)
                            else
                                Provenance(ProvenanceSource.UNRESOLVED, null, 0f),
                            resolved = dto.calories != null && dto.calories > 0f
                        )
                    }
                    val foodDraft = DraftData.FoodDraft(
                        items = draftItems,
                        totalCalories = 0f,
                        totalCarbsG = 0f,
                        totalProteinG = 0f,
                        totalFatG = 0f,
                        narrative = body.narrative,
                        date = date
                    )
                    _draftState.value = DraftState.FoodDraft(foodDraft)
                    resolveFoodDraft(foodDraft)
                } else {
                    _draftState.value = DraftState.Error("Empty response from server")
                }
            } else {
                _draftState.value = DraftState.Error("Failed to parse food: ${response.message()}")
            }
        } catch (e: Exception) {
            _draftState.value = DraftState.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun draftFoodFromImage(imageBase64: String, date: LocalDate) {
        _draftState.value = DraftState.Drafting(date)
        try {
            val profile = profileRepository.getProfileOnce()
            val userWeightKg = profile?.currentWeightKg ?: 70f
            val response = backendApi.logFood(LogFoodRequest(text = null, imageBase64 = imageBase64, userWeightKg = userWeightKg))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val draftItems = body.items.map { dto ->
                        DraftFoodItem(
                            name = sentenceCase(dto.name),
                            matchedName = null,
                            quantity = dto.quantity.toDouble(),
                            unit = dto.unit,
                            calories = dto.calories ?: 0f,
                            carbsG = dto.carbs ?: 0f,
                            proteinG = dto.protein ?: 0f,
                            fatG = dto.fat ?: 0f,
                            provenance = if (dto.calories != null && dto.calories > 0f)
                                Provenance(ProvenanceSource.AI_ESTIMATE, null, dto.confidence)
                            else
                                Provenance(ProvenanceSource.UNRESOLVED, null, 0f),
                            resolved = dto.calories != null && dto.calories > 0f
                        )
                    }
                    val foodDraft = DraftData.FoodDraft(
                        items = draftItems,
                        totalCalories = 0f,
                        totalCarbsG = 0f,
                        totalProteinG = 0f,
                        totalFatG = 0f,
                        narrative = body.narrative,
                        date = date
                    )
                    _draftState.value = DraftState.FoodDraft(foodDraft)
                    resolveFoodDraft(foodDraft)
                }
            }
        } catch (e: Exception) {
            _draftState.value = DraftState.Error(e.message ?: "Unknown error")
        }
    }

    private suspend fun resolveFoodDraft(draft: DraftData.FoodDraft) {
        val resolvedItems = draft.items.map { item ->
            val result = foodsRepository.resolveFood(
                name = item.name,
                quantity = item.quantity.toFloat(),
                unit = item.unit,
                aiCalories = item.calories.takeIf { it > 0f },
                aiCarbsG = item.carbsG.takeIf { it > 0f },
                aiProteinG = item.proteinG.takeIf { it > 0f },
                aiFatG = item.fatG.takeIf { it > 0f }
            )
            when (result) {
                is ResolvedFoodResult.Success -> {
                    val food = result.foodItem
                    item.copy(
                        matchedName = food.matchedName,
                        calories = food.calories,
                        carbsG = food.carbsG,
                        proteinG = food.proteinG,
                        fatG = food.fatG,
                        provenance = food.provenance,
                        resolved = true,
                        isAnalyzing = false
                    )
                }
                else -> item.copy(isAnalyzing = false)
            }
        }
        
        val updatedDraft = draft.copy(
            items = resolvedItems,
            totalCalories = resolvedItems.sumOf { it.calories.toDouble() }.toFloat(),
            totalCarbsG = resolvedItems.sumOf { it.carbsG.toDouble() }.toFloat(),
            totalProteinG = resolvedItems.sumOf { it.proteinG.toDouble() }.toFloat(),
            totalFatG = resolvedItems.sumOf { it.fatG.toDouble() }.toFloat()
        )
        _draftState.value = DraftState.FoodDraft(updatedDraft)
    }

    suspend fun draftExerciseFromText(text: String, date: LocalDate) {
        _draftState.value = DraftState.Drafting(date)
        try {
            val profile = profileRepository.getProfileOnce()
            val userWeightKg = profile?.currentWeightKg ?: 70f
            val response = backendApi.logExercise(LogExerciseRequest(text = text, userWeightKg = userWeightKg))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.exercises.isNotEmpty()) {
                    val localIntensity = parseIntensityFromText(text)
                    val draftItems = body.exercises.map { parsed ->
                        DraftExerciseItem(
                            activity = sentenceCase(parsed.activity),
                            durationMinutes = parsed.durationMinutes,
                            metValue = 0f,
                            caloriesBurned = 0f,
                            intensity = localIntensity ?: ExerciseIntensity.fromValue(parsed.intensity) ?: ExerciseIntensity.MODERATE,
                            resolved = false
                        )
                    }
                    val exerciseDraft = DraftData.ExerciseDraft(
                        items = draftItems,
                        totalCalories = 0f,
                        totalDurationMinutes = draftItems.sumOf { it.durationMinutes },
                        date = date
                    )
                    _draftState.value = DraftState.ExerciseDraft(exerciseDraft)
                    resolveExerciseDraft(exerciseDraft)
                }
            }
        } catch (e: Exception) {
            _draftState.value = DraftState.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun draftExerciseFromImage(imageBase64: String, date: LocalDate) {
        _draftState.value = DraftState.Drafting(date)
        try {
            val profile = profileRepository.getProfileOnce()
            val userWeightKg = profile?.currentWeightKg ?: 70f
            val response = backendApi.logExercise(LogExerciseRequest(text = null, imageBase64 = imageBase64, userWeightKg = userWeightKg))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.exercises.isNotEmpty()) {
                    val draftItems = body.exercises.map { parsed ->
                        DraftExerciseItem(
                            activity = sentenceCase(parsed.activity),
                            durationMinutes = parsed.durationMinutes,
                            metValue = parsed.metValue ?: 0f,  // Use MET from image if available
                            caloriesBurned = parsed.caloriesBurned?.toFloat() ?: 0f,  // Use calories from image if available
                            intensity = ExerciseIntensity.fromValue(parsed.intensity) ?: ExerciseIntensity.MODERATE,
                            resolved = parsed.caloriesBurned != null  // Mark as resolved if calories were extracted from image
                        )
                    }
                    val exerciseDraft = DraftData.ExerciseDraft(
                        items = draftItems,
                        totalCalories = draftItems.map { it.caloriesBurned }.sum(),
                        totalDurationMinutes = draftItems.sumOf { it.durationMinutes },
                        date = date
                    )
                    _draftState.value = DraftState.ExerciseDraft(exerciseDraft)
                    // Only resolve items that don't already have calories from the image
                    if (draftItems.any { !it.resolved }) {
                        resolveExerciseDraft(exerciseDraft)
                    }
                }
            }
        } catch (e: Exception) {
            _draftState.value = DraftState.Error(e.message ?: "Unknown error")
        }
    }


    private suspend fun resolveExerciseDraft(draft: DraftData.ExerciseDraft) {
        val profile = profileRepository.getProfileOnce()
        val userWeightKg = profile?.currentWeightKg ?: 70f
        
        try {
            // Resolve all items in parallel
            val resolvedItems = coroutineScope {
                draft.items.map { item ->
                    async {
                        try {
                            val response = backendApi.resolveExercise(
                                ResolveExerciseRequest(
                                    activity = item.activity,
                                    durationMinutes = item.durationMinutes,
                                    userWeightKg = userWeightKg
                                )
                            )
                            if (response.isSuccessful) {
                                val body = response.body()
                                if (body != null) {
                                    var met = body.metValue ?: 0f
                                    var calories = body.caloriesBurned?.toFloat() ?: 0f
                                    val resolved = body.resolved
                                    
                                    // Adjust for intensity if resolved and we have a valid MET
                                    if (resolved && met > 0) {
                                        // Helper to adjust MET
                                        fun getFactor(i: ExerciseIntensity): Float = when(i) {
                                            ExerciseIntensity.LOW -> 0.7f
                                            ExerciseIntensity.MODERATE -> 1.0f
                                            ExerciseIntensity.HIGH -> 1.4f
                                        }
                                        
                                        // Backend assumes Moderate for lookup usually. 
                                        // If local intensity is different, we adjust the returned MET.
                                        if (item.intensity != ExerciseIntensity.MODERATE) {
                                            val factor = getFactor(item.intensity)
                                            met *= factor
                                            // Recalculate calories: (MET * 3.5 * weight * mins) / 200
                                            calories = (met * 3.5f * userWeightKg * item.durationMinutes) / 200f
                                        }
                                    }

                                    item.copy(
                                        caloriesBurned = calories,
                                        metValue = met,
                                        resolved = resolved,
                                        isAnalyzing = false
                                    )
                                } else item
                            } else item
                        } catch (e: Exception) {
                            item
                        }
                    }
                }.awaitAll()
            }
            
            val updatedDraft = draft.copy(
                items = resolvedItems,
                totalCalories = resolvedItems.map { it.caloriesBurned }.sum()
            )
            _draftState.value = DraftState.ExerciseDraft(updatedDraft)
        } catch (e: Exception) {
            // If parallel execution fails, keep original
        }
    }

    suspend fun draftAutoFromText(text: String, date: LocalDate) {
        _draftState.value = DraftState.Drafting(date)
        try {
            val profile = profileRepository.getProfileOnce()
            val userWeightKg = profile?.currentWeightKg ?: 70f
            val response = backendApi.logAuto(LogAutoRequest(text = text, userWeightKg = userWeightKg))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    if (body.entry_type == "food") {
                        val draftItems = body.food_items.map { dto ->
                            DraftFoodItem(
                                name = sentenceCase(dto.name),
                                matchedName = null,
                                quantity = dto.quantity.toDouble(),
                                unit = dto.unit,
                                calories = dto.calories ?: 0f,
                                carbsG = dto.carbs ?: 0f,
                                proteinG = dto.protein ?: 0f,
                                fatG = dto.fat ?: 0f,
                                provenance = if (dto.calories != null && dto.calories > 0f)
                                    Provenance(ProvenanceSource.AI_ESTIMATE, null, dto.confidence)
                                else
                                    Provenance(ProvenanceSource.UNRESOLVED, null, 0f),
                                resolved = dto.calories != null && dto.calories > 0f
                            )
                        }
                        val foodDraft = DraftData.FoodDraft(
                            items = draftItems,
                            totalCalories = 0f,
                            totalCarbsG = 0f,
                            totalProteinG = 0f,
                            totalFatG = 0f,
                            narrative = body.narrative,
                            date = date
                        )
                        _draftState.value = DraftState.FoodDraft(foodDraft)
                        resolveFoodDraft(foodDraft)
                    } else if (body.entry_type == "exercise") {
                        val localIntensity = parseIntensityFromText(text)
                        val draftItems = body.exercises.map { parsed ->
                            DraftExerciseItem(
                                activity = sentenceCase(parsed.activity),
                                durationMinutes = parsed.durationMinutes,
                                metValue = 0f,
                                caloriesBurned = 0f,
                                intensity = localIntensity ?: ExerciseIntensity.fromValue(parsed.intensity) ?: ExerciseIntensity.MODERATE,
                                resolved = false
                            )
                        }
                        val exerciseDraft = DraftData.ExerciseDraft(
                            items = draftItems,
                            totalCalories = 0f,
                            totalDurationMinutes = draftItems.sumOf { it.durationMinutes },
                            date = date
                        )
                        _draftState.value = DraftState.ExerciseDraft(exerciseDraft)
                        resolveExerciseDraft(exerciseDraft)
                    } else {
                        _draftState.value = DraftState.Idle
                    }
                }
            }
        } catch (e: Exception) {
            _draftState.value = DraftState.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun draftAutoFromImage(imageBase64: String, date: LocalDate) {
        _draftState.value = DraftState.Drafting(date)
        try {
            val profile = profileRepository.getProfileOnce()
            val userWeightKg = profile?.currentWeightKg ?: 70f
            val response = backendApi.logAuto(LogAutoRequest(text = null, imageBase64 = imageBase64, userWeightKg = userWeightKg))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    if (body.entry_type == "food") {
                        val draftItems = body.food_items.map { dto ->
                            DraftFoodItem(
                                name = sentenceCase(dto.name),
                                matchedName = null,
                                quantity = dto.quantity.toDouble(),
                                unit = dto.unit,
                                calories = dto.calories ?: 0f,
                                carbsG = dto.carbs ?: 0f,
                                proteinG = dto.protein ?: 0f,
                                fatG = dto.fat ?: 0f,
                                provenance = if (dto.calories != null && dto.calories > 0f)
                                    Provenance(ProvenanceSource.AI_ESTIMATE, null, dto.confidence)
                                else
                                    Provenance(ProvenanceSource.UNRESOLVED, null, 0f),
                                resolved = dto.calories != null && dto.calories > 0f
                            )
                        }
                        val foodDraft = DraftData.FoodDraft(
                            items = draftItems,
                            totalCalories = 0f,
                            totalCarbsG = 0f,
                            totalProteinG = 0f,
                            totalFatG = 0f,
                            narrative = body.narrative,
                            date = date
                        )
                        _draftState.value = DraftState.FoodDraft(foodDraft)
                        resolveFoodDraft(foodDraft)
                    } else if (body.entry_type == "exercise") {
                        val draftItems = body.exercises.map { parsed ->
                            DraftExerciseItem(
                                activity = sentenceCase(parsed.activity),
                                durationMinutes = parsed.durationMinutes,
                                metValue = parsed.metValue ?: 0f,
                                caloriesBurned = parsed.caloriesBurned?.toFloat() ?: 0f,
                                intensity = ExerciseIntensity.fromValue(parsed.intensity) ?: ExerciseIntensity.MODERATE,
                                resolved = parsed.caloriesBurned != null
                            )
                        }
                        val exerciseDraft = DraftData.ExerciseDraft(
                            items = draftItems,
                            totalCalories = draftItems.map { it.caloriesBurned }.sum(),
                            totalDurationMinutes = draftItems.sumOf { it.durationMinutes },
                            date = date
                        )
                        _draftState.value = DraftState.ExerciseDraft(exerciseDraft)
                        // Only resolve items that don't already have calories from the image
                        if (draftItems.any { !it.resolved }) {
                            resolveExerciseDraft(exerciseDraft)
                        }
                    } else {
                        _draftState.value = DraftState.Error("Could not determine if image contains food or exercise")
                    }
                }
            }
        } catch (e: Exception) {
            _draftState.value = DraftState.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun confirmFoodDraft(draft: DraftData.FoodDraft, date: LocalDate): ConfirmResult {
        try {
            val now = Clock.System.now()
            
            // Create FoodItems from draft items
            val foodItems = draft.items.mapIndexed { index, item ->
                FoodItem(
                    id = 0,
                    name = item.name,
                    matchedName = item.matchedName,
                    quantity = item.quantity.toFloat(),
                    unit = item.unit,
                    calories = item.calories,
                    carbsG = item.carbsG,
                    proteinG = item.proteinG,
                    fatG = item.fatG,
                    provenance = item.provenance,
                    displayOrder = index,
                    isManualMacros = item.isManualMacros
                )
            }
            
            // Create a single FoodEntry with all items
            val foodEntry = FoodEntry(
                id = 0,
                date = date.toString(),
                time = now.toLocalDateTime(TimeZone.currentSystemDefault()).time.toString(),
                timestamp = now.toEpochMilliseconds(),
                totalCalories = draft.totalCalories,
                totalCarbsG = draft.totalCarbsG,
                totalProteinG = draft.totalProteinG,
                totalFatG = draft.totalFatG,
                analysisNarrative = draft.narrative,
                photoPath = null,
                originalInput = "",
                items = foodItems,
                createdAt = now.toEpochMilliseconds(),
                updatedAt = now.toEpochMilliseconds()
            )
            
            loggingRepository.saveFoodEntry(foodEntry)
            _draftState.value = DraftState.Idle
            return ConfirmResult.Success
        } catch (e: Exception) {
            return ConfirmResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun confirmExerciseDraft(draft: DraftData.ExerciseDraft, date: LocalDate): ConfirmResult {
        try {
            val now = Clock.System.now()
            val profile = profileRepository.getProfileOnce()
            val userWeightKg = profile?.currentWeightKg ?: 0f
            
            val exerciseItems = draft.items.mapIndexed { index, item ->
                ExerciseItem(
                    id = 0,
                    activityName = item.activity,
                    durationMinutes = item.durationMinutes,
                    metValue = item.metValue,
                    caloriesBurned = item.caloriesBurned,
                    intensity = item.intensity,
                    provenance = Provenance(ProvenanceSource.DATASET, null, 1.0f),
                    displayOrder = index,
                    isManual = item.isManual
                )
            }
            
            val exerciseEntry = ExerciseEntry(
                id = 0,
                date = date.toString(),
                time = now.toLocalDateTime(TimeZone.currentSystemDefault()).time.toString(),
                timestamp = now.toEpochMilliseconds(),
                items = exerciseItems,
                totalCalories = draft.totalCalories,
                totalDurationMinutes = draft.totalDurationMinutes,
                userWeightKg = userWeightKg,
                originalInput = "",
                createdAt = now.toEpochMilliseconds(),
                updatedAt = now.toEpochMilliseconds()
            )
            loggingRepository.saveExerciseEntry(exerciseEntry)
            _draftState.value = DraftState.Idle
            return ConfirmResult.Success
        } catch (e: Exception) {
            return ConfirmResult.Error(e.message ?: "Unknown error")
        }
    }

    fun cancelDraft(draftId: Long? = null) {
        _draftState.value = DraftState.Idle
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Draft Modifications
    // ─────────────────────────────────────────────────────────────────────────

    suspend fun addToFoodDraft(text: String) {
        val current = _draftState.value
        if (current is DraftState.FoodDraft) {
            val originalItems = current.data.items
            // 1. Emit optimistic loading state
            val placeholder = DraftFoodItem(
                name = "Analyzing...",
                matchedName = null,
                quantity = 1.0,
                unit = "serving",
                calories = 0f,
                carbsG = 0f,
                proteinG = 0f,
                fatG = 0f,
                provenance = Provenance(ProvenanceSource.AI_ESTIMATE, null, 0f),
                resolved = false,
                isAnalyzing = true
            )
            _draftState.value = DraftState.FoodDraft(
                current.data.copy(items = originalItems + placeholder)
            )

            try {
                val profile = profileRepository.getProfileOnce()
                val userWeightKg = profile?.currentWeightKg ?: 70f
                val response = backendApi.logFood(LogFoodRequest(text = text, imageBase64 = null, userWeightKg = userWeightKg))
                
                if (response.isSuccessful) {
                    val body = response.body()
                        if (body != null) {
                            val newItems = body.items.map { dto ->
                                DraftFoodItem(
                                    name = sentenceCase(dto.name),
                                    matchedName = null,
                                    quantity = dto.quantity.toDouble(),
                                    unit = dto.unit,
                                    calories = dto.calories ?: 0f,
                                    carbsG = dto.carbs ?: 0f,
                                    proteinG = dto.protein ?: 0f,
                                    fatG = dto.fat ?: 0f,
                                    provenance = if (dto.calories != null && dto.calories > 0f)
                                        Provenance(ProvenanceSource.AI_ESTIMATE, null, dto.confidence)
                                    else
                                        Provenance(ProvenanceSource.UNRESOLVED, null, 0f),
                                    resolved = dto.calories != null && dto.calories > 0f,
                                    isAnalyzing = true
                                )
                            }
                            
                            val combinedItems = originalItems + newItems
                            val draft = current.data.copy(items = combinedItems)
                            _draftState.value = DraftState.FoodDraft(draft)
                            resolveFoodDraft(draft)
                        } else {
                            // Revert on empty body
                             _draftState.value = DraftState.FoodDraft(current.data.copy(items = originalItems))
                        }
                } else {
                    // Revert on error
                    _draftState.value = DraftState.FoodDraft(current.data.copy(items = originalItems))
                }
            } catch (e: Exception) {
                // Revert on exception
                _draftState.value = DraftState.FoodDraft(current.data.copy(items = originalItems))
            }
        }
    }

    suspend fun addToFoodDraftFromImage(imageBase64: String) {
        val current = _draftState.value
        if (current is DraftState.FoodDraft) {
            val originalItems = current.data.items
             // 1. Emit optimistic loading state
            val placeholder = DraftFoodItem(
                name = "Analyzing...",
                matchedName = null,
                quantity = 1.0,
                unit = "serving",
                calories = 0f,
                carbsG = 0f,
                proteinG = 0f,
                fatG = 0f,
                provenance = Provenance(ProvenanceSource.AI_ESTIMATE, null, 0f),
                resolved = false,
                isAnalyzing = true
            )
            _draftState.value = DraftState.FoodDraft(
                current.data.copy(items = originalItems + placeholder)
            )

            try {
                val profile = profileRepository.getProfileOnce()
                val userWeightKg = profile?.currentWeightKg ?: 70f
                val response = backendApi.logFood(LogFoodRequest(text = null, imageBase64 = imageBase64, userWeightKg = userWeightKg))
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val newItems = body.items.map { dto ->
                            DraftFoodItem(
                                name = sentenceCase(dto.name),
                                matchedName = null,
                                quantity = dto.quantity.toDouble(),
                                unit = dto.unit,
                                calories = dto.calories ?: 0f,
                                carbsG = dto.carbs ?: 0f,
                                proteinG = dto.protein ?: 0f,
                                fatG = dto.fat ?: 0f,
                                provenance = if (dto.calories != null && dto.calories > 0f)
                                    Provenance(ProvenanceSource.AI_ESTIMATE, null, dto.confidence)
                                else
                                    Provenance(ProvenanceSource.UNRESOLVED, null, 0f),
                                resolved = dto.calories != null && dto.calories > 0f,
                                isAnalyzing = true
                            )
                        }
                        
                        val combinedItems = originalItems + newItems
                        val draft = current.data.copy(items = combinedItems)
                        _draftState.value = DraftState.FoodDraft(draft)
                        resolveFoodDraft(draft)
                    } else {
                         _draftState.value = DraftState.FoodDraft(current.data.copy(items = originalItems))
                    }
                } else {
                     _draftState.value = DraftState.FoodDraft(current.data.copy(items = originalItems))
                }
            } catch (e: Exception) {
                // Revert
                 _draftState.value = DraftState.FoodDraft(current.data.copy(items = originalItems))
            }
        }
    }

    suspend fun addToExerciseDraft(text: String) {
        val current = _draftState.value
        if (current is DraftState.ExerciseDraft) {
            val originalItems = current.data.items
             // 1. Emit optimistic loading state
            val placeholder = DraftExerciseItem(
                activity = "Analyzing...",
                durationMinutes = 0,
                metValue = 0f,
                caloriesBurned = 0f,
                intensity = ExerciseIntensity.MODERATE,
                resolved = false
            )
             _draftState.value = DraftState.ExerciseDraft(
                current.data.copy(items = originalItems + placeholder)
            )

            try {
                val profile = profileRepository.getProfileOnce()
                val userWeightKg = profile?.currentWeightKg ?: 70f
                val response = backendApi.logExercise(LogExerciseRequest(text = text, userWeightKg = userWeightKg))
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.exercises.isNotEmpty()) {
                        val localIntensity = parseIntensityFromText(text)
                        val newItems = body.exercises.map { parsed ->
                            DraftExerciseItem(
                                activity = sentenceCase(parsed.activity),
                                durationMinutes = parsed.durationMinutes,
                                metValue = 0f,
                                caloriesBurned = 0f,
                                intensity = localIntensity ?: ExerciseIntensity.fromValue(parsed.intensity) ?: ExerciseIntensity.MODERATE,
                                resolved = false
                            )
                        }

                        val combinedItems = originalItems + newItems
                        val draft = current.data.copy(
                            items = combinedItems
                            // Totals recalculated in resolveExerciseDraft
                        )
                        _draftState.value = DraftState.ExerciseDraft(draft)
                        resolveExerciseDraft(draft)
                    } else {
                         _draftState.value = DraftState.ExerciseDraft(current.data.copy(items = originalItems))
                    }
                } else {
                     _draftState.value = DraftState.ExerciseDraft(current.data.copy(items = originalItems))
                }
            } catch (e: Exception) {
                 _draftState.value = DraftState.ExerciseDraft(current.data.copy(items = originalItems))
            }
        }
    }

    suspend fun addToExerciseDraftFromImage(imageBase64: String) {
        val current = _draftState.value
        if (current is DraftState.ExerciseDraft) {
            val originalItems = current.data.items
             // 1. Emit optimistic loading state
            val placeholder = DraftExerciseItem(
                activity = "Analyzing...",
                durationMinutes = 0,
                metValue = 0f,
                caloriesBurned = 0f,
                intensity = ExerciseIntensity.MODERATE,
                resolved = false
            )
             _draftState.value = DraftState.ExerciseDraft(
                current.data.copy(items = originalItems + placeholder)
            )

            try {
                val profile = profileRepository.getProfileOnce()
                val userWeightKg = profile?.currentWeightKg ?: 70f
                val response = backendApi.logExercise(LogExerciseRequest(text = null, imageBase64 = imageBase64, userWeightKg = userWeightKg))
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.exercises.isNotEmpty()) {
                        val newItems = body.exercises.map { parsed ->
                            DraftExerciseItem(
                                activity = sentenceCase(parsed.activity),
                                durationMinutes = parsed.durationMinutes,
                                metValue = parsed.metValue ?: 0f,
                                caloriesBurned = parsed.caloriesBurned?.toFloat() ?: 0f,
                                intensity = ExerciseIntensity.fromValue(parsed.intensity) ?: ExerciseIntensity.MODERATE,
                                resolved = parsed.caloriesBurned != null
                            )
                        }

                        val combinedItems = originalItems + newItems
                        val draft = current.data.copy(
                            items = combinedItems
                        )
                        _draftState.value = DraftState.ExerciseDraft(draft)
                        // Only resolve items that don't already have calories from the image
                        if (newItems.any { !it.resolved }) {
                            resolveExerciseDraft(draft)
                        }
                    } else {
                         _draftState.value = DraftState.ExerciseDraft(current.data.copy(items = originalItems))
                    }
                } else {
                     _draftState.value = DraftState.ExerciseDraft(current.data.copy(items = originalItems))
                }
            } catch (e: Exception) {
                 _draftState.value = DraftState.ExerciseDraft(current.data.copy(items = originalItems))
            }
        }
    }

    suspend fun updateFoodDraftItem(
        draftId: Long, 
        index: Int, 
        name: String, 
        quantity: Double, 
        unit: String,
        calories: Float? = null,
        carbs: Float? = null,
        protein: Float? = null,
        fat: Float? = null,
        isManual: Boolean = false // This flag means "macros were manually edited"
    ) {
        val current = _draftState.value
        if (current is DraftState.FoodDraft) {
            val items = current.data.items.toMutableList()
            if (index in items.indices) {
                val currentItem = items[index]
                val nameChanged = currentItem.name != name
                
                // If name changed, we increment revision to bind async results
                val newRevision = if (nameChanged) currentItem.analysisRevision + 1 else currentItem.analysisRevision
                
                // If name changed, mark as analyzing. If manual macros was false, it stays false until specific field edit overrides.
                // However, the caller passes 'isManual' based on *which fields* were edited in the UI. 
                // We should respect the passed 'isManual' for immediate updates.
                
                val updatedItem = currentItem.copy(
                    name = sentenceCase(name), 
                    quantity = quantity, 
                    unit = unit,
                    calories = calories ?: currentItem.calories,
                    carbsG = carbs ?: currentItem.carbsG,
                    proteinG = protein ?: currentItem.proteinG,
                    fatG = fat ?: currentItem.fatG,
                    isManualMacros = isManual,
                    isAnalyzing = nameChanged,
                    analysisRevision = newRevision
                )
                items[index] = updatedItem

                // Recalculate totals immediately with user inputs
                val newDraft = current.data.copy(
                    items = items,
                    totalCalories = items.sumOf { it.calories.toDouble() }.toFloat(),
                    totalCarbsG = items.sumOf { it.carbsG.toDouble() }.toFloat(),
                    totalProteinG = items.sumOf { it.proteinG.toDouble() }.toFloat(),
                    totalFatG = items.sumOf { it.fatG.toDouble() }.toFloat()
                )
                _draftState.value = DraftState.FoodDraft(newDraft)

                // Trigger Async Re-analysis if name changed
                if (nameChanged) {
                    coroutineScope {
                        async {
                            try {
                                val result = foodsRepository.resolveFood(
                                        name = name, 
                                        quantity = quantity.toFloat(), 
                                        unit = unit
                                )
                                
                                // Apply result carefully
                                val freshState = _draftState.value
                                if (freshState is DraftState.FoodDraft) {
                                    val freshItems = freshState.data.items.toMutableList()
                                    if (index in freshItems.indices) {
                                        val freshItem = freshItems[index]
                                        
                                        // 1. Revision Check
                                        if (freshItem.analysisRevision == newRevision) {
                                            // 2. Manual Lock Check
                                            if (!freshItem.isManualMacros) {
                                                // Safe to overwrite
                                                if (result is ResolvedFoodResult.Success) {
                                                    val food = result.foodItem
                                                    freshItems[index] = freshItem.copy(
                                                        matchedName = food.matchedName,
                                                        calories = food.calories,
                                                        carbsG = food.carbsG,
                                                        proteinG = food.proteinG,
                                                        fatG = food.fatG,
                                                        provenance = food.provenance, // Set origin to AI
                                                        resolved = true,
                                                        isAnalyzing = false
                                                    )
                                                } else {
                                                    // Failed resolution, just stop animating
                                                    freshItems[index] = freshItem.copy(isAnalyzing = false)
                                                }
                                                
                                                // Recalculate totals again
                                                val updatedDraft = freshState.data.copy(
                                                    items = freshItems,
                                                    totalCalories = freshItems.sumOf { it.calories.toDouble() }.toFloat(),
                                                    totalCarbsG = freshItems.sumOf { it.carbsG.toDouble() }.toFloat(),
                                                    totalProteinG = freshItems.sumOf { it.proteinG.toDouble() }.toFloat(),
                                                    totalFatG = freshItems.sumOf { it.fatG.toDouble() }.toFloat()
                                                )
                                                _draftState.value = DraftState.FoodDraft(updatedDraft)
                                            } else {
                                                // Manual macros locked: Do NOT overwrite. 
                                                // Ideally we'd show a "Suggestion Available" but for drafts we just stop loading.
                                                freshItems[index] = freshItem.copy(isAnalyzing = false)
                                                val updatedDraft = freshState.data.copy(items = freshItems)
                                                _draftState.value = DraftState.FoodDraft(updatedDraft)
                                            }
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                // Clear loading state on error
                                val freshState = _draftState.value
                                if (freshState is DraftState.FoodDraft) {
                                     val freshItems = freshState.data.items.toMutableList()
                                     if (index in freshItems.indices && freshItems[index].analysisRevision == newRevision) {
                                         freshItems[index] = freshItems[index].copy(isAnalyzing = false)
                                         _draftState.value = DraftState.FoodDraft(freshState.data.copy(items = freshItems))
                                     }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    suspend fun updateExerciseDraftItem(
        draftId: Long, 
        index: Int, 
        activity: String, 
        durationMinutes: Int,
        intensity: ExerciseIntensity? = null,
        calories: Float? = null,
        isManual: Boolean = false
    ) {
        val current = _draftState.value
        if (current is DraftState.ExerciseDraft) {
            val items = current.data.items.toMutableList()
            if (index in items.indices) {
                val currentItem = items[index]
                val nameChanged = currentItem.activity != activity
                
                val updatedItem = currentItem.copy(
                    activity = sentenceCase(activity), 
                    durationMinutes = durationMinutes,
                    intensity = intensity ?: currentItem.intensity,
                    caloriesBurned = calories ?: currentItem.caloriesBurned,
                    isManual = isManual
                )
                items[index] = updatedItem
                
                val newDraft = current.data.copy(
                    items = items,
                    totalCalories = items.map { it.caloriesBurned }.sum(),
                    totalDurationMinutes = items.sumOf { it.durationMinutes }
                )
                _draftState.value = DraftState.ExerciseDraft(newDraft)
                
                // For exercise, simple re-resolve if name changed, but respecting manual overrides is harder without specific endpoint support
                // For now we will just re-trigger resolveExerciseDraft logic if name changed AND not manual
                if (nameChanged && !isManual) {
                     resolveExerciseDraft(newDraft)
                }
            }
        }
    }
}
