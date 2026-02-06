package com.tracky.app.ui.screens.splash;

import com.tracky.app.data.remote.TrackyBackendApi;
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
public final class SplashViewModel_Factory implements Factory<SplashViewModel> {
  private final Provider<ProfileRepository> profileRepositoryProvider;

  private final Provider<TrackyBackendApi> backendApiProvider;

  public SplashViewModel_Factory(Provider<ProfileRepository> profileRepositoryProvider,
      Provider<TrackyBackendApi> backendApiProvider) {
    this.profileRepositoryProvider = profileRepositoryProvider;
    this.backendApiProvider = backendApiProvider;
  }

  @Override
  public SplashViewModel get() {
    return newInstance(profileRepositoryProvider.get(), backendApiProvider.get());
  }

  public static SplashViewModel_Factory create(
      Provider<ProfileRepository> profileRepositoryProvider,
      Provider<TrackyBackendApi> backendApiProvider) {
    return new SplashViewModel_Factory(profileRepositoryProvider, backendApiProvider);
  }

  public static SplashViewModel newInstance(ProfileRepository profileRepository,
      TrackyBackendApi backendApi) {
    return new SplashViewModel(profileRepository, backendApi);
  }
}
