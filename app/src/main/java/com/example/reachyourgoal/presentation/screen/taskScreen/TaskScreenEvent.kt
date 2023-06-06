package com.example.reachyourgoal.presentation.screen.taskScreen

import android.net.Uri
import com.example.reachyourgoal.domain.model.local.TaskFileModel

sealed class TaskScreenEvent {
    data class OnTaskNameChanged(val taskName: String) : TaskScreenEvent()
    data class OnTaskDescriptionChanged(val taskDescription: String) : TaskScreenEvent()
    data class OnFilesAdded(val fileUris: List<Uri>?) : TaskScreenEvent()
    data class OnFileDeleted(val file: TaskFileModel) : TaskScreenEvent()
    object OnAddFileBtnClicked : TaskScreenEvent()
    object OnCloseBtnClicked : TaskScreenEvent()
    object OnSaveBtnClicked : TaskScreenEvent()
    object OnDeleteAllFilesBtnClicked : TaskScreenEvent()
}