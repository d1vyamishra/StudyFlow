package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.StudyDatabase
import com.example.data.StudyRepository
import com.example.ui.StudyTrackerApp
import com.example.ui.StudyViewModel
import com.example.ui.StudyViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Room Database & Repository
        val database = StudyDatabase.getDatabase(this)
        val repository = StudyRepository(database.studyDao())
        
        // Retrieve ViewModel using custom Factory
        val viewModel: StudyViewModel by viewModels {
            StudyViewModelFactory(repository)
        }
        
        enableEdgeToEdge()
        
        setContent {
            val isDarkThemeState by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            MyApplicationTheme(darkTheme = isDarkThemeState) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StudyTrackerApp(viewModel = viewModel)
                }
            }
        }
    }
}
