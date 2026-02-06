package com.tracky.app;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.tracky.app.data.local.TrackyDatabase;
import com.tracky.app.data.local.dao.ChatMessageDao;
import com.tracky.app.data.local.dao.DailyGoalDao;
import com.tracky.app.data.local.dao.ExerciseEntryDao;
import com.tracky.app.data.local.dao.FoodEntryDao;
import com.tracky.app.data.local.dao.FoodsDatasetDao;
import com.tracky.app.data.local.dao.SavedEntryDao;
import com.tracky.app.data.local.dao.UserProfileDao;
import com.tracky.app.data.local.dao.WeightEntryDao;
import com.tracky.app.data.local.preferences.UserPreferencesDataStore;
import com.tracky.app.data.remote.TrackyBackendApi;
import com.tracky.app.data.repository.ChatRepository;
import com.tracky.app.data.repository.FoodsRepository;
import com.tracky.app.data.repository.GoalRepository;
import com.tracky.app.data.repository.LoggingRepository;
import com.tracky.app.data.repository.ProfileRepository;
import com.tracky.app.data.repository.WeightRepository;
import com.tracky.app.di.DatabaseModule_ProvideChatMessageDaoFactory;
import com.tracky.app.di.DatabaseModule_ProvideDailyGoalDaoFactory;
import com.tracky.app.di.DatabaseModule_ProvideDatabaseFactory;
import com.tracky.app.di.DatabaseModule_ProvideExerciseEntryDaoFactory;
import com.tracky.app.di.DatabaseModule_ProvideFoodEntryDaoFactory;
import com.tracky.app.di.DatabaseModule_ProvideFoodsDatasetDaoFactory;
import com.tracky.app.di.DatabaseModule_ProvideSavedEntryDaoFactory;
import com.tracky.app.di.DatabaseModule_ProvideUserProfileDaoFactory;
import com.tracky.app.di.DatabaseModule_ProvideWeightEntryDaoFactory;
import com.tracky.app.di.NetworkModule_ProvideJsonFactory;
import com.tracky.app.di.NetworkModule_ProvideOkHttpClientFactory;
import com.tracky.app.di.NetworkModule_ProvideRetrofitFactory;
import com.tracky.app.di.NetworkModule_ProvideTrackyBackendApiFactory;
import com.tracky.app.domain.usecase.DraftLoggingInteractor;
import com.tracky.app.ui.screens.entrydetail.EntryDetailViewModel;
import com.tracky.app.ui.screens.entrydetail.EntryDetailViewModel_HiltModules;
import com.tracky.app.ui.screens.goals.DailyGoalsViewModel;
import com.tracky.app.ui.screens.goals.DailyGoalsViewModel_HiltModules;
import com.tracky.app.ui.screens.home.HomeViewModel;
import com.tracky.app.ui.screens.home.HomeViewModel_HiltModules;
import com.tracky.app.ui.screens.onboarding.OnboardingViewModel;
import com.tracky.app.ui.screens.onboarding.OnboardingViewModel_HiltModules;
import com.tracky.app.ui.screens.saved.SavedEntriesViewModel;
import com.tracky.app.ui.screens.saved.SavedEntriesViewModel_HiltModules;
import com.tracky.app.ui.screens.settings.SettingsViewModel;
import com.tracky.app.ui.screens.settings.SettingsViewModel_HiltModules;
import com.tracky.app.ui.screens.splash.SplashViewModel;
import com.tracky.app.ui.screens.splash.SplashViewModel_HiltModules;
import com.tracky.app.ui.screens.weight.WeightTrackerViewModel;
import com.tracky.app.ui.screens.weight.WeightTrackerViewModel_HiltModules;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import kotlinx.serialization.json.Json;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

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
public final class DaggerTrackyApp_HiltComponents_SingletonC {
  private DaggerTrackyApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public TrackyApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements TrackyApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public TrackyApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements TrackyApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public TrackyApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements TrackyApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public TrackyApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements TrackyApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public TrackyApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements TrackyApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public TrackyApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements TrackyApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public TrackyApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements TrackyApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public TrackyApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends TrackyApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends TrackyApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends TrackyApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends TrackyApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(8).put(LazyClassKeyProvider.com_tracky_app_ui_screens_goals_DailyGoalsViewModel, DailyGoalsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_tracky_app_ui_screens_entrydetail_EntryDetailViewModel, EntryDetailViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_tracky_app_ui_screens_home_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_tracky_app_ui_screens_onboarding_OnboardingViewModel, OnboardingViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_tracky_app_ui_screens_saved_SavedEntriesViewModel, SavedEntriesViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_tracky_app_ui_screens_settings_SettingsViewModel, SettingsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_tracky_app_ui_screens_splash_SplashViewModel, SplashViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_tracky_app_ui_screens_weight_WeightTrackerViewModel, WeightTrackerViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectProfileRepository(instance, singletonCImpl.profileRepositoryProvider.get());
      MainActivity_MembersInjector.injectPreferencesDataStore(instance, singletonCImpl.userPreferencesDataStoreProvider.get());
      return instance;
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_tracky_app_ui_screens_goals_DailyGoalsViewModel = "com.tracky.app.ui.screens.goals.DailyGoalsViewModel";

      static String com_tracky_app_ui_screens_splash_SplashViewModel = "com.tracky.app.ui.screens.splash.SplashViewModel";

      static String com_tracky_app_ui_screens_onboarding_OnboardingViewModel = "com.tracky.app.ui.screens.onboarding.OnboardingViewModel";

      static String com_tracky_app_ui_screens_settings_SettingsViewModel = "com.tracky.app.ui.screens.settings.SettingsViewModel";

      static String com_tracky_app_ui_screens_home_HomeViewModel = "com.tracky.app.ui.screens.home.HomeViewModel";

      static String com_tracky_app_ui_screens_weight_WeightTrackerViewModel = "com.tracky.app.ui.screens.weight.WeightTrackerViewModel";

      static String com_tracky_app_ui_screens_saved_SavedEntriesViewModel = "com.tracky.app.ui.screens.saved.SavedEntriesViewModel";

      static String com_tracky_app_ui_screens_entrydetail_EntryDetailViewModel = "com.tracky.app.ui.screens.entrydetail.EntryDetailViewModel";

      @KeepFieldType
      DailyGoalsViewModel com_tracky_app_ui_screens_goals_DailyGoalsViewModel2;

      @KeepFieldType
      SplashViewModel com_tracky_app_ui_screens_splash_SplashViewModel2;

      @KeepFieldType
      OnboardingViewModel com_tracky_app_ui_screens_onboarding_OnboardingViewModel2;

      @KeepFieldType
      SettingsViewModel com_tracky_app_ui_screens_settings_SettingsViewModel2;

      @KeepFieldType
      HomeViewModel com_tracky_app_ui_screens_home_HomeViewModel2;

      @KeepFieldType
      WeightTrackerViewModel com_tracky_app_ui_screens_weight_WeightTrackerViewModel2;

      @KeepFieldType
      SavedEntriesViewModel com_tracky_app_ui_screens_saved_SavedEntriesViewModel2;

      @KeepFieldType
      EntryDetailViewModel com_tracky_app_ui_screens_entrydetail_EntryDetailViewModel2;
    }
  }

  private static final class ViewModelCImpl extends TrackyApp_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<DailyGoalsViewModel> dailyGoalsViewModelProvider;

    private Provider<EntryDetailViewModel> entryDetailViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<OnboardingViewModel> onboardingViewModelProvider;

    private Provider<SavedEntriesViewModel> savedEntriesViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<SplashViewModel> splashViewModelProvider;

    private Provider<WeightTrackerViewModel> weightTrackerViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.dailyGoalsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.entryDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.onboardingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.savedEntriesViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.splashViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.weightTrackerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(8).put(LazyClassKeyProvider.com_tracky_app_ui_screens_goals_DailyGoalsViewModel, ((Provider) dailyGoalsViewModelProvider)).put(LazyClassKeyProvider.com_tracky_app_ui_screens_entrydetail_EntryDetailViewModel, ((Provider) entryDetailViewModelProvider)).put(LazyClassKeyProvider.com_tracky_app_ui_screens_home_HomeViewModel, ((Provider) homeViewModelProvider)).put(LazyClassKeyProvider.com_tracky_app_ui_screens_onboarding_OnboardingViewModel, ((Provider) onboardingViewModelProvider)).put(LazyClassKeyProvider.com_tracky_app_ui_screens_saved_SavedEntriesViewModel, ((Provider) savedEntriesViewModelProvider)).put(LazyClassKeyProvider.com_tracky_app_ui_screens_settings_SettingsViewModel, ((Provider) settingsViewModelProvider)).put(LazyClassKeyProvider.com_tracky_app_ui_screens_splash_SplashViewModel, ((Provider) splashViewModelProvider)).put(LazyClassKeyProvider.com_tracky_app_ui_screens_weight_WeightTrackerViewModel, ((Provider) weightTrackerViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_tracky_app_ui_screens_saved_SavedEntriesViewModel = "com.tracky.app.ui.screens.saved.SavedEntriesViewModel";

      static String com_tracky_app_ui_screens_home_HomeViewModel = "com.tracky.app.ui.screens.home.HomeViewModel";

      static String com_tracky_app_ui_screens_weight_WeightTrackerViewModel = "com.tracky.app.ui.screens.weight.WeightTrackerViewModel";

      static String com_tracky_app_ui_screens_splash_SplashViewModel = "com.tracky.app.ui.screens.splash.SplashViewModel";

      static String com_tracky_app_ui_screens_onboarding_OnboardingViewModel = "com.tracky.app.ui.screens.onboarding.OnboardingViewModel";

      static String com_tracky_app_ui_screens_entrydetail_EntryDetailViewModel = "com.tracky.app.ui.screens.entrydetail.EntryDetailViewModel";

      static String com_tracky_app_ui_screens_goals_DailyGoalsViewModel = "com.tracky.app.ui.screens.goals.DailyGoalsViewModel";

      static String com_tracky_app_ui_screens_settings_SettingsViewModel = "com.tracky.app.ui.screens.settings.SettingsViewModel";

      @KeepFieldType
      SavedEntriesViewModel com_tracky_app_ui_screens_saved_SavedEntriesViewModel2;

      @KeepFieldType
      HomeViewModel com_tracky_app_ui_screens_home_HomeViewModel2;

      @KeepFieldType
      WeightTrackerViewModel com_tracky_app_ui_screens_weight_WeightTrackerViewModel2;

      @KeepFieldType
      SplashViewModel com_tracky_app_ui_screens_splash_SplashViewModel2;

      @KeepFieldType
      OnboardingViewModel com_tracky_app_ui_screens_onboarding_OnboardingViewModel2;

      @KeepFieldType
      EntryDetailViewModel com_tracky_app_ui_screens_entrydetail_EntryDetailViewModel2;

      @KeepFieldType
      DailyGoalsViewModel com_tracky_app_ui_screens_goals_DailyGoalsViewModel2;

      @KeepFieldType
      SettingsViewModel com_tracky_app_ui_screens_settings_SettingsViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.tracky.app.ui.screens.goals.DailyGoalsViewModel 
          return (T) new DailyGoalsViewModel(singletonCImpl.goalRepositoryProvider.get());

          case 1: // com.tracky.app.ui.screens.entrydetail.EntryDetailViewModel 
          return (T) new EntryDetailViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.loggingRepositoryProvider.get(), singletonCImpl.savedEntryDao(), singletonCImpl.foodsRepositoryProvider.get(), singletonCImpl.provideTrackyBackendApiProvider.get(), singletonCImpl.profileRepositoryProvider.get());

          case 2: // com.tracky.app.ui.screens.home.HomeViewModel 
          return (T) new HomeViewModel(singletonCImpl.loggingRepositoryProvider.get(), singletonCImpl.profileRepositoryProvider.get(), singletonCImpl.goalRepositoryProvider.get(), singletonCImpl.draftLoggingInteractorProvider.get(), singletonCImpl.chatRepositoryProvider.get(), singletonCImpl.userPreferencesDataStoreProvider.get());

          case 3: // com.tracky.app.ui.screens.onboarding.OnboardingViewModel 
          return (T) new OnboardingViewModel(singletonCImpl.profileRepositoryProvider.get(), singletonCImpl.goalRepositoryProvider.get(), singletonCImpl.weightRepositoryProvider.get());

          case 4: // com.tracky.app.ui.screens.saved.SavedEntriesViewModel 
          return (T) new SavedEntriesViewModel(singletonCImpl.savedEntryDao(), singletonCImpl.loggingRepositoryProvider.get(), singletonCImpl.profileRepositoryProvider.get());

          case 5: // com.tracky.app.ui.screens.settings.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.profileRepositoryProvider.get(), singletonCImpl.userPreferencesDataStoreProvider.get(), singletonCImpl.provideDatabaseProvider.get());

          case 6: // com.tracky.app.ui.screens.splash.SplashViewModel 
          return (T) new SplashViewModel(singletonCImpl.profileRepositoryProvider.get(), singletonCImpl.provideTrackyBackendApiProvider.get());

          case 7: // com.tracky.app.ui.screens.weight.WeightTrackerViewModel 
          return (T) new WeightTrackerViewModel(singletonCImpl.weightRepositoryProvider.get(), singletonCImpl.profileRepositoryProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends TrackyApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends TrackyApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends TrackyApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<TrackyDatabase> provideDatabaseProvider;

    private Provider<UserPreferencesDataStore> userPreferencesDataStoreProvider;

    private Provider<ProfileRepository> profileRepositoryProvider;

    private Provider<GoalRepository> goalRepositoryProvider;

    private Provider<LoggingRepository> loggingRepositoryProvider;

    private Provider<OkHttpClient> provideOkHttpClientProvider;

    private Provider<Json> provideJsonProvider;

    private Provider<Retrofit> provideRetrofitProvider;

    private Provider<TrackyBackendApi> provideTrackyBackendApiProvider;

    private Provider<FoodsRepository> foodsRepositoryProvider;

    private Provider<DraftLoggingInteractor> draftLoggingInteractorProvider;

    private Provider<ChatRepository> chatRepositoryProvider;

    private Provider<WeightRepository> weightRepositoryProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private UserProfileDao userProfileDao() {
      return DatabaseModule_ProvideUserProfileDaoFactory.provideUserProfileDao(provideDatabaseProvider.get());
    }

    private DailyGoalDao dailyGoalDao() {
      return DatabaseModule_ProvideDailyGoalDaoFactory.provideDailyGoalDao(provideDatabaseProvider.get());
    }

    private FoodEntryDao foodEntryDao() {
      return DatabaseModule_ProvideFoodEntryDaoFactory.provideFoodEntryDao(provideDatabaseProvider.get());
    }

    private ExerciseEntryDao exerciseEntryDao() {
      return DatabaseModule_ProvideExerciseEntryDaoFactory.provideExerciseEntryDao(provideDatabaseProvider.get());
    }

    private SavedEntryDao savedEntryDao() {
      return DatabaseModule_ProvideSavedEntryDaoFactory.provideSavedEntryDao(provideDatabaseProvider.get());
    }

    private FoodsDatasetDao foodsDatasetDao() {
      return DatabaseModule_ProvideFoodsDatasetDaoFactory.provideFoodsDatasetDao(provideDatabaseProvider.get());
    }

    private ChatMessageDao chatMessageDao() {
      return DatabaseModule_ProvideChatMessageDaoFactory.provideChatMessageDao(provideDatabaseProvider.get());
    }

    private WeightEntryDao weightEntryDao() {
      return DatabaseModule_ProvideWeightEntryDaoFactory.provideWeightEntryDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<TrackyDatabase>(singletonCImpl, 1));
      this.userPreferencesDataStoreProvider = DoubleCheck.provider(new SwitchingProvider<UserPreferencesDataStore>(singletonCImpl, 2));
      this.profileRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ProfileRepository>(singletonCImpl, 0));
      this.goalRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<GoalRepository>(singletonCImpl, 3));
      this.loggingRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<LoggingRepository>(singletonCImpl, 4));
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 8));
      this.provideJsonProvider = DoubleCheck.provider(new SwitchingProvider<Json>(singletonCImpl, 9));
      this.provideRetrofitProvider = DoubleCheck.provider(new SwitchingProvider<Retrofit>(singletonCImpl, 7));
      this.provideTrackyBackendApiProvider = DoubleCheck.provider(new SwitchingProvider<TrackyBackendApi>(singletonCImpl, 6));
      this.foodsRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<FoodsRepository>(singletonCImpl, 5));
      this.draftLoggingInteractorProvider = DoubleCheck.provider(new SwitchingProvider<DraftLoggingInteractor>(singletonCImpl, 10));
      this.chatRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ChatRepository>(singletonCImpl, 11));
      this.weightRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<WeightRepository>(singletonCImpl, 12));
    }

    @Override
    public void injectTrackyApp(TrackyApp trackyApp) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.tracky.app.data.repository.ProfileRepository 
          return (T) new ProfileRepository(singletonCImpl.userProfileDao(), singletonCImpl.userPreferencesDataStoreProvider.get());

          case 1: // com.tracky.app.data.local.TrackyDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.tracky.app.data.local.preferences.UserPreferencesDataStore 
          return (T) new UserPreferencesDataStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.tracky.app.data.repository.GoalRepository 
          return (T) new GoalRepository(singletonCImpl.dailyGoalDao());

          case 4: // com.tracky.app.data.repository.LoggingRepository 
          return (T) new LoggingRepository(singletonCImpl.foodEntryDao(), singletonCImpl.exerciseEntryDao(), singletonCImpl.goalRepositoryProvider.get());

          case 5: // com.tracky.app.data.repository.FoodsRepository 
          return (T) new FoodsRepository(singletonCImpl.foodsDatasetDao(), singletonCImpl.provideTrackyBackendApiProvider.get());

          case 6: // com.tracky.app.data.remote.TrackyBackendApi 
          return (T) NetworkModule_ProvideTrackyBackendApiFactory.provideTrackyBackendApi(singletonCImpl.provideRetrofitProvider.get());

          case 7: // retrofit2.Retrofit 
          return (T) NetworkModule_ProvideRetrofitFactory.provideRetrofit(singletonCImpl.provideOkHttpClientProvider.get(), singletonCImpl.provideJsonProvider.get());

          case 8: // okhttp3.OkHttpClient 
          return (T) NetworkModule_ProvideOkHttpClientFactory.provideOkHttpClient();

          case 9: // kotlinx.serialization.json.Json 
          return (T) NetworkModule_ProvideJsonFactory.provideJson();

          case 10: // com.tracky.app.domain.usecase.DraftLoggingInteractor 
          return (T) new DraftLoggingInteractor(singletonCImpl.provideTrackyBackendApiProvider.get(), singletonCImpl.foodsRepositoryProvider.get(), singletonCImpl.loggingRepositoryProvider.get(), singletonCImpl.profileRepositoryProvider.get());

          case 11: // com.tracky.app.data.repository.ChatRepository 
          return (T) new ChatRepository(singletonCImpl.chatMessageDao());

          case 12: // com.tracky.app.data.repository.WeightRepository 
          return (T) new WeightRepository(singletonCImpl.weightEntryDao());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
