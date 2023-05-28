package com.example.reachyourgoal.presentation.screen.createTaskScreen

import java.io.File
import java.util.Calendar

data class CreateTaskScreenState(
    val isLoading: Boolean,
    val taskName: String,
    val taskDescription: String,
    val files: List<File>,
    val remainderTime: Calendar?,
    val startTime: Calendar?,
    val endTime: Calendar?,
    val taskNameError: String?,
    val taskDescriptionError: String?,
    val startTimeSelecting: Boolean,
    val endTimeSelecting: Boolean,
    val remainderTimeSelecting: Boolean,
)