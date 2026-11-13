package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AuraBottomNavigation
import com.example.ui.components.AuraTopBar
import com.example.ui.components.BannerToast
import com.example.ui.screens.ExercisesScreen
import com.example.ui.screens.InsightsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.theme.AuraTheme
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.WellnessViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
  private val viewModel: WellnessViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val uiState by viewModel.uiState.collectAsStateWithLifecycle()

      val systemDark = isSystemInDarkTheme()
      val isDarkTheme = when (uiState.userProfile.darkModeSetting) {
        "light" -> false
        "system" -> systemDark
        else -> true
      }

      AuraTheme(
        themeMode = uiState.userProfile.darkModeSetting,
        darkTheme = isDarkTheme
      ) {
        // BackHandler: return to TODAY screen if on sub-screens
        BackHandler(enabled = uiState.currentScreen != Screen.TODAY) {
          viewModel.setScreen(Screen.TODAY)
        }

        val dateDisplay = try {
          val sdf = SimpleDateFormat("MMMM d", Locale.getDefault())
          sdf.format(Date())
        } catch (e: Exception) {
          uiState.todayDate
        }

        Scaffold(
          modifier = Modifier.fillMaxSize(),
          topBar = {
            if (uiState.currentScreen == Screen.TODAY) {
              AuraTopBar(
                dateText = dateDisplay
              )
            }
          },
          bottomBar = {
            AuraBottomNavigation(
              currentScreen = uiState.currentScreen,
              onSelectScreen = { viewModel.setScreen(it) }
            )
          }
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            // Animated screen navigation transitions
            AnimatedContent(
              targetState = uiState.currentScreen,
              transitionSpec = {
                val forward = targetState.ordinal > initialState.ordinal
                val slideIn = slideInHorizontally(
                  initialOffsetX = { fullWidth -> if (forward) fullWidth / 4 else -fullWidth / 4 },
                  animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(durationMillis = 260))

                val slideOut = slideOutHorizontally(
                  targetOffsetX = { fullWidth -> if (forward) -fullWidth / 4 else fullWidth / 4 },
                  animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(durationMillis = 200))

                slideIn togetherWith slideOut
              },
              label = "screen_transition"
            ) { screen ->
              when (screen) {
                Screen.TODAY -> TodayScreen(uiState = uiState, viewModel = viewModel)
                Screen.INSIGHTS -> InsightsScreen(uiState = uiState)
                Screen.EXERCISES -> ExercisesScreen(viewModel = viewModel)
                Screen.SETTINGS -> SettingsScreen(uiState = uiState, viewModel = viewModel)
              }
            }

            // Top-anchored confirmation banner toast with spring slide-in
            BannerToast(
              message = uiState.bannerNotification,
              onDismiss = { viewModel.dismissBanner() },
              modifier = Modifier.align(Alignment.TopCenter)
            )
          }
        }
      }
    }
  }
}
