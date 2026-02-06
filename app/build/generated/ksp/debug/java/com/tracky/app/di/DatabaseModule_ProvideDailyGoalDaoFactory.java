package com.tracky.app.di;

import com.tracky.app.data.local.TrackyDatabase;
import com.tracky.app.data.local.dao.DailyGoalDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideDailyGoalDaoFactory implements Factory<DailyGoalDao> {
  private final Provider<TrackyDatabase> databaseProvider;

  public DatabaseModule_ProvideDailyGoalDaoFactory(Provider<TrackyDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public DailyGoalDao get() {
    return provideDailyGoalDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideDailyGoalDaoFactory create(
      Provider<TrackyDatabase> databaseProvider) {
    return new DatabaseModule_ProvideDailyGoalDaoFactory(databaseProvider);
  }

  public static DailyGoalDao provideDailyGoalDao(TrackyDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideDailyGoalDao(database));
  }
}
