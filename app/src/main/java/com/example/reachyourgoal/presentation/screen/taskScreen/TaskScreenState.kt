package com.example.reachyourgoal.presentation.screen.taskScreen

import android.net.Uri

data class TaskScreenState(
    val taskName: String,
    val taskDescription: String,
    val taskFiles: List<Uri>,
    val taskNameError: String?,
    val taskDescriptionError: String?,
    val isFilesBeingSelected: Boolean
)