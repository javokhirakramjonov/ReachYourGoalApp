package com.example.reachyourgoal.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.reachyourgoal.presentation.screen.authScreen.AuthScreen
import com.example.reachyourgoal.presentation.screen.mainScreen.MainScreen
import com.example.reachyourgoal.presentation.screen.splashScreen.SplashScreen

@Composable
fun SetupNavGraph(navHostController: NavHostController) {

    NavHost(navController = navHostController, startDestination = Screen.Splash.route) {
        composable(route = Screen.Splash.route) {
            SplashScreen(navHostController)
        }

        composable(route = Screen.AuthScreen.route) {
            AuthScreen(navHostController)
        }

        composable(route = Screen.MainScreen.route) {
            MainScreen(navHostController)
        }
    }

}