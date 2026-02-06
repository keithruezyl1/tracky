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
import com.tracky.app.data.local.entity.ExerciseEntryEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class ExerciseEntryDao_Impl implements ExerciseEntryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ExerciseEntryEntity> __insertionAdapterOfExerciseEntryEntity;

  private final EntityDeletionOrUpdateAdapter<ExerciseEntryEntity> __deletionAdapterOfExerciseEntryEntity;

  private final EntityDeletionOrUpdateAdapter<ExerciseEntryEntity> __updateAdapterOfExerciseEntryEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public ExerciseEntryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfExerciseEntryEntity = new EntityInsertionAdapter<ExerciseEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `exercise_entries` (`id`,`date`,`time`,`timestamp`,`activityName`,`durationMinutes`,`metValue`,`userWeightKg`,`caloriesBurned`,`intensity`,`originalInput`,`source`,`sourceId`,`confidence`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExerciseEntryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDate());
        statement.bindString(3, entity.getTime());
        statement.bindLong(4, entity.getTimestamp());
        statement.bindString(5, entity.getActivityName());
        statement.bindLong(6, entity.getDurationMinutes());
        statement.bindDouble(7, entity.getMetValue());
        statement.bindDouble(8, entity.getUserWeightKg());
        statement.bindLong(9, entity.getCaloriesBurned());
        if (entity.getIntensity() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getIntensity());
        }
        if (entity.getOriginalInput() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getOriginalInput());
        }
        statement.bindString(12, entity.getSource());
        if (entity.getSourceId() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getSourceId());
        }
        statement.bindDouble(14, entity.getConfidence());
        statement.bindLong(15, entity.getCreatedAt());
        statement.bindLong(16, entity.getUpdatedAt());
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
        return "UPDATE OR ABORT `exercise_entries` SET `id` = ?,`date` = ?,`time` = ?,`timestamp` = ?,`activityName` = ?,`durationMinutes` = ?,`metValue` = ?,`userWeightKg` = ?,`caloriesBurned` = ?,`intensity` = ?,`originalInput` = ?,`source` = ?,`sourceId` = ?,`confidence` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExerciseEntryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDate());
        statement.bindString(3, entity.getTime());
        statement.bindLong(4, entity.getTimestamp());
        statement.bindString(5, entity.getActivityName());
        statement.bindLong(6, entity.getDurationMinutes());
        statement.bindDouble(7, entity.getMetValue());
        statement.bindDouble(8, entity.getUserWeightKg());
        statement.bindLong(9, entity.getCaloriesBurned());
        if (entity.getIntensity() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getIntensity());
        }
        if (entity.getOriginalInput() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getOriginalInput());
        }
        statement.bindString(12, entity.getSource());
        if (entity.getSourceId() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getSourceId());
        }
        statement.bindDouble(14, entity.getConfidence());
        statement.bindLong(15, entity.getCreatedAt());
        statement.bindLong(16, entity.getUpdatedAt());
        statement.bindLong(17, entity.getId());
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
  public Flow<List<ExerciseEntryEntity>> getEntriesForDate(final String date) {
    final String _sql = "SELECT * FROM exercise_entries WHERE date = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"exercise_entries"}, new Callable<List<ExerciseEntryEntity>>() {
      @Override
      @NonNull
      public List<ExerciseEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfActivityName = CursorUtil.getColumnIndexOrThrow(_cursor, "activityName");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfMetValue = CursorUtil.getColumnIndexOrThrow(_cursor, "metValue");
          final int _cursorIndexOfUserWeightKg = CursorUtil.getColumnIndexOrThrow(_cursor, "userWeightKg");
          final int _cursorIndexOfCaloriesBurned = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesBurned");
          final int _cursorIndexOfIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "intensity");
          final int _cursorIndexOfOriginalInput = CursorUtil.getColumnIndexOrThrow(_cursor, "originalInput");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfSourceId = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceId");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<ExerciseEntryEntity> _result = new ArrayList<ExerciseEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExerciseEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpTime;
            _tmpTime = _cursor.getString(_cursorIndexOfTime);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpActivityName;
            _tmpActivityName = _cursor.getString(_cursorIndexOfActivityName);
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            final float _tmpMetValue;
            _tmpMetValue = _cursor.getFloat(_cursorIndexOfMetValue);
            final float _tmpUserWeightKg;
            _tmpUserWeightKg = _cursor.getFloat(_cursorIndexOfUserWeightKg);
            final int _tmpCaloriesBurned;
            _tmpCaloriesBurned = _cursor.getInt(_cursorIndexOfCaloriesBurned);
            final String _tmpIntensity;
            if (_cursor.isNull(_cursorIndexOfIntensity)) {
              _tmpIntensity = null;
            } else {
              _tmpIntensity = _cursor.getString(_cursorIndexOfIntensity);
            }
            final String _tmpOriginalInput;
            if (_cursor.isNull(_cursorIndexOfOriginalInput)) {
              _tmpOriginalInput = null;
            } else {
              _tmpOriginalInput = _cursor.getString(_cursorIndexOfOriginalInput);
            }
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpSourceId;
            if (_cursor.isNull(_cursorIndexOfSourceId)) {
              _tmpSourceId = null;
            } else {
              _tmpSourceId = _cursor.getString(_cursorIndexOfSourceId);
            }
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new ExerciseEntryEntity(_tmpId,_tmpDate,_tmpTime,_tmpTimestamp,_tmpActivityName,_tmpDurationMinutes,_tmpMetValue,_tmpUserWeightKg,_tmpCaloriesBurned,_tmpIntensity,_tmpOriginalInput,_tmpSource,_tmpSourceId,_tmpConfidence,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getEntriesForDateOnce(final String date,
      final Continuation<? super List<ExerciseEntryEntity>> $completion) {
    final String _sql = "SELECT * FROM exercise_entries WHERE date = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ExerciseEntryEntity>>() {
      @Override
      @NonNull
      public List<ExerciseEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfActivityName = CursorUtil.getColumnIndexOrThrow(_cursor, "activityName");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfMetValue = CursorUtil.getColumnIndexOrThrow(_cursor, "metValue");
          final int _cursorIndexOfUserWeightKg = CursorUtil.getColumnIndexOrThrow(_cursor, "userWeightKg");
          final int _cursorIndexOfCaloriesBurned = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesBurned");
          final int _cursorIndexOfIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "intensity");
          final int _cursorIndexOfOriginalInput = CursorUtil.getColumnIndexOrThrow(_cursor, "originalInput");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfSourceId = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceId");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<ExerciseEntryEntity> _result = new ArrayList<ExerciseEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExerciseEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpTime;
            _tmpTime = _cursor.getString(_cursorIndexOfTime);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpActivityName;
            _tmpActivityName = _cursor.getString(_cursorIndexOfActivityName);
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            final float _tmpMetValue;
            _tmpMetValue = _cursor.getFloat(_cursorIndexOfMetValue);
            final float _tmpUserWeightKg;
            _tmpUserWeightKg = _cursor.getFloat(_cursorIndexOfUserWeightKg);
            final int _tmpCaloriesBurned;
            _tmpCaloriesBurned = _cursor.getInt(_cursorIndexOfCaloriesBurned);
            final String _tmpIntensity;
            if (_cursor.isNull(_cursorIndexOfIntensity)) {
              _tmpIntensity = null;
            } else {
              _tmpIntensity = _cursor.getString(_cursorIndexOfIntensity);
            }
            final String _tmpOriginalInput;
            if (_cursor.isNull(_cursorIndexOfOriginalInput)) {
              _tmpOriginalInput = null;
            } else {
              _tmpOriginalInput = _cursor.getString(_cursorIndexOfOriginalInput);
            }
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpSourceId;
            if (_cursor.isNull(_cursorIndexOfSourceId)) {
              _tmpSourceId = null;
            } else {
              _tmpSourceId = _cursor.getString(_cursorIndexOfSourceId);
            }
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new ExerciseEntryEntity(_tmpId,_tmpDate,_tmpTime,_tmpTimestamp,_tmpActivityName,_tmpDurationMinutes,_tmpMetValue,_tmpUserWeightKg,_tmpCaloriesBurned,_tmpIntensity,_tmpOriginalInput,_tmpSource,_tmpSourceId,_tmpConfidence,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getEntryById(final long id,
      final Continuation<? super ExerciseEntryEntity> $completion) {
    final String _sql = "SELECT * FROM exercise_entries WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ExerciseEntryEntity>() {
      @Override
      @Nullable
      public ExerciseEntryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfActivityName = CursorUtil.getColumnIndexOrThrow(_cursor, "activityName");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfMetValue = CursorUtil.getColumnIndexOrThrow(_cursor, "metValue");
          final int _cursorIndexOfUserWeightKg = CursorUtil.getColumnIndexOrThrow(_cursor, "userWeightKg");
          final int _cursorIndexOfCaloriesBurned = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesBurned");
          final int _cursorIndexOfIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "intensity");
          final int _cursorIndexOfOriginalInput = CursorUtil.getColumnIndexOrThrow(_cursor, "originalInput");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfSourceId = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceId");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final ExerciseEntryEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpTime;
            _tmpTime = _cursor.getString(_cursorIndexOfTime);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpActivityName;
            _tmpActivityName = _cursor.getString(_cursorIndexOfActivityName);
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            final float _tmpMetValue;
            _tmpMetValue = _cursor.getFloat(_cursorIndexOfMetValue);
            final float _tmpUserWeightKg;
            _tmpUserWeightKg = _cursor.getFloat(_cursorIndexOfUserWeightKg);
            final int _tmpCaloriesBurned;
            _tmpCaloriesBurned = _cursor.getInt(_cursorIndexOfCaloriesBurned);
            final String _tmpIntensity;
            if (_cursor.isNull(_cursorIndexOfIntensity)) {
              _tmpIntensity = null;
            } else {
              _tmpIntensity = _cursor.getString(_cursorIndexOfIntensity);
            }
            final String _tmpOriginalInput;
            if (_cursor.isNull(_cursorIndexOfOriginalInput)) {
              _tmpOriginalInput = null;
            } else {
              _tmpOriginalInput = _cursor.getString(_cursorIndexOfOriginalInput);
            }
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpSourceId;
            if (_cursor.isNull(_cursorIndexOfSourceId)) {
              _tmpSourceId = null;
            } else {
              _tmpSourceId = _cursor.getString(_cursorIndexOfSourceId);
            }
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new ExerciseEntryEntity(_tmpId,_tmpDate,_tmpTime,_tmpTimestamp,_tmpActivityName,_tmpDurationMinutes,_tmpMetValue,_tmpUserWeightKg,_tmpCaloriesBurned,_tmpIntensity,_tmpOriginalInput,_tmpSource,_tmpSourceId,_tmpConfidence,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<ExerciseEntryEntity> getEntryByIdFlow(final long id) {
    final String _sql = "SELECT * FROM exercise_entries WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"exercise_entries"}, new Callable<ExerciseEntryEntity>() {
      @Override
      @Nullable
      public ExerciseEntryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfActivityName = CursorUtil.getColumnIndexOrThrow(_cursor, "activityName");
          final int _cursorIndexOfDurationMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMinutes");
          final int _cursorIndexOfMetValue = CursorUtil.getColumnIndexOrThrow(_cursor, "metValue");
          final int _cursorIndexOfUserWeightKg = CursorUtil.getColumnIndexOrThrow(_cursor, "userWeightKg");
          final int _cursorIndexOfCaloriesBurned = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesBurned");
          final int _cursorIndexOfIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "intensity");
          final int _cursorIndexOfOriginalInput = CursorUtil.getColumnIndexOrThrow(_cursor, "originalInput");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfSourceId = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceId");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final ExerciseEntryEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpTime;
            _tmpTime = _cursor.getString(_cursorIndexOfTime);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpActivityName;
            _tmpActivityName = _cursor.getString(_cursorIndexOfActivityName);
            final int _tmpDurationMinutes;
            _tmpDurationMinutes = _cursor.getInt(_cursorIndexOfDurationMinutes);
            final float _tmpMetValue;
            _tmpMetValue = _cursor.getFloat(_cursorIndexOfMetValue);
            final float _tmpUserWeightKg;
            _tmpUserWeightKg = _cursor.getFloat(_cursorIndexOfUserWeightKg);
            final int _tmpCaloriesBurned;
            _tmpCaloriesBurned = _cursor.getInt(_cursorIndexOfCaloriesBurned);
            final String _tmpIntensity;
            if (_cursor.isNull(_cursorIndexOfIntensity)) {
              _tmpIntensity = null;
            } else {
              _tmpIntensity = _cursor.getString(_cursorIndexOfIntensity);
            }
            final String _tmpOriginalInput;
            if (_cursor.isNull(_cursorIndexOfOriginalInput)) {
              _tmpOriginalInput = null;
            } else {
              _tmpOriginalInput = _cursor.getString(_cursorIndexOfOriginalInput);
            }
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpSourceId;
            if (_cursor.isNull(_cursorIndexOfSourceId)) {
              _tmpSourceId = null;
            } else {
              _tmpSourceId = _cursor.getString(_cursorIndexOfSourceId);
            }
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new ExerciseEntryEntity(_tmpId,_tmpDate,_tmpTime,_tmpTimestamp,_tmpActivityName,_tmpDurationMinutes,_tmpMetValue,_tmpUserWeightKg,_tmpCaloriesBurned,_tmpIntensity,_tmpOriginalInput,_tmpSource,_tmpSourceId,_tmpConfidence,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<Integer> getTotalCaloriesBurnedForDate(final String date) {
    final String _sql = "SELECT SUM(caloriesBurned) FROM exercise_entries WHERE date = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"exercise_entries"}, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
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
  public Flow<Integer> getTotalCaloriesBurnedBetween(final String startDate, final String endDate) {
    final String _sql = "SELECT SUM(caloriesBurned) FROM exercise_entries WHERE date >= ? AND date <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindString(_argIndex, endDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"exercise_entries"}, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
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
}
