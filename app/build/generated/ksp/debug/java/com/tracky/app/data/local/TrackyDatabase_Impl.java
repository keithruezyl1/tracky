package com.tracky.app.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.FtsTableInfo;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.tracky.app.data.local.dao.ChatMessageDao;
import com.tracky.app.data.local.dao.ChatMessageDao_Impl;
import com.tracky.app.data.local.dao.DailyGoalDao;
import com.tracky.app.data.local.dao.DailyGoalDao_Impl;
import com.tracky.app.data.local.dao.ExerciseEntryDao;
import com.tracky.app.data.local.dao.ExerciseEntryDao_Impl;
import com.tracky.app.data.local.dao.FoodEntryDao;
import com.tracky.app.data.local.dao.FoodEntryDao_Impl;
import com.tracky.app.data.local.dao.FoodsDatasetDao;
import com.tracky.app.data.local.dao.FoodsDatasetDao_Impl;
import com.tracky.app.data.local.dao.SavedEntryDao;
import com.tracky.app.data.local.dao.SavedEntryDao_Impl;
import com.tracky.app.data.local.dao.UserProfileDao;
import com.tracky.app.data.local.dao.UserProfileDao_Impl;
import com.tracky.app.data.local.dao.WeightEntryDao;
import com.tracky.app.data.local.dao.WeightEntryDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TrackyDatabase_Impl extends TrackyDatabase {
  private volatile UserProfileDao _userProfileDao;

  private volatile DailyGoalDao _dailyGoalDao;

  private volatile FoodEntryDao _foodEntryDao;

  private volatile ExerciseEntryDao _exerciseEntryDao;

  private volatile WeightEntryDao _weightEntryDao;

  private volatile SavedEntryDao _savedEntryDao;

  private volatile ChatMessageDao _chatMessageDao;

  private volatile FoodsDatasetDao _foodsDatasetDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(4) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `user_profile` (`id` INTEGER NOT NULL, `heightCm` REAL NOT NULL, `currentWeightKg` REAL NOT NULL, `targetWeightKg` REAL NOT NULL, `unitPreference` TEXT NOT NULL, `timezone` TEXT NOT NULL, `bmi` REAL NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `daily_goals` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `effectiveFromDate` TEXT NOT NULL, `calorieGoalKcal` REAL NOT NULL, `carbsPct` INTEGER NOT NULL, `proteinPct` INTEGER NOT NULL, `fatPct` INTEGER NOT NULL, `carbsTargetG` REAL NOT NULL, `proteinTargetG` REAL NOT NULL, `fatTargetG` REAL NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_daily_goals_effectiveFromDate` ON `daily_goals` (`effectiveFromDate`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `food_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `time` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `totalCalories` REAL NOT NULL, `totalCarbsG` REAL NOT NULL, `totalProteinG` REAL NOT NULL, `totalFatG` REAL NOT NULL, `analysisNarrative` TEXT, `photoPath` TEXT, `originalInput` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_food_entries_date` ON `food_entries` (`date`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_food_entries_createdAt` ON `food_entries` (`createdAt`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `food_items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `foodEntryId` INTEGER NOT NULL, `name` TEXT NOT NULL, `matchedName` TEXT, `quantity` REAL NOT NULL, `unit` TEXT NOT NULL, `calories` REAL NOT NULL, `carbsG` REAL NOT NULL, `proteinG` REAL NOT NULL, `fatG` REAL NOT NULL, `source` TEXT NOT NULL, `sourceId` TEXT, `confidence` REAL NOT NULL, `displayOrder` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_food_items_foodEntryId` ON `food_items` (`foodEntryId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `exercise_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `time` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `totalCalories` REAL NOT NULL, `totalDurationMinutes` INTEGER NOT NULL, `userWeightKg` REAL NOT NULL, `originalInput` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `exercise_items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `entryId` INTEGER NOT NULL, `activityName` TEXT NOT NULL, `durationMinutes` INTEGER NOT NULL, `metValue` REAL NOT NULL, `caloriesBurned` REAL NOT NULL, `intensity` TEXT, `source` TEXT NOT NULL, `confidence` REAL NOT NULL, `displayOrder` INTEGER NOT NULL, FOREIGN KEY(`entryId`) REFERENCES `exercise_entries`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_exercise_items_entryId` ON `exercise_items` (`entryId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `weight_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `weightKg` REAL NOT NULL, `note` TEXT, `timestamp` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_weight_entries_date` ON `weight_entries` (`date`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_weight_entries_timestamp` ON `weight_entries` (`timestamp`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `saved_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `entryType` TEXT NOT NULL, `entryDataJson` TEXT NOT NULL, `totalCalories` REAL NOT NULL, `useCount` INTEGER NOT NULL, `lastUsedAt` INTEGER, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `chat_messages` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `messageType` TEXT NOT NULL, `role` TEXT NOT NULL, `content` TEXT, `imagePath` TEXT, `entryDataJson` TEXT, `draftStatus` TEXT, `linkedFoodEntryId` INTEGER, `linkedExerciseEntryId` INTEGER, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_chat_messages_date` ON `chat_messages` (`date`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_chat_messages_timestamp` ON `chat_messages` (`timestamp`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `foods_dataset` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `category` TEXT, `servingSize` REAL NOT NULL, `servingUnit` TEXT NOT NULL, `caloriesPerServing` INTEGER NOT NULL, `carbsPerServingG` REAL NOT NULL, `proteinPerServingG` REAL NOT NULL, `fatPerServingG` REAL NOT NULL, `fiberPerServingG` REAL, `sugarPerServingG` REAL, `sodiumPerServingMg` REAL, `usdaFdcId` INTEGER, `dataSource` TEXT NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_foods_dataset_name` ON `foods_dataset` (`name`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_foods_dataset_category` ON `foods_dataset` (`category`)");
        db.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `foods_fts` USING FTS4(`name` TEXT NOT NULL, `category` TEXT, content=`foods_dataset`)");
        db.execSQL("CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_foods_fts_BEFORE_UPDATE BEFORE UPDATE ON `foods_dataset` BEGIN DELETE FROM `foods_fts` WHERE `docid`=OLD.`rowid`; END");
        db.execSQL("CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_foods_fts_BEFORE_DELETE BEFORE DELETE ON `foods_dataset` BEGIN DELETE FROM `foods_fts` WHERE `docid`=OLD.`rowid`; END");
        db.execSQL("CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_foods_fts_AFTER_UPDATE AFTER UPDATE ON `foods_dataset` BEGIN INSERT INTO `foods_fts`(`docid`, `name`, `category`) VALUES (NEW.`rowid`, NEW.`name`, NEW.`category`); END");
        db.execSQL("CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_foods_fts_AFTER_INSERT AFTER INSERT ON `foods_dataset` BEGIN INSERT INTO `foods_fts`(`docid`, `name`, `category`) VALUES (NEW.`rowid`, NEW.`name`, NEW.`category`); END");
        db.execSQL("CREATE TABLE IF NOT EXISTS `food_synonyms` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `synonym` TEXT NOT NULL, `foodDatasetId` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_food_synonyms_synonym` ON `food_synonyms` (`synonym`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_food_synonyms_foodDatasetId` ON `food_synonyms` (`foodDatasetId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '669805c4f0d1d535668b5f209eba03b3')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `user_profile`");
        db.execSQL("DROP TABLE IF EXISTS `daily_goals`");
        db.execSQL("DROP TABLE IF EXISTS `food_entries`");
        db.execSQL("DROP TABLE IF EXISTS `food_items`");
        db.execSQL("DROP TABLE IF EXISTS `exercise_entries`");
        db.execSQL("DROP TABLE IF EXISTS `exercise_items`");
        db.execSQL("DROP TABLE IF EXISTS `weight_entries`");
        db.execSQL("DROP TABLE IF EXISTS `saved_entries`");
        db.execSQL("DROP TABLE IF EXISTS `chat_messages`");
        db.execSQL("DROP TABLE IF EXISTS `foods_dataset`");
        db.execSQL("DROP TABLE IF EXISTS `foods_fts`");
        db.execSQL("DROP TABLE IF EXISTS `food_synonyms`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_foods_fts_BEFORE_UPDATE BEFORE UPDATE ON `foods_dataset` BEGIN DELETE FROM `foods_fts` WHERE `docid`=OLD.`rowid`; END");
        db.execSQL("CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_foods_fts_BEFORE_DELETE BEFORE DELETE ON `foods_dataset` BEGIN DELETE FROM `foods_fts` WHERE `docid`=OLD.`rowid`; END");
        db.execSQL("CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_foods_fts_AFTER_UPDATE AFTER UPDATE ON `foods_dataset` BEGIN INSERT INTO `foods_fts`(`docid`, `name`, `category`) VALUES (NEW.`rowid`, NEW.`name`, NEW.`category`); END");
        db.execSQL("CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_foods_fts_AFTER_INSERT AFTER INSERT ON `foods_dataset` BEGIN INSERT INTO `foods_fts`(`docid`, `name`, `category`) VALUES (NEW.`rowid`, NEW.`name`, NEW.`category`); END");
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsUserProfile = new HashMap<String, TableInfo.Column>(9);
        _columnsUserProfile.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("heightCm", new TableInfo.Column("heightCm", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("currentWeightKg", new TableInfo.Column("currentWeightKg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("targetWeightKg", new TableInfo.Column("targetWeightKg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("unitPreference", new TableInfo.Column("unitPreference", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("timezone", new TableInfo.Column("timezone", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("bmi", new TableInfo.Column("bmi", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserProfile = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserProfile = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserProfile = new TableInfo("user_profile", _columnsUserProfile, _foreignKeysUserProfile, _indicesUserProfile);
        final TableInfo _existingUserProfile = TableInfo.read(db, "user_profile");
        if (!_infoUserProfile.equals(_existingUserProfile)) {
          return new RoomOpenHelper.ValidationResult(false, "user_profile(com.tracky.app.data.local.entity.UserProfileEntity).\n"
                  + " Expected:\n" + _infoUserProfile + "\n"
                  + " Found:\n" + _existingUserProfile);
        }
        final HashMap<String, TableInfo.Column> _columnsDailyGoals = new HashMap<String, TableInfo.Column>(10);
        _columnsDailyGoals.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyGoals.put("effectiveFromDate", new TableInfo.Column("effectiveFromDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyGoals.put("calorieGoalKcal", new TableInfo.Column("calorieGoalKcal", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyGoals.put("carbsPct", new TableInfo.Column("carbsPct", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyGoals.put("proteinPct", new TableInfo.Column("proteinPct", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyGoals.put("fatPct", new TableInfo.Column("fatPct", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyGoals.put("carbsTargetG", new TableInfo.Column("carbsTargetG", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyGoals.put("proteinTargetG", new TableInfo.Column("proteinTargetG", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyGoals.put("fatTargetG", new TableInfo.Column("fatTargetG", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyGoals.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDailyGoals = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDailyGoals = new HashSet<TableInfo.Index>(1);
        _indicesDailyGoals.add(new TableInfo.Index("index_daily_goals_effectiveFromDate", true, Arrays.asList("effectiveFromDate"), Arrays.asList("ASC")));
        final TableInfo _infoDailyGoals = new TableInfo("daily_goals", _columnsDailyGoals, _foreignKeysDailyGoals, _indicesDailyGoals);
        final TableInfo _existingDailyGoals = TableInfo.read(db, "daily_goals");
        if (!_infoDailyGoals.equals(_existingDailyGoals)) {
          return new RoomOpenHelper.ValidationResult(false, "daily_goals(com.tracky.app.data.local.entity.DailyGoalEntity).\n"
                  + " Expected:\n" + _infoDailyGoals + "\n"
                  + " Found:\n" + _existingDailyGoals);
        }
        final HashMap<String, TableInfo.Column> _columnsFoodEntries = new HashMap<String, TableInfo.Column>(13);
        _columnsFoodEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodEntries.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodEntries.put("time", new TableInfo.Column("time", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodEntries.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodEntries.put("totalCalories", new TableInfo.Column("totalCalories", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodEntries.put("totalCarbsG", new TableInfo.Column("totalCarbsG", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodEntries.put("totalProteinG", new TableInfo.Column("totalProteinG", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodEntries.put("totalFatG", new TableInfo.Column("totalFatG", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodEntries.put("analysisNarrative", new TableInfo.Column("analysisNarrative", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodEntries.put("photoPath", new TableInfo.Column("photoPath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodEntries.put("originalInput", new TableInfo.Column("originalInput", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodEntries.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodEntries.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFoodEntries = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFoodEntries = new HashSet<TableInfo.Index>(2);
        _indicesFoodEntries.add(new TableInfo.Index("index_food_entries_date", false, Arrays.asList("date"), Arrays.asList("ASC")));
        _indicesFoodEntries.add(new TableInfo.Index("index_food_entries_createdAt", false, Arrays.asList("createdAt"), Arrays.asList("ASC")));
        final TableInfo _infoFoodEntries = new TableInfo("food_entries", _columnsFoodEntries, _foreignKeysFoodEntries, _indicesFoodEntries);
        final TableInfo _existingFoodEntries = TableInfo.read(db, "food_entries");
        if (!_infoFoodEntries.equals(_existingFoodEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "food_entries(com.tracky.app.data.local.entity.FoodEntryEntity).\n"
                  + " Expected:\n" + _infoFoodEntries + "\n"
                  + " Found:\n" + _existingFoodEntries);
        }
        final HashMap<String, TableInfo.Column> _columnsFoodItems = new HashMap<String, TableInfo.Column>(14);
        _columnsFoodItems.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("foodEntryId", new TableInfo.Column("foodEntryId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("matchedName", new TableInfo.Column("matchedName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("quantity", new TableInfo.Column("quantity", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("unit", new TableInfo.Column("unit", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("calories", new TableInfo.Column("calories", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("carbsG", new TableInfo.Column("carbsG", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("proteinG", new TableInfo.Column("proteinG", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("fatG", new TableInfo.Column("fatG", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("source", new TableInfo.Column("source", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("sourceId", new TableInfo.Column("sourceId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("confidence", new TableInfo.Column("confidence", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodItems.put("displayOrder", new TableInfo.Column("displayOrder", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFoodItems = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFoodItems = new HashSet<TableInfo.Index>(1);
        _indicesFoodItems.add(new TableInfo.Index("index_food_items_foodEntryId", false, Arrays.asList("foodEntryId"), Arrays.asList("ASC")));
        final TableInfo _infoFoodItems = new TableInfo("food_items", _columnsFoodItems, _foreignKeysFoodItems, _indicesFoodItems);
        final TableInfo _existingFoodItems = TableInfo.read(db, "food_items");
        if (!_infoFoodItems.equals(_existingFoodItems)) {
          return new RoomOpenHelper.ValidationResult(false, "food_items(com.tracky.app.data.local.entity.FoodItemEntity).\n"
                  + " Expected:\n" + _infoFoodItems + "\n"
                  + " Found:\n" + _existingFoodItems);
        }
        final HashMap<String, TableInfo.Column> _columnsExerciseEntries = new HashMap<String, TableInfo.Column>(10);
        _columnsExerciseEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseEntries.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseEntries.put("time", new TableInfo.Column("time", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseEntries.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseEntries.put("totalCalories", new TableInfo.Column("totalCalories", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseEntries.put("totalDurationMinutes", new TableInfo.Column("totalDurationMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseEntries.put("userWeightKg", new TableInfo.Column("userWeightKg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseEntries.put("originalInput", new TableInfo.Column("originalInput", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseEntries.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseEntries.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExerciseEntries = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesExerciseEntries = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoExerciseEntries = new TableInfo("exercise_entries", _columnsExerciseEntries, _foreignKeysExerciseEntries, _indicesExerciseEntries);
        final TableInfo _existingExerciseEntries = TableInfo.read(db, "exercise_entries");
        if (!_infoExerciseEntries.equals(_existingExerciseEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "exercise_entries(com.tracky.app.data.local.entity.ExerciseEntryEntity).\n"
                  + " Expected:\n" + _infoExerciseEntries + "\n"
                  + " Found:\n" + _existingExerciseEntries);
        }
        final HashMap<String, TableInfo.Column> _columnsExerciseItems = new HashMap<String, TableInfo.Column>(10);
        _columnsExerciseItems.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseItems.put("entryId", new TableInfo.Column("entryId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseItems.put("activityName", new TableInfo.Column("activityName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseItems.put("durationMinutes", new TableInfo.Column("durationMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseItems.put("metValue", new TableInfo.Column("metValue", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseItems.put("caloriesBurned", new TableInfo.Column("caloriesBurned", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseItems.put("intensity", new TableInfo.Column("intensity", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseItems.put("source", new TableInfo.Column("source", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseItems.put("confidence", new TableInfo.Column("confidence", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseItems.put("displayOrder", new TableInfo.Column("displayOrder", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExerciseItems = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysExerciseItems.add(new TableInfo.ForeignKey("exercise_entries", "CASCADE", "NO ACTION", Arrays.asList("entryId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesExerciseItems = new HashSet<TableInfo.Index>(1);
        _indicesExerciseItems.add(new TableInfo.Index("index_exercise_items_entryId", false, Arrays.asList("entryId"), Arrays.asList("ASC")));
        final TableInfo _infoExerciseItems = new TableInfo("exercise_items", _columnsExerciseItems, _foreignKeysExerciseItems, _indicesExerciseItems);
        final TableInfo _existingExerciseItems = TableInfo.read(db, "exercise_items");
        if (!_infoExerciseItems.equals(_existingExerciseItems)) {
          return new RoomOpenHelper.ValidationResult(false, "exercise_items(com.tracky.app.data.local.entity.ExerciseItemEntity).\n"
                  + " Expected:\n" + _infoExerciseItems + "\n"
                  + " Found:\n" + _existingExerciseItems);
        }
        final HashMap<String, TableInfo.Column> _columnsWeightEntries = new HashMap<String, TableInfo.Column>(7);
        _columnsWeightEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeightEntries.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeightEntries.put("weightKg", new TableInfo.Column("weightKg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeightEntries.put("note", new TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeightEntries.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeightEntries.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeightEntries.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWeightEntries = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWeightEntries = new HashSet<TableInfo.Index>(2);
        _indicesWeightEntries.add(new TableInfo.Index("index_weight_entries_date", false, Arrays.asList("date"), Arrays.asList("ASC")));
        _indicesWeightEntries.add(new TableInfo.Index("index_weight_entries_timestamp", false, Arrays.asList("timestamp"), Arrays.asList("ASC")));
        final TableInfo _infoWeightEntries = new TableInfo("weight_entries", _columnsWeightEntries, _foreignKeysWeightEntries, _indicesWeightEntries);
        final TableInfo _existingWeightEntries = TableInfo.read(db, "weight_entries");
        if (!_infoWeightEntries.equals(_existingWeightEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "weight_entries(com.tracky.app.data.local.entity.WeightEntryEntity).\n"
                  + " Expected:\n" + _infoWeightEntries + "\n"
                  + " Found:\n" + _existingWeightEntries);
        }
        final HashMap<String, TableInfo.Column> _columnsSavedEntries = new HashMap<String, TableInfo.Column>(9);
        _columnsSavedEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedEntries.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedEntries.put("entryType", new TableInfo.Column("entryType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedEntries.put("entryDataJson", new TableInfo.Column("entryDataJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedEntries.put("totalCalories", new TableInfo.Column("totalCalories", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedEntries.put("useCount", new TableInfo.Column("useCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedEntries.put("lastUsedAt", new TableInfo.Column("lastUsedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedEntries.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSavedEntries.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSavedEntries = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSavedEntries = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSavedEntries = new TableInfo("saved_entries", _columnsSavedEntries, _foreignKeysSavedEntries, _indicesSavedEntries);
        final TableInfo _existingSavedEntries = TableInfo.read(db, "saved_entries");
        if (!_infoSavedEntries.equals(_existingSavedEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "saved_entries(com.tracky.app.data.local.entity.SavedEntryEntity).\n"
                  + " Expected:\n" + _infoSavedEntries + "\n"
                  + " Found:\n" + _existingSavedEntries);
        }
        final HashMap<String, TableInfo.Column> _columnsChatMessages = new HashMap<String, TableInfo.Column>(12);
        _columnsChatMessages.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("messageType", new TableInfo.Column("messageType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("role", new TableInfo.Column("role", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("content", new TableInfo.Column("content", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("imagePath", new TableInfo.Column("imagePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("entryDataJson", new TableInfo.Column("entryDataJson", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("draftStatus", new TableInfo.Column("draftStatus", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("linkedFoodEntryId", new TableInfo.Column("linkedFoodEntryId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("linkedExerciseEntryId", new TableInfo.Column("linkedExerciseEntryId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChatMessages.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysChatMessages = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesChatMessages = new HashSet<TableInfo.Index>(2);
        _indicesChatMessages.add(new TableInfo.Index("index_chat_messages_date", false, Arrays.asList("date"), Arrays.asList("ASC")));
        _indicesChatMessages.add(new TableInfo.Index("index_chat_messages_timestamp", false, Arrays.asList("timestamp"), Arrays.asList("ASC")));
        final TableInfo _infoChatMessages = new TableInfo("chat_messages", _columnsChatMessages, _foreignKeysChatMessages, _indicesChatMessages);
        final TableInfo _existingChatMessages = TableInfo.read(db, "chat_messages");
        if (!_infoChatMessages.equals(_existingChatMessages)) {
          return new RoomOpenHelper.ValidationResult(false, "chat_messages(com.tracky.app.data.local.entity.ChatMessageEntity).\n"
                  + " Expected:\n" + _infoChatMessages + "\n"
                  + " Found:\n" + _existingChatMessages);
        }
        final HashMap<String, TableInfo.Column> _columnsFoodsDataset = new HashMap<String, TableInfo.Column>(14);
        _columnsFoodsDataset.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodsDataset.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodsDataset.put("category", new TableInfo.Column("category", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodsDataset.put("servingSize", new TableInfo.Column("servingSize", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodsDataset.put("servingUnit", new TableInfo.Column("servingUnit", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodsDataset.put("caloriesPerServing", new TableInfo.Column("caloriesPerServing", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodsDataset.put("carbsPerServingG", new TableInfo.Column("carbsPerServingG", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodsDataset.put("proteinPerServingG", new TableInfo.Column("proteinPerServingG", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodsDataset.put("fatPerServingG", new TableInfo.Column("fatPerServingG", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodsDataset.put("fiberPerServingG", new TableInfo.Column("fiberPerServingG", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodsDataset.put("sugarPerServingG", new TableInfo.Column("sugarPerServingG", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodsDataset.put("sodiumPerServingMg", new TableInfo.Column("sodiumPerServingMg", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodsDataset.put("usdaFdcId", new TableInfo.Column("usdaFdcId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodsDataset.put("dataSource", new TableInfo.Column("dataSource", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFoodsDataset = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFoodsDataset = new HashSet<TableInfo.Index>(2);
        _indicesFoodsDataset.add(new TableInfo.Index("index_foods_dataset_name", false, Arrays.asList("name"), Arrays.asList("ASC")));
        _indicesFoodsDataset.add(new TableInfo.Index("index_foods_dataset_category", false, Arrays.asList("category"), Arrays.asList("ASC")));
        final TableInfo _infoFoodsDataset = new TableInfo("foods_dataset", _columnsFoodsDataset, _foreignKeysFoodsDataset, _indicesFoodsDataset);
        final TableInfo _existingFoodsDataset = TableInfo.read(db, "foods_dataset");
        if (!_infoFoodsDataset.equals(_existingFoodsDataset)) {
          return new RoomOpenHelper.ValidationResult(false, "foods_dataset(com.tracky.app.data.local.entity.FoodsDatasetEntity).\n"
                  + " Expected:\n" + _infoFoodsDataset + "\n"
                  + " Found:\n" + _existingFoodsDataset);
        }
        final HashSet<String> _columnsFoodsFts = new HashSet<String>(2);
        _columnsFoodsFts.add("name");
        _columnsFoodsFts.add("category");
        final FtsTableInfo _infoFoodsFts = new FtsTableInfo("foods_fts", _columnsFoodsFts, "CREATE VIRTUAL TABLE IF NOT EXISTS `foods_fts` USING FTS4(`name` TEXT NOT NULL, `category` TEXT, content=`foods_dataset`)");
        final FtsTableInfo _existingFoodsFts = FtsTableInfo.read(db, "foods_fts");
        if (!_infoFoodsFts.equals(_existingFoodsFts)) {
          return new RoomOpenHelper.ValidationResult(false, "foods_fts(com.tracky.app.data.local.entity.FoodsFtsEntity).\n"
                  + " Expected:\n" + _infoFoodsFts + "\n"
                  + " Found:\n" + _existingFoodsFts);
        }
        final HashMap<String, TableInfo.Column> _columnsFoodSynonyms = new HashMap<String, TableInfo.Column>(3);
        _columnsFoodSynonyms.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodSynonyms.put("synonym", new TableInfo.Column("synonym", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoodSynonyms.put("foodDatasetId", new TableInfo.Column("foodDatasetId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFoodSynonyms = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFoodSynonyms = new HashSet<TableInfo.Index>(2);
        _indicesFoodSynonyms.add(new TableInfo.Index("index_food_synonyms_synonym", false, Arrays.asList("synonym"), Arrays.asList("ASC")));
        _indicesFoodSynonyms.add(new TableInfo.Index("index_food_synonyms_foodDatasetId", false, Arrays.asList("foodDatasetId"), Arrays.asList("ASC")));
        final TableInfo _infoFoodSynonyms = new TableInfo("food_synonyms", _columnsFoodSynonyms, _foreignKeysFoodSynonyms, _indicesFoodSynonyms);
        final TableInfo _existingFoodSynonyms = TableInfo.read(db, "food_synonyms");
        if (!_infoFoodSynonyms.equals(_existingFoodSynonyms)) {
          return new RoomOpenHelper.ValidationResult(false, "food_synonyms(com.tracky.app.data.local.entity.SynonymEntity).\n"
                  + " Expected:\n" + _infoFoodSynonyms + "\n"
                  + " Found:\n" + _existingFoodSynonyms);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "669805c4f0d1d535668b5f209eba03b3", "124d160ac786325db36817590aeb9598");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(1);
    _shadowTablesMap.put("foods_fts", "foods_dataset");
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "user_profile","daily_goals","food_entries","food_items","exercise_entries","exercise_items","weight_entries","saved_entries","chat_messages","foods_dataset","foods_fts","food_synonyms");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `user_profile`");
      _db.execSQL("DELETE FROM `daily_goals`");
      _db.execSQL("DELETE FROM `food_entries`");
      _db.execSQL("DELETE FROM `food_items`");
      _db.execSQL("DELETE FROM `exercise_entries`");
      _db.execSQL("DELETE FROM `exercise_items`");
      _db.execSQL("DELETE FROM `weight_entries`");
      _db.execSQL("DELETE FROM `saved_entries`");
      _db.execSQL("DELETE FROM `chat_messages`");
      _db.execSQL("DELETE FROM `foods_dataset`");
      _db.execSQL("DELETE FROM `foods_fts`");
      _db.execSQL("DELETE FROM `food_synonyms`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(UserProfileDao.class, UserProfileDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DailyGoalDao.class, DailyGoalDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FoodEntryDao.class, FoodEntryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ExerciseEntryDao.class, ExerciseEntryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WeightEntryDao.class, WeightEntryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SavedEntryDao.class, SavedEntryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ChatMessageDao.class, ChatMessageDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FoodsDatasetDao.class, FoodsDatasetDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public UserProfileDao userProfileDao() {
    if (_userProfileDao != null) {
      return _userProfileDao;
    } else {
      synchronized(this) {
        if(_userProfileDao == null) {
          _userProfileDao = new UserProfileDao_Impl(this);
        }
        return _userProfileDao;
      }
    }
  }

  @Override
  public DailyGoalDao dailyGoalDao() {
    if (_dailyGoalDao != null) {
      return _dailyGoalDao;
    } else {
      synchronized(this) {
        if(_dailyGoalDao == null) {
          _dailyGoalDao = new DailyGoalDao_Impl(this);
        }
        return _dailyGoalDao;
      }
    }
  }

  @Override
  public FoodEntryDao foodEntryDao() {
    if (_foodEntryDao != null) {
      return _foodEntryDao;
    } else {
      synchronized(this) {
        if(_foodEntryDao == null) {
          _foodEntryDao = new FoodEntryDao_Impl(this);
        }
        return _foodEntryDao;
      }
    }
  }

  @Override
  public ExerciseEntryDao exerciseEntryDao() {
    if (_exerciseEntryDao != null) {
      return _exerciseEntryDao;
    } else {
      synchronized(this) {
        if(_exerciseEntryDao == null) {
          _exerciseEntryDao = new ExerciseEntryDao_Impl(this);
        }
        return _exerciseEntryDao;
      }
    }
  }

  @Override
  public WeightEntryDao weightEntryDao() {
    if (_weightEntryDao != null) {
      return _weightEntryDao;
    } else {
      synchronized(this) {
        if(_weightEntryDao == null) {
          _weightEntryDao = new WeightEntryDao_Impl(this);
        }
        return _weightEntryDao;
      }
    }
  }

  @Override
  public SavedEntryDao savedEntryDao() {
    if (_savedEntryDao != null) {
      return _savedEntryDao;
    } else {
      synchronized(this) {
        if(_savedEntryDao == null) {
          _savedEntryDao = new SavedEntryDao_Impl(this);
        }
        return _savedEntryDao;
      }
    }
  }

  @Override
  public ChatMessageDao chatMessageDao() {
    if (_chatMessageDao != null) {
      return _chatMessageDao;
    } else {
      synchronized(this) {
        if(_chatMessageDao == null) {
          _chatMessageDao = new ChatMessageDao_Impl(this);
        }
        return _chatMessageDao;
      }
    }
  }

  @Override
  public FoodsDatasetDao foodsDatasetDao() {
    if (_foodsDatasetDao != null) {
      return _foodsDatasetDao;
    } else {
      synchronized(this) {
        if(_foodsDatasetDao == null) {
          _foodsDatasetDao = new FoodsDatasetDao_Impl(this);
        }
        return _foodsDatasetDao;
      }
    }
  }
}
