package com.tracky.app.ui.screens.saved;

import com.tracky.app.data.local.dao.SavedEntryDao;
import com.tracky.app.data.repository.LoggingRepository;
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
public final class SavedEntriesViewModel_Factory implements Factory<SavedEntriesViewModel> {
  private final Provider<SavedEntryDao> savedEntryDaoProvider;

  private final Provider<LoggingRepository> loggingRepositoryProvider;

  private final Provider<ProfileRepository> profileRepositoryProvider;

  public SavedEntriesViewModel_Factory(Provider<SavedEntryDao> savedEntryDaoProvider,
      Provider<LoggingRepository> loggingRepositoryProvider,
      Provider<ProfileRepository> profileRepositoryProvider) {
    this.savedEntryDaoProvider = savedEntryDaoProvider;
    this.loggingRepositoryProvider = loggingRepositoryProvider;
    this.profileRepositoryProvider = profileRepositoryProvider;
  }

  @Override
  public SavedEntriesViewModel get() {
    return newInstance(savedEntryDaoProvider.get(), loggingRepositoryProvider.get(), profileRepositoryProvider.get());
  }

  public static SavedEntriesViewModel_Factory create(Provider<SavedEntryDao> savedEntryDaoProvider,
      Provider<LoggingRepository> loggingRepositoryProvider,
      Provider<ProfileRepository> profileRepositoryProvider) {
    return new SavedEntriesViewModel_Factory(savedEntryDaoProvider, loggingRepositoryProvider, profileRepositoryProvider);
  }

  public static SavedEntriesViewModel newInstance(SavedEntryDao savedEntryDao,
      LoggingRepository loggingRepository, ProfileRepository profileRepository) {
    return new SavedEntriesViewModel(savedEntryDao, loggingRepository, profileRepository);
  }
}
