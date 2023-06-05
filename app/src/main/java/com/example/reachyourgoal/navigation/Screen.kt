package com.example.reachyourgoal.navigation

sealed class Screen(val route: String) {

    object Splash : Screen("splash_screen")

    object LoginScreen : Screen("login_screen")

    object UserDetailsScreen : Screen("user_details_screen")

    object MainScreen : Screen("main_screen")

    object CreateTaskScreen : Screen("create_task_screen")
}