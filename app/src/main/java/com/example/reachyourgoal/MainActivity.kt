package com.example.reachyourgoal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.reachyourgoal.presentation.screen.NavGraphs
import com.example.reachyourgoal.ui.theme.ReachYourGoalTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            ReachYourGoalTheme {
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }
}