package com.example.reachyourgoal.service

import com.example.reachyourgoal.domain.model.databaseModel.TaskFileEntity
import com.example.reachyourgoal.domain.model.local.FileUploadModel
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface FirebaseFileUploader {

    fun uploadFiles(taskId: UUID, taskFiles: List<TaskFileEntity>)

    fun startUploadFile(notificationId: Int): Flow<FileUploadModel>

    fun pauseUploadFile(notificationId: Int): FileUploadModel

    fun resumeUploadFile(notificationId: Int): FileUploadModel

    fun cancelUploadFile(notificationId: Int): FileUploadModel

    fun restartUploadFile(notificationId: Int)

}