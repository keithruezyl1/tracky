package com.tracky.app.di;

import com.tracky.app.data.local.TrackyDatabase;
import com.tracky.app.data.local.dao.FoodsDatasetDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideFoodsDatasetDaoFactory implements Factory<FoodsDatasetDao> {
  private final Provider<TrackyDatabase> databaseProvider;

  public DatabaseModule_ProvideFoodsDatasetDaoFactory(Provider<TrackyDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public FoodsDatasetDao get() {
    return provideFoodsDatasetDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideFoodsDatasetDaoFactory create(
      Provider<TrackyDatabase> databaseProvider) {
    return new DatabaseModule_ProvideFoodsDatasetDaoFactory(databaseProvider);
  }

  public static FoodsDatasetDao provideFoodsDatasetDao(TrackyDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideFoodsDatasetDao(database));
  }
}
