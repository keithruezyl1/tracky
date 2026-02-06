package com.tracky.app.di;

import com.tracky.app.data.local.TrackyDatabase;
import com.tracky.app.data.local.dao.ChatMessageDao;
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
public final class DatabaseModule_ProvideChatMessageDaoFactory implements Factory<ChatMessageDao> {
  private final Provider<TrackyDatabase> databaseProvider;

  public DatabaseModule_ProvideChatMessageDaoFactory(Provider<TrackyDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public ChatMessageDao get() {
    return provideChatMessageDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideChatMessageDaoFactory create(
      Provider<TrackyDatabase> databaseProvider) {
    return new DatabaseModule_ProvideChatMessageDaoFactory(databaseProvider);
  }

  public static ChatMessageDao provideChatMessageDao(TrackyDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideChatMessageDao(database));
  }
}
