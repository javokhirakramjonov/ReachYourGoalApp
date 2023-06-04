package com.example.reachyourgoal.presentation.screen.createTaskScreen

sealed class CreateTaskScreenEffect {
    object CloseScreen : CreateTaskScreenEffect()
    data class ShowErrorMessage(val errorMessage: String) : CreateTaskScreenEffect()
    data class ShowSuccessMessage(val successMessage: String) : CreateTaskScreenEffect()
}