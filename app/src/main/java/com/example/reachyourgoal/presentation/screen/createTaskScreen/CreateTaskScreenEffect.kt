package com.example.reachyourgoal.presentation.screen.createTaskScreen

sealed class CreateTaskScreenEffect {
    object CloseScreen : CreateTaskScreenEffect()
    data class ShowErrorMessage(val message: String) : CreateTaskScreenEffect()
}