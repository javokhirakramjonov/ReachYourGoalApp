package com.example.reachyourgoal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.reachyourgoal.navigation.SetupNavGraph
import com.example.reachyourgoal.ui.theme.ReachYourGoalTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            ReachYourGoalTheme {
                SetupNavGraph(navHostController = rememberNavController())
            }
        }
    }
}