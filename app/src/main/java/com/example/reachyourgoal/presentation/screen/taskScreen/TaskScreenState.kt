package com.example.reachyourgoal.presentation.screen.taskScreen

import com.example.reachyourgoal.domain.model.local.AvailableStatus
import com.example.reachyourgoal.domain.model.local.TaskFileModel

data class TaskScreenState(
    val availableStatus: AvailableStatus,
    val isLoading: Boolean,
    val taskName: String,
    val taskDescription: String,
    val taskFiles: List<TaskFileModel>,
    val taskNameError: String?,
    val taskDescriptionError: String?,
    val isFilesBeingSelected: Boolean
)