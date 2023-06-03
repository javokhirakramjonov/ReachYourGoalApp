package com.example.reachyourgoal.presentation.screen.createTaskScreen

import android.net.Uri

sealed class CreateTaskScreenEvent {
    data class OnTaskNameChanged(val taskName: String) : CreateTaskScreenEvent()
    data class OnTaskDescriptionChanged(val taskDescription: String) : CreateTaskScreenEvent()
    data class OnFilesAdded(val fileUris: List<Uri>?) : CreateTaskScreenEvent()
    data class OnFileDeleted(val fileUri: Uri) : CreateTaskScreenEvent()
    object OnAddFileBtnClicked : CreateTaskScreenEvent()
    object OnCloseBtnClicked : CreateTaskScreenEvent()
    object OnCreateTaskBtnClicked : CreateTaskScreenEvent()
    object OnDeleteAllFilesBtnClicked : CreateTaskScreenEvent()
}