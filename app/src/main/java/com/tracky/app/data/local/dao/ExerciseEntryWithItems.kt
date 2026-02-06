package com.tracky.app.data.local.dao

import androidx.room.Embedded
import androidx.room.Relation
import com.tracky.app.data.local.entity.ExerciseEntryEntity
import com.tracky.app.data.local.entity.ExerciseItemEntity

data class ExerciseEntryWithItems(
    @Embedded val entry: ExerciseEntryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "entryId"
    )
    val items: List<ExerciseItemEntity>
)
