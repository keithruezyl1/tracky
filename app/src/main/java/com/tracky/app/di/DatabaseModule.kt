package com.tracky.app.di

import android.content.Context
import com.tracky.app.data.local.TrackyDatabase
import com.tracky.app.data.local.dao.ChatMessageDao
import com.tracky.app.data.local.dao.DailyGoalDao
import com.tracky.app.data.local.dao.ExerciseEntryDao
import com.tracky.app.data.local.dao.FoodEntryDao
import com.tracky.app.data.local.dao.FoodsDatasetDao
import com.tracky.app.data.local.dao.SavedEntryDao
import com.tracky.app.data.local.dao.UserProfileDao
import com.tracky.app.data.local.dao.WeightEntryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TrackyDatabase {
        return TrackyDatabase.getInstance(context)
    }

    @Provides
    fun provideUserProfileDao(database: TrackyDatabase): UserProfileDao {
        return database.userProfileDao()
    }

    @Provides
    fun provideDailyGoalDao(database: TrackyDatabase): DailyGoalDao {
        return database.dailyGoalDao()
    }

    @Provides
    fun provideFoodEntryDao(database: TrackyDatabase): FoodEntryDao {
        return database.foodEntryDao()
    }

    @Provides
    fun provideExerciseEntryDao(database: TrackyDatabase): ExerciseEntryDao {
        return database.exerciseEntryDao()
    }

    @Provides
    fun provideWeightEntryDao(database: TrackyDatabase): WeightEntryDao {
        return database.weightEntryDao()
    }

    @Provides
    fun provideSavedEntryDao(database: TrackyDatabase): SavedEntryDao {
        return database.savedEntryDao()
    }

    @Provides
    fun provideChatMessageDao(database: TrackyDatabase): ChatMessageDao {
        return database.chatMessageDao()
    }

    @Provides
    fun provideFoodsDatasetDao(database: TrackyDatabase): FoodsDatasetDao {
        return database.foodsDatasetDao()
    }
}
