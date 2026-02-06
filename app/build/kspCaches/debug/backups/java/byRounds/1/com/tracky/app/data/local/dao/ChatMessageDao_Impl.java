package com.tracky.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.tracky.app.data.local.entity.ChatMessageEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ChatMessageDao_Impl implements ChatMessageDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ChatMessageEntity> __insertionAdapterOfChatMessageEntity;

  private final EntityDeletionOrUpdateAdapter<ChatMessageEntity> __deletionAdapterOfChatMessageEntity;

  private final EntityDeletionOrUpdateAdapter<ChatMessageEntity> __updateAdapterOfChatMessageEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfUpdateDraftStatus;

  private final SharedSQLiteStatement __preparedStmtOfLinkConfirmedEntry;

  public ChatMessageDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfChatMessageEntity = new EntityInsertionAdapter<ChatMessageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `chat_messages` (`id`,`date`,`timestamp`,`messageType`,`role`,`content`,`imagePath`,`entryDataJson`,`draftStatus`,`linkedFoodEntryId`,`linkedExerciseEntryId`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ChatMessageEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDate());
        statement.bindLong(3, entity.getTimestamp());
        statement.bindString(4, entity.getMessageType());
        statement.bindString(5, entity.getRole());
        if (entity.getContent() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getContent());
        }
        if (entity.getImagePath() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getImagePath());
        }
        if (entity.getEntryDataJson() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getEntryDataJson());
        }
        if (entity.getDraftStatus() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getDraftStatus());
        }
        if (entity.getLinkedFoodEntryId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getLinkedFoodEntryId());
        }
        if (entity.getLinkedExerciseEntryId() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getLinkedExerciseEntryId());
        }
        statement.bindLong(12, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfChatMessageEntity = new EntityDeletionOrUpdateAdapter<ChatMessageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `chat_messages` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ChatMessageEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfChatMessageEntity = new EntityDeletionOrUpdateAdapter<ChatMessageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `chat_messages` SET `id` = ?,`date` = ?,`timestamp` = ?,`messageType` = ?,`role` = ?,`content` = ?,`imagePath` = ?,`entryDataJson` = ?,`draftStatus` = ?,`linkedFoodEntryId` = ?,`linkedExerciseEntryId` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ChatMessageEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDate());
        statement.bindLong(3, entity.getTimestamp());
        statement.bindString(4, entity.getMessageType());
        statement.bindString(5, entity.getRole());
        if (entity.getContent() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getContent());
        }
        if (entity.getImagePath() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getImagePath());
        }
        if (entity.getEntryDataJson() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getEntryDataJson());
        }
        if (entity.getDraftStatus() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getDraftStatus());
        }
        if (entity.getLinkedFoodEntryId() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getLinkedFoodEntryId());
        }
        if (entity.getLinkedExerciseEntryId() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getLinkedExerciseEntryId());
        }
        statement.bindLong(12, entity.getCreatedAt());
        statement.bindLong(13, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM chat_messages WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateDraftStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE chat_messages SET draftStatus = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfLinkConfirmedEntry = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE chat_messages \n"
                + "        SET draftStatus = 'confirmed', \n"
                + "            linkedFoodEntryId = ?, \n"
                + "            linkedExerciseEntryId = ? \n"
                + "        WHERE id = ?\n"
                + "    ";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final ChatMessageEntity message,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfChatMessageEntity.insertAndReturnId(message);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final ChatMessageEntity message,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfChatMessageEntity.handle(message);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final ChatMessageEntity message,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfChatMessageEntity.handle(message);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDraftStatus(final long id, final String status,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateDraftStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateDraftStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object linkConfirmedEntry(final long messageId, final Long foodEntryId,
      final Long exerciseEntryId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfLinkConfirmedEntry.acquire();
        int _argIndex = 1;
        if (foodEntryId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, foodEntryId);
        }
        _argIndex = 2;
        if (exerciseEntryId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, exerciseEntryId);
        }
        _argIndex = 3;
        _stmt.bindLong(_argIndex, messageId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfLinkConfirmedEntry.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ChatMessageEntity>> getMessagesForDate(final String date) {
    final String _sql = "SELECT * FROM chat_messages WHERE date = ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"chat_messages"}, new Callable<List<ChatMessageEntity>>() {
      @Override
      @NonNull
      public List<ChatMessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfMessageType = CursorUtil.getColumnIndexOrThrow(_cursor, "messageType");
          final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "imagePath");
          final int _cursorIndexOfEntryDataJson = CursorUtil.getColumnIndexOrThrow(_cursor, "entryDataJson");
          final int _cursorIndexOfDraftStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "draftStatus");
          final int _cursorIndexOfLinkedFoodEntryId = CursorUtil.getColumnIndexOrThrow(_cursor, "linkedFoodEntryId");
          final int _cursorIndexOfLinkedExerciseEntryId = CursorUtil.getColumnIndexOrThrow(_cursor, "linkedExerciseEntryId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ChatMessageEntity> _result = new ArrayList<ChatMessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChatMessageEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpMessageType;
            _tmpMessageType = _cursor.getString(_cursorIndexOfMessageType);
            final String _tmpRole;
            _tmpRole = _cursor.getString(_cursorIndexOfRole);
            final String _tmpContent;
            if (_cursor.isNull(_cursorIndexOfContent)) {
              _tmpContent = null;
            } else {
              _tmpContent = _cursor.getString(_cursorIndexOfContent);
            }
            final String _tmpImagePath;
            if (_cursor.isNull(_cursorIndexOfImagePath)) {
              _tmpImagePath = null;
            } else {
              _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            }
            final String _tmpEntryDataJson;
            if (_cursor.isNull(_cursorIndexOfEntryDataJson)) {
              _tmpEntryDataJson = null;
            } else {
              _tmpEntryDataJson = _cursor.getString(_cursorIndexOfEntryDataJson);
            }
            final String _tmpDraftStatus;
            if (_cursor.isNull(_cursorIndexOfDraftStatus)) {
              _tmpDraftStatus = null;
            } else {
              _tmpDraftStatus = _cursor.getString(_cursorIndexOfDraftStatus);
            }
            final Long _tmpLinkedFoodEntryId;
            if (_cursor.isNull(_cursorIndexOfLinkedFoodEntryId)) {
              _tmpLinkedFoodEntryId = null;
            } else {
              _tmpLinkedFoodEntryId = _cursor.getLong(_cursorIndexOfLinkedFoodEntryId);
            }
            final Long _tmpLinkedExerciseEntryId;
            if (_cursor.isNull(_cursorIndexOfLinkedExerciseEntryId)) {
              _tmpLinkedExerciseEntryId = null;
            } else {
              _tmpLinkedExerciseEntryId = _cursor.getLong(_cursorIndexOfLinkedExerciseEntryId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ChatMessageEntity(_tmpId,_tmpDate,_tmpTimestamp,_tmpMessageType,_tmpRole,_tmpContent,_tmpImagePath,_tmpEntryDataJson,_tmpDraftStatus,_tmpLinkedFoodEntryId,_tmpLinkedExerciseEntryId,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getMessagesForDateOnce(final String date,
      final Continuation<? super List<ChatMessageEntity>> $completion) {
    final String _sql = "SELECT * FROM chat_messages WHERE date = ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ChatMessageEntity>>() {
      @Override
      @NonNull
      public List<ChatMessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfMessageType = CursorUtil.getColumnIndexOrThrow(_cursor, "messageType");
          final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "imagePath");
          final int _cursorIndexOfEntryDataJson = CursorUtil.getColumnIndexOrThrow(_cursor, "entryDataJson");
          final int _cursorIndexOfDraftStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "draftStatus");
          final int _cursorIndexOfLinkedFoodEntryId = CursorUtil.getColumnIndexOrThrow(_cursor, "linkedFoodEntryId");
          final int _cursorIndexOfLinkedExerciseEntryId = CursorUtil.getColumnIndexOrThrow(_cursor, "linkedExerciseEntryId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ChatMessageEntity> _result = new ArrayList<ChatMessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChatMessageEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpMessageType;
            _tmpMessageType = _cursor.getString(_cursorIndexOfMessageType);
            final String _tmpRole;
            _tmpRole = _cursor.getString(_cursorIndexOfRole);
            final String _tmpContent;
            if (_cursor.isNull(_cursorIndexOfContent)) {
              _tmpContent = null;
            } else {
              _tmpContent = _cursor.getString(_cursorIndexOfContent);
            }
            final String _tmpImagePath;
            if (_cursor.isNull(_cursorIndexOfImagePath)) {
              _tmpImagePath = null;
            } else {
              _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            }
            final String _tmpEntryDataJson;
            if (_cursor.isNull(_cursorIndexOfEntryDataJson)) {
              _tmpEntryDataJson = null;
            } else {
              _tmpEntryDataJson = _cursor.getString(_cursorIndexOfEntryDataJson);
            }
            final String _tmpDraftStatus;
            if (_cursor.isNull(_cursorIndexOfDraftStatus)) {
              _tmpDraftStatus = null;
            } else {
              _tmpDraftStatus = _cursor.getString(_cursorIndexOfDraftStatus);
            }
            final Long _tmpLinkedFoodEntryId;
            if (_cursor.isNull(_cursorIndexOfLinkedFoodEntryId)) {
              _tmpLinkedFoodEntryId = null;
            } else {
              _tmpLinkedFoodEntryId = _cursor.getLong(_cursorIndexOfLinkedFoodEntryId);
            }
            final Long _tmpLinkedExerciseEntryId;
            if (_cursor.isNull(_cursorIndexOfLinkedExerciseEntryId)) {
              _tmpLinkedExerciseEntryId = null;
            } else {
              _tmpLinkedExerciseEntryId = _cursor.getLong(_cursorIndexOfLinkedExerciseEntryId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ChatMessageEntity(_tmpId,_tmpDate,_tmpTimestamp,_tmpMessageType,_tmpRole,_tmpContent,_tmpImagePath,_tmpEntryDataJson,_tmpDraftStatus,_tmpLinkedFoodEntryId,_tmpLinkedExerciseEntryId,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ChatMessageEntity>> getRecentMessages(final int limit) {
    final String _sql = "SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"chat_messages"}, new Callable<List<ChatMessageEntity>>() {
      @Override
      @NonNull
      public List<ChatMessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfMessageType = CursorUtil.getColumnIndexOrThrow(_cursor, "messageType");
          final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "imagePath");
          final int _cursorIndexOfEntryDataJson = CursorUtil.getColumnIndexOrThrow(_cursor, "entryDataJson");
          final int _cursorIndexOfDraftStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "draftStatus");
          final int _cursorIndexOfLinkedFoodEntryId = CursorUtil.getColumnIndexOrThrow(_cursor, "linkedFoodEntryId");
          final int _cursorIndexOfLinkedExerciseEntryId = CursorUtil.getColumnIndexOrThrow(_cursor, "linkedExerciseEntryId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ChatMessageEntity> _result = new ArrayList<ChatMessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChatMessageEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpMessageType;
            _tmpMessageType = _cursor.getString(_cursorIndexOfMessageType);
            final String _tmpRole;
            _tmpRole = _cursor.getString(_cursorIndexOfRole);
            final String _tmpContent;
            if (_cursor.isNull(_cursorIndexOfContent)) {
              _tmpContent = null;
            } else {
              _tmpContent = _cursor.getString(_cursorIndexOfContent);
            }
            final String _tmpImagePath;
            if (_cursor.isNull(_cursorIndexOfImagePath)) {
              _tmpImagePath = null;
            } else {
              _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            }
            final String _tmpEntryDataJson;
            if (_cursor.isNull(_cursorIndexOfEntryDataJson)) {
              _tmpEntryDataJson = null;
            } else {
              _tmpEntryDataJson = _cursor.getString(_cursorIndexOfEntryDataJson);
            }
            final String _tmpDraftStatus;
            if (_cursor.isNull(_cursorIndexOfDraftStatus)) {
              _tmpDraftStatus = null;
            } else {
              _tmpDraftStatus = _cursor.getString(_cursorIndexOfDraftStatus);
            }
            final Long _tmpLinkedFoodEntryId;
            if (_cursor.isNull(_cursorIndexOfLinkedFoodEntryId)) {
              _tmpLinkedFoodEntryId = null;
            } else {
              _tmpLinkedFoodEntryId = _cursor.getLong(_cursorIndexOfLinkedFoodEntryId);
            }
            final Long _tmpLinkedExerciseEntryId;
            if (_cursor.isNull(_cursorIndexOfLinkedExerciseEntryId)) {
              _tmpLinkedExerciseEntryId = null;
            } else {
              _tmpLinkedExerciseEntryId = _cursor.getLong(_cursorIndexOfLinkedExerciseEntryId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ChatMessageEntity(_tmpId,_tmpDate,_tmpTimestamp,_tmpMessageType,_tmpRole,_tmpContent,_tmpImagePath,_tmpEntryDataJson,_tmpDraftStatus,_tmpLinkedFoodEntryId,_tmpLinkedExerciseEntryId,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getMessageById(final long id,
      final Continuation<? super ChatMessageEntity> $completion) {
    final String _sql = "SELECT * FROM chat_messages WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ChatMessageEntity>() {
      @Override
      @Nullable
      public ChatMessageEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfMessageType = CursorUtil.getColumnIndexOrThrow(_cursor, "messageType");
          final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "imagePath");
          final int _cursorIndexOfEntryDataJson = CursorUtil.getColumnIndexOrThrow(_cursor, "entryDataJson");
          final int _cursorIndexOfDraftStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "draftStatus");
          final int _cursorIndexOfLinkedFoodEntryId = CursorUtil.getColumnIndexOrThrow(_cursor, "linkedFoodEntryId");
          final int _cursorIndexOfLinkedExerciseEntryId = CursorUtil.getColumnIndexOrThrow(_cursor, "linkedExerciseEntryId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final ChatMessageEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpMessageType;
            _tmpMessageType = _cursor.getString(_cursorIndexOfMessageType);
            final String _tmpRole;
            _tmpRole = _cursor.getString(_cursorIndexOfRole);
            final String _tmpContent;
            if (_cursor.isNull(_cursorIndexOfContent)) {
              _tmpContent = null;
            } else {
              _tmpContent = _cursor.getString(_cursorIndexOfContent);
            }
            final String _tmpImagePath;
            if (_cursor.isNull(_cursorIndexOfImagePath)) {
              _tmpImagePath = null;
            } else {
              _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            }
            final String _tmpEntryDataJson;
            if (_cursor.isNull(_cursorIndexOfEntryDataJson)) {
              _tmpEntryDataJson = null;
            } else {
              _tmpEntryDataJson = _cursor.getString(_cursorIndexOfEntryDataJson);
            }
            final String _tmpDraftStatus;
            if (_cursor.isNull(_cursorIndexOfDraftStatus)) {
              _tmpDraftStatus = null;
            } else {
              _tmpDraftStatus = _cursor.getString(_cursorIndexOfDraftStatus);
            }
            final Long _tmpLinkedFoodEntryId;
            if (_cursor.isNull(_cursorIndexOfLinkedFoodEntryId)) {
              _tmpLinkedFoodEntryId = null;
            } else {
              _tmpLinkedFoodEntryId = _cursor.getLong(_cursorIndexOfLinkedFoodEntryId);
            }
            final Long _tmpLinkedExerciseEntryId;
            if (_cursor.isNull(_cursorIndexOfLinkedExerciseEntryId)) {
              _tmpLinkedExerciseEntryId = null;
            } else {
              _tmpLinkedExerciseEntryId = _cursor.getLong(_cursorIndexOfLinkedExerciseEntryId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new ChatMessageEntity(_tmpId,_tmpDate,_tmpTimestamp,_tmpMessageType,_tmpRole,_tmpContent,_tmpImagePath,_tmpEntryDataJson,_tmpDraftStatus,_tmpLinkedFoodEntryId,_tmpLinkedExerciseEntryId,_tmpCreatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ChatMessageEntity>> getPendingDrafts() {
    final String _sql = "\n"
            + "        SELECT * FROM chat_messages \n"
            + "        WHERE draftStatus = 'needs_confirmation' \n"
            + "        ORDER BY timestamp DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"chat_messages"}, new Callable<List<ChatMessageEntity>>() {
      @Override
      @NonNull
      public List<ChatMessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfMessageType = CursorUtil.getColumnIndexOrThrow(_cursor, "messageType");
          final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "imagePath");
          final int _cursorIndexOfEntryDataJson = CursorUtil.getColumnIndexOrThrow(_cursor, "entryDataJson");
          final int _cursorIndexOfDraftStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "draftStatus");
          final int _cursorIndexOfLinkedFoodEntryId = CursorUtil.getColumnIndexOrThrow(_cursor, "linkedFoodEntryId");
          final int _cursorIndexOfLinkedExerciseEntryId = CursorUtil.getColumnIndexOrThrow(_cursor, "linkedExerciseEntryId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ChatMessageEntity> _result = new ArrayList<ChatMessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChatMessageEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpMessageType;
            _tmpMessageType = _cursor.getString(_cursorIndexOfMessageType);
            final String _tmpRole;
            _tmpRole = _cursor.getString(_cursorIndexOfRole);
            final String _tmpContent;
            if (_cursor.isNull(_cursorIndexOfContent)) {
              _tmpContent = null;
            } else {
              _tmpContent = _cursor.getString(_cursorIndexOfContent);
            }
            final String _tmpImagePath;
            if (_cursor.isNull(_cursorIndexOfImagePath)) {
              _tmpImagePath = null;
            } else {
              _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            }
            final String _tmpEntryDataJson;
            if (_cursor.isNull(_cursorIndexOfEntryDataJson)) {
              _tmpEntryDataJson = null;
            } else {
              _tmpEntryDataJson = _cursor.getString(_cursorIndexOfEntryDataJson);
            }
            final String _tmpDraftStatus;
            if (_cursor.isNull(_cursorIndexOfDraftStatus)) {
              _tmpDraftStatus = null;
            } else {
              _tmpDraftStatus = _cursor.getString(_cursorIndexOfDraftStatus);
            }
            final Long _tmpLinkedFoodEntryId;
            if (_cursor.isNull(_cursorIndexOfLinkedFoodEntryId)) {
              _tmpLinkedFoodEntryId = null;
            } else {
              _tmpLinkedFoodEntryId = _cursor.getLong(_cursorIndexOfLinkedFoodEntryId);
            }
            final Long _tmpLinkedExerciseEntryId;
            if (_cursor.isNull(_cursorIndexOfLinkedExerciseEntryId)) {
              _tmpLinkedExerciseEntryId = null;
            } else {
              _tmpLinkedExerciseEntryId = _cursor.getLong(_cursorIndexOfLinkedExerciseEntryId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ChatMessageEntity(_tmpId,_tmpDate,_tmpTimestamp,_tmpMessageType,_tmpRole,_tmpContent,_tmpImagePath,_tmpEntryDataJson,_tmpDraftStatus,_tmpLinkedFoodEntryId,_tmpLinkedExerciseEntryId,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getMostRecentUserMessage(final String date,
      final Continuation<? super ChatMessageEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM chat_messages \n"
            + "        WHERE date = ? AND messageType = 'user_text' \n"
            + "        ORDER BY timestamp DESC \n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ChatMessageEntity>() {
      @Override
      @Nullable
      public ChatMessageEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfMessageType = CursorUtil.getColumnIndexOrThrow(_cursor, "messageType");
          final int _cursorIndexOfRole = CursorUtil.getColumnIndexOrThrow(_cursor, "role");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "imagePath");
          final int _cursorIndexOfEntryDataJson = CursorUtil.getColumnIndexOrThrow(_cursor, "entryDataJson");
          final int _cursorIndexOfDraftStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "draftStatus");
          final int _cursorIndexOfLinkedFoodEntryId = CursorUtil.getColumnIndexOrThrow(_cursor, "linkedFoodEntryId");
          final int _cursorIndexOfLinkedExerciseEntryId = CursorUtil.getColumnIndexOrThrow(_cursor, "linkedExerciseEntryId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final ChatMessageEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpMessageType;
            _tmpMessageType = _cursor.getString(_cursorIndexOfMessageType);
            final String _tmpRole;
            _tmpRole = _cursor.getString(_cursorIndexOfRole);
            final String _tmpContent;
            if (_cursor.isNull(_cursorIndexOfContent)) {
              _tmpContent = null;
            } else {
              _tmpContent = _cursor.getString(_cursorIndexOfContent);
            }
            final String _tmpImagePath;
            if (_cursor.isNull(_cursorIndexOfImagePath)) {
              _tmpImagePath = null;
            } else {
              _tmpImagePath = _cursor.getString(_cursorIndexOfImagePath);
            }
            final String _tmpEntryDataJson;
            if (_cursor.isNull(_cursorIndexOfEntryDataJson)) {
              _tmpEntryDataJson = null;
            } else {
              _tmpEntryDataJson = _cursor.getString(_cursorIndexOfEntryDataJson);
            }
            final String _tmpDraftStatus;
            if (_cursor.isNull(_cursorIndexOfDraftStatus)) {
              _tmpDraftStatus = null;
            } else {
              _tmpDraftStatus = _cursor.getString(_cursorIndexOfDraftStatus);
            }
            final Long _tmpLinkedFoodEntryId;
            if (_cursor.isNull(_cursorIndexOfLinkedFoodEntryId)) {
              _tmpLinkedFoodEntryId = null;
            } else {
              _tmpLinkedFoodEntryId = _cursor.getLong(_cursorIndexOfLinkedFoodEntryId);
            }
            final Long _tmpLinkedExerciseEntryId;
            if (_cursor.isNull(_cursorIndexOfLinkedExerciseEntryId)) {
              _tmpLinkedExerciseEntryId = null;
            } else {
              _tmpLinkedExerciseEntryId = _cursor.getLong(_cursorIndexOfLinkedExerciseEntryId);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new ChatMessageEntity(_tmpId,_tmpDate,_tmpTimestamp,_tmpMessageType,_tmpRole,_tmpContent,_tmpImagePath,_tmpEntryDataJson,_tmpDraftStatus,_tmpLinkedFoodEntryId,_tmpLinkedExerciseEntryId,_tmpCreatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
