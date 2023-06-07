package com.example.reachyourgoal.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.reachyourgoal.presentation.screen.auth.loginScreen.LoginScreen
import com.example.reachyourgoal.presentation.screen.auth.userDetailsScreen.UserDetailsScreen
import com.example.reachyourgoal.presentation.screen.mainScreen.MainScreen
import com.example.reachyourgoal.presentation.screen.splashScreen.SplashScreen
import com.example.reachyourgoal.presentation.screen.taskScreen.TaskScreen

@Composable
fun SetupNavGraph(navHostController: NavHostController) {

    NavHost(navController = navHostController, startDestination = Screen.TaskScreen.route) {
        composable(route = Screen.Splash.route) {
            SplashScreen(navHostController)
        }

        composable(route = Screen.LoginScreen.route) {
            LoginScreen(navHostController)
        }

        composable(route = Screen.UserDetailsScreen.route) {
            UserDetailsScreen(navHostController)
        }

        composable(route = Screen.MainScreen.route) {
            MainScreen(navHostController)
        }

        composable(route = "${Screen.TaskScreen.route}") {
            TaskScreen(navHostController)
        }
    }

}