package com.example.reachyourgoal.presentation.screen.authScreen

data class AuthScreenState(
    val isLoading: Boolean = false,
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
)