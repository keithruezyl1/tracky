package com.tracky.app.di;

import com.tracky.app.data.local.TrackyDatabase;
import com.tracky.app.data.local.dao.UserProfileDao;
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
public final class DatabaseModule_ProvideUserProfileDaoFactory implements Factory<UserProfileDao> {
  private final Provider<TrackyDatabase> databaseProvider;

  public DatabaseModule_ProvideUserProfileDaoFactory(Provider<TrackyDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public UserProfileDao get() {
    return provideUserProfileDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideUserProfileDaoFactory create(
      Provider<TrackyDatabase> databaseProvider) {
    return new DatabaseModule_ProvideUserProfileDaoFactory(databaseProvider);
  }

  public static UserProfileDao provideUserProfileDao(TrackyDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideUserProfileDao(database));
  }
}
