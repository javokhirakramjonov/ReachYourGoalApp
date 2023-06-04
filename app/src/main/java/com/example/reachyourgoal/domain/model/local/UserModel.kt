package com.example.reachyourgoal.domain.model.local

import android.net.Uri

data class UserModel(
    val firstname: String,
    val lastname: String,
    val username: String,
    val imageUri: Uri?,
    val sex: Sex
)