package com.example.reachyourgoal.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.reachyourgoal.presentation.screen.createTaskScreen.CreateTaskScreen
import com.example.reachyourgoal.presentation.screen.loginScreen.LoginScreen
import com.example.reachyourgoal.presentation.screen.mainScreen.MainScreen
import com.example.reachyourgoal.presentation.screen.registerScreen.RegisterScreen
import com.example.reachyourgoal.presentation.screen.splashScreen.SplashScreen

@Composable
fun SetupNavGraph(navHostController: NavHostController) {

    NavHost(navController = navHostController, startDestination = Screen.CreateTaskScreen.route) {
        composable(route = Screen.Splash.route) {
            SplashScreen(navHostController)
        }

        composable(route = Screen.LoginScreen.route) {
            LoginScreen(navHostController)
        }

        composable(route = Screen.MainScreen.route) {
            MainScreen(navHostController)
        }

        composable(route = Screen.RegisterScreen.route) {
            RegisterScreen(navHostController)
        }

        composable(route = Screen.CreateTaskScreen.route) {
            CreateTaskScreen(navHostController)
        }
    }

}