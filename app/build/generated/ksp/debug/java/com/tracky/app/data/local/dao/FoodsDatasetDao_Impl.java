package com.tracky.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.tracky.app.data.local.entity.FoodsDatasetEntity;
import com.tracky.app.data.local.entity.SynonymEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Float;
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
public final class FoodsDatasetDao_Impl implements FoodsDatasetDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FoodsDatasetEntity> __insertionAdapterOfFoodsDatasetEntity;

  private final EntityInsertionAdapter<SynonymEntity> __insertionAdapterOfSynonymEntity;

  public FoodsDatasetDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFoodsDatasetEntity = new EntityInsertionAdapter<FoodsDatasetEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `foods_dataset` (`id`,`name`,`category`,`servingSize`,`servingUnit`,`caloriesPerServing`,`carbsPerServingG`,`proteinPerServingG`,`fatPerServingG`,`fiberPerServingG`,`sugarPerServingG`,`sodiumPerServingMg`,`usdaFdcId`,`dataSource`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FoodsDatasetEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        if (entity.getCategory() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getCategory());
        }
        statement.bindDouble(4, entity.getServingSize());
        statement.bindString(5, entity.getServingUnit());
        statement.bindLong(6, entity.getCaloriesPerServing());
        statement.bindDouble(7, entity.getCarbsPerServingG());
        statement.bindDouble(8, entity.getProteinPerServingG());
        statement.bindDouble(9, entity.getFatPerServingG());
        if (entity.getFiberPerServingG() == null) {
          statement.bindNull(10);
        } else {
          statement.bindDouble(10, entity.getFiberPerServingG());
        }
        if (entity.getSugarPerServingG() == null) {
          statement.bindNull(11);
        } else {
          statement.bindDouble(11, entity.getSugarPerServingG());
        }
        if (entity.getSodiumPerServingMg() == null) {
          statement.bindNull(12);
        } else {
          statement.bindDouble(12, entity.getSodiumPerServingMg());
        }
        if (entity.getUsdaFdcId() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getUsdaFdcId());
        }
        statement.bindString(14, entity.getDataSource());
      }
    };
    this.__insertionAdapterOfSynonymEntity = new EntityInsertionAdapter<SynonymEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `food_synonyms` (`id`,`synonym`,`foodDatasetId`) VALUES (nullif(?, 0),?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SynonymEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getSynonym());
        statement.bindLong(3, entity.getFoodDatasetId());
      }
    };
  }

  @Override
  public Object insert(final FoodsDatasetEntity food,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfFoodsDatasetEntity.insertAndReturnId(food);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<FoodsDatasetEntity> foods,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfFoodsDatasetEntity.insert(foods);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertSynonym(final SynonymEntity synonym,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSynonymEntity.insertAndReturnId(synonym);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertSynonyms(final List<SynonymEntity> synonyms,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSynonymEntity.insert(synonyms);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object searchFoods(final String query, final int limit,
      final Continuation<? super List<FoodsDatasetEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT foods_dataset.* FROM foods_dataset\n"
            + "        JOIN foods_fts ON foods_dataset.rowid = foods_fts.rowid\n"
            + "        WHERE foods_fts MATCH ? || '*'\n"
            + "        LIMIT ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FoodsDatasetEntity>>() {
      @Override
      @NonNull
      public List<FoodsDatasetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfServingSize = CursorUtil.getColumnIndexOrThrow(_cursor, "servingSize");
          final int _cursorIndexOfServingUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "servingUnit");
          final int _cursorIndexOfCaloriesPerServing = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesPerServing");
          final int _cursorIndexOfCarbsPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsPerServingG");
          final int _cursorIndexOfProteinPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinPerServingG");
          final int _cursorIndexOfFatPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "fatPerServingG");
          final int _cursorIndexOfFiberPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "fiberPerServingG");
          final int _cursorIndexOfSugarPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "sugarPerServingG");
          final int _cursorIndexOfSodiumPerServingMg = CursorUtil.getColumnIndexOrThrow(_cursor, "sodiumPerServingMg");
          final int _cursorIndexOfUsdaFdcId = CursorUtil.getColumnIndexOrThrow(_cursor, "usdaFdcId");
          final int _cursorIndexOfDataSource = CursorUtil.getColumnIndexOrThrow(_cursor, "dataSource");
          final List<FoodsDatasetEntity> _result = new ArrayList<FoodsDatasetEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FoodsDatasetEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final float _tmpServingSize;
            _tmpServingSize = _cursor.getFloat(_cursorIndexOfServingSize);
            final String _tmpServingUnit;
            _tmpServingUnit = _cursor.getString(_cursorIndexOfServingUnit);
            final int _tmpCaloriesPerServing;
            _tmpCaloriesPerServing = _cursor.getInt(_cursorIndexOfCaloriesPerServing);
            final float _tmpCarbsPerServingG;
            _tmpCarbsPerServingG = _cursor.getFloat(_cursorIndexOfCarbsPerServingG);
            final float _tmpProteinPerServingG;
            _tmpProteinPerServingG = _cursor.getFloat(_cursorIndexOfProteinPerServingG);
            final float _tmpFatPerServingG;
            _tmpFatPerServingG = _cursor.getFloat(_cursorIndexOfFatPerServingG);
            final Float _tmpFiberPerServingG;
            if (_cursor.isNull(_cursorIndexOfFiberPerServingG)) {
              _tmpFiberPerServingG = null;
            } else {
              _tmpFiberPerServingG = _cursor.getFloat(_cursorIndexOfFiberPerServingG);
            }
            final Float _tmpSugarPerServingG;
            if (_cursor.isNull(_cursorIndexOfSugarPerServingG)) {
              _tmpSugarPerServingG = null;
            } else {
              _tmpSugarPerServingG = _cursor.getFloat(_cursorIndexOfSugarPerServingG);
            }
            final Float _tmpSodiumPerServingMg;
            if (_cursor.isNull(_cursorIndexOfSodiumPerServingMg)) {
              _tmpSodiumPerServingMg = null;
            } else {
              _tmpSodiumPerServingMg = _cursor.getFloat(_cursorIndexOfSodiumPerServingMg);
            }
            final Integer _tmpUsdaFdcId;
            if (_cursor.isNull(_cursorIndexOfUsdaFdcId)) {
              _tmpUsdaFdcId = null;
            } else {
              _tmpUsdaFdcId = _cursor.getInt(_cursorIndexOfUsdaFdcId);
            }
            final String _tmpDataSource;
            _tmpDataSource = _cursor.getString(_cursorIndexOfDataSource);
            _item = new FoodsDatasetEntity(_tmpId,_tmpName,_tmpCategory,_tmpServingSize,_tmpServingUnit,_tmpCaloriesPerServing,_tmpCarbsPerServingG,_tmpProteinPerServingG,_tmpFatPerServingG,_tmpFiberPerServingG,_tmpSugarPerServingG,_tmpSodiumPerServingMg,_tmpUsdaFdcId,_tmpDataSource);
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
  public Object searchFoodsInCategory(final String query, final String category, final int limit,
      final Continuation<? super List<FoodsDatasetEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT foods_dataset.* FROM foods_dataset\n"
            + "        JOIN foods_fts ON foods_dataset.rowid = foods_fts.rowid\n"
            + "        WHERE foods_fts MATCH ? || '*' AND foods_dataset.category = ?\n"
            + "        LIMIT ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, category);
    _argIndex = 3;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FoodsDatasetEntity>>() {
      @Override
      @NonNull
      public List<FoodsDatasetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfServingSize = CursorUtil.getColumnIndexOrThrow(_cursor, "servingSize");
          final int _cursorIndexOfServingUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "servingUnit");
          final int _cursorIndexOfCaloriesPerServing = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesPerServing");
          final int _cursorIndexOfCarbsPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsPerServingG");
          final int _cursorIndexOfProteinPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinPerServingG");
          final int _cursorIndexOfFatPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "fatPerServingG");
          final int _cursorIndexOfFiberPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "fiberPerServingG");
          final int _cursorIndexOfSugarPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "sugarPerServingG");
          final int _cursorIndexOfSodiumPerServingMg = CursorUtil.getColumnIndexOrThrow(_cursor, "sodiumPerServingMg");
          final int _cursorIndexOfUsdaFdcId = CursorUtil.getColumnIndexOrThrow(_cursor, "usdaFdcId");
          final int _cursorIndexOfDataSource = CursorUtil.getColumnIndexOrThrow(_cursor, "dataSource");
          final List<FoodsDatasetEntity> _result = new ArrayList<FoodsDatasetEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FoodsDatasetEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final float _tmpServingSize;
            _tmpServingSize = _cursor.getFloat(_cursorIndexOfServingSize);
            final String _tmpServingUnit;
            _tmpServingUnit = _cursor.getString(_cursorIndexOfServingUnit);
            final int _tmpCaloriesPerServing;
            _tmpCaloriesPerServing = _cursor.getInt(_cursorIndexOfCaloriesPerServing);
            final float _tmpCarbsPerServingG;
            _tmpCarbsPerServingG = _cursor.getFloat(_cursorIndexOfCarbsPerServingG);
            final float _tmpProteinPerServingG;
            _tmpProteinPerServingG = _cursor.getFloat(_cursorIndexOfProteinPerServingG);
            final float _tmpFatPerServingG;
            _tmpFatPerServingG = _cursor.getFloat(_cursorIndexOfFatPerServingG);
            final Float _tmpFiberPerServingG;
            if (_cursor.isNull(_cursorIndexOfFiberPerServingG)) {
              _tmpFiberPerServingG = null;
            } else {
              _tmpFiberPerServingG = _cursor.getFloat(_cursorIndexOfFiberPerServingG);
            }
            final Float _tmpSugarPerServingG;
            if (_cursor.isNull(_cursorIndexOfSugarPerServingG)) {
              _tmpSugarPerServingG = null;
            } else {
              _tmpSugarPerServingG = _cursor.getFloat(_cursorIndexOfSugarPerServingG);
            }
            final Float _tmpSodiumPerServingMg;
            if (_cursor.isNull(_cursorIndexOfSodiumPerServingMg)) {
              _tmpSodiumPerServingMg = null;
            } else {
              _tmpSodiumPerServingMg = _cursor.getFloat(_cursorIndexOfSodiumPerServingMg);
            }
            final Integer _tmpUsdaFdcId;
            if (_cursor.isNull(_cursorIndexOfUsdaFdcId)) {
              _tmpUsdaFdcId = null;
            } else {
              _tmpUsdaFdcId = _cursor.getInt(_cursorIndexOfUsdaFdcId);
            }
            final String _tmpDataSource;
            _tmpDataSource = _cursor.getString(_cursorIndexOfDataSource);
            _item = new FoodsDatasetEntity(_tmpId,_tmpName,_tmpCategory,_tmpServingSize,_tmpServingUnit,_tmpCaloriesPerServing,_tmpCarbsPerServingG,_tmpProteinPerServingG,_tmpFatPerServingG,_tmpFiberPerServingG,_tmpSugarPerServingG,_tmpSodiumPerServingMg,_tmpUsdaFdcId,_tmpDataSource);
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
  public Object getFoodById(final long id,
      final Continuation<? super FoodsDatasetEntity> $completion) {
    final String _sql = "SELECT * FROM foods_dataset WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<FoodsDatasetEntity>() {
      @Override
      @Nullable
      public FoodsDatasetEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfServingSize = CursorUtil.getColumnIndexOrThrow(_cursor, "servingSize");
          final int _cursorIndexOfServingUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "servingUnit");
          final int _cursorIndexOfCaloriesPerServing = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesPerServing");
          final int _cursorIndexOfCarbsPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsPerServingG");
          final int _cursorIndexOfProteinPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinPerServingG");
          final int _cursorIndexOfFatPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "fatPerServingG");
          final int _cursorIndexOfFiberPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "fiberPerServingG");
          final int _cursorIndexOfSugarPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "sugarPerServingG");
          final int _cursorIndexOfSodiumPerServingMg = CursorUtil.getColumnIndexOrThrow(_cursor, "sodiumPerServingMg");
          final int _cursorIndexOfUsdaFdcId = CursorUtil.getColumnIndexOrThrow(_cursor, "usdaFdcId");
          final int _cursorIndexOfDataSource = CursorUtil.getColumnIndexOrThrow(_cursor, "dataSource");
          final FoodsDatasetEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final float _tmpServingSize;
            _tmpServingSize = _cursor.getFloat(_cursorIndexOfServingSize);
            final String _tmpServingUnit;
            _tmpServingUnit = _cursor.getString(_cursorIndexOfServingUnit);
            final int _tmpCaloriesPerServing;
            _tmpCaloriesPerServing = _cursor.getInt(_cursorIndexOfCaloriesPerServing);
            final float _tmpCarbsPerServingG;
            _tmpCarbsPerServingG = _cursor.getFloat(_cursorIndexOfCarbsPerServingG);
            final float _tmpProteinPerServingG;
            _tmpProteinPerServingG = _cursor.getFloat(_cursorIndexOfProteinPerServingG);
            final float _tmpFatPerServingG;
            _tmpFatPerServingG = _cursor.getFloat(_cursorIndexOfFatPerServingG);
            final Float _tmpFiberPerServingG;
            if (_cursor.isNull(_cursorIndexOfFiberPerServingG)) {
              _tmpFiberPerServingG = null;
            } else {
              _tmpFiberPerServingG = _cursor.getFloat(_cursorIndexOfFiberPerServingG);
            }
            final Float _tmpSugarPerServingG;
            if (_cursor.isNull(_cursorIndexOfSugarPerServingG)) {
              _tmpSugarPerServingG = null;
            } else {
              _tmpSugarPerServingG = _cursor.getFloat(_cursorIndexOfSugarPerServingG);
            }
            final Float _tmpSodiumPerServingMg;
            if (_cursor.isNull(_cursorIndexOfSodiumPerServingMg)) {
              _tmpSodiumPerServingMg = null;
            } else {
              _tmpSodiumPerServingMg = _cursor.getFloat(_cursorIndexOfSodiumPerServingMg);
            }
            final Integer _tmpUsdaFdcId;
            if (_cursor.isNull(_cursorIndexOfUsdaFdcId)) {
              _tmpUsdaFdcId = null;
            } else {
              _tmpUsdaFdcId = _cursor.getInt(_cursorIndexOfUsdaFdcId);
            }
            final String _tmpDataSource;
            _tmpDataSource = _cursor.getString(_cursorIndexOfDataSource);
            _result = new FoodsDatasetEntity(_tmpId,_tmpName,_tmpCategory,_tmpServingSize,_tmpServingUnit,_tmpCaloriesPerServing,_tmpCarbsPerServingG,_tmpProteinPerServingG,_tmpFatPerServingG,_tmpFiberPerServingG,_tmpSugarPerServingG,_tmpSodiumPerServingMg,_tmpUsdaFdcId,_tmpDataSource);
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
  public Object getFoodByFdcId(final int fdcId,
      final Continuation<? super FoodsDatasetEntity> $completion) {
    final String _sql = "SELECT * FROM foods_dataset WHERE usdaFdcId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, fdcId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<FoodsDatasetEntity>() {
      @Override
      @Nullable
      public FoodsDatasetEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfServingSize = CursorUtil.getColumnIndexOrThrow(_cursor, "servingSize");
          final int _cursorIndexOfServingUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "servingUnit");
          final int _cursorIndexOfCaloriesPerServing = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesPerServing");
          final int _cursorIndexOfCarbsPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsPerServingG");
          final int _cursorIndexOfProteinPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinPerServingG");
          final int _cursorIndexOfFatPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "fatPerServingG");
          final int _cursorIndexOfFiberPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "fiberPerServingG");
          final int _cursorIndexOfSugarPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "sugarPerServingG");
          final int _cursorIndexOfSodiumPerServingMg = CursorUtil.getColumnIndexOrThrow(_cursor, "sodiumPerServingMg");
          final int _cursorIndexOfUsdaFdcId = CursorUtil.getColumnIndexOrThrow(_cursor, "usdaFdcId");
          final int _cursorIndexOfDataSource = CursorUtil.getColumnIndexOrThrow(_cursor, "dataSource");
          final FoodsDatasetEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final float _tmpServingSize;
            _tmpServingSize = _cursor.getFloat(_cursorIndexOfServingSize);
            final String _tmpServingUnit;
            _tmpServingUnit = _cursor.getString(_cursorIndexOfServingUnit);
            final int _tmpCaloriesPerServing;
            _tmpCaloriesPerServing = _cursor.getInt(_cursorIndexOfCaloriesPerServing);
            final float _tmpCarbsPerServingG;
            _tmpCarbsPerServingG = _cursor.getFloat(_cursorIndexOfCarbsPerServingG);
            final float _tmpProteinPerServingG;
            _tmpProteinPerServingG = _cursor.getFloat(_cursorIndexOfProteinPerServingG);
            final float _tmpFatPerServingG;
            _tmpFatPerServingG = _cursor.getFloat(_cursorIndexOfFatPerServingG);
            final Float _tmpFiberPerServingG;
            if (_cursor.isNull(_cursorIndexOfFiberPerServingG)) {
              _tmpFiberPerServingG = null;
            } else {
              _tmpFiberPerServingG = _cursor.getFloat(_cursorIndexOfFiberPerServingG);
            }
            final Float _tmpSugarPerServingG;
            if (_cursor.isNull(_cursorIndexOfSugarPerServingG)) {
              _tmpSugarPerServingG = null;
            } else {
              _tmpSugarPerServingG = _cursor.getFloat(_cursorIndexOfSugarPerServingG);
            }
            final Float _tmpSodiumPerServingMg;
            if (_cursor.isNull(_cursorIndexOfSodiumPerServingMg)) {
              _tmpSodiumPerServingMg = null;
            } else {
              _tmpSodiumPerServingMg = _cursor.getFloat(_cursorIndexOfSodiumPerServingMg);
            }
            final Integer _tmpUsdaFdcId;
            if (_cursor.isNull(_cursorIndexOfUsdaFdcId)) {
              _tmpUsdaFdcId = null;
            } else {
              _tmpUsdaFdcId = _cursor.getInt(_cursorIndexOfUsdaFdcId);
            }
            final String _tmpDataSource;
            _tmpDataSource = _cursor.getString(_cursorIndexOfDataSource);
            _result = new FoodsDatasetEntity(_tmpId,_tmpName,_tmpCategory,_tmpServingSize,_tmpServingUnit,_tmpCaloriesPerServing,_tmpCarbsPerServingG,_tmpProteinPerServingG,_tmpFatPerServingG,_tmpFiberPerServingG,_tmpSugarPerServingG,_tmpSodiumPerServingMg,_tmpUsdaFdcId,_tmpDataSource);
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
  public Object searchByName(final String name, final int limit,
      final Continuation<? super List<FoodsDatasetEntity>> $completion) {
    final String _sql = "SELECT * FROM foods_dataset WHERE name LIKE '%' || ? || '%' LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, name);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FoodsDatasetEntity>>() {
      @Override
      @NonNull
      public List<FoodsDatasetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfServingSize = CursorUtil.getColumnIndexOrThrow(_cursor, "servingSize");
          final int _cursorIndexOfServingUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "servingUnit");
          final int _cursorIndexOfCaloriesPerServing = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesPerServing");
          final int _cursorIndexOfCarbsPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsPerServingG");
          final int _cursorIndexOfProteinPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinPerServingG");
          final int _cursorIndexOfFatPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "fatPerServingG");
          final int _cursorIndexOfFiberPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "fiberPerServingG");
          final int _cursorIndexOfSugarPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "sugarPerServingG");
          final int _cursorIndexOfSodiumPerServingMg = CursorUtil.getColumnIndexOrThrow(_cursor, "sodiumPerServingMg");
          final int _cursorIndexOfUsdaFdcId = CursorUtil.getColumnIndexOrThrow(_cursor, "usdaFdcId");
          final int _cursorIndexOfDataSource = CursorUtil.getColumnIndexOrThrow(_cursor, "dataSource");
          final List<FoodsDatasetEntity> _result = new ArrayList<FoodsDatasetEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FoodsDatasetEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final float _tmpServingSize;
            _tmpServingSize = _cursor.getFloat(_cursorIndexOfServingSize);
            final String _tmpServingUnit;
            _tmpServingUnit = _cursor.getString(_cursorIndexOfServingUnit);
            final int _tmpCaloriesPerServing;
            _tmpCaloriesPerServing = _cursor.getInt(_cursorIndexOfCaloriesPerServing);
            final float _tmpCarbsPerServingG;
            _tmpCarbsPerServingG = _cursor.getFloat(_cursorIndexOfCarbsPerServingG);
            final float _tmpProteinPerServingG;
            _tmpProteinPerServingG = _cursor.getFloat(_cursorIndexOfProteinPerServingG);
            final float _tmpFatPerServingG;
            _tmpFatPerServingG = _cursor.getFloat(_cursorIndexOfFatPerServingG);
            final Float _tmpFiberPerServingG;
            if (_cursor.isNull(_cursorIndexOfFiberPerServingG)) {
              _tmpFiberPerServingG = null;
            } else {
              _tmpFiberPerServingG = _cursor.getFloat(_cursorIndexOfFiberPerServingG);
            }
            final Float _tmpSugarPerServingG;
            if (_cursor.isNull(_cursorIndexOfSugarPerServingG)) {
              _tmpSugarPerServingG = null;
            } else {
              _tmpSugarPerServingG = _cursor.getFloat(_cursorIndexOfSugarPerServingG);
            }
            final Float _tmpSodiumPerServingMg;
            if (_cursor.isNull(_cursorIndexOfSodiumPerServingMg)) {
              _tmpSodiumPerServingMg = null;
            } else {
              _tmpSodiumPerServingMg = _cursor.getFloat(_cursorIndexOfSodiumPerServingMg);
            }
            final Integer _tmpUsdaFdcId;
            if (_cursor.isNull(_cursorIndexOfUsdaFdcId)) {
              _tmpUsdaFdcId = null;
            } else {
              _tmpUsdaFdcId = _cursor.getInt(_cursorIndexOfUsdaFdcId);
            }
            final String _tmpDataSource;
            _tmpDataSource = _cursor.getString(_cursorIndexOfDataSource);
            _item = new FoodsDatasetEntity(_tmpId,_tmpName,_tmpCategory,_tmpServingSize,_tmpServingUnit,_tmpCaloriesPerServing,_tmpCarbsPerServingG,_tmpProteinPerServingG,_tmpFatPerServingG,_tmpFiberPerServingG,_tmpSugarPerServingG,_tmpSodiumPerServingMg,_tmpUsdaFdcId,_tmpDataSource);
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
  public Flow<List<FoodsDatasetEntity>> getFoodsByCategory(final String category, final int limit) {
    final String _sql = "SELECT * FROM foods_dataset WHERE category = ? ORDER BY name LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, category);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"foods_dataset"}, new Callable<List<FoodsDatasetEntity>>() {
      @Override
      @NonNull
      public List<FoodsDatasetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfServingSize = CursorUtil.getColumnIndexOrThrow(_cursor, "servingSize");
          final int _cursorIndexOfServingUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "servingUnit");
          final int _cursorIndexOfCaloriesPerServing = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesPerServing");
          final int _cursorIndexOfCarbsPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsPerServingG");
          final int _cursorIndexOfProteinPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinPerServingG");
          final int _cursorIndexOfFatPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "fatPerServingG");
          final int _cursorIndexOfFiberPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "fiberPerServingG");
          final int _cursorIndexOfSugarPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "sugarPerServingG");
          final int _cursorIndexOfSodiumPerServingMg = CursorUtil.getColumnIndexOrThrow(_cursor, "sodiumPerServingMg");
          final int _cursorIndexOfUsdaFdcId = CursorUtil.getColumnIndexOrThrow(_cursor, "usdaFdcId");
          final int _cursorIndexOfDataSource = CursorUtil.getColumnIndexOrThrow(_cursor, "dataSource");
          final List<FoodsDatasetEntity> _result = new ArrayList<FoodsDatasetEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FoodsDatasetEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final float _tmpServingSize;
            _tmpServingSize = _cursor.getFloat(_cursorIndexOfServingSize);
            final String _tmpServingUnit;
            _tmpServingUnit = _cursor.getString(_cursorIndexOfServingUnit);
            final int _tmpCaloriesPerServing;
            _tmpCaloriesPerServing = _cursor.getInt(_cursorIndexOfCaloriesPerServing);
            final float _tmpCarbsPerServingG;
            _tmpCarbsPerServingG = _cursor.getFloat(_cursorIndexOfCarbsPerServingG);
            final float _tmpProteinPerServingG;
            _tmpProteinPerServingG = _cursor.getFloat(_cursorIndexOfProteinPerServingG);
            final float _tmpFatPerServingG;
            _tmpFatPerServingG = _cursor.getFloat(_cursorIndexOfFatPerServingG);
            final Float _tmpFiberPerServingG;
            if (_cursor.isNull(_cursorIndexOfFiberPerServingG)) {
              _tmpFiberPerServingG = null;
            } else {
              _tmpFiberPerServingG = _cursor.getFloat(_cursorIndexOfFiberPerServingG);
            }
            final Float _tmpSugarPerServingG;
            if (_cursor.isNull(_cursorIndexOfSugarPerServingG)) {
              _tmpSugarPerServingG = null;
            } else {
              _tmpSugarPerServingG = _cursor.getFloat(_cursorIndexOfSugarPerServingG);
            }
            final Float _tmpSodiumPerServingMg;
            if (_cursor.isNull(_cursorIndexOfSodiumPerServingMg)) {
              _tmpSodiumPerServingMg = null;
            } else {
              _tmpSodiumPerServingMg = _cursor.getFloat(_cursorIndexOfSodiumPerServingMg);
            }
            final Integer _tmpUsdaFdcId;
            if (_cursor.isNull(_cursorIndexOfUsdaFdcId)) {
              _tmpUsdaFdcId = null;
            } else {
              _tmpUsdaFdcId = _cursor.getInt(_cursorIndexOfUsdaFdcId);
            }
            final String _tmpDataSource;
            _tmpDataSource = _cursor.getString(_cursorIndexOfDataSource);
            _item = new FoodsDatasetEntity(_tmpId,_tmpName,_tmpCategory,_tmpServingSize,_tmpServingUnit,_tmpCaloriesPerServing,_tmpCarbsPerServingG,_tmpProteinPerServingG,_tmpFatPerServingG,_tmpFiberPerServingG,_tmpSugarPerServingG,_tmpSodiumPerServingMg,_tmpUsdaFdcId,_tmpDataSource);
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
  public Flow<List<String>> getAllCategories() {
    final String _sql = "SELECT DISTINCT category FROM foods_dataset WHERE category IS NOT NULL ORDER BY category";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"foods_dataset"}, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
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
  public Object getCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM foods_dataset";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
  public Object searchBySynonym(final String synonym, final int limit,
      final Continuation<? super List<FoodsDatasetEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT foods_dataset.* FROM foods_dataset\n"
            + "        JOIN food_synonyms ON foods_dataset.id = food_synonyms.foodDatasetId\n"
            + "        WHERE food_synonyms.synonym LIKE '%' || ? || '%'\n"
            + "        LIMIT ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, synonym);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FoodsDatasetEntity>>() {
      @Override
      @NonNull
      public List<FoodsDatasetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfServingSize = CursorUtil.getColumnIndexOrThrow(_cursor, "servingSize");
          final int _cursorIndexOfServingUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "servingUnit");
          final int _cursorIndexOfCaloriesPerServing = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesPerServing");
          final int _cursorIndexOfCarbsPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsPerServingG");
          final int _cursorIndexOfProteinPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinPerServingG");
          final int _cursorIndexOfFatPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "fatPerServingG");
          final int _cursorIndexOfFiberPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "fiberPerServingG");
          final int _cursorIndexOfSugarPerServingG = CursorUtil.getColumnIndexOrThrow(_cursor, "sugarPerServingG");
          final int _cursorIndexOfSodiumPerServingMg = CursorUtil.getColumnIndexOrThrow(_cursor, "sodiumPerServingMg");
          final int _cursorIndexOfUsdaFdcId = CursorUtil.getColumnIndexOrThrow(_cursor, "usdaFdcId");
          final int _cursorIndexOfDataSource = CursorUtil.getColumnIndexOrThrow(_cursor, "dataSource");
          final List<FoodsDatasetEntity> _result = new ArrayList<FoodsDatasetEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FoodsDatasetEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final float _tmpServingSize;
            _tmpServingSize = _cursor.getFloat(_cursorIndexOfServingSize);
            final String _tmpServingUnit;
            _tmpServingUnit = _cursor.getString(_cursorIndexOfServingUnit);
            final int _tmpCaloriesPerServing;
            _tmpCaloriesPerServing = _cursor.getInt(_cursorIndexOfCaloriesPerServing);
            final float _tmpCarbsPerServingG;
            _tmpCarbsPerServingG = _cursor.getFloat(_cursorIndexOfCarbsPerServingG);
            final float _tmpProteinPerServingG;
            _tmpProteinPerServingG = _cursor.getFloat(_cursorIndexOfProteinPerServingG);
            final float _tmpFatPerServingG;
            _tmpFatPerServingG = _cursor.getFloat(_cursorIndexOfFatPerServingG);
            final Float _tmpFiberPerServingG;
            if (_cursor.isNull(_cursorIndexOfFiberPerServingG)) {
              _tmpFiberPerServingG = null;
            } else {
              _tmpFiberPerServingG = _cursor.getFloat(_cursorIndexOfFiberPerServingG);
            }
            final Float _tmpSugarPerServingG;
            if (_cursor.isNull(_cursorIndexOfSugarPerServingG)) {
              _tmpSugarPerServingG = null;
            } else {
              _tmpSugarPerServingG = _cursor.getFloat(_cursorIndexOfSugarPerServingG);
            }
            final Float _tmpSodiumPerServingMg;
            if (_cursor.isNull(_cursorIndexOfSodiumPerServingMg)) {
              _tmpSodiumPerServingMg = null;
            } else {
              _tmpSodiumPerServingMg = _cursor.getFloat(_cursorIndexOfSodiumPerServingMg);
            }
            final Integer _tmpUsdaFdcId;
            if (_cursor.isNull(_cursorIndexOfUsdaFdcId)) {
              _tmpUsdaFdcId = null;
            } else {
              _tmpUsdaFdcId = _cursor.getInt(_cursorIndexOfUsdaFdcId);
            }
            final String _tmpDataSource;
            _tmpDataSource = _cursor.getString(_cursorIndexOfDataSource);
            _item = new FoodsDatasetEntity(_tmpId,_tmpName,_tmpCategory,_tmpServingSize,_tmpServingUnit,_tmpCaloriesPerServing,_tmpCarbsPerServingG,_tmpProteinPerServingG,_tmpFatPerServingG,_tmpFiberPerServingG,_tmpSugarPerServingG,_tmpSodiumPerServingMg,_tmpUsdaFdcId,_tmpDataSource);
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
  public Object getSynonymsForFood(final long foodId,
      final Continuation<? super List<SynonymEntity>> $completion) {
    final String _sql = "SELECT * FROM food_synonyms WHERE foodDatasetId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, foodId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SynonymEntity>>() {
      @Override
      @NonNull
      public List<SynonymEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSynonym = CursorUtil.getColumnIndexOrThrow(_cursor, "synonym");
          final int _cursorIndexOfFoodDatasetId = CursorUtil.getColumnIndexOrThrow(_cursor, "foodDatasetId");
          final List<SynonymEntity> _result = new ArrayList<SynonymEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SynonymEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSynonym;
            _tmpSynonym = _cursor.getString(_cursorIndexOfSynonym);
            final long _tmpFoodDatasetId;
            _tmpFoodDatasetId = _cursor.getLong(_cursorIndexOfFoodDatasetId);
            _item = new SynonymEntity(_tmpId,_tmpSynonym,_tmpFoodDatasetId);
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
