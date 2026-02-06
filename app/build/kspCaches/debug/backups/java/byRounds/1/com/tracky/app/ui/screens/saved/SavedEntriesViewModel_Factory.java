package com.tracky.app.ui.screens.saved;

import com.tracky.app.data.local.dao.SavedEntryDao;
import com.tracky.app.data.repository.LoggingRepository;
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

  public SavedEntriesViewModel_Factory(Provider<SavedEntryDao> savedEntryDaoProvider,
      Provider<LoggingRepository> loggingRepositoryProvider) {
    this.savedEntryDaoProvider = savedEntryDaoProvider;
    this.loggingRepositoryProvider = loggingRepositoryProvider;
  }

  @Override
  public SavedEntriesViewModel get() {
    return newInstance(savedEntryDaoProvider.get(), loggingRepositoryProvider.get());
  }

  public static SavedEntriesViewModel_Factory create(Provider<SavedEntryDao> savedEntryDaoProvider,
      Provider<LoggingRepository> loggingRepositoryProvider) {
    return new SavedEntriesViewModel_Factory(savedEntryDaoProvider, loggingRepositoryProvider);
  }

  public static SavedEntriesViewModel newInstance(SavedEntryDao savedEntryDao,
      LoggingRepository loggingRepository) {
    return new SavedEntriesViewModel(savedEntryDao, loggingRepository);
  }
}
