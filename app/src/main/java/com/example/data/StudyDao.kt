package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {
    @Query("SELECT * FROM study_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<StudyRecord>>

    @Query("SELECT * FROM study_records WHERE date = :date LIMIT 1")
    suspend fun getRecordForDate(date: String): StudyRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRecord(record: StudyRecord)

    @Query("DELETE FROM study_records WHERE date = :date")
    suspend fun deleteRecordForDate(date: String)
}
