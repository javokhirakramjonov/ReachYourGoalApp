package com.example.reachyourgoal.domain.repository

import com.example.reachyourgoal.domain.model.FileUploadModel
import com.example.reachyourgoal.domain.model.TaskModel
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun createTask(task: TaskModel): Flow<Result<TaskModel>>

    fun startUploadFile(notificationId: Int): Flow<FileUploadModel>

    fun pauseUploadFile(notificationId: Int): FileUploadModel

    fun resumeUploadFile(notificationId: Int): FileUploadModel

    fun cancelUploadFile(notificationId: Int): FileUploadModel

    fun restartUploadFile(notificationId: Int)
}