package com.tracky.app.ui.screens.weight;

import com.tracky.app.data.repository.ProfileRepository;
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
public final class WeightTrackerViewModel_Factory implements Factory<WeightTrackerViewModel> {
  private final Provider<WeightRepository> weightRepositoryProvider;

  private final Provider<ProfileRepository> profileRepositoryProvider;

  public WeightTrackerViewModel_Factory(Provider<WeightRepository> weightRepositoryProvider,
      Provider<ProfileRepository> profileRepositoryProvider) {
    this.weightRepositoryProvider = weightRepositoryProvider;
    this.profileRepositoryProvider = profileRepositoryProvider;
  }

  @Override
  public WeightTrackerViewModel get() {
    return newInstance(weightRepositoryProvider.get(), profileRepositoryProvider.get());
  }

  public static WeightTrackerViewModel_Factory create(
      Provider<WeightRepository> weightRepositoryProvider,
      Provider<ProfileRepository> profileRepositoryProvider) {
    return new WeightTrackerViewModel_Factory(weightRepositoryProvider, profileRepositoryProvider);
  }

  public static WeightTrackerViewModel newInstance(WeightRepository weightRepository,
      ProfileRepository profileRepository) {
    return new WeightTrackerViewModel(weightRepository, profileRepository);
  }
}
