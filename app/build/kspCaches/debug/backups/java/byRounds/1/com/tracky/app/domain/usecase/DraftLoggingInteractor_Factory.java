package com.tracky.app.domain.usecase;

import com.tracky.app.data.remote.TrackyBackendApi;
import com.tracky.app.data.repository.FoodsRepository;
import com.tracky.app.data.repository.LoggingRepository;
import com.tracky.app.data.repository.ProfileRepository;
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
public final class DraftLoggingInteractor_Factory implements Factory<DraftLoggingInteractor> {
  private final Provider<TrackyBackendApi> backendApiProvider;

  private final Provider<FoodsRepository> foodsRepositoryProvider;

  private final Provider<LoggingRepository> loggingRepositoryProvider;

  private final Provider<ProfileRepository> profileRepositoryProvider;

  public DraftLoggingInteractor_Factory(Provider<TrackyBackendApi> backendApiProvider,
      Provider<FoodsRepository> foodsRepositoryProvider,
      Provider<LoggingRepository> loggingRepositoryProvider,
      Provider<ProfileRepository> profileRepositoryProvider) {
    this.backendApiProvider = backendApiProvider;
    this.foodsRepositoryProvider = foodsRepositoryProvider;
    this.loggingRepositoryProvider = loggingRepositoryProvider;
    this.profileRepositoryProvider = profileRepositoryProvider;
  }

  @Override
  public DraftLoggingInteractor get() {
    return newInstance(backendApiProvider.get(), foodsRepositoryProvider.get(), loggingRepositoryProvider.get(), profileRepositoryProvider.get());
  }

  public static DraftLoggingInteractor_Factory create(Provider<TrackyBackendApi> backendApiProvider,
      Provider<FoodsRepository> foodsRepositoryProvider,
      Provider<LoggingRepository> loggingRepositoryProvider,
      Provider<ProfileRepository> profileRepositoryProvider) {
    return new DraftLoggingInteractor_Factory(backendApiProvider, foodsRepositoryProvider, loggingRepositoryProvider, profileRepositoryProvider);
  }

  public static DraftLoggingInteractor newInstance(TrackyBackendApi backendApi,
      FoodsRepository foodsRepository, LoggingRepository loggingRepository,
      ProfileRepository profileRepository) {
    return new DraftLoggingInteractor(backendApi, foodsRepository, loggingRepository, profileRepository);
  }
}
