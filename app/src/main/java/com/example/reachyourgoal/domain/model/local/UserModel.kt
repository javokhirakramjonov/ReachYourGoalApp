package com.example.reachyourgoal.domain.model.local

import android.net.Uri

data class UserModel(
    val firstname: String,
    val lastname: String,
    val username: String,
    val email: String,
    val password: String,
    val imageUri: Uri?
)