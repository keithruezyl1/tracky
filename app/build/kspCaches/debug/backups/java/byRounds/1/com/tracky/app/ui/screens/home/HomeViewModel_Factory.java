package com.tracky.app.ui.screens.home;

import com.tracky.app.data.local.preferences.UserPreferencesDataStore;
import com.tracky.app.data.repository.ChatRepository;
import com.tracky.app.data.repository.GoalRepository;
import com.tracky.app.data.repository.LoggingRepository;
import com.tracky.app.data.repository.ProfileRepository;
import com.tracky.app.domain.usecase.DraftLoggingInteractor;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<LoggingRepository> loggingRepositoryProvider;

  private final Provider<ProfileRepository> profileRepositoryProvider;

  private final Provider<GoalRepository> goalRepositoryProvider;

  private final Provider<DraftLoggingInteractor> draftLoggingInteractorProvider;

  private final Provider<ChatRepository> chatRepositoryProvider;

  private final Provider<UserPreferencesDataStore> preferencesDataStoreProvider;

  public HomeViewModel_Factory(Provider<LoggingRepository> loggingRepositoryProvider,
      Provider<ProfileRepository> profileRepositoryProvider,
      Provider<GoalRepository> goalRepositoryProvider,
      Provider<DraftLoggingInteractor> draftLoggingInteractorProvider,
      Provider<ChatRepository> chatRepositoryProvider,
      Provider<UserPreferencesDataStore> preferencesDataStoreProvider) {
    this.loggingRepositoryProvider = loggingRepositoryProvider;
    this.profileRepositoryProvider = profileRepositoryProvider;
    this.goalRepositoryProvider = goalRepositoryProvider;
    this.draftLoggingInteractorProvider = draftLoggingInteractorProvider;
    this.chatRepositoryProvider = chatRepositoryProvider;
    this.preferencesDataStoreProvider = preferencesDataStoreProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(loggingRepositoryProvider.get(), profileRepositoryProvider.get(), goalRepositoryProvider.get(), draftLoggingInteractorProvider.get(), chatRepositoryProvider.get(), preferencesDataStoreProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<LoggingRepository> loggingRepositoryProvider,
      Provider<ProfileRepository> profileRepositoryProvider,
      Provider<GoalRepository> goalRepositoryProvider,
      Provider<DraftLoggingInteractor> draftLoggingInteractorProvider,
      Provider<ChatRepository> chatRepositoryProvider,
      Provider<UserPreferencesDataStore> preferencesDataStoreProvider) {
    return new HomeViewModel_Factory(loggingRepositoryProvider, profileRepositoryProvider, goalRepositoryProvider, draftLoggingInteractorProvider, chatRepositoryProvider, preferencesDataStoreProvider);
  }

  public static HomeViewModel newInstance(LoggingRepository loggingRepository,
      ProfileRepository profileRepository, GoalRepository goalRepository,
      DraftLoggingInteractor draftLoggingInteractor, ChatRepository chatRepository,
      UserPreferencesDataStore preferencesDataStore) {
    return new HomeViewModel(loggingRepository, profileRepository, goalRepository, draftLoggingInteractor, chatRepository, preferencesDataStore);
  }
}
