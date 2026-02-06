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
import com.tracky.app.data.local.entity.DailyGoalEntity;
import java.lang.Boolean;
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
public final class DailyGoalDao_Impl implements DailyGoalDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DailyGoalEntity> __insertionAdapterOfDailyGoalEntity;

  private final EntityDeletionOrUpdateAdapter<DailyGoalEntity> __updateAdapterOfDailyGoalEntity;

  private final SharedSQLiteStatement __preparedStmtOfDelete;

  public DailyGoalDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDailyGoalEntity = new EntityInsertionAdapter<DailyGoalEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `daily_goals` (`id`,`effectiveFromDate`,`calorieGoalKcal`,`carbsPct`,`proteinPct`,`fatPct`,`carbsTargetG`,`proteinTargetG`,`fatTargetG`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyGoalEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getEffectiveFromDate());
        statement.bindDouble(3, entity.getCalorieGoalKcal());
        statement.bindLong(4, entity.getCarbsPct());
        statement.bindLong(5, entity.getProteinPct());
        statement.bindLong(6, entity.getFatPct());
        statement.bindDouble(7, entity.getCarbsTargetG());
        statement.bindDouble(8, entity.getProteinTargetG());
        statement.bindDouble(9, entity.getFatTargetG());
        statement.bindLong(10, entity.getCreatedAt());
      }
    };
    this.__updateAdapterOfDailyGoalEntity = new EntityDeletionOrUpdateAdapter<DailyGoalEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `daily_goals` SET `id` = ?,`effectiveFromDate` = ?,`calorieGoalKcal` = ?,`carbsPct` = ?,`proteinPct` = ?,`fatPct` = ?,`carbsTargetG` = ?,`proteinTargetG` = ?,`fatTargetG` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DailyGoalEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getEffectiveFromDate());
        statement.bindDouble(3, entity.getCalorieGoalKcal());
        statement.bindLong(4, entity.getCarbsPct());
        statement.bindLong(5, entity.getProteinPct());
        statement.bindLong(6, entity.getFatPct());
        statement.bindDouble(7, entity.getCarbsTargetG());
        statement.bindDouble(8, entity.getProteinTargetG());
        statement.bindDouble(9, entity.getFatTargetG());
        statement.bindLong(10, entity.getCreatedAt());
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM daily_goals WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final DailyGoalEntity goal, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfDailyGoalEntity.insertAndReturnId(goal);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final DailyGoalEntity goal, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDailyGoalEntity.handle(goal);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDelete.acquire();
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
          __preparedStmtOfDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<DailyGoalEntity> getCurrentGoal(final String date) {
    final String _sql = "\n"
            + "        SELECT * FROM daily_goals \n"
            + "        WHERE effectiveFromDate <= ? \n"
            + "        ORDER BY effectiveFromDate DESC \n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_goals"}, new Callable<DailyGoalEntity>() {
      @Override
      @Nullable
      public DailyGoalEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEffectiveFromDate = CursorUtil.getColumnIndexOrThrow(_cursor, "effectiveFromDate");
          final int _cursorIndexOfCalorieGoalKcal = CursorUtil.getColumnIndexOrThrow(_cursor, "calorieGoalKcal");
          final int _cursorIndexOfCarbsPct = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsPct");
          final int _cursorIndexOfProteinPct = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinPct");
          final int _cursorIndexOfFatPct = CursorUtil.getColumnIndexOrThrow(_cursor, "fatPct");
          final int _cursorIndexOfCarbsTargetG = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsTargetG");
          final int _cursorIndexOfProteinTargetG = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinTargetG");
          final int _cursorIndexOfFatTargetG = CursorUtil.getColumnIndexOrThrow(_cursor, "fatTargetG");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final DailyGoalEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpEffectiveFromDate;
            _tmpEffectiveFromDate = _cursor.getString(_cursorIndexOfEffectiveFromDate);
            final float _tmpCalorieGoalKcal;
            _tmpCalorieGoalKcal = _cursor.getFloat(_cursorIndexOfCalorieGoalKcal);
            final int _tmpCarbsPct;
            _tmpCarbsPct = _cursor.getInt(_cursorIndexOfCarbsPct);
            final int _tmpProteinPct;
            _tmpProteinPct = _cursor.getInt(_cursorIndexOfProteinPct);
            final int _tmpFatPct;
            _tmpFatPct = _cursor.getInt(_cursorIndexOfFatPct);
            final float _tmpCarbsTargetG;
            _tmpCarbsTargetG = _cursor.getFloat(_cursorIndexOfCarbsTargetG);
            final float _tmpProteinTargetG;
            _tmpProteinTargetG = _cursor.getFloat(_cursorIndexOfProteinTargetG);
            final float _tmpFatTargetG;
            _tmpFatTargetG = _cursor.getFloat(_cursorIndexOfFatTargetG);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new DailyGoalEntity(_tmpId,_tmpEffectiveFromDate,_tmpCalorieGoalKcal,_tmpCarbsPct,_tmpProteinPct,_tmpFatPct,_tmpCarbsTargetG,_tmpProteinTargetG,_tmpFatTargetG,_tmpCreatedAt);
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
  public Object getCurrentGoalOnce(final String date,
      final Continuation<? super DailyGoalEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM daily_goals \n"
            + "        WHERE effectiveFromDate <= ? \n"
            + "        ORDER BY effectiveFromDate DESC \n"
            + "        LIMIT 1\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DailyGoalEntity>() {
      @Override
      @Nullable
      public DailyGoalEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEffectiveFromDate = CursorUtil.getColumnIndexOrThrow(_cursor, "effectiveFromDate");
          final int _cursorIndexOfCalorieGoalKcal = CursorUtil.getColumnIndexOrThrow(_cursor, "calorieGoalKcal");
          final int _cursorIndexOfCarbsPct = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsPct");
          final int _cursorIndexOfProteinPct = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinPct");
          final int _cursorIndexOfFatPct = CursorUtil.getColumnIndexOrThrow(_cursor, "fatPct");
          final int _cursorIndexOfCarbsTargetG = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsTargetG");
          final int _cursorIndexOfProteinTargetG = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinTargetG");
          final int _cursorIndexOfFatTargetG = CursorUtil.getColumnIndexOrThrow(_cursor, "fatTargetG");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final DailyGoalEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpEffectiveFromDate;
            _tmpEffectiveFromDate = _cursor.getString(_cursorIndexOfEffectiveFromDate);
            final float _tmpCalorieGoalKcal;
            _tmpCalorieGoalKcal = _cursor.getFloat(_cursorIndexOfCalorieGoalKcal);
            final int _tmpCarbsPct;
            _tmpCarbsPct = _cursor.getInt(_cursorIndexOfCarbsPct);
            final int _tmpProteinPct;
            _tmpProteinPct = _cursor.getInt(_cursorIndexOfProteinPct);
            final int _tmpFatPct;
            _tmpFatPct = _cursor.getInt(_cursorIndexOfFatPct);
            final float _tmpCarbsTargetG;
            _tmpCarbsTargetG = _cursor.getFloat(_cursorIndexOfCarbsTargetG);
            final float _tmpProteinTargetG;
            _tmpProteinTargetG = _cursor.getFloat(_cursorIndexOfProteinTargetG);
            final float _tmpFatTargetG;
            _tmpFatTargetG = _cursor.getFloat(_cursorIndexOfFatTargetG);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new DailyGoalEntity(_tmpId,_tmpEffectiveFromDate,_tmpCalorieGoalKcal,_tmpCarbsPct,_tmpProteinPct,_tmpFatPct,_tmpCarbsTargetG,_tmpProteinTargetG,_tmpFatTargetG,_tmpCreatedAt);
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
  public Object getGoalForDate(final String date,
      final Continuation<? super DailyGoalEntity> $completion) {
    final String _sql = "SELECT * FROM daily_goals WHERE effectiveFromDate = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DailyGoalEntity>() {
      @Override
      @Nullable
      public DailyGoalEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEffectiveFromDate = CursorUtil.getColumnIndexOrThrow(_cursor, "effectiveFromDate");
          final int _cursorIndexOfCalorieGoalKcal = CursorUtil.getColumnIndexOrThrow(_cursor, "calorieGoalKcal");
          final int _cursorIndexOfCarbsPct = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsPct");
          final int _cursorIndexOfProteinPct = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinPct");
          final int _cursorIndexOfFatPct = CursorUtil.getColumnIndexOrThrow(_cursor, "fatPct");
          final int _cursorIndexOfCarbsTargetG = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsTargetG");
          final int _cursorIndexOfProteinTargetG = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinTargetG");
          final int _cursorIndexOfFatTargetG = CursorUtil.getColumnIndexOrThrow(_cursor, "fatTargetG");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final DailyGoalEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpEffectiveFromDate;
            _tmpEffectiveFromDate = _cursor.getString(_cursorIndexOfEffectiveFromDate);
            final float _tmpCalorieGoalKcal;
            _tmpCalorieGoalKcal = _cursor.getFloat(_cursorIndexOfCalorieGoalKcal);
            final int _tmpCarbsPct;
            _tmpCarbsPct = _cursor.getInt(_cursorIndexOfCarbsPct);
            final int _tmpProteinPct;
            _tmpProteinPct = _cursor.getInt(_cursorIndexOfProteinPct);
            final int _tmpFatPct;
            _tmpFatPct = _cursor.getInt(_cursorIndexOfFatPct);
            final float _tmpCarbsTargetG;
            _tmpCarbsTargetG = _cursor.getFloat(_cursorIndexOfCarbsTargetG);
            final float _tmpProteinTargetG;
            _tmpProteinTargetG = _cursor.getFloat(_cursorIndexOfProteinTargetG);
            final float _tmpFatTargetG;
            _tmpFatTargetG = _cursor.getFloat(_cursorIndexOfFatTargetG);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new DailyGoalEntity(_tmpId,_tmpEffectiveFromDate,_tmpCalorieGoalKcal,_tmpCarbsPct,_tmpProteinPct,_tmpFatPct,_tmpCarbsTargetG,_tmpProteinTargetG,_tmpFatTargetG,_tmpCreatedAt);
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
  public Flow<List<DailyGoalEntity>> getAllGoals() {
    final String _sql = "SELECT * FROM daily_goals ORDER BY effectiveFromDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"daily_goals"}, new Callable<List<DailyGoalEntity>>() {
      @Override
      @NonNull
      public List<DailyGoalEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfEffectiveFromDate = CursorUtil.getColumnIndexOrThrow(_cursor, "effectiveFromDate");
          final int _cursorIndexOfCalorieGoalKcal = CursorUtil.getColumnIndexOrThrow(_cursor, "calorieGoalKcal");
          final int _cursorIndexOfCarbsPct = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsPct");
          final int _cursorIndexOfProteinPct = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinPct");
          final int _cursorIndexOfFatPct = CursorUtil.getColumnIndexOrThrow(_cursor, "fatPct");
          final int _cursorIndexOfCarbsTargetG = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsTargetG");
          final int _cursorIndexOfProteinTargetG = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinTargetG");
          final int _cursorIndexOfFatTargetG = CursorUtil.getColumnIndexOrThrow(_cursor, "fatTargetG");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<DailyGoalEntity> _result = new ArrayList<DailyGoalEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyGoalEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpEffectiveFromDate;
            _tmpEffectiveFromDate = _cursor.getString(_cursorIndexOfEffectiveFromDate);
            final float _tmpCalorieGoalKcal;
            _tmpCalorieGoalKcal = _cursor.getFloat(_cursorIndexOfCalorieGoalKcal);
            final int _tmpCarbsPct;
            _tmpCarbsPct = _cursor.getInt(_cursorIndexOfCarbsPct);
            final int _tmpProteinPct;
            _tmpProteinPct = _cursor.getInt(_cursorIndexOfProteinPct);
            final int _tmpFatPct;
            _tmpFatPct = _cursor.getInt(_cursorIndexOfFatPct);
            final float _tmpCarbsTargetG;
            _tmpCarbsTargetG = _cursor.getFloat(_cursorIndexOfCarbsTargetG);
            final float _tmpProteinTargetG;
            _tmpProteinTargetG = _cursor.getFloat(_cursorIndexOfProteinTargetG);
            final float _tmpFatTargetG;
            _tmpFatTargetG = _cursor.getFloat(_cursorIndexOfFatTargetG);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new DailyGoalEntity(_tmpId,_tmpEffectiveFromDate,_tmpCalorieGoalKcal,_tmpCarbsPct,_tmpProteinPct,_tmpFatPct,_tmpCarbsTargetG,_tmpProteinTargetG,_tmpFatTargetG,_tmpCreatedAt);
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
  public Object hasAnyGoals(final Continuation<? super Boolean> $completion) {
    final String _sql = "SELECT EXISTS(SELECT 1 FROM daily_goals)";
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
