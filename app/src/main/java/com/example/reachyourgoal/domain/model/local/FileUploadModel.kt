package com.example.reachyourgoal.domain.model.local

import android.net.Uri
import java.io.Serializable

data class FileUploadModel(
    val uri: Uri,
    val progress: Int,
    val notificationId: Int,
    val state: FileUploadState
) : Serializable