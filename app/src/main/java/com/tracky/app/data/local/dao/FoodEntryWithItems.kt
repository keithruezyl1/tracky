package com.tracky.app.data.local.dao

import androidx.room.Embedded
import androidx.room.Relation
import com.tracky.app.data.local.entity.FoodEntryEntity
import com.tracky.app.data.local.entity.FoodItemEntity

data class FoodEntryWithItems(
    @Embedded val entry: FoodEntryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "foodEntryId"
    )
    val items: List<FoodItemEntity>
)
