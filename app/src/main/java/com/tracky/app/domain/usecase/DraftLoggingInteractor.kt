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
    data object Drafting : DraftState()
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
        if (text.isEmpty()) return text
        return text.lowercase().replaceFirstChar { it.uppercase() }
    }

    suspend fun draftFoodFromText(text: String, date: LocalDate) {
        _draftState.value = DraftState.Drafting
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
                            calories = 0f,
                            carbsG = 0f,
                            proteinG = 0f,
                            fatG = 0f,
                            provenance = Provenance(ProvenanceSource.UNRESOLVED, null, 0f),
                            resolved = false
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
        _draftState.value = DraftState.Drafting
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
                            calories = 0f,
                            carbsG = 0f,
                            proteinG = 0f,
                            fatG = 0f,
                            provenance = Provenance(ProvenanceSource.UNRESOLVED, null, 0f),
                            resolved = false
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
            val result = foodsRepository.resolveFood(item.name, item.quantity.toFloat(), item.unit)
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
                        resolved = true
                    )
                }
                else -> item
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
        _draftState.value = DraftState.Drafting
        try {
            val profile = profileRepository.getProfileOnce()
            val userWeightKg = profile?.currentWeightKg ?: 70f
            val response = backendApi.logExercise(LogExerciseRequest(text = text, userWeightKg = userWeightKg))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.exercises.isNotEmpty()) {
                    val draftItems = body.exercises.map { parsed ->
                        DraftExerciseItem(
                            activity = sentenceCase(parsed.activity),
                            durationMinutes = parsed.durationMinutes,
                            metValue = 0f,
                            caloriesBurned = 0f,
                            intensity = ExerciseIntensity.fromValue(parsed.intensity) ?: ExerciseIntensity.MODERATE,
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
        _draftState.value = DraftState.Drafting
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
                                    item.copy(
                                        caloriesBurned = body.caloriesBurned?.toFloat() ?: 0f,
                                        metValue = body.metValue ?: 0f,
                                        resolved = body.resolved
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
        _draftState.value = DraftState.Drafting
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
                                calories = 0f,
                                carbsG = 0f,
                                proteinG = 0f,
                                fatG = 0f,
                                provenance = Provenance(ProvenanceSource.UNRESOLVED, null, 0f),
                                resolved = false
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
                                metValue = 0f,
                                caloriesBurned = 0f,
                                intensity = ExerciseIntensity.fromValue(parsed.intensity) ?: ExerciseIntensity.MODERATE,
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
        _draftState.value = DraftState.Drafting
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
                                calories = 0f,
                                carbsG = 0f,
                                proteinG = 0f,
                                fatG = 0f,
                                provenance = Provenance(ProvenanceSource.UNRESOLVED, null, 0f),
                                resolved = false
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
                    displayOrder = index
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
                    displayOrder = index
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

    fun updateFoodDraftItem(draftId: Long, index: Int, name: String, quantity: Double, unit: String) {
        val current = _draftState.value
        if (current is DraftState.FoodDraft) {
            val items = current.data.items.toMutableList()
            if (index in items.indices) {
                items[index] = items[index].copy(name = sentenceCase(name), quantity = quantity, unit = unit)
                _draftState.value = DraftState.FoodDraft(current.data.copy(items = items))
            }
        }
    }

    fun updateExerciseDraftItem(draftId: Long, index: Int, activity: String, durationMinutes: Int) {
        val current = _draftState.value
        if (current is DraftState.ExerciseDraft) {
            val items = current.data.items.toMutableList()
            if (index in items.indices) {
                items[index] = items[index].copy(activity = sentenceCase(activity), durationMinutes = durationMinutes)
                _draftState.value = DraftState.ExerciseDraft(current.data.copy(items = items))
            }
        }
    }
}
