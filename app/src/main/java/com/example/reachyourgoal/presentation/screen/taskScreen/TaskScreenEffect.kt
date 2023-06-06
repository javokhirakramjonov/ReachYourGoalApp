package com.example.reachyourgoal.presentation.screen.taskScreen

sealed class TaskScreenEffect {
    object CloseScreen : TaskScreenEffect()
    data class ShowErrorMessage(val errorMessage: String) : TaskScreenEffect()
    data class ShowSuccessMessage(val successMessage: String) : TaskScreenEffect()
}