package com.tracky.app.ui.screens.entrydetail;

import androidx.lifecycle.SavedStateHandle;
import com.tracky.app.data.local.dao.SavedEntryDao;
import com.tracky.app.data.remote.TrackyBackendApi;
import com.tracky.app.data.repository.FoodsRepository;
import com.tracky.app.data.repository.LoggingRepository;
import com.tracky.app.data.repository.ProfileRepository;
import com.tracky.app.data.repository.WeightRepository;
import com.tracky.app.ui.haptics.HapticManager;
import com.tracky.app.ui.sound.SoundManager;
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
public final class EntryDetailViewModel_Factory implements Factory<EntryDetailViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<LoggingRepository> loggingRepositoryProvider;

  private final Provider<SavedEntryDao> savedEntryDaoProvider;

  private final Provider<FoodsRepository> foodsRepositoryProvider;

  private final Provider<TrackyBackendApi> backendApiProvider;

  private final Provider<ProfileRepository> profileRepositoryProvider;

  private final Provider<WeightRepository> weightRepositoryProvider;

  private final Provider<SoundManager> soundManagerProvider;

  private final Provider<HapticManager> hapticManagerProvider;

  public EntryDetailViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<LoggingRepository> loggingRepositoryProvider,
      Provider<SavedEntryDao> savedEntryDaoProvider,
      Provider<FoodsRepository> foodsRepositoryProvider,
      Provider<TrackyBackendApi> backendApiProvider,
      Provider<ProfileRepository> profileRepositoryProvider,
      Provider<WeightRepository> weightRepositoryProvider,
      Provider<SoundManager> soundManagerProvider, Provider<HapticManager> hapticManagerProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.loggingRepositoryProvider = loggingRepositoryProvider;
    this.savedEntryDaoProvider = savedEntryDaoProvider;
    this.foodsRepositoryProvider = foodsRepositoryProvider;
    this.backendApiProvider = backendApiProvider;
    this.profileRepositoryProvider = profileRepositoryProvider;
    this.weightRepositoryProvider = weightRepositoryProvider;
    this.soundManagerProvider = soundManagerProvider;
    this.hapticManagerProvider = hapticManagerProvider;
  }

  @Override
  public EntryDetailViewModel get() {
    return newInstance(savedStateHandleProvider.get(), loggingRepositoryProvider.get(), savedEntryDaoProvider.get(), foodsRepositoryProvider.get(), backendApiProvider.get(), profileRepositoryProvider.get(), weightRepositoryProvider.get(), soundManagerProvider.get(), hapticManagerProvider.get());
  }

  public static EntryDetailViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<LoggingRepository> loggingRepositoryProvider,
      Provider<SavedEntryDao> savedEntryDaoProvider,
      Provider<FoodsRepository> foodsRepositoryProvider,
      Provider<TrackyBackendApi> backendApiProvider,
      Provider<ProfileRepository> profileRepositoryProvider,
      Provider<WeightRepository> weightRepositoryProvider,
      Provider<SoundManager> soundManagerProvider, Provider<HapticManager> hapticManagerProvider) {
    return new EntryDetailViewModel_Factory(savedStateHandleProvider, loggingRepositoryProvider, savedEntryDaoProvider, foodsRepositoryProvider, backendApiProvider, profileRepositoryProvider, weightRepositoryProvider, soundManagerProvider, hapticManagerProvider);
  }

  public static EntryDetailViewModel newInstance(SavedStateHandle savedStateHandle,
      LoggingRepository loggingRepository, SavedEntryDao savedEntryDao,
      FoodsRepository foodsRepository, TrackyBackendApi backendApi,
      ProfileRepository profileRepository, WeightRepository weightRepository,
      SoundManager soundManager, HapticManager hapticManager) {
    return new EntryDetailViewModel(savedStateHandle, loggingRepository, savedEntryDao, foodsRepository, backendApi, profileRepository, weightRepository, soundManager, hapticManager);
  }
}
