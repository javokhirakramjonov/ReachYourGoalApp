package com.example.reachyourgoal.presentation.screen.splashScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.reachyourgoal.navigation.Screen
import com.example.reachyourgoal.ui.common.navigateWithPopUp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navHostController: NavHostController) {

    LaunchedEffect(true) {
        delay(2000)
        navHostController.navigateWithPopUp(Screen.LoginScreen.route, Screen.Splash.route)
    }

    Surface {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Reach your goals!!!"
            )
        }
    }
}