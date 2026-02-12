package com.tracky.app.data.repository;

import com.tracky.app.data.local.dao.FoodsDatasetDao;
import com.tracky.app.domain.resolver.CanonicalKeyGenerator;
import com.tracky.app.domain.resolver.UserHistoryResolver;
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
public final class FoodsRepository_Factory implements Factory<FoodsRepository> {
  private final Provider<FoodsDatasetDao> foodsDatasetDaoProvider;

  private final Provider<UserHistoryResolver> userHistoryResolverProvider;

  private final Provider<CanonicalKeyGenerator> canonicalKeyGeneratorProvider;

  public FoodsRepository_Factory(Provider<FoodsDatasetDao> foodsDatasetDaoProvider,
      Provider<UserHistoryResolver> userHistoryResolverProvider,
      Provider<CanonicalKeyGenerator> canonicalKeyGeneratorProvider) {
    this.foodsDatasetDaoProvider = foodsDatasetDaoProvider;
    this.userHistoryResolverProvider = userHistoryResolverProvider;
    this.canonicalKeyGeneratorProvider = canonicalKeyGeneratorProvider;
  }

  @Override
  public FoodsRepository get() {
    return newInstance(foodsDatasetDaoProvider.get(), userHistoryResolverProvider.get(), canonicalKeyGeneratorProvider.get());
  }

  public static FoodsRepository_Factory create(Provider<FoodsDatasetDao> foodsDatasetDaoProvider,
      Provider<UserHistoryResolver> userHistoryResolverProvider,
      Provider<CanonicalKeyGenerator> canonicalKeyGeneratorProvider) {
    return new FoodsRepository_Factory(foodsDatasetDaoProvider, userHistoryResolverProvider, canonicalKeyGeneratorProvider);
  }

  public static FoodsRepository newInstance(FoodsDatasetDao foodsDatasetDao,
      UserHistoryResolver userHistoryResolver, CanonicalKeyGenerator canonicalKeyGenerator) {
    return new FoodsRepository(foodsDatasetDao, userHistoryResolver, canonicalKeyGenerator);
  }
}
