package com.tracky.app;

import com.tracky.app.data.local.preferences.UserPreferencesDataStore;
import com.tracky.app.data.repository.ProfileRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<ProfileRepository> profileRepositoryProvider;

  private final Provider<UserPreferencesDataStore> preferencesDataStoreProvider;

  public MainActivity_MembersInjector(Provider<ProfileRepository> profileRepositoryProvider,
      Provider<UserPreferencesDataStore> preferencesDataStoreProvider) {
    this.profileRepositoryProvider = profileRepositoryProvider;
    this.preferencesDataStoreProvider = preferencesDataStoreProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<ProfileRepository> profileRepositoryProvider,
      Provider<UserPreferencesDataStore> preferencesDataStoreProvider) {
    return new MainActivity_MembersInjector(profileRepositoryProvider, preferencesDataStoreProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectProfileRepository(instance, profileRepositoryProvider.get());
    injectPreferencesDataStore(instance, preferencesDataStoreProvider.get());
  }

  @InjectedFieldSignature("com.tracky.app.MainActivity.profileRepository")
  public static void injectProfileRepository(MainActivity instance,
      ProfileRepository profileRepository) {
    instance.profileRepository = profileRepository;
  }

  @InjectedFieldSignature("com.tracky.app.MainActivity.preferencesDataStore")
  public static void injectPreferencesDataStore(MainActivity instance,
      UserPreferencesDataStore preferencesDataStore) {
    instance.preferencesDataStore = preferencesDataStore;
  }
}
