package com.example.reachyourgoal.domain.model.local

import android.net.Uri

data class FileUploadModel(
    val taskFileId: Long,
    val uri: Uri,
    val progress: Int,
    val notificationId: Int,
    val state: FileUploadState
)