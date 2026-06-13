package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_records")
data class StudyRecord(
    @PrimaryKey val date: String, // format: "yyyy-MM-dd"
    val lectureSeconds: Long = 0L,
    val selfStudySeconds: Long = 0L,
    val questionPracticeSeconds: Long = 0L
) {
    val totalSeconds: Long
        get() = lectureSeconds + selfStudySeconds + questionPracticeSeconds
}
