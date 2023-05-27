package com.example.reachyourgoal.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")

    object LoginScreen : Screen("login_screen")

    object RegisterScreen : Screen("register_screen")

    object MainScreen : Screen("main_screen")
}