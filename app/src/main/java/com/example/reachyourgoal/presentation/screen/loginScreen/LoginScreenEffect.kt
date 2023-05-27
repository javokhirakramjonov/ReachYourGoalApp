package com.example.reachyourgoal.presentation.screen.loginScreen

sealed class LoginScreenEffect {
    object Default : LoginScreenEffect()
    object NavigateToRegisterScreen : LoginScreenEffect()
    object NavigateToMainScreen : LoginScreenEffect()
    data class ShowErrorMessage(val errorMessage: String) : LoginScreenEffect()
}