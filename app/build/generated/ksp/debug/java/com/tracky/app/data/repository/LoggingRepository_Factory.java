package com.tracky.app.data.repository;

import com.tracky.app.data.local.dao.DailyLogSummaryDao;
import com.tracky.app.data.local.dao.ExerciseEntryDao;
import com.tracky.app.data.local.dao.FoodEntryDao;
import com.tracky.app.domain.usecase.StreakInteractor;
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

  private final Provider<DailyLogSummaryDao> dailyLogSummaryDaoProvider;

  private final Provider<StreakInteractor> streakInteractorProvider;

  private final Provider<GoalRepository> goalRepositoryProvider;

  public LoggingRepository_Factory(Provider<FoodEntryDao> foodEntryDaoProvider,
      Provider<ExerciseEntryDao> exerciseEntryDaoProvider,
      Provider<DailyLogSummaryDao> dailyLogSummaryDaoProvider,
      Provider<StreakInteractor> streakInteractorProvider,
      Provider<GoalRepository> goalRepositoryProvider) {
    this.foodEntryDaoProvider = foodEntryDaoProvider;
    this.exerciseEntryDaoProvider = exerciseEntryDaoProvider;
    this.dailyLogSummaryDaoProvider = dailyLogSummaryDaoProvider;
    this.streakInteractorProvider = streakInteractorProvider;
    this.goalRepositoryProvider = goalRepositoryProvider;
  }

  @Override
  public LoggingRepository get() {
    return newInstance(foodEntryDaoProvider.get(), exerciseEntryDaoProvider.get(), dailyLogSummaryDaoProvider.get(), streakInteractorProvider.get(), goalRepositoryProvider.get());
  }

  public static LoggingRepository_Factory create(Provider<FoodEntryDao> foodEntryDaoProvider,
      Provider<ExerciseEntryDao> exerciseEntryDaoProvider,
      Provider<DailyLogSummaryDao> dailyLogSummaryDaoProvider,
      Provider<StreakInteractor> streakInteractorProvider,
      Provider<GoalRepository> goalRepositoryProvider) {
    return new LoggingRepository_Factory(foodEntryDaoProvider, exerciseEntryDaoProvider, dailyLogSummaryDaoProvider, streakInteractorProvider, goalRepositoryProvider);
  }

  public static LoggingRepository newInstance(FoodEntryDao foodEntryDao,
      ExerciseEntryDao exerciseEntryDao, DailyLogSummaryDao dailyLogSummaryDao,
      StreakInteractor streakInteractor, GoalRepository goalRepository) {
    return new LoggingRepository(foodEntryDao, exerciseEntryDao, dailyLogSummaryDao, streakInteractor, goalRepository);
  }
}
