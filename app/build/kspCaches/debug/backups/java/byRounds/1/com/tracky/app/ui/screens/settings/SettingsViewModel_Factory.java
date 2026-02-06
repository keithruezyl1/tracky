package com.tracky.app.ui.screens.settings;

import com.tracky.app.data.local.TrackyDatabase;
import com.tracky.app.data.local.preferences.UserPreferencesDataStore;
import com.tracky.app.data.repository.ProfileRepository;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<ProfileRepository> profileRepositoryProvider;

  private final Provider<UserPreferencesDataStore> preferencesDataStoreProvider;

  private final Provider<TrackyDatabase> databaseProvider;

  public SettingsViewModel_Factory(Provider<ProfileRepository> profileRepositoryProvider,
      Provider<UserPreferencesDataStore> preferencesDataStoreProvider,
      Provider<TrackyDatabase> databaseProvider) {
    this.profileRepositoryProvider = profileRepositoryProvider;
    this.preferencesDataStoreProvider = preferencesDataStoreProvider;
    this.databaseProvider = databaseProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(profileRepositoryProvider.get(), preferencesDataStoreProvider.get(), databaseProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<ProfileRepository> profileRepositoryProvider,
      Provider<UserPreferencesDataStore> preferencesDataStoreProvider,
      Provider<TrackyDatabase> databaseProvider) {
    return new SettingsViewModel_Factory(profileRepositoryProvider, preferencesDataStoreProvider, databaseProvider);
  }

  public static SettingsViewModel newInstance(ProfileRepository profileRepository,
      UserPreferencesDataStore preferencesDataStore, TrackyDatabase database) {
    return new SettingsViewModel(profileRepository, preferencesDataStore, database);
  }
}
