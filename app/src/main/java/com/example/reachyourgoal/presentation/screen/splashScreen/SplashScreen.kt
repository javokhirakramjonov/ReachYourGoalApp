package com.example.reachyourgoal.presentation.screen.splashScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.reachyourgoal.presentation.screen.destinations.SplashScreenDestination
import com.example.reachyourgoal.presentation.screen.destinations.TasksScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay

@RootNavGraph(start = true)
@Destination
@Composable
fun SplashScreen(
    navigator: DestinationsNavigator,
) {

    LaunchedEffect(true) {
        delay(2000)
        navigator.navigate(TasksScreenDestination()) {
            popUpTo(SplashScreenDestination.route) {
                inclusive = true
            }
        }
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