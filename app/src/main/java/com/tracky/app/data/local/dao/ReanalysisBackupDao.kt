package com.tracky.app.data.local.dao

import androidx.room.*
import com.tracky.app.data.local.entity.ReanalysisBackupEntity

@Dao
interface ReanalysisBackupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBackup(backup: ReanalysisBackupEntity)

    @Query("SELECT * FROM reanalysis_backups WHERE originalEntryId = :id AND type = :type")
    suspend fun getBackup(id: Long, type: String): ReanalysisBackupEntity?

    @Query("DELETE FROM reanalysis_backups WHERE originalEntryId = :id AND type = :type")
    suspend fun deleteBackup(id: Long, type: String)

    @Query("SELECT * FROM reanalysis_backups")
    suspend fun getAllBackups(): List<ReanalysisBackupEntity>

    @Query("DELETE FROM reanalysis_backups")
    suspend fun clearAllBackups()
}
