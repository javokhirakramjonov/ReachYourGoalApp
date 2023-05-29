package com.example.reachyourgoal.presentation.screen.createTaskScreen

import android.net.Uri
import java.util.Calendar

data class CreateTaskScreenState(
    val isLoading: Boolean,
    val taskName: String,
    val taskDescription: String,
    val fileUris: List<Uri>,
    val remainderTime: Calendar?,
    val startTime: Calendar?,
    val endTime: Calendar?,
    val taskNameError: String?,
    val taskDescriptionError: String?,
    val isStartTimeBeingSelected: Boolean,
    val isEndTimeBeingSelected: Boolean,
    val isRemainderTimeBeingSelected: Boolean,
    val isFilesBeingSelected: Boolean
)