package com.tracky.app.data.mapper

import com.tracky.app.data.local.entity.ChatMessageEntity
import com.tracky.app.data.local.entity.DailyGoalEntity
import com.tracky.app.data.local.entity.ExerciseEntryEntity
import com.tracky.app.data.local.entity.ExerciseItemEntity
import com.tracky.app.data.local.entity.FoodEntryEntity
import com.tracky.app.data.local.entity.FoodItemEntity
import com.tracky.app.data.local.entity.UserProfileEntity
import com.tracky.app.data.local.entity.WeightEntryEntity
import com.tracky.app.domain.model.ChatMessage
import com.tracky.app.domain.model.ChatMessageType
import com.tracky.app.domain.model.DailyGoal
import com.tracky.app.domain.model.DraftStatus
import com.tracky.app.domain.model.ExerciseEntry
import com.tracky.app.domain.model.ExerciseIntensity
import com.tracky.app.domain.model.ExerciseItem
import com.tracky.app.domain.model.FoodEntry
import com.tracky.app.domain.model.FoodItem
import com.tracky.app.domain.model.MessageRole
import com.tracky.app.domain.model.Provenance
import com.tracky.app.domain.model.ProvenanceSource
import com.tracky.app.domain.model.UnitPreference
import com.tracky.app.domain.model.UserProfile
import com.tracky.app.domain.model.WeightEntry
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

// ─────────────────────────────────────────────────────────────────────────────
// UserProfile Mappers
// ─────────────────────────────────────────────────────────────────────────────

fun UserProfileEntity.toDomain(): UserProfile = UserProfile(
    heightCm = heightCm,
    currentWeightKg = currentWeightKg,
    targetWeightKg = targetWeightKg,
    unitPreference = UnitPreference.fromValue(unitPreference),
    timezone = timezone,
    bmi = bmi,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun UserProfile.toEntity(): UserProfileEntity = UserProfileEntity(
    id = 1, // Single user
    heightCm = heightCm,
    currentWeightKg = currentWeightKg,
    targetWeightKg = targetWeightKg,
    unitPreference = unitPreference.value,
    timezone = timezone,
    bmi = bmi,
    createdAt = createdAt,
    updatedAt = updatedAt
)

// ─────────────────────────────────────────────────────────────────────────────
// DailyGoal Mappers
// ─────────────────────────────────────────────────────────────────────────────

fun DailyGoalEntity.toDomain(): DailyGoal = DailyGoal(
    id = id,
    effectiveFromDate = effectiveFromDate,
    calorieGoalKcal = calorieGoalKcal,
    carbsPct = carbsPct,
    proteinPct = proteinPct,
    fatPct = fatPct,
    carbsTargetG = carbsTargetG,
    proteinTargetG = proteinTargetG,
    fatTargetG = fatTargetG,
    createdAt = createdAt
)

fun DailyGoal.toEntity(): DailyGoalEntity = DailyGoalEntity(
    id = id,
    effectiveFromDate = effectiveFromDate,
    calorieGoalKcal = calorieGoalKcal,
    carbsPct = carbsPct,
    proteinPct = proteinPct,
    fatPct = fatPct,
    carbsTargetG = carbsTargetG,
    proteinTargetG = proteinTargetG,
    fatTargetG = fatTargetG,
    createdAt = createdAt
)

// ─────────────────────────────────────────────────────────────────────────────
// FoodEntry Mappers
// ─────────────────────────────────────────────────────────────────────────────

fun FoodEntryEntity.toDomain(items: List<FoodItemEntity>): FoodEntry = FoodEntry(
    id = id,
    date = date,
    time = time,
    timestamp = timestamp,
    totalCalories = totalCalories,
    totalCarbsG = totalCarbsG,
    totalProteinG = totalProteinG,
    totalFatG = totalFatG,
    analysisNarrative = analysisNarrative,
    photoPath = photoPath,
    originalInput = originalInput,
    items = items.map { it.toDomain() },
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun FoodEntry.toEntity(): FoodEntryEntity = FoodEntryEntity(
    id = id,
    date = date,
    time = time,
    timestamp = timestamp,
    totalCalories = totalCalories,
    totalCarbsG = totalCarbsG,
    totalProteinG = totalProteinG,
    totalFatG = totalFatG,
    analysisNarrative = analysisNarrative,
    photoPath = photoPath,
    originalInput = originalInput,
    createdAt = createdAt,
    updatedAt = updatedAt
)



// ... (existing imports will be preserved by replace_file_content if I target specific blocks, but I need to be careful not to remove them if I replace large chunks)
// To avoid messing up imports, I will just add the import at the top if needed, but replace_file_content replaces a block.
// I'll assume standard imports are there. I'll use FQCN or add imports if I can.
// Actually, I should check if I can add imports easily.
// I'll just use `kotlinx.serialization.json.Json` inline or add imports in a separate call if needed, but `replace_file_content` is for contiguous blocks.
// I'll replace the specific functions.

// FoodItemEntity.toDomain
fun FoodItemEntity.toDomain(): FoodItem = FoodItem(
    id = id,
    name = name,
    matchedName = matchedName,
    quantity = quantity,
    unit = unit,
    calories = calories,
    carbsG = carbsG,
    proteinG = proteinG,
    fatG = fatG,
    provenance = Provenance(
        source = ProvenanceSource.fromValue(source),
        sourceId = sourceId,
        confidence = confidence,
        reusedCount = reusedCount
    ),
    displayOrder = displayOrder,
    canonicalKey = canonicalKey,
    isManualMacros = isManualMacros,
    analysisRevision = analysisRevision,
    pendingSuggestion = pendingSuggestionJson?.let { 
        try {
            Json.decodeFromString<FoodItem>(it)
        } catch (e: Exception) {
            null
        }
    }
)

fun FoodItem.toEntity(foodEntryId: Long): FoodItemEntity {
    return FoodItemEntity(
        id = id,
        foodEntryId = foodEntryId,
        name = name,
        matchedName = matchedName,
        quantity = quantity,
        unit = unit,
        calories = calories,
        carbsG = carbsG,
        proteinG = proteinG,
        fatG = fatG,
        source = provenance.source.value,
        sourceId = provenance.sourceId,
        confidence = provenance.confidence,
        displayOrder = displayOrder,
        canonicalKey = canonicalKey,
        isManualMacros = isManualMacros,
        analysisRevision = analysisRevision,
        pendingSuggestionJson = pendingSuggestion?.let { Json.encodeToString(it) },
        reusedCount = provenance.reusedCount
    )
}

// ...

fun ExerciseItemEntity.toDomain(): ExerciseItem = ExerciseItem(
    id = id,
    activityName = activityName,
    durationMinutes = durationMinutes,
    metValue = metValue,
    caloriesBurned = caloriesBurned,
    intensity = ExerciseIntensity.fromValue(intensity),
    provenance = Provenance(
        source = ProvenanceSource.fromValue(source),
        sourceId = null,
        confidence = confidence
    ),
    displayOrder = displayOrder,
    isManual = isManual,
    analysisRevision = analysisRevision,
    pendingSuggestion = pendingSuggestionJson?.let {
        try {
            Json.decodeFromString<ExerciseItem>(it)
        } catch (e: Exception) {
            null
        }
    }
)

fun ExerciseItem.toEntity(entryId: Long): ExerciseItemEntity = ExerciseItemEntity(
    id = id,
    entryId = entryId,
    activityName = activityName,
    durationMinutes = durationMinutes,
    metValue = metValue,
    caloriesBurned = caloriesBurned,
    intensity = intensity?.value,
    source = provenance.source.value,
    confidence = provenance.confidence,
    displayOrder = displayOrder,
    isManual = isManual,
    analysisRevision = analysisRevision,
    pendingSuggestionJson = pendingSuggestion?.let { Json.encodeToString(it) }
)

// ─────────────────────────────────────────────────────────────────────────────
// ExerciseEntry Mappers
// ─────────────────────────────────────────────────────────────────────────────

fun ExerciseEntryEntity.toDomain(items: List<ExerciseItemEntity>): ExerciseEntry = ExerciseEntry(
    id = id,
    date = date,
    time = time,
    timestamp = timestamp,
    items = items.map { it.toDomain() },
    totalCalories = totalCalories,
    totalDurationMinutes = totalDurationMinutes,
    userWeightKg = userWeightKg,
    originalInput = originalInput,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun ExerciseEntry.toEntity(): ExerciseEntryEntity = ExerciseEntryEntity(
    id = id,
    date = date,
    time = time,
    timestamp = timestamp,
    totalCalories = totalCalories,
    totalDurationMinutes = totalDurationMinutes,
    userWeightKg = userWeightKg,
    originalInput = originalInput,
    createdAt = createdAt,
    updatedAt = updatedAt
)

// ─────────────────────────────────────────────────────────────────────────────
// WeightEntry Mappers
// ─────────────────────────────────────────────────────────────────────────────

fun WeightEntryEntity.toDomain(): WeightEntry = WeightEntry(
    id = id,
    date = date,
    weightKg = weightKg,
    note = note,
    timestamp = timestamp,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun WeightEntry.toEntity(): WeightEntryEntity = WeightEntryEntity(
    id = id,
    date = date,
    weightKg = weightKg,
    note = note,
    timestamp = timestamp,
    createdAt = createdAt,
    updatedAt = updatedAt
)

// ─────────────────────────────────────────────────────────────────────────────
// ChatMessage Mappers
// ─────────────────────────────────────────────────────────────────────────────

fun ChatMessageEntity.toDomain(): ChatMessage = ChatMessage(
    id = id,
    date = date,
    timestamp = timestamp,
    messageType = ChatMessageType.fromValue(messageType),
    role = MessageRole.fromValue(role),
    content = content,
    imagePath = imagePath,
    draftData = null,
    draftStatus = DraftStatus.fromValue(draftStatus),
    linkedFoodEntryId = linkedFoodEntryId,
    linkedExerciseEntryId = linkedExerciseEntryId,
    createdAt = createdAt
)

fun ChatMessage.toEntity(): ChatMessageEntity = ChatMessageEntity(
    id = id,
    date = date,
    timestamp = timestamp,
    messageType = messageType.value,
    role = role.value,
    content = content,
    imagePath = imagePath,
    entryDataJson = null,
    draftStatus = draftStatus?.value,
    linkedFoodEntryId = linkedFoodEntryId,
    linkedExerciseEntryId = linkedExerciseEntryId,
    createdAt = createdAt
)
