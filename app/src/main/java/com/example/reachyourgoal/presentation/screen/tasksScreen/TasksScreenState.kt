package com.example.reachyourgoal.presentation.screen.tasksScreen

import com.example.reachyourgoal.domain.model.databaseModel.TaskAndFileEntity

data class TasksScreenState(
    val isLoading: Boolean,
    val tasks: List<TaskAndFileEntity>,
)