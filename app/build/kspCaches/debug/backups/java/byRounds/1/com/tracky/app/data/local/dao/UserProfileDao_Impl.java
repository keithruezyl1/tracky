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
import com.tracky.app.data.local.entity.UserProfileEntity;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UserProfileDao_Impl implements UserProfileDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserProfileEntity> __insertionAdapterOfUserProfileEntity;

  private final EntityDeletionOrUpdateAdapter<UserProfileEntity> __updateAdapterOfUserProfileEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateWeight;

  public UserProfileDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserProfileEntity = new EntityInsertionAdapter<UserProfileEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `user_profile` (`id`,`heightCm`,`currentWeightKg`,`targetWeightKg`,`unitPreference`,`timezone`,`bmi`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserProfileEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindDouble(2, entity.getHeightCm());
        statement.bindDouble(3, entity.getCurrentWeightKg());
        statement.bindDouble(4, entity.getTargetWeightKg());
        statement.bindString(5, entity.getUnitPreference());
        statement.bindString(6, entity.getTimezone());
        statement.bindDouble(7, entity.getBmi());
        statement.bindLong(8, entity.getCreatedAt());
        statement.bindLong(9, entity.getUpdatedAt());
      }
    };
    this.__updateAdapterOfUserProfileEntity = new EntityDeletionOrUpdateAdapter<UserProfileEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `user_profile` SET `id` = ?,`heightCm` = ?,`currentWeightKg` = ?,`targetWeightKg` = ?,`unitPreference` = ?,`timezone` = ?,`bmi` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserProfileEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindDouble(2, entity.getHeightCm());
        statement.bindDouble(3, entity.getCurrentWeightKg());
        statement.bindDouble(4, entity.getTargetWeightKg());
        statement.bindString(5, entity.getUnitPreference());
        statement.bindString(6, entity.getTimezone());
        statement.bindDouble(7, entity.getBmi());
        statement.bindLong(8, entity.getCreatedAt());
        statement.bindLong(9, entity.getUpdatedAt());
        statement.bindLong(10, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateWeight = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_profile SET currentWeightKg = ?, bmi = ? WHERE id = 1";
        return _query;
      }
    };
  }

  @Override
  public Object insertOrUpdate(final UserProfileEntity profile,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUserProfileEntity.insert(profile);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final UserProfileEntity profile,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfUserProfileEntity.handle(profile);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateWeight(final float weight, final float bmi,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateWeight.acquire();
        int _argIndex = 1;
        _stmt.bindDouble(_argIndex, weight);
        _argIndex = 2;
        _stmt.bindDouble(_argIndex, bmi);
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
          __preparedStmtOfUpdateWeight.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<UserProfileEntity> getProfile() {
    final String _sql = "SELECT * FROM user_profile WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"user_profile"}, new Callable<UserProfileEntity>() {
      @Override
      @Nullable
      public UserProfileEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHeightCm = CursorUtil.getColumnIndexOrThrow(_cursor, "heightCm");
          final int _cursorIndexOfCurrentWeightKg = CursorUtil.getColumnIndexOrThrow(_cursor, "currentWeightKg");
          final int _cursorIndexOfTargetWeightKg = CursorUtil.getColumnIndexOrThrow(_cursor, "targetWeightKg");
          final int _cursorIndexOfUnitPreference = CursorUtil.getColumnIndexOrThrow(_cursor, "unitPreference");
          final int _cursorIndexOfTimezone = CursorUtil.getColumnIndexOrThrow(_cursor, "timezone");
          final int _cursorIndexOfBmi = CursorUtil.getColumnIndexOrThrow(_cursor, "bmi");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final UserProfileEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final float _tmpHeightCm;
            _tmpHeightCm = _cursor.getFloat(_cursorIndexOfHeightCm);
            final float _tmpCurrentWeightKg;
            _tmpCurrentWeightKg = _cursor.getFloat(_cursorIndexOfCurrentWeightKg);
            final float _tmpTargetWeightKg;
            _tmpTargetWeightKg = _cursor.getFloat(_cursorIndexOfTargetWeightKg);
            final String _tmpUnitPreference;
            _tmpUnitPreference = _cursor.getString(_cursorIndexOfUnitPreference);
            final String _tmpTimezone;
            _tmpTimezone = _cursor.getString(_cursorIndexOfTimezone);
            final float _tmpBmi;
            _tmpBmi = _cursor.getFloat(_cursorIndexOfBmi);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new UserProfileEntity(_tmpId,_tmpHeightCm,_tmpCurrentWeightKg,_tmpTargetWeightKg,_tmpUnitPreference,_tmpTimezone,_tmpBmi,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getProfileOnce(final Continuation<? super UserProfileEntity> $completion) {
    final String _sql = "SELECT * FROM user_profile WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserProfileEntity>() {
      @Override
      @Nullable
      public UserProfileEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfHeightCm = CursorUtil.getColumnIndexOrThrow(_cursor, "heightCm");
          final int _cursorIndexOfCurrentWeightKg = CursorUtil.getColumnIndexOrThrow(_cursor, "currentWeightKg");
          final int _cursorIndexOfTargetWeightKg = CursorUtil.getColumnIndexOrThrow(_cursor, "targetWeightKg");
          final int _cursorIndexOfUnitPreference = CursorUtil.getColumnIndexOrThrow(_cursor, "unitPreference");
          final int _cursorIndexOfTimezone = CursorUtil.getColumnIndexOrThrow(_cursor, "timezone");
          final int _cursorIndexOfBmi = CursorUtil.getColumnIndexOrThrow(_cursor, "bmi");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final UserProfileEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final float _tmpHeightCm;
            _tmpHeightCm = _cursor.getFloat(_cursorIndexOfHeightCm);
            final float _tmpCurrentWeightKg;
            _tmpCurrentWeightKg = _cursor.getFloat(_cursorIndexOfCurrentWeightKg);
            final float _tmpTargetWeightKg;
            _tmpTargetWeightKg = _cursor.getFloat(_cursorIndexOfTargetWeightKg);
            final String _tmpUnitPreference;
            _tmpUnitPreference = _cursor.getString(_cursorIndexOfUnitPreference);
            final String _tmpTimezone;
            _tmpTimezone = _cursor.getString(_cursorIndexOfTimezone);
            final float _tmpBmi;
            _tmpBmi = _cursor.getFloat(_cursorIndexOfBmi);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new UserProfileEntity(_tmpId,_tmpHeightCm,_tmpCurrentWeightKg,_tmpTargetWeightKg,_tmpUnitPreference,_tmpTimezone,_tmpBmi,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object hasProfile(final Continuation<? super Boolean> $completion) {
    final String _sql = "SELECT EXISTS(SELECT 1 FROM user_profile WHERE id = 1)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Boolean>() {
      @Override
      @NonNull
      public Boolean call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Boolean _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp != 0;
          } else {
            _result = false;
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
