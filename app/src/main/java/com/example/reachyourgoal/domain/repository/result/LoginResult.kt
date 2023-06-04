package com.example.reachyourgoal.domain.repository.result

sealed class LoginResult {
    object Success : LoginResult()
    object UserDetailsRequired : LoginResult()
    data class Error(val message: String) : LoginResult()
}