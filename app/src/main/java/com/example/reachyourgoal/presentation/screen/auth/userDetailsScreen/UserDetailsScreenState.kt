package com.example.reachyourgoal.presentation.screen.auth.userDetailsScreen

import android.net.Uri
import com.example.reachyourgoal.domain.model.local.Sex

data class UserDetailsScreenState(
    val isLoading: Boolean,
    val firstname: String,
    val lastname: String,
    val username: String,
    val sex: Sex,
    val firstnameError: String?,
    val lastnameError: String?,
    val usernameError: String?,
    val sexError: String?,
    val imageUri: Uri?
)