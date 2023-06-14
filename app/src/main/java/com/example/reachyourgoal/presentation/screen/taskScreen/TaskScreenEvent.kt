package com.example.reachyourgoal.presentation.screen.taskScreen

import android.net.Uri
import java.util.UUID

sealed class TaskScreenEvent {
    data class OnTaskOpened(val taskId: UUID) : TaskScreenEvent()
    data class OnTaskNameChanged(val taskName: String) : TaskScreenEvent()
    data class OnTaskDescriptionChanged(val taskDescription: String) : TaskScreenEvent()
    data class OnFilesAdded(val fileUris: List<Uri>?) : TaskScreenEvent()
    data class OnFileDeleted(val file: Uri) : TaskScreenEvent()
    object OnAddFileBtnClicked : TaskScreenEvent()
    object OnDeleteAllFilesBtnClicked : TaskScreenEvent()
    object OnSaveBtnClicked : TaskScreenEvent()
    object OnBackBtnClicked : TaskScreenEvent()
}