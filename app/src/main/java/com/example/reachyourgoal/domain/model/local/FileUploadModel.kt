package com.example.reachyourgoal.domain.model.local

import android.net.Uri
import java.util.UUID

data class FileUploadModel(
    val taskFileId: UUID,
    val uri: Uri,
    val progress: Int,
    val notificationId: Int,
    val state: FileUploadState
)