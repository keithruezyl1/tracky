package com.tracky.app.ui.screens.onboarding;

import com.tracky.app.data.repository.GoalRepository;
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
public final class OnboardingViewModel_Factory implements Factory<OnboardingViewModel> {
  private final Provider<ProfileRepository> profileRepositoryProvider;

  private final Provider<GoalRepository> goalRepositoryProvider;

  private final Provider<WeightRepository> weightRepositoryProvider;

  public OnboardingViewModel_Factory(Provider<ProfileRepository> profileRepositoryProvider,
      Provider<GoalRepository> goalRepositoryProvider,
      Provider<WeightRepository> weightRepositoryProvider) {
    this.profileRepositoryProvider = profileRepositoryProvider;
    this.goalRepositoryProvider = goalRepositoryProvider;
    this.weightRepositoryProvider = weightRepositoryProvider;
  }

  @Override
  public OnboardingViewModel get() {
    return newInstance(profileRepositoryProvider.get(), goalRepositoryProvider.get(), weightRepositoryProvider.get());
  }

  public static OnboardingViewModel_Factory create(
      Provider<ProfileRepository> profileRepositoryProvider,
      Provider<GoalRepository> goalRepositoryProvider,
      Provider<WeightRepository> weightRepositoryProvider) {
    return new OnboardingViewModel_Factory(profileRepositoryProvider, goalRepositoryProvider, weightRepositoryProvider);
  }

  public static OnboardingViewModel newInstance(ProfileRepository profileRepository,
      GoalRepository goalRepository, WeightRepository weightRepository) {
    return new OnboardingViewModel(profileRepository, goalRepository, weightRepository);
  }
}
