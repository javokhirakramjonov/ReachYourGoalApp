package com.example.reachyourgoal.presentation.screen.registerScreen

sealed class RegisterScreenEffect {
    object NavigateToLoginScreen : RegisterScreenEffect()
    object NavigateToMainScreen : RegisterScreenEffect()
    object ShowPhotoPicker : RegisterScreenEffect()
    data class ShowErrorMessage(val errorMessage: String) : RegisterScreenEffect()
}