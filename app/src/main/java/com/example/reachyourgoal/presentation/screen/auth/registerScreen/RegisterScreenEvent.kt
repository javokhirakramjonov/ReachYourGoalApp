package com.example.reachyourgoal.presentation.screen.auth.registerScreen

sealed class RegisterScreenEvent {
    data class OnEmailChanged(val email: String) : RegisterScreenEvent()
    data class OnPasswordChanged(val password: String) : RegisterScreenEvent()
    data class OnPasswordRepeatChanged(val password: String) : RegisterScreenEvent()
    object OnRegisterBtnClicked : RegisterScreenEvent()
    object OnLoginBtnClicked : RegisterScreenEvent()
}