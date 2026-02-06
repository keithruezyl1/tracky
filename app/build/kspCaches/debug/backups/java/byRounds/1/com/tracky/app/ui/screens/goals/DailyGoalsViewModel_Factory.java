package com.tracky.app.ui.screens.goals;

import com.tracky.app.data.repository.GoalRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class DailyGoalsViewModel_Factory implements Factory<DailyGoalsViewModel> {
  private final Provider<GoalRepository> goalRepositoryProvider;

  public DailyGoalsViewModel_Factory(Provider<GoalRepository> goalRepositoryProvider) {
    this.goalRepositoryProvider = goalRepositoryProvider;
  }

  @Override
  public DailyGoalsViewModel get() {
    return newInstance(goalRepositoryProvider.get());
  }

  public static DailyGoalsViewModel_Factory create(
      Provider<GoalRepository> goalRepositoryProvider) {
    return new DailyGoalsViewModel_Factory(goalRepositoryProvider);
  }

  public static DailyGoalsViewModel newInstance(GoalRepository goalRepository) {
    return new DailyGoalsViewModel(goalRepository);
  }
}
