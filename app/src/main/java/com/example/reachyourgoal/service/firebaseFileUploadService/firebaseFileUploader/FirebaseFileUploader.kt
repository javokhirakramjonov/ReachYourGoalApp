package com.example.reachyourgoal.service.firebaseFileUploadService.firebaseFileUploader

import com.example.reachyourgoal.domain.model.local.FileUploadModel
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface FirebaseFileUploader {

    suspend fun uploadFiles(taskId: UUID)

    fun startUploadFile(notificationId: Int): Flow<FileUploadModel>

    fun pauseUploadFile(notificationId: Int): FileUploadModel

    fun resumeUploadFile(notificationId: Int): FileUploadModel

    fun cancelUploadFile(notificationId: Int): FileUploadModel

    fun restartUploadFile(notificationId: Int)

}