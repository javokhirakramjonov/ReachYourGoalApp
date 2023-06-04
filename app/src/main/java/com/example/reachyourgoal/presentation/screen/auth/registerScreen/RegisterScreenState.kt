package com.example.reachyourgoal.presentation.screen.auth.registerScreen

data class RegisterScreenState(
    val isLoading: Boolean,
    val email: String,
    val password: String,
    val passwordRepeat: String,
    val emailError: String?,
    val passwordError: String?,
    val passwordRepeatError: String?
)