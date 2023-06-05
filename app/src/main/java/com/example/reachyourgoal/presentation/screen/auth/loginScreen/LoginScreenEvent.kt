package com.example.reachyourgoal.presentation.screen.auth.loginScreen

sealed class LoginScreenEvent {
    data class OnEmailChanged(val email: String) : LoginScreenEvent()
    data class OnPasswordChanged(val password: String) : LoginScreenEvent()
    object OnLoginBtnClicked : LoginScreenEvent()
}