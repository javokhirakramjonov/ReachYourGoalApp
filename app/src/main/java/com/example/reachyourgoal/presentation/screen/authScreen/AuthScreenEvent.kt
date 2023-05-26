package com.example.reachyourgoal.presentation.screen.authScreen

sealed class AuthScreenEvent {
    data class OnEmailChanged(val email: String) : AuthScreenEvent()
    data class OnPasswordChanged(val password: String) : AuthScreenEvent()
    object OnLoginBtnClicked : AuthScreenEvent()
}