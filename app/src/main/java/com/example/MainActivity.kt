package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.navigation.NutriTrackNavGraph
import com.example.ui.theme.NutriTrackTheme
import com.example.ui.viewmodel.NutriTrackViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: NutriTrackViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
      val systemDark = isSystemInDarkTheme()
      val isDarkTheme = when (themeMode) {
        "DARK" -> true
        "LIGHT" -> false
        else -> systemDark
      }

      NutriTrackTheme(darkTheme = isDarkTheme) {
        NutriTrackNavGraph(viewModel = viewModel)
      }
    }
  }
}

