package com.example.reachyourgoal.navigation

sealed class Screen(val route: String) {
    object Splash: Screen("splash_screen")

    object AuthScreen: Screen("auth_screen")

    object MainScreen: Screen("main_screen")
}