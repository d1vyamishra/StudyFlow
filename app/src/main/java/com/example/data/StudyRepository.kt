package com.example.data

import kotlinx.coroutines.flow.Flow

class StudyRepository(private val studyDao: StudyDao) {
    val allRecords: Flow<List<StudyRecord>> = studyDao.getAllRecords()

    suspend fun getRecordForDate(date: String): StudyRecord? {
        return studyDao.getRecordForDate(date)
    }

    suspend fun saveRecord(record: StudyRecord) {
        studyDao.insertOrUpdateRecord(record)
    }

    suspend fun deleteRecord(date: String) {
        studyDao.deleteRecordForDate(date)
    }
}
