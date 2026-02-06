package com.tracky.app.data.repository;

import com.tracky.app.data.local.dao.WeightEntryDao;
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
public final class WeightRepository_Factory implements Factory<WeightRepository> {
  private final Provider<WeightEntryDao> weightEntryDaoProvider;

  public WeightRepository_Factory(Provider<WeightEntryDao> weightEntryDaoProvider) {
    this.weightEntryDaoProvider = weightEntryDaoProvider;
  }

  @Override
  public WeightRepository get() {
    return newInstance(weightEntryDaoProvider.get());
  }

  public static WeightRepository_Factory create(Provider<WeightEntryDao> weightEntryDaoProvider) {
    return new WeightRepository_Factory(weightEntryDaoProvider);
  }

  public static WeightRepository newInstance(WeightEntryDao weightEntryDao) {
    return new WeightRepository(weightEntryDao);
  }
}
