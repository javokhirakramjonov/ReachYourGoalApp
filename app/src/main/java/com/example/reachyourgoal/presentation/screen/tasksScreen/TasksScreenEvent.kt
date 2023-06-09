package com.example.reachyourgoal.presentation.screen.tasksScreen

import java.util.UUID

sealed class TasksScreenEvent {
    object OnLoadTasks : TasksScreenEvent()
    object OnCreateTaskBtnClicked : TasksScreenEvent()
    data class OnOpenTask(val taskId: UUID) : TasksScreenEvent()
    data class OnDeleteTask(val taskId: UUID) : TasksScreenEvent()
    data class OnDeleteTaskConfirmed(val taskId: UUID) : TasksScreenEvent()
}