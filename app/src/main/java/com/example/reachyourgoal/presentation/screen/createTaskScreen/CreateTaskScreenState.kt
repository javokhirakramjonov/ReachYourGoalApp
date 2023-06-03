package com.example.reachyourgoal.presentation.screen.createTaskScreen

import android.net.Uri

data class CreateTaskScreenState(
    val isLoading: Boolean,
    val taskName: String,
    val taskDescription: String,
    val fileUris: List<Uri>,
    val taskNameError: String?,
    val taskDescriptionError: String?,
    val isFilesBeingSelected: Boolean
)