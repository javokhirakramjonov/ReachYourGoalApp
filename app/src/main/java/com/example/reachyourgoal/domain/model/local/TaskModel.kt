package com.example.reachyourgoal.domain.model.local

data class TaskModel(
    val name: String,
    val description: String,
    val availableStatus: AvailableStatus,
    val taskFiles: List<TaskFileModel>
)