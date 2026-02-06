package com.tracky.app.ui.screens.summary;

import com.tracky.app.data.repository.LoggingRepository;
import com.tracky.app.data.repository.WeightRepository;
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
public final class SummaryViewModel_Factory implements Factory<SummaryViewModel> {
  private final Provider<WeightRepository> weightRepositoryProvider;

  private final Provider<LoggingRepository> loggingRepositoryProvider;

  public SummaryViewModel_Factory(Provider<WeightRepository> weightRepositoryProvider,
      Provider<LoggingRepository> loggingRepositoryProvider) {
    this.weightRepositoryProvider = weightRepositoryProvider;
    this.loggingRepositoryProvider = loggingRepositoryProvider;
  }

  @Override
  public SummaryViewModel get() {
    return newInstance(weightRepositoryProvider.get(), loggingRepositoryProvider.get());
  }

  public static SummaryViewModel_Factory create(Provider<WeightRepository> weightRepositoryProvider,
      Provider<LoggingRepository> loggingRepositoryProvider) {
    return new SummaryViewModel_Factory(weightRepositoryProvider, loggingRepositoryProvider);
  }

  public static SummaryViewModel newInstance(WeightRepository weightRepository,
      LoggingRepository loggingRepository) {
    return new SummaryViewModel(weightRepository, loggingRepository);
  }
}
