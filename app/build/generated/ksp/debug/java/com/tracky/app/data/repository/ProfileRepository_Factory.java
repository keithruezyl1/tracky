package com.tracky.app.data.repository;

import com.tracky.app.data.local.dao.UserProfileDao;
import com.tracky.app.data.local.preferences.UserPreferencesDataStore;
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
public final class ProfileRepository_Factory implements Factory<ProfileRepository> {
  private final Provider<UserProfileDao> userProfileDaoProvider;

  private final Provider<UserPreferencesDataStore> preferencesDataStoreProvider;

  public ProfileRepository_Factory(Provider<UserProfileDao> userProfileDaoProvider,
      Provider<UserPreferencesDataStore> preferencesDataStoreProvider) {
    this.userProfileDaoProvider = userProfileDaoProvider;
    this.preferencesDataStoreProvider = preferencesDataStoreProvider;
  }

  @Override
  public ProfileRepository get() {
    return newInstance(userProfileDaoProvider.get(), preferencesDataStoreProvider.get());
  }

  public static ProfileRepository_Factory create(Provider<UserProfileDao> userProfileDaoProvider,
      Provider<UserPreferencesDataStore> preferencesDataStoreProvider) {
    return new ProfileRepository_Factory(userProfileDaoProvider, preferencesDataStoreProvider);
  }

  public static ProfileRepository newInstance(UserProfileDao userProfileDao,
      UserPreferencesDataStore preferencesDataStore) {
    return new ProfileRepository(userProfileDao, preferencesDataStore);
  }
}
