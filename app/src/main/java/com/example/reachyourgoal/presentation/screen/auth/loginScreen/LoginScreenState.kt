package com.example.reachyourgoal.presentation.screen.auth.loginScreen

data class LoginScreenState(
    val isLoading: Boolean,
    val email: String,
    val password: String,
    val emailError: String?,
    val passwordError: String?,
)