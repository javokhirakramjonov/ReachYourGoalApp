package com.example.reachyourgoal.presentation.screen.tasksScreen

import java.util.UUID

sealed class TasksScreenEffect {
    data class OpenTask(val taskId: UUID?) : TasksScreenEffect()
    data class ShowErrorMessage(val errorMessage: String) : TasksScreenEffect()
    data class ShowSuccessMessage(val successMessage: String) : TasksScreenEffect()
}