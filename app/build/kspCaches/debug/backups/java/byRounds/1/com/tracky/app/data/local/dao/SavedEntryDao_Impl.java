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
import com.tracky.app.data.local.entity.SavedEntryEntity;
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
public final class SavedEntryDao_Impl implements SavedEntryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SavedEntryEntity> __insertionAdapterOfSavedEntryEntity;

  private final EntityDeletionOrUpdateAdapter<SavedEntryEntity> __deletionAdapterOfSavedEntryEntity;

  private final EntityDeletionOrUpdateAdapter<SavedEntryEntity> __updateAdapterOfSavedEntryEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfIncrementUseCount;

  public SavedEntryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSavedEntryEntity = new EntityInsertionAdapter<SavedEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `saved_entries` (`id`,`name`,`entryType`,`entryDataJson`,`totalCalories`,`useCount`,`lastUsedAt`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SavedEntryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getEntryType());
        statement.bindString(4, entity.getEntryDataJson());
        statement.bindLong(5, entity.getTotalCalories());
        statement.bindLong(6, entity.getUseCount());
        if (entity.getLastUsedAt() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getLastUsedAt());
        }
        statement.bindLong(8, entity.getCreatedAt());
        statement.bindLong(9, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfSavedEntryEntity = new EntityDeletionOrUpdateAdapter<SavedEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `saved_entries` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SavedEntryEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfSavedEntryEntity = new EntityDeletionOrUpdateAdapter<SavedEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `saved_entries` SET `id` = ?,`name` = ?,`entryType` = ?,`entryDataJson` = ?,`totalCalories` = ?,`useCount` = ?,`lastUsedAt` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SavedEntryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getEntryType());
        statement.bindString(4, entity.getEntryDataJson());
        statement.bindLong(5, entity.getTotalCalories());
        statement.bindLong(6, entity.getUseCount());
        if (entity.getLastUsedAt() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getLastUsedAt());
        }
        statement.bindLong(8, entity.getCreatedAt());
        statement.bindLong(9, entity.getUpdatedAt());
        statement.bindLong(10, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM saved_entries WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementUseCount = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE saved_entries \n"
                + "        SET useCount = useCount + 1, lastUsedAt = ?, updatedAt = ? \n"
                + "        WHERE id = ?\n"
                + "    ";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final SavedEntryEntity entry, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSavedEntryEntity.insertAndReturnId(entry);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final SavedEntryEntity entry, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfSavedEntryEntity.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final SavedEntryEntity entry, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSavedEntryEntity.handle(entry);
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
  public Object incrementUseCount(final long id, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementUseCount.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
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
          __preparedStmtOfIncrementUseCount.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SavedEntryEntity>> getAllEntries() {
    final String _sql = "\n"
            + "        SELECT * FROM saved_entries \n"
            + "        ORDER BY useCount DESC, (lastUsedAt IS NULL) ASC, lastUsedAt DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"saved_entries"}, new Callable<List<SavedEntryEntity>>() {
      @Override
      @NonNull
      public List<SavedEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfEntryType = CursorUtil.getColumnIndexOrThrow(_cursor, "entryType");
          final int _cursorIndexOfEntryDataJson = CursorUtil.getColumnIndexOrThrow(_cursor, "entryDataJson");
          final int _cursorIndexOfTotalCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCalories");
          final int _cursorIndexOfUseCount = CursorUtil.getColumnIndexOrThrow(_cursor, "useCount");
          final int _cursorIndexOfLastUsedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUsedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<SavedEntryEntity> _result = new ArrayList<SavedEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SavedEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpEntryType;
            _tmpEntryType = _cursor.getString(_cursorIndexOfEntryType);
            final String _tmpEntryDataJson;
            _tmpEntryDataJson = _cursor.getString(_cursorIndexOfEntryDataJson);
            final int _tmpTotalCalories;
            _tmpTotalCalories = _cursor.getInt(_cursorIndexOfTotalCalories);
            final int _tmpUseCount;
            _tmpUseCount = _cursor.getInt(_cursorIndexOfUseCount);
            final Long _tmpLastUsedAt;
            if (_cursor.isNull(_cursorIndexOfLastUsedAt)) {
              _tmpLastUsedAt = null;
            } else {
              _tmpLastUsedAt = _cursor.getLong(_cursorIndexOfLastUsedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new SavedEntryEntity(_tmpId,_tmpName,_tmpEntryType,_tmpEntryDataJson,_tmpTotalCalories,_tmpUseCount,_tmpLastUsedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<SavedEntryEntity>> getEntriesByType(final String type) {
    final String _sql = "\n"
            + "        SELECT * FROM saved_entries \n"
            + "        WHERE entryType = ? \n"
            + "        ORDER BY useCount DESC, (lastUsedAt IS NULL) ASC, lastUsedAt DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, type);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"saved_entries"}, new Callable<List<SavedEntryEntity>>() {
      @Override
      @NonNull
      public List<SavedEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfEntryType = CursorUtil.getColumnIndexOrThrow(_cursor, "entryType");
          final int _cursorIndexOfEntryDataJson = CursorUtil.getColumnIndexOrThrow(_cursor, "entryDataJson");
          final int _cursorIndexOfTotalCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCalories");
          final int _cursorIndexOfUseCount = CursorUtil.getColumnIndexOrThrow(_cursor, "useCount");
          final int _cursorIndexOfLastUsedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUsedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<SavedEntryEntity> _result = new ArrayList<SavedEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SavedEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpEntryType;
            _tmpEntryType = _cursor.getString(_cursorIndexOfEntryType);
            final String _tmpEntryDataJson;
            _tmpEntryDataJson = _cursor.getString(_cursorIndexOfEntryDataJson);
            final int _tmpTotalCalories;
            _tmpTotalCalories = _cursor.getInt(_cursorIndexOfTotalCalories);
            final int _tmpUseCount;
            _tmpUseCount = _cursor.getInt(_cursorIndexOfUseCount);
            final Long _tmpLastUsedAt;
            if (_cursor.isNull(_cursorIndexOfLastUsedAt)) {
              _tmpLastUsedAt = null;
            } else {
              _tmpLastUsedAt = _cursor.getLong(_cursorIndexOfLastUsedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new SavedEntryEntity(_tmpId,_tmpName,_tmpEntryType,_tmpEntryDataJson,_tmpTotalCalories,_tmpUseCount,_tmpLastUsedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<SavedEntryEntity>> searchEntries(final String query) {
    final String _sql = "\n"
            + "        SELECT * FROM saved_entries \n"
            + "        WHERE name LIKE '%' || ? || '%' \n"
            + "        ORDER BY useCount DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"saved_entries"}, new Callable<List<SavedEntryEntity>>() {
      @Override
      @NonNull
      public List<SavedEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfEntryType = CursorUtil.getColumnIndexOrThrow(_cursor, "entryType");
          final int _cursorIndexOfEntryDataJson = CursorUtil.getColumnIndexOrThrow(_cursor, "entryDataJson");
          final int _cursorIndexOfTotalCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCalories");
          final int _cursorIndexOfUseCount = CursorUtil.getColumnIndexOrThrow(_cursor, "useCount");
          final int _cursorIndexOfLastUsedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUsedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<SavedEntryEntity> _result = new ArrayList<SavedEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SavedEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpEntryType;
            _tmpEntryType = _cursor.getString(_cursorIndexOfEntryType);
            final String _tmpEntryDataJson;
            _tmpEntryDataJson = _cursor.getString(_cursorIndexOfEntryDataJson);
            final int _tmpTotalCalories;
            _tmpTotalCalories = _cursor.getInt(_cursorIndexOfTotalCalories);
            final int _tmpUseCount;
            _tmpUseCount = _cursor.getInt(_cursorIndexOfUseCount);
            final Long _tmpLastUsedAt;
            if (_cursor.isNull(_cursorIndexOfLastUsedAt)) {
              _tmpLastUsedAt = null;
            } else {
              _tmpLastUsedAt = _cursor.getLong(_cursorIndexOfLastUsedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new SavedEntryEntity(_tmpId,_tmpName,_tmpEntryType,_tmpEntryDataJson,_tmpTotalCalories,_tmpUseCount,_tmpLastUsedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getEntryById(final long id,
      final Continuation<? super SavedEntryEntity> $completion) {
    final String _sql = "SELECT * FROM saved_entries WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SavedEntryEntity>() {
      @Override
      @Nullable
      public SavedEntryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfEntryType = CursorUtil.getColumnIndexOrThrow(_cursor, "entryType");
          final int _cursorIndexOfEntryDataJson = CursorUtil.getColumnIndexOrThrow(_cursor, "entryDataJson");
          final int _cursorIndexOfTotalCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCalories");
          final int _cursorIndexOfUseCount = CursorUtil.getColumnIndexOrThrow(_cursor, "useCount");
          final int _cursorIndexOfLastUsedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUsedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final SavedEntryEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpEntryType;
            _tmpEntryType = _cursor.getString(_cursorIndexOfEntryType);
            final String _tmpEntryDataJson;
            _tmpEntryDataJson = _cursor.getString(_cursorIndexOfEntryDataJson);
            final int _tmpTotalCalories;
            _tmpTotalCalories = _cursor.getInt(_cursorIndexOfTotalCalories);
            final int _tmpUseCount;
            _tmpUseCount = _cursor.getInt(_cursorIndexOfUseCount);
            final Long _tmpLastUsedAt;
            if (_cursor.isNull(_cursorIndexOfLastUsedAt)) {
              _tmpLastUsedAt = null;
            } else {
              _tmpLastUsedAt = _cursor.getLong(_cursorIndexOfLastUsedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new SavedEntryEntity(_tmpId,_tmpName,_tmpEntryType,_tmpEntryDataJson,_tmpTotalCalories,_tmpUseCount,_tmpLastUsedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
