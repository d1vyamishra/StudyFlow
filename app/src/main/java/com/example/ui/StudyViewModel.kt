package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.StudyRecord
import com.example.data.StudyRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class TimerType {
    LECTURE,
    SELF_STUDY,
    QUESTION
}

class StudyViewModel(private val repository: StudyRepository) : ViewModel() {

    // Theme state (true for dark theme, false for light theme)
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme = _isDarkTheme.asStateFlow()

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    // All-time session history
    val allRecords: StateFlow<List<StudyRecord>> = repository.allRecords
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _lectureSeconds = MutableStateFlow(0L)
    val lectureSeconds = _lectureSeconds.asStateFlow()

    private val _selfStudySeconds = MutableStateFlow(0L)
    val selfStudySeconds = _selfStudySeconds.asStateFlow()

    private val _questionPracticeSeconds = MutableStateFlow(0L)
    val questionPracticeSeconds = _questionPracticeSeconds.asStateFlow()

    private val _activeTimer = MutableStateFlow<TimerType?>(null)
    val activeTimer = _activeTimer.asStateFlow()

    private var timerJob: Job? = null
    private var saveCounter = 0

    init {
        // Load today's initial times from DB
        viewModelScope.launch {
            val todayStr = getTodayDateString()
            val todayRecord = repository.getRecordForDate(todayStr)
            if (todayRecord != null) {
                _lectureSeconds.value = todayRecord.lectureSeconds
                _selfStudySeconds.value = todayRecord.selfStudySeconds
                _questionPracticeSeconds.value = todayRecord.questionPracticeSeconds
            }
        }
    }

    fun startTimer(type: TimerType) {
        if (_activeTimer.value == type) return
        
        // Cancel existing active timer ticker
        pauseTimerInternal()
        
        _activeTimer.value = type
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                incrementTimer(type)
                
                // Periodically save to disk every 5 seconds to prevent loss
                saveCounter++
                if (saveCounter >= 5) {
                    saveTodayProgress()
                    saveCounter = 0
                }
            }
        }
    }

    fun pauseTimer() {
        pauseTimerInternal()
        saveTodayProgress()
    }

    private fun pauseTimerInternal() {
        timerJob?.cancel()
        timerJob = null
        _activeTimer.value = null
    }

    private fun incrementTimer(type: TimerType) {
        when (type) {
            TimerType.LECTURE -> _lectureSeconds.value++
            TimerType.SELF_STUDY -> _selfStudySeconds.value++
            TimerType.QUESTION -> _questionPracticeSeconds.value++
        }
    }

    fun adjustTimer(type: TimerType, deltaSeconds: Long) {
        when (type) {
            TimerType.LECTURE -> _lectureSeconds.value = (_lectureSeconds.value + deltaSeconds).coerceAtLeast(0L)
            TimerType.SELF_STUDY -> _selfStudySeconds.value = (_selfStudySeconds.value + deltaSeconds).coerceAtLeast(0L)
            TimerType.QUESTION -> _questionPracticeSeconds.value = (_questionPracticeSeconds.value + deltaSeconds).coerceAtLeast(0L)
        }
        saveTodayProgress()
    }

    fun resetTodayTimers() {
        pauseTimerInternal()
        _lectureSeconds.value = 0L
        _selfStudySeconds.value = 0L
        _questionPracticeSeconds.value = 0L
        saveTodayProgress()
    }

    fun saveTodayProgress() {
        val todayStr = getTodayDateString()
        val record = StudyRecord(
            date = todayStr,
            lectureSeconds = _lectureSeconds.value,
            selfStudySeconds = _selfStudySeconds.value,
            questionPracticeSeconds = _questionPracticeSeconds.value
        )
        viewModelScope.launch {
            repository.saveRecord(record)
        }
    }

    fun deleteRecord(date: String) {
        viewModelScope.launch {
            repository.deleteRecord(date)
            // If the deleted record was for today, reset ViewModel in-memory timers as well
            if (date == getTodayDateString()) {
                _lectureSeconds.value = 0L
                _selfStudySeconds.value = 0L
                _questionPracticeSeconds.value = 0L
                _activeTimer.value = null
                timerJob?.cancel()
                timerJob = null
            }
        }
    }

    // Manual save helper for individual past days (if user wants to update/insert past dates or fix data)
    fun savePastRecord(record: StudyRecord) {
        viewModelScope.launch {
            repository.saveRecord(record)
            // If the record edited was for today, sync our VM states
            if (record.date == getTodayDateString()) {
                _lectureSeconds.value = record.lectureSeconds
                _selfStudySeconds.value = record.selfStudySeconds
                _questionPracticeSeconds.value = record.questionPracticeSeconds
            }
        }
    }

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}

class StudyViewModelFactory(private val repository: StudyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StudyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
