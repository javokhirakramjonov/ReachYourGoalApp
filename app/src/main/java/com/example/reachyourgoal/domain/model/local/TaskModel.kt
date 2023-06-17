package com.example.reachyourgoal.domain.model.local

import android.net.Uri

data class TaskModel(
    val name: String,
    val description: String,
    val taskFiles: List<Uri>
)