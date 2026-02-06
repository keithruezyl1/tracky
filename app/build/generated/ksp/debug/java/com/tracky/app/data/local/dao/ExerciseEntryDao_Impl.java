package com.tracky.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.tracky.app.data.local.entity.ExerciseEntryEntity;
import com.tracky.app.data.local.entity.ExerciseItemEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Float;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
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
public final class ExerciseEntryDao_Impl implements ExerciseEntryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ExerciseEntryEntity> __insertionAdapterOfExerciseEntryEntity;

  private final EntityInsertionAdapter<ExerciseItemEntity> __insertionAdapterOfExerciseItemEntity;

  private final EntityDeletionOrUpdateAdapter<ExerciseEntryEntity> __deletionAdapterOfExerciseEntryEntity;

  private final EntityDeletionOrUpdateAdapter<ExerciseEntryEntity> __updateAdapterOfExerciseEntryEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteItemsForEntry;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteItemById;

  public ExerciseEntryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfExerciseEntryEntity = new EntityInsertionAdapter<ExerciseEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `exercise_entries` (`id`,`date`,`time`,`timestamp`,`totalCalories`,`totalDurationMinutes`,`userWeightKg`,`originalInput`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExerciseEntryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDate());
        statement.bindString(3, entity.getTime());
        statement.bindLong(4, entity.getTimestamp());
        statement.bindDouble(5, entity.getTotalCalories());
        statement.bindLong(6, entity.getTotalDurationMinutes());
        statement.bindDouble(7, entity.getUserWeightKg());
        if (entity.getOriginalInput() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getOriginalInput());
        }
        statement.bindLong(9, entity.getCreatedAt());
        statement.bindLong(10, entity.getUpdatedAt());
      }
    };
    this.__insertionAdapterOfExerciseItemEntity = new EntityInsertionAdapter<ExerciseItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `exercise_items` (`id`,`entryId`,`activityName`,`durationMinutes`,`metValue`,`caloriesBurned`,`intensity`,`source`,`confidence`,`displayOrder`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExerciseItemEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getEntryId());
        statement.bindString(3, entity.getActivityName());
        statement.bindLong(4, entity.getDurationMinutes());
        statement.bindDouble(5, entity.getMetValue());
        statement.bindDouble(6, entity.getCaloriesBurned());
        if (entity.getIntensity() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getIntensity());
        }
        statement.bindString(8, entity.getSource());
        statement.bindDouble(9, entity.getConfidence());
        statement.bindLong(10, entity.getDisplayOrder());
      }
    };
    this.__deletionAdapterOfExerciseEntryEntity = new EntityDeletionOrUpdateAdapter<ExerciseEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `exercise_entries` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExerciseEntryEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfExerciseEntryEntity = new EntityDeletionOrUpdateAdapter<ExerciseEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `exercise_entries` SET `id` = ?,`date` = ?,`time` = ?,`timestamp` = ?,`totalCalories` = ?,`totalDurationMinutes` = ?,`userWeightKg` = ?,`originalInput` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExerciseEntryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDate());
        statement.bindString(3, entity.getTime());
        statement.bindLong(4, entity.getTimestamp());
        statement.bindDouble(5, entity.getTotalCalories());
        statement.bindLong(6, entity.getTotalDurationMinutes());
        statement.bindDouble(7, entity.getUserWeightKg());
        if (entity.getOriginalInput() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getOriginalInput());
        }
        statement.bindLong(9, entity.getCreatedAt());
        statement.bindLong(10, entity.getUpdatedAt());
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteItemsForEntry = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM exercise_items WHERE entryId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM exercise_entries WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteItemById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM exercise_items WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final ExerciseEntryEntity entry,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfExerciseEntryEntity.insertAndReturnId(entry);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertItems(final List<ExerciseItemEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfExerciseItemEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final ExerciseEntryEntity entry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfExerciseEntryEntity.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final ExerciseEntryEntity entry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfExerciseEntryEntity.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteItemsForEntry(final long entryId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteItemsForEntry.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, entryId);
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
          __preparedStmtOfDeleteItemsForEntry.release(_stmt);
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
  public Object deleteItemById(final long itemId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteItemById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, itemId);
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
          __preparedStmtOfDeleteItemById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ExerciseEntryWithItems>> getEntriesForDate(final String date) {
    final String _sql = "SELECT * FROM exercise_entries WHERE date = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"exercise_items",
        "exercise_entries"}, new Callable<List<ExerciseEntryWithItems>>() {
      @Override
      @NonNull
      public List<ExerciseEntryWithItems> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
            final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
            final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
            final int _cursorIndexOfTotalCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCalories");
            final int _cursorIndexOfTotalDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalDurationMinutes");
            final int _cursorIndexOfUserWeightKg = CursorUtil.getColumnIndexOrThrow(_cursor, "userWeightKg");
            final int _cursorIndexOfOriginalInput = CursorUtil.getColumnIndexOrThrow(_cursor, "originalInput");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
            final LongSparseArray<ArrayList<ExerciseItemEntity>> _collectionItems = new LongSparseArray<ArrayList<ExerciseItemEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionItems.containsKey(_tmpKey)) {
                _collectionItems.put(_tmpKey, new ArrayList<ExerciseItemEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipexerciseItemsAscomTrackyAppDataLocalEntityExerciseItemEntity(_collectionItems);
            final List<ExerciseEntryWithItems> _result = new ArrayList<ExerciseEntryWithItems>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final ExerciseEntryWithItems _item;
              final ExerciseEntryEntity _tmpEntry;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpDate;
              _tmpDate = _cursor.getString(_cursorIndexOfDate);
              final String _tmpTime;
              _tmpTime = _cursor.getString(_cursorIndexOfTime);
              final long _tmpTimestamp;
              _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
              final float _tmpTotalCalories;
              _tmpTotalCalories = _cursor.getFloat(_cursorIndexOfTotalCalories);
              final int _tmpTotalDurationMinutes;
              _tmpTotalDurationMinutes = _cursor.getInt(_cursorIndexOfTotalDurationMinutes);
              final float _tmpUserWeightKg;
              _tmpUserWeightKg = _cursor.getFloat(_cursorIndexOfUserWeightKg);
              final String _tmpOriginalInput;
              if (_cursor.isNull(_cursorIndexOfOriginalInput)) {
                _tmpOriginalInput = null;
              } else {
                _tmpOriginalInput = _cursor.getString(_cursorIndexOfOriginalInput);
              }
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              final long _tmpUpdatedAt;
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
              _tmpEntry = new ExerciseEntryEntity(_tmpId,_tmpDate,_tmpTime,_tmpTimestamp,_tmpTotalCalories,_tmpTotalDurationMinutes,_tmpUserWeightKg,_tmpOriginalInput,_tmpCreatedAt,_tmpUpdatedAt);
              final ArrayList<ExerciseItemEntity> _tmpItemsCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpItemsCollection = _collectionItems.get(_tmpKey_1);
              _item = new ExerciseEntryWithItems(_tmpEntry,_tmpItemsCollection);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getEntriesForDateOnce(final String date,
      final Continuation<? super List<ExerciseEntryWithItems>> $completion) {
    final String _sql = "SELECT * FROM exercise_entries WHERE date = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, true, _cancellationSignal, new Callable<List<ExerciseEntryWithItems>>() {
      @Override
      @NonNull
      public List<ExerciseEntryWithItems> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
            final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
            final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
            final int _cursorIndexOfTotalCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCalories");
            final int _cursorIndexOfTotalDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalDurationMinutes");
            final int _cursorIndexOfUserWeightKg = CursorUtil.getColumnIndexOrThrow(_cursor, "userWeightKg");
            final int _cursorIndexOfOriginalInput = CursorUtil.getColumnIndexOrThrow(_cursor, "originalInput");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
            final LongSparseArray<ArrayList<ExerciseItemEntity>> _collectionItems = new LongSparseArray<ArrayList<ExerciseItemEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionItems.containsKey(_tmpKey)) {
                _collectionItems.put(_tmpKey, new ArrayList<ExerciseItemEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipexerciseItemsAscomTrackyAppDataLocalEntityExerciseItemEntity(_collectionItems);
            final List<ExerciseEntryWithItems> _result = new ArrayList<ExerciseEntryWithItems>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final ExerciseEntryWithItems _item;
              final ExerciseEntryEntity _tmpEntry;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpDate;
              _tmpDate = _cursor.getString(_cursorIndexOfDate);
              final String _tmpTime;
              _tmpTime = _cursor.getString(_cursorIndexOfTime);
              final long _tmpTimestamp;
              _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
              final float _tmpTotalCalories;
              _tmpTotalCalories = _cursor.getFloat(_cursorIndexOfTotalCalories);
              final int _tmpTotalDurationMinutes;
              _tmpTotalDurationMinutes = _cursor.getInt(_cursorIndexOfTotalDurationMinutes);
              final float _tmpUserWeightKg;
              _tmpUserWeightKg = _cursor.getFloat(_cursorIndexOfUserWeightKg);
              final String _tmpOriginalInput;
              if (_cursor.isNull(_cursorIndexOfOriginalInput)) {
                _tmpOriginalInput = null;
              } else {
                _tmpOriginalInput = _cursor.getString(_cursorIndexOfOriginalInput);
              }
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              final long _tmpUpdatedAt;
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
              _tmpEntry = new ExerciseEntryEntity(_tmpId,_tmpDate,_tmpTime,_tmpTimestamp,_tmpTotalCalories,_tmpTotalDurationMinutes,_tmpUserWeightKg,_tmpOriginalInput,_tmpCreatedAt,_tmpUpdatedAt);
              final ArrayList<ExerciseItemEntity> _tmpItemsCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpItemsCollection = _collectionItems.get(_tmpKey_1);
              _item = new ExerciseEntryWithItems(_tmpEntry,_tmpItemsCollection);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
            _statement.release();
          }
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getEntryById(final long id,
      final Continuation<? super ExerciseEntryWithItems> $completion) {
    final String _sql = "SELECT * FROM exercise_entries WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, true, _cancellationSignal, new Callable<ExerciseEntryWithItems>() {
      @Override
      @Nullable
      public ExerciseEntryWithItems call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
            final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
            final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
            final int _cursorIndexOfTotalCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCalories");
            final int _cursorIndexOfTotalDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalDurationMinutes");
            final int _cursorIndexOfUserWeightKg = CursorUtil.getColumnIndexOrThrow(_cursor, "userWeightKg");
            final int _cursorIndexOfOriginalInput = CursorUtil.getColumnIndexOrThrow(_cursor, "originalInput");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
            final LongSparseArray<ArrayList<ExerciseItemEntity>> _collectionItems = new LongSparseArray<ArrayList<ExerciseItemEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionItems.containsKey(_tmpKey)) {
                _collectionItems.put(_tmpKey, new ArrayList<ExerciseItemEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipexerciseItemsAscomTrackyAppDataLocalEntityExerciseItemEntity(_collectionItems);
            final ExerciseEntryWithItems _result;
            if (_cursor.moveToFirst()) {
              final ExerciseEntryEntity _tmpEntry;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpDate;
              _tmpDate = _cursor.getString(_cursorIndexOfDate);
              final String _tmpTime;
              _tmpTime = _cursor.getString(_cursorIndexOfTime);
              final long _tmpTimestamp;
              _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
              final float _tmpTotalCalories;
              _tmpTotalCalories = _cursor.getFloat(_cursorIndexOfTotalCalories);
              final int _tmpTotalDurationMinutes;
              _tmpTotalDurationMinutes = _cursor.getInt(_cursorIndexOfTotalDurationMinutes);
              final float _tmpUserWeightKg;
              _tmpUserWeightKg = _cursor.getFloat(_cursorIndexOfUserWeightKg);
              final String _tmpOriginalInput;
              if (_cursor.isNull(_cursorIndexOfOriginalInput)) {
                _tmpOriginalInput = null;
              } else {
                _tmpOriginalInput = _cursor.getString(_cursorIndexOfOriginalInput);
              }
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              final long _tmpUpdatedAt;
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
              _tmpEntry = new ExerciseEntryEntity(_tmpId,_tmpDate,_tmpTime,_tmpTimestamp,_tmpTotalCalories,_tmpTotalDurationMinutes,_tmpUserWeightKg,_tmpOriginalInput,_tmpCreatedAt,_tmpUpdatedAt);
              final ArrayList<ExerciseItemEntity> _tmpItemsCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpItemsCollection = _collectionItems.get(_tmpKey_1);
              _result = new ExerciseEntryWithItems(_tmpEntry,_tmpItemsCollection);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
            _statement.release();
          }
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<ExerciseEntryWithItems> getEntryByIdFlow(final long id) {
    final String _sql = "SELECT * FROM exercise_entries WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"exercise_items",
        "exercise_entries"}, new Callable<ExerciseEntryWithItems>() {
      @Override
      @Nullable
      public ExerciseEntryWithItems call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
            final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
            final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
            final int _cursorIndexOfTotalCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCalories");
            final int _cursorIndexOfTotalDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalDurationMinutes");
            final int _cursorIndexOfUserWeightKg = CursorUtil.getColumnIndexOrThrow(_cursor, "userWeightKg");
            final int _cursorIndexOfOriginalInput = CursorUtil.getColumnIndexOrThrow(_cursor, "originalInput");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
            final LongSparseArray<ArrayList<ExerciseItemEntity>> _collectionItems = new LongSparseArray<ArrayList<ExerciseItemEntity>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionItems.containsKey(_tmpKey)) {
                _collectionItems.put(_tmpKey, new ArrayList<ExerciseItemEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipexerciseItemsAscomTrackyAppDataLocalEntityExerciseItemEntity(_collectionItems);
            final ExerciseEntryWithItems _result;
            if (_cursor.moveToFirst()) {
              final ExerciseEntryEntity _tmpEntry;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpDate;
              _tmpDate = _cursor.getString(_cursorIndexOfDate);
              final String _tmpTime;
              _tmpTime = _cursor.getString(_cursorIndexOfTime);
              final long _tmpTimestamp;
              _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
              final float _tmpTotalCalories;
              _tmpTotalCalories = _cursor.getFloat(_cursorIndexOfTotalCalories);
              final int _tmpTotalDurationMinutes;
              _tmpTotalDurationMinutes = _cursor.getInt(_cursorIndexOfTotalDurationMinutes);
              final float _tmpUserWeightKg;
              _tmpUserWeightKg = _cursor.getFloat(_cursorIndexOfUserWeightKg);
              final String _tmpOriginalInput;
              if (_cursor.isNull(_cursorIndexOfOriginalInput)) {
                _tmpOriginalInput = null;
              } else {
                _tmpOriginalInput = _cursor.getString(_cursorIndexOfOriginalInput);
              }
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              final long _tmpUpdatedAt;
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
              _tmpEntry = new ExerciseEntryEntity(_tmpId,_tmpDate,_tmpTime,_tmpTimestamp,_tmpTotalCalories,_tmpTotalDurationMinutes,_tmpUserWeightKg,_tmpOriginalInput,_tmpCreatedAt,_tmpUpdatedAt);
              final ArrayList<ExerciseItemEntity> _tmpItemsCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpItemsCollection = _collectionItems.get(_tmpKey_1);
              _result = new ExerciseEntryWithItems(_tmpEntry,_tmpItemsCollection);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Float> getTotalCaloriesBurnedForDate(final String date) {
    final String _sql = "SELECT SUM(totalCalories) FROM exercise_entries WHERE date = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"exercise_entries"}, new Callable<Float>() {
      @Override
      @Nullable
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final Float _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getFloat(0);
            }
            _result = _tmp;
          } else {
            _result = null;
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
  public Flow<Float> getTotalCaloriesBurnedBetween(final String startDate, final String endDate) {
    final String _sql = "SELECT SUM(totalCalories) FROM exercise_entries WHERE date >= ? AND date <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindString(_argIndex, endDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"exercise_entries"}, new Callable<Float>() {
      @Override
      @Nullable
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final Float _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getFloat(0);
            }
            _result = _tmp;
          } else {
            _result = null;
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipexerciseItemsAscomTrackyAppDataLocalEntityExerciseItemEntity(
      @NonNull final LongSparseArray<ArrayList<ExerciseItemEntity>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (map) -> {
        __fetchRelationshipexerciseItemsAscomTrackyAppDataLocalEntityExerciseItemEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`entryId`,`activityName`,`durationMinutes`,`metValue`,`caloriesBurned`,`intensity`,`source`,`confidence`,`displayOrder` FROM `exercise_items` WHERE `entryId` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "entryId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfEntryId = 1;
      final int _cursorIndexOfActivityName = 2;
      final int _cursorIndexOfDurationMinutes = 3;
      final int _cursorIndexOfMetValue = 4;
      final int _cursorIndexOfCaloriesBurned = 5;
      final int _cursorIndexOfIntensity = 6;
      final int _cursorIndexOfSource = 7;
      final int _cursorIndexOfConfidence = 8;
      final int _cursorIndexOfDisplayOrder = 9;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        final ArrayList<ExerciseItemEntity> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final ExerciseItemEntity _item_1;
          final long _tmpId;
          _tmpId = _cursor.getLong(_cursorIndexOfId);
          final long _tmpEntryId;
          _tmpEntryId = _cursor.getLong(_cursorIndexOfEntryId);
          final String _tmpActivityName;
          _tmpActivityName = _cursor.getString(_cursorIndexOfActivityName);
          final int _tmpDurationMinutes;
          _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
          final float _tmpMetValue;
          _tmpMetValue = _cursor.getFloat(_cursorIndexOfMetValue);
          final float _tmpCaloriesBurned;
          _tmpCaloriesBurned = _cursor.getFloat(_cursorIndexOfCaloriesBurned);
          final String _tmpIntensity;
          if (_cursor.isNull(_cursorIndexOfIntensity)) {
            _tmpIntensity = null;
          } else {
            _tmpIntensity = _cursor.getString(_cursorIndexOfIntensity);
          }
          final String _tmpSource;
          _tmpSource = _cursor.getString(_cursorIndexOfSource);
          final float _tmpConfidence;
          _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
          final int _tmpDisplayOrder;
          _tmpDisplayOrder = _cursor.getInt(_cursorIndexOfDisplayOrder);
          _item_1 = new ExerciseItemEntity(_tmpId,_tmpEntryId,_tmpActivityName,_tmpDurationMinutes,_tmpMetValue,_tmpCaloriesBurned,_tmpIntensity,_tmpSource,_tmpConfidence,_tmpDisplayOrder);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
