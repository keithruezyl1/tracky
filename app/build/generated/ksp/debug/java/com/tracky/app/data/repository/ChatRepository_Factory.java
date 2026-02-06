package com.tracky.app.data.repository;

import com.tracky.app.data.local.dao.ChatMessageDao;
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
public final class ChatRepository_Factory implements Factory<ChatRepository> {
  private final Provider<ChatMessageDao> chatMessageDaoProvider;

  public ChatRepository_Factory(Provider<ChatMessageDao> chatMessageDaoProvider) {
    this.chatMessageDaoProvider = chatMessageDaoProvider;
  }

  @Override
  public ChatRepository get() {
    return newInstance(chatMessageDaoProvider.get());
  }

  public static ChatRepository_Factory create(Provider<ChatMessageDao> chatMessageDaoProvider) {
    return new ChatRepository_Factory(chatMessageDaoProvider);
  }

  public static ChatRepository newInstance(ChatMessageDao chatMessageDao) {
    return new ChatRepository(chatMessageDao);
  }
}
