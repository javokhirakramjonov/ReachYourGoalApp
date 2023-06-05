package com.example.reachyourgoal.presentation.screen.auth.loginScreen

sealed class LoginScreenEffect {
    object NavigateToMainScreen : LoginScreenEffect()
    object NavigateToUserDetailsScreen : LoginScreenEffect()
    data class ShowErrorMessage(val errorMessage: String) : LoginScreenEffect()
    data class ShowSuccessMessage(val successMessage: String) : LoginScreenEffect()
}