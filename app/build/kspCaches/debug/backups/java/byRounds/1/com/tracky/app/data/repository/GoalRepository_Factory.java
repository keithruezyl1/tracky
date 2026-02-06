package com.tracky.app.data.repository;

import com.tracky.app.data.local.dao.DailyGoalDao;
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
public final class GoalRepository_Factory implements Factory<GoalRepository> {
  private final Provider<DailyGoalDao> dailyGoalDaoProvider;

  public GoalRepository_Factory(Provider<DailyGoalDao> dailyGoalDaoProvider) {
    this.dailyGoalDaoProvider = dailyGoalDaoProvider;
  }

  @Override
  public GoalRepository get() {
    return newInstance(dailyGoalDaoProvider.get());
  }

  public static GoalRepository_Factory create(Provider<DailyGoalDao> dailyGoalDaoProvider) {
    return new GoalRepository_Factory(dailyGoalDaoProvider);
  }

  public static GoalRepository newInstance(DailyGoalDao dailyGoalDao) {
    return new GoalRepository(dailyGoalDao);
  }
}
