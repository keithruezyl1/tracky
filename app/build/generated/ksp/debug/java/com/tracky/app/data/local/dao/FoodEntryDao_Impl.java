package com.tracky.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.tracky.app.data.local.entity.FoodEntryEntity;
import com.tracky.app.data.local.entity.FoodItemEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Float;
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
public final class FoodEntryDao_Impl implements FoodEntryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FoodEntryEntity> __insertionAdapterOfFoodEntryEntity;

  private final EntityInsertionAdapter<FoodItemEntity> __insertionAdapterOfFoodItemEntity;

  private final EntityDeletionOrUpdateAdapter<FoodEntryEntity> __deletionAdapterOfFoodEntryEntity;

  private final EntityDeletionOrUpdateAdapter<FoodItemEntity> __deletionAdapterOfFoodItemEntity;

  private final EntityDeletionOrUpdateAdapter<FoodEntryEntity> __updateAdapterOfFoodEntryEntity;

  private final EntityDeletionOrUpdateAdapter<FoodItemEntity> __updateAdapterOfFoodItemEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteItemsForEntry;

  public FoodEntryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFoodEntryEntity = new EntityInsertionAdapter<FoodEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `food_entries` (`id`,`date`,`time`,`timestamp`,`totalCalories`,`totalCarbsG`,`totalProteinG`,`totalFatG`,`analysisNarrative`,`photoPath`,`originalInput`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FoodEntryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDate());
        statement.bindString(3, entity.getTime());
        statement.bindLong(4, entity.getTimestamp());
        statement.bindDouble(5, entity.getTotalCalories());
        statement.bindDouble(6, entity.getTotalCarbsG());
        statement.bindDouble(7, entity.getTotalProteinG());
        statement.bindDouble(8, entity.getTotalFatG());
        if (entity.getAnalysisNarrative() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getAnalysisNarrative());
        }
        if (entity.getPhotoPath() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getPhotoPath());
        }
        if (entity.getOriginalInput() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getOriginalInput());
        }
        statement.bindLong(12, entity.getCreatedAt());
        statement.bindLong(13, entity.getUpdatedAt());
      }
    };
    this.__insertionAdapterOfFoodItemEntity = new EntityInsertionAdapter<FoodItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `food_items` (`id`,`foodEntryId`,`name`,`matchedName`,`quantity`,`unit`,`calories`,`carbsG`,`proteinG`,`fatG`,`source`,`sourceId`,`confidence`,`displayOrder`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FoodItemEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getFoodEntryId());
        statement.bindString(3, entity.getName());
        if (entity.getMatchedName() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getMatchedName());
        }
        statement.bindDouble(5, entity.getQuantity());
        statement.bindString(6, entity.getUnit());
        statement.bindDouble(7, entity.getCalories());
        statement.bindDouble(8, entity.getCarbsG());
        statement.bindDouble(9, entity.getProteinG());
        statement.bindDouble(10, entity.getFatG());
        statement.bindString(11, entity.getSource());
        if (entity.getSourceId() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getSourceId());
        }
        statement.bindDouble(13, entity.getConfidence());
        statement.bindLong(14, entity.getDisplayOrder());
      }
    };
    this.__deletionAdapterOfFoodEntryEntity = new EntityDeletionOrUpdateAdapter<FoodEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `food_entries` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FoodEntryEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__deletionAdapterOfFoodItemEntity = new EntityDeletionOrUpdateAdapter<FoodItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `food_items` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FoodItemEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfFoodEntryEntity = new EntityDeletionOrUpdateAdapter<FoodEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `food_entries` SET `id` = ?,`date` = ?,`time` = ?,`timestamp` = ?,`totalCalories` = ?,`totalCarbsG` = ?,`totalProteinG` = ?,`totalFatG` = ?,`analysisNarrative` = ?,`photoPath` = ?,`originalInput` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FoodEntryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDate());
        statement.bindString(3, entity.getTime());
        statement.bindLong(4, entity.getTimestamp());
        statement.bindDouble(5, entity.getTotalCalories());
        statement.bindDouble(6, entity.getTotalCarbsG());
        statement.bindDouble(7, entity.getTotalProteinG());
        statement.bindDouble(8, entity.getTotalFatG());
        if (entity.getAnalysisNarrative() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getAnalysisNarrative());
        }
        if (entity.getPhotoPath() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getPhotoPath());
        }
        if (entity.getOriginalInput() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getOriginalInput());
        }
        statement.bindLong(12, entity.getCreatedAt());
        statement.bindLong(13, entity.getUpdatedAt());
        statement.bindLong(14, entity.getId());
      }
    };
    this.__updateAdapterOfFoodItemEntity = new EntityDeletionOrUpdateAdapter<FoodItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `food_items` SET `id` = ?,`foodEntryId` = ?,`name` = ?,`matchedName` = ?,`quantity` = ?,`unit` = ?,`calories` = ?,`carbsG` = ?,`proteinG` = ?,`fatG` = ?,`source` = ?,`sourceId` = ?,`confidence` = ?,`displayOrder` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FoodItemEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getFoodEntryId());
        statement.bindString(3, entity.getName());
        if (entity.getMatchedName() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getMatchedName());
        }
        statement.bindDouble(5, entity.getQuantity());
        statement.bindString(6, entity.getUnit());
        statement.bindDouble(7, entity.getCalories());
        statement.bindDouble(8, entity.getCarbsG());
        statement.bindDouble(9, entity.getProteinG());
        statement.bindDouble(10, entity.getFatG());
        statement.bindString(11, entity.getSource());
        if (entity.getSourceId() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getSourceId());
        }
        statement.bindDouble(13, entity.getConfidence());
        statement.bindLong(14, entity.getDisplayOrder());
        statement.bindLong(15, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM food_entries WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteItemsForEntry = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM food_items WHERE foodEntryId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final FoodEntryEntity entry, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfFoodEntryEntity.insertAndReturnId(entry);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertItem(final FoodItemEntity item,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfFoodItemEntity.insertAndReturnId(item);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertItems(final List<FoodItemEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfFoodItemEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final FoodEntryEntity entry, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfFoodEntryEntity.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteItem(final FoodItemEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfFoodItemEntity.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final FoodEntryEntity entry, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfFoodEntryEntity.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateItem(final FoodItemEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfFoodItemEntity.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertEntryWithItems(final FoodEntryEntity entry, final List<FoodItemEntity> items,
      final Continuation<? super Long> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> FoodEntryDao.DefaultImpls.insertEntryWithItems(FoodEntryDao_Impl.this, entry, items, __cont), $completion);
  }

  @Override
  public Object deleteEntryWithItems(final long entryId,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> FoodEntryDao.DefaultImpls.deleteEntryWithItems(FoodEntryDao_Impl.this, entryId, __cont), $completion);
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
  public Flow<List<FoodEntryEntity>> getEntriesForDate(final String date) {
    final String _sql = "SELECT * FROM food_entries WHERE date = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"food_entries"}, new Callable<List<FoodEntryEntity>>() {
      @Override
      @NonNull
      public List<FoodEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfTotalCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCalories");
          final int _cursorIndexOfTotalCarbsG = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCarbsG");
          final int _cursorIndexOfTotalProteinG = CursorUtil.getColumnIndexOrThrow(_cursor, "totalProteinG");
          final int _cursorIndexOfTotalFatG = CursorUtil.getColumnIndexOrThrow(_cursor, "totalFatG");
          final int _cursorIndexOfAnalysisNarrative = CursorUtil.getColumnIndexOrThrow(_cursor, "analysisNarrative");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfOriginalInput = CursorUtil.getColumnIndexOrThrow(_cursor, "originalInput");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<FoodEntryEntity> _result = new ArrayList<FoodEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FoodEntryEntity _item;
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
            final float _tmpTotalCarbsG;
            _tmpTotalCarbsG = _cursor.getFloat(_cursorIndexOfTotalCarbsG);
            final float _tmpTotalProteinG;
            _tmpTotalProteinG = _cursor.getFloat(_cursorIndexOfTotalProteinG);
            final float _tmpTotalFatG;
            _tmpTotalFatG = _cursor.getFloat(_cursorIndexOfTotalFatG);
            final String _tmpAnalysisNarrative;
            if (_cursor.isNull(_cursorIndexOfAnalysisNarrative)) {
              _tmpAnalysisNarrative = null;
            } else {
              _tmpAnalysisNarrative = _cursor.getString(_cursorIndexOfAnalysisNarrative);
            }
            final String _tmpPhotoPath;
            if (_cursor.isNull(_cursorIndexOfPhotoPath)) {
              _tmpPhotoPath = null;
            } else {
              _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            }
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
            _item = new FoodEntryEntity(_tmpId,_tmpDate,_tmpTime,_tmpTimestamp,_tmpTotalCalories,_tmpTotalCarbsG,_tmpTotalProteinG,_tmpTotalFatG,_tmpAnalysisNarrative,_tmpPhotoPath,_tmpOriginalInput,_tmpCreatedAt,_tmpUpdatedAt);
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
      final Continuation<? super List<FoodEntryEntity>> $completion) {
    final String _sql = "SELECT * FROM food_entries WHERE date = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FoodEntryEntity>>() {
      @Override
      @NonNull
      public List<FoodEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfTotalCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCalories");
          final int _cursorIndexOfTotalCarbsG = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCarbsG");
          final int _cursorIndexOfTotalProteinG = CursorUtil.getColumnIndexOrThrow(_cursor, "totalProteinG");
          final int _cursorIndexOfTotalFatG = CursorUtil.getColumnIndexOrThrow(_cursor, "totalFatG");
          final int _cursorIndexOfAnalysisNarrative = CursorUtil.getColumnIndexOrThrow(_cursor, "analysisNarrative");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfOriginalInput = CursorUtil.getColumnIndexOrThrow(_cursor, "originalInput");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<FoodEntryEntity> _result = new ArrayList<FoodEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FoodEntryEntity _item;
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
            final float _tmpTotalCarbsG;
            _tmpTotalCarbsG = _cursor.getFloat(_cursorIndexOfTotalCarbsG);
            final float _tmpTotalProteinG;
            _tmpTotalProteinG = _cursor.getFloat(_cursorIndexOfTotalProteinG);
            final float _tmpTotalFatG;
            _tmpTotalFatG = _cursor.getFloat(_cursorIndexOfTotalFatG);
            final String _tmpAnalysisNarrative;
            if (_cursor.isNull(_cursorIndexOfAnalysisNarrative)) {
              _tmpAnalysisNarrative = null;
            } else {
              _tmpAnalysisNarrative = _cursor.getString(_cursorIndexOfAnalysisNarrative);
            }
            final String _tmpPhotoPath;
            if (_cursor.isNull(_cursorIndexOfPhotoPath)) {
              _tmpPhotoPath = null;
            } else {
              _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            }
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
            _item = new FoodEntryEntity(_tmpId,_tmpDate,_tmpTime,_tmpTimestamp,_tmpTotalCalories,_tmpTotalCarbsG,_tmpTotalProteinG,_tmpTotalFatG,_tmpAnalysisNarrative,_tmpPhotoPath,_tmpOriginalInput,_tmpCreatedAt,_tmpUpdatedAt);
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
      final Continuation<? super FoodEntryEntity> $completion) {
    final String _sql = "SELECT * FROM food_entries WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<FoodEntryEntity>() {
      @Override
      @Nullable
      public FoodEntryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfTotalCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCalories");
          final int _cursorIndexOfTotalCarbsG = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCarbsG");
          final int _cursorIndexOfTotalProteinG = CursorUtil.getColumnIndexOrThrow(_cursor, "totalProteinG");
          final int _cursorIndexOfTotalFatG = CursorUtil.getColumnIndexOrThrow(_cursor, "totalFatG");
          final int _cursorIndexOfAnalysisNarrative = CursorUtil.getColumnIndexOrThrow(_cursor, "analysisNarrative");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfOriginalInput = CursorUtil.getColumnIndexOrThrow(_cursor, "originalInput");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final FoodEntryEntity _result;
          if (_cursor.moveToFirst()) {
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
            final float _tmpTotalCarbsG;
            _tmpTotalCarbsG = _cursor.getFloat(_cursorIndexOfTotalCarbsG);
            final float _tmpTotalProteinG;
            _tmpTotalProteinG = _cursor.getFloat(_cursorIndexOfTotalProteinG);
            final float _tmpTotalFatG;
            _tmpTotalFatG = _cursor.getFloat(_cursorIndexOfTotalFatG);
            final String _tmpAnalysisNarrative;
            if (_cursor.isNull(_cursorIndexOfAnalysisNarrative)) {
              _tmpAnalysisNarrative = null;
            } else {
              _tmpAnalysisNarrative = _cursor.getString(_cursorIndexOfAnalysisNarrative);
            }
            final String _tmpPhotoPath;
            if (_cursor.isNull(_cursorIndexOfPhotoPath)) {
              _tmpPhotoPath = null;
            } else {
              _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            }
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
            _result = new FoodEntryEntity(_tmpId,_tmpDate,_tmpTime,_tmpTimestamp,_tmpTotalCalories,_tmpTotalCarbsG,_tmpTotalProteinG,_tmpTotalFatG,_tmpAnalysisNarrative,_tmpPhotoPath,_tmpOriginalInput,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<FoodEntryEntity> getEntryByIdFlow(final long id) {
    final String _sql = "SELECT * FROM food_entries WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"food_entries"}, new Callable<FoodEntryEntity>() {
      @Override
      @Nullable
      public FoodEntryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfTotalCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCalories");
          final int _cursorIndexOfTotalCarbsG = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCarbsG");
          final int _cursorIndexOfTotalProteinG = CursorUtil.getColumnIndexOrThrow(_cursor, "totalProteinG");
          final int _cursorIndexOfTotalFatG = CursorUtil.getColumnIndexOrThrow(_cursor, "totalFatG");
          final int _cursorIndexOfAnalysisNarrative = CursorUtil.getColumnIndexOrThrow(_cursor, "analysisNarrative");
          final int _cursorIndexOfPhotoPath = CursorUtil.getColumnIndexOrThrow(_cursor, "photoPath");
          final int _cursorIndexOfOriginalInput = CursorUtil.getColumnIndexOrThrow(_cursor, "originalInput");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final FoodEntryEntity _result;
          if (_cursor.moveToFirst()) {
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
            final float _tmpTotalCarbsG;
            _tmpTotalCarbsG = _cursor.getFloat(_cursorIndexOfTotalCarbsG);
            final float _tmpTotalProteinG;
            _tmpTotalProteinG = _cursor.getFloat(_cursorIndexOfTotalProteinG);
            final float _tmpTotalFatG;
            _tmpTotalFatG = _cursor.getFloat(_cursorIndexOfTotalFatG);
            final String _tmpAnalysisNarrative;
            if (_cursor.isNull(_cursorIndexOfAnalysisNarrative)) {
              _tmpAnalysisNarrative = null;
            } else {
              _tmpAnalysisNarrative = _cursor.getString(_cursorIndexOfAnalysisNarrative);
            }
            final String _tmpPhotoPath;
            if (_cursor.isNull(_cursorIndexOfPhotoPath)) {
              _tmpPhotoPath = null;
            } else {
              _tmpPhotoPath = _cursor.getString(_cursorIndexOfPhotoPath);
            }
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
            _result = new FoodEntryEntity(_tmpId,_tmpDate,_tmpTime,_tmpTimestamp,_tmpTotalCalories,_tmpTotalCarbsG,_tmpTotalProteinG,_tmpTotalFatG,_tmpAnalysisNarrative,_tmpPhotoPath,_tmpOriginalInput,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<Float> getTotalCaloriesForDate(final String date) {
    final String _sql = "\n"
            + "        SELECT SUM(totalCalories) FROM food_entries \n"
            + "        WHERE date = ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"food_entries"}, new Callable<Float>() {
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
  public Flow<MacroTotals> getMacroTotalsForDate(final String date) {
    final String _sql = "\n"
            + "        SELECT SUM(totalCarbsG) as carbs, SUM(totalProteinG) as protein, SUM(totalFatG) as fat\n"
            + "        FROM food_entries WHERE date = ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"food_entries"}, new Callable<MacroTotals>() {
      @Override
      @Nullable
      public MacroTotals call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCarbs = 0;
          final int _cursorIndexOfProtein = 1;
          final int _cursorIndexOfFat = 2;
          final MacroTotals _result;
          if (_cursor.moveToFirst()) {
            final Float _tmpCarbs;
            if (_cursor.isNull(_cursorIndexOfCarbs)) {
              _tmpCarbs = null;
            } else {
              _tmpCarbs = _cursor.getFloat(_cursorIndexOfCarbs);
            }
            final Float _tmpProtein;
            if (_cursor.isNull(_cursorIndexOfProtein)) {
              _tmpProtein = null;
            } else {
              _tmpProtein = _cursor.getFloat(_cursorIndexOfProtein);
            }
            final Float _tmpFat;
            if (_cursor.isNull(_cursorIndexOfFat)) {
              _tmpFat = null;
            } else {
              _tmpFat = _cursor.getFloat(_cursorIndexOfFat);
            }
            _result = new MacroTotals(_tmpCarbs,_tmpProtein,_tmpFat);
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
  public Flow<Float> getTotalCaloriesBetween(final String startDate, final String endDate) {
    final String _sql = "\n"
            + "        SELECT SUM(totalCalories) FROM food_entries \n"
            + "        WHERE date >= ? AND date <= ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindString(_argIndex, endDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"food_entries"}, new Callable<Float>() {
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
  public Flow<MacroTotals> getMacroTotalsBetween(final String startDate, final String endDate) {
    final String _sql = "\n"
            + "        SELECT SUM(totalCarbsG) as carbs, SUM(totalProteinG) as protein, SUM(totalFatG) as fat\n"
            + "        FROM food_entries WHERE date >= ? AND date <= ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindString(_argIndex, endDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"food_entries"}, new Callable<MacroTotals>() {
      @Override
      @Nullable
      public MacroTotals call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCarbs = 0;
          final int _cursorIndexOfProtein = 1;
          final int _cursorIndexOfFat = 2;
          final MacroTotals _result;
          if (_cursor.moveToFirst()) {
            final Float _tmpCarbs;
            if (_cursor.isNull(_cursorIndexOfCarbs)) {
              _tmpCarbs = null;
            } else {
              _tmpCarbs = _cursor.getFloat(_cursorIndexOfCarbs);
            }
            final Float _tmpProtein;
            if (_cursor.isNull(_cursorIndexOfProtein)) {
              _tmpProtein = null;
            } else {
              _tmpProtein = _cursor.getFloat(_cursorIndexOfProtein);
            }
            final Float _tmpFat;
            if (_cursor.isNull(_cursorIndexOfFat)) {
              _tmpFat = null;
            } else {
              _tmpFat = _cursor.getFloat(_cursorIndexOfFat);
            }
            _result = new MacroTotals(_tmpCarbs,_tmpProtein,_tmpFat);
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
  public Flow<List<FoodItemEntity>> getItemsForEntry(final long entryId) {
    final String _sql = "SELECT * FROM food_items WHERE foodEntryId = ? ORDER BY displayOrder";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, entryId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"food_items"}, new Callable<List<FoodItemEntity>>() {
      @Override
      @NonNull
      public List<FoodItemEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFoodEntryId = CursorUtil.getColumnIndexOrThrow(_cursor, "foodEntryId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfMatchedName = CursorUtil.getColumnIndexOrThrow(_cursor, "matchedName");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "calories");
          final int _cursorIndexOfCarbsG = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsG");
          final int _cursorIndexOfProteinG = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinG");
          final int _cursorIndexOfFatG = CursorUtil.getColumnIndexOrThrow(_cursor, "fatG");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfSourceId = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceId");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfDisplayOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "displayOrder");
          final List<FoodItemEntity> _result = new ArrayList<FoodItemEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FoodItemEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpFoodEntryId;
            _tmpFoodEntryId = _cursor.getLong(_cursorIndexOfFoodEntryId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpMatchedName;
            if (_cursor.isNull(_cursorIndexOfMatchedName)) {
              _tmpMatchedName = null;
            } else {
              _tmpMatchedName = _cursor.getString(_cursorIndexOfMatchedName);
            }
            final float _tmpQuantity;
            _tmpQuantity = _cursor.getFloat(_cursorIndexOfQuantity);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final float _tmpCalories;
            _tmpCalories = _cursor.getFloat(_cursorIndexOfCalories);
            final float _tmpCarbsG;
            _tmpCarbsG = _cursor.getFloat(_cursorIndexOfCarbsG);
            final float _tmpProteinG;
            _tmpProteinG = _cursor.getFloat(_cursorIndexOfProteinG);
            final float _tmpFatG;
            _tmpFatG = _cursor.getFloat(_cursorIndexOfFatG);
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
            final int _tmpDisplayOrder;
            _tmpDisplayOrder = _cursor.getInt(_cursorIndexOfDisplayOrder);
            _item = new FoodItemEntity(_tmpId,_tmpFoodEntryId,_tmpName,_tmpMatchedName,_tmpQuantity,_tmpUnit,_tmpCalories,_tmpCarbsG,_tmpProteinG,_tmpFatG,_tmpSource,_tmpSourceId,_tmpConfidence,_tmpDisplayOrder);
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
  public Object getItemsForEntryOnce(final long entryId,
      final Continuation<? super List<FoodItemEntity>> $completion) {
    final String _sql = "SELECT * FROM food_items WHERE foodEntryId = ? ORDER BY displayOrder";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, entryId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FoodItemEntity>>() {
      @Override
      @NonNull
      public List<FoodItemEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFoodEntryId = CursorUtil.getColumnIndexOrThrow(_cursor, "foodEntryId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfMatchedName = CursorUtil.getColumnIndexOrThrow(_cursor, "matchedName");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "calories");
          final int _cursorIndexOfCarbsG = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsG");
          final int _cursorIndexOfProteinG = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinG");
          final int _cursorIndexOfFatG = CursorUtil.getColumnIndexOrThrow(_cursor, "fatG");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfSourceId = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceId");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfDisplayOrder = CursorUtil.getColumnIndexOrThrow(_cursor, "displayOrder");
          final List<FoodItemEntity> _result = new ArrayList<FoodItemEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FoodItemEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpFoodEntryId;
            _tmpFoodEntryId = _cursor.getLong(_cursorIndexOfFoodEntryId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpMatchedName;
            if (_cursor.isNull(_cursorIndexOfMatchedName)) {
              _tmpMatchedName = null;
            } else {
              _tmpMatchedName = _cursor.getString(_cursorIndexOfMatchedName);
            }
            final float _tmpQuantity;
            _tmpQuantity = _cursor.getFloat(_cursorIndexOfQuantity);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final float _tmpCalories;
            _tmpCalories = _cursor.getFloat(_cursorIndexOfCalories);
            final float _tmpCarbsG;
            _tmpCarbsG = _cursor.getFloat(_cursorIndexOfCarbsG);
            final float _tmpProteinG;
            _tmpProteinG = _cursor.getFloat(_cursorIndexOfProteinG);
            final float _tmpFatG;
            _tmpFatG = _cursor.getFloat(_cursorIndexOfFatG);
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
            final int _tmpDisplayOrder;
            _tmpDisplayOrder = _cursor.getInt(_cursorIndexOfDisplayOrder);
            _item = new FoodItemEntity(_tmpId,_tmpFoodEntryId,_tmpName,_tmpMatchedName,_tmpQuantity,_tmpUnit,_tmpCalories,_tmpCarbsG,_tmpProteinG,_tmpFatG,_tmpSource,_tmpSourceId,_tmpConfidence,_tmpDisplayOrder);
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
  public Object searchUserHistory(final String query, final int limit,
      final Continuation<? super List<FoodItemEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT DISTINCT \n"
            + "            id, foodEntryId, name, matchedName, quantity, unit, \n"
            + "            calories, carbsG, proteinG, fatG, source, sourceId, confidence, displayOrder\n"
            + "        FROM food_items\n"
            + "        WHERE (name LIKE '%' || ? || '%' OR matchedName LIKE '%' || ? || '%')\n"
            + "          AND source != 'unresolved'\n"
            + "        ORDER BY confidence DESC, id DESC\n"
            + "        LIMIT ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    _argIndex = 3;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FoodItemEntity>>() {
      @Override
      @NonNull
      public List<FoodItemEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfFoodEntryId = 1;
          final int _cursorIndexOfName = 2;
          final int _cursorIndexOfMatchedName = 3;
          final int _cursorIndexOfQuantity = 4;
          final int _cursorIndexOfUnit = 5;
          final int _cursorIndexOfCalories = 6;
          final int _cursorIndexOfCarbsG = 7;
          final int _cursorIndexOfProteinG = 8;
          final int _cursorIndexOfFatG = 9;
          final int _cursorIndexOfSource = 10;
          final int _cursorIndexOfSourceId = 11;
          final int _cursorIndexOfConfidence = 12;
          final int _cursorIndexOfDisplayOrder = 13;
          final List<FoodItemEntity> _result = new ArrayList<FoodItemEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FoodItemEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpFoodEntryId;
            _tmpFoodEntryId = _cursor.getLong(_cursorIndexOfFoodEntryId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpMatchedName;
            if (_cursor.isNull(_cursorIndexOfMatchedName)) {
              _tmpMatchedName = null;
            } else {
              _tmpMatchedName = _cursor.getString(_cursorIndexOfMatchedName);
            }
            final float _tmpQuantity;
            _tmpQuantity = _cursor.getFloat(_cursorIndexOfQuantity);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final float _tmpCalories;
            _tmpCalories = _cursor.getFloat(_cursorIndexOfCalories);
            final float _tmpCarbsG;
            _tmpCarbsG = _cursor.getFloat(_cursorIndexOfCarbsG);
            final float _tmpProteinG;
            _tmpProteinG = _cursor.getFloat(_cursorIndexOfProteinG);
            final float _tmpFatG;
            _tmpFatG = _cursor.getFloat(_cursorIndexOfFatG);
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
            final int _tmpDisplayOrder;
            _tmpDisplayOrder = _cursor.getInt(_cursorIndexOfDisplayOrder);
            _item = new FoodItemEntity(_tmpId,_tmpFoodEntryId,_tmpName,_tmpMatchedName,_tmpQuantity,_tmpUnit,_tmpCalories,_tmpCarbsG,_tmpProteinG,_tmpFatG,_tmpSource,_tmpSourceId,_tmpConfidence,_tmpDisplayOrder);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
