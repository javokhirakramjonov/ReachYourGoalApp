package com.example.reachyourgoal.domain.repository.impl

import com.example.reachyourgoal.data.dao.TaskDao
import com.example.reachyourgoal.domain.model.databaseModel.TaskEntity
import com.example.reachyourgoal.domain.model.databaseModel.TaskFileEntity
import com.example.reachyourgoal.domain.model.local.TaskModel
import com.example.reachyourgoal.domain.model.remote.FirestoreTaskModel
import com.example.reachyourgoal.domain.repository.TaskRepository
import com.example.reachyourgoal.domain.repository.result.SaveTaskResult
import com.example.reachyourgoal.service.FirebaseFileUploader
import com.example.reachyourgoal.service.NetworkStatusService
import com.example.reachyourgoal.util.INTERNET_IS_NOT_AVAILABLE
import com.example.reachyourgoal.util.getErrorMessageOrDefault
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val networkStatusService: NetworkStatusService,
    private val firebaseFileUploader: FirebaseFileUploader,
    private val taskDao: TaskDao
) : TaskRepository {

    private val fireStore = Firebase.firestore

    companion object {
        private const val TASK_COLLECTION = "tasks"
    }

    override fun saveTask(task: TaskModel) = flow {

        val taskEntity = TaskEntity(
            name = task.name,
            description = task.description,
            isOnServer = false
        )

        val taskId = taskDao.insertTask(taskEntity)

        task.taskFiles.forEach { taskFile ->
            taskDao.insertTaskFile(
                TaskFileEntity(
                    fileUri = taskFile.uri.toString(),
                    isOnServer = false,
                    taskId = taskEntity.id
                )
            )
        }

        emit(SaveTaskResult.TaskSavedOffline(taskEntity.id))

        if (!networkStatusService.isInternetAvailable()) {
            throw Exception(INTERNET_IS_NOT_AVAILABLE)
        }

        runCatching {
            fireStore
                .collection(TASK_COLLECTION)
                .document(taskId.toString())
                .set(FirestoreTaskModel(taskEntity.id, task.name, task.description))
        }.getOrElse {
            throw Exception(getErrorMessageOrDefault(it))
        }

        taskDao.updateTaskOnServerStatus(taskEntity.id, true)

        firebaseFileUploader.uploadFiles(taskEntity.id)
    }

    override suspend fun getTask(taskId: UUID) = taskDao.getTaskAndFileFlow(taskId)

}