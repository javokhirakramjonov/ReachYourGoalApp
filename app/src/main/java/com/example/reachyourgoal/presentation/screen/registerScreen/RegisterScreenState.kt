package com.example.reachyourgoal.presentation.screen.registerScreen

data class RegisterScreenState(
    val isLoading: Boolean,
    val firstname: String,
    val lastname: String,
    val username: String,
    val email: String,
    val password: String,
    val passwordRepeat: String,
    val firstnameError: String?,
    val lastnameError: String?,
    val usernameError: String?,
    val emailError: String?,
    val passwordError: String?,
    val passwordRepeatError: String?,
    val imageUri: String?
)