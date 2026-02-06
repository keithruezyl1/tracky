package com.tracky.app.di;

import com.tracky.app.data.local.TrackyDatabase;
import com.tracky.app.data.local.dao.ExerciseEntryDao;
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
public final class DatabaseModule_ProvideExerciseEntryDaoFactory implements Factory<ExerciseEntryDao> {
  private final Provider<TrackyDatabase> databaseProvider;

  public DatabaseModule_ProvideExerciseEntryDaoFactory(Provider<TrackyDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public ExerciseEntryDao get() {
    return provideExerciseEntryDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideExerciseEntryDaoFactory create(
      Provider<TrackyDatabase> databaseProvider) {
    return new DatabaseModule_ProvideExerciseEntryDaoFactory(databaseProvider);
  }

  public static ExerciseEntryDao provideExerciseEntryDao(TrackyDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideExerciseEntryDao(database));
  }
}
