package com.example.habitmountain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.example.habitmountain.ui.HabitMountainApp
import com.example.habitmountain.ui.theme.HabitMountainTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HabitMountainTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    HabitMountainApp()
                }
            }
        }
    }
}
