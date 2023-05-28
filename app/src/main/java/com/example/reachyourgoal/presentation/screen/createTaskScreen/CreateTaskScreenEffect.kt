package com.example.reachyourgoal.presentation.screen.createTaskScreen

sealed class CreateTaskScreenEffect {
    object CloseScreen: CreateTaskScreenEffect()
    object ShowFilePicker: CreateTaskScreenEffect()
    data class ShowErrorMessage(val message: String): CreateTaskScreenEffect()
}