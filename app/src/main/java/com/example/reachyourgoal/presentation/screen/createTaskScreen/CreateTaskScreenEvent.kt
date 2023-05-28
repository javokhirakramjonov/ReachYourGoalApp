package com.example.reachyourgoal.presentation.screen.createTaskScreen

import java.io.File
import java.util.Calendar

sealed class CreateTaskScreenEvent {
    data class OnTaskNameChanged(val taskName: String) : CreateTaskScreenEvent()
    data class OnTaskDescriptionChanged(val taskDescription: String) : CreateTaskScreenEvent()
    data class OnFileAdded(val file: File) : CreateTaskScreenEvent()
    data class OnFileDeleted(val file: File) : CreateTaskScreenEvent()
    data class OnRemainderTimeChanged(val remainderTime: Calendar?) : CreateTaskScreenEvent()
    data class OnTaskStartTimeChanged(val taskStartTime: Calendar?) : CreateTaskScreenEvent()
    data class OnTaskEndTimeChanged(val taskEndTime: Calendar?) : CreateTaskScreenEvent()
    object OnUpdateRemainderTimeBtnClicked : CreateTaskScreenEvent()
    object OnUpdateTaskStartTimeBtnClicked : CreateTaskScreenEvent()
    object OnUpdateTaskEndTimeBtnClicked : CreateTaskScreenEvent()
    object OnAddFileBtnClicked: CreateTaskScreenEvent()
    object OnCloseBtnClicked : CreateTaskScreenEvent()
    object OnCreateTaskBtnClicked : CreateTaskScreenEvent()
    object OnDeleteAllFilesBtnClicked: CreateTaskScreenEvent()
}