package com.example.reachyourgoal.presentation.screen.auth.registerScreen

sealed class RegisterScreenEffect {
    object NavigateToLoginScreen : RegisterScreenEffect()
    object NavigateToUserDetailsScreen : RegisterScreenEffect()
    data class ShowErrorMessage(val errorMessage: String) : RegisterScreenEffect()
    data class ShowSuccessMessage(val successMessage: String) : RegisterScreenEffect()
}