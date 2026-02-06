package com.tracky.app.data.repository;

import com.tracky.app.data.local.dao.ExerciseEntryDao;
import com.tracky.app.data.local.dao.FoodEntryDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class LoggingRepository_Factory implements Factory<LoggingRepository> {
  private final Provider<FoodEntryDao> foodEntryDaoProvider;

  private final Provider<ExerciseEntryDao> exerciseEntryDaoProvider;

  private final Provider<GoalRepository> goalRepositoryProvider;

  public LoggingRepository_Factory(Provider<FoodEntryDao> foodEntryDaoProvider,
      Provider<ExerciseEntryDao> exerciseEntryDaoProvider,
      Provider<GoalRepository> goalRepositoryProvider) {
    this.foodEntryDaoProvider = foodEntryDaoProvider;
    this.exerciseEntryDaoProvider = exerciseEntryDaoProvider;
    this.goalRepositoryProvider = goalRepositoryProvider;
  }

  @Override
  public LoggingRepository get() {
    return newInstance(foodEntryDaoProvider.get(), exerciseEntryDaoProvider.get(), goalRepositoryProvider.get());
  }

  public static LoggingRepository_Factory create(Provider<FoodEntryDao> foodEntryDaoProvider,
      Provider<ExerciseEntryDao> exerciseEntryDaoProvider,
      Provider<GoalRepository> goalRepositoryProvider) {
    return new LoggingRepository_Factory(foodEntryDaoProvider, exerciseEntryDaoProvider, goalRepositoryProvider);
  }

  public static LoggingRepository newInstance(FoodEntryDao foodEntryDao,
      ExerciseEntryDao exerciseEntryDao, GoalRepository goalRepository) {
    return new LoggingRepository(foodEntryDao, exerciseEntryDao, goalRepository);
  }
}
