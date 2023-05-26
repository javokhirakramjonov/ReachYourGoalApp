package com.example.reachyourgoal.presentation.screen.authScreen

sealed class AuthScreenEffect {
    object NavigateToRegisterScreen : AuthScreenEffect()
    object NavigateToMainScreen : AuthScreenEffect()
    data class ErrorMessage(val message: String) : AuthScreenEffect()
}