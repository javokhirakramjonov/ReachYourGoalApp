package com.example.reachyourgoal.domain.repository.impl

import com.example.reachyourgoal.data.dao.TaskDao
import com.example.reachyourgoal.domain.model.databaseModel.TaskEntity
import com.example.reachyourgoal.domain.model.databaseModel.TaskFileEntity
import com.example.reachyourgoal.domain.model.local.AvailableStatus
import com.example.reachyourgoal.domain.model.local.TaskModel
import com.example.reachyourgoal.domain.model.remote.FirestoreTaskModel
import com.example.reachyourgoal.domain.repository.AuthRepository
import com.example.reachyourgoal.domain.repository.TaskRepository
import com.example.reachyourgoal.domain.repository.result.SaveTaskResult
import com.example.reachyourgoal.service.NetworkStatusService
import com.example.reachyourgoal.service.firebaseFileUploader.FirebaseFileUploader
import com.example.reachyourgoal.util.INTERNET_IS_NOT_AVAILABLE
import com.example.reachyourgoal.util.getErrorMessageOrDefault
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val networkStatusService: NetworkStatusService,
    private val firebaseFileUploader: FirebaseFileUploader,
    private val taskDao: TaskDao
) : TaskRepository {

    private val fireStore = Firebase.firestore
    private val storage = Firebase.storage.reference

    companion object {
        private const val TASK_COLLECTION = "tasks"
        const val TASK_FILE_COLLECTION = "task_files"
    }

    override fun createTask(task: TaskModel) = flow {

        val taskEntity = createTaskInDatabase(task)

        emit(SaveTaskResult.TaskSavedOffline(taskEntity.id))

        if (!networkStatusService.isInternetAvailable()) {
            throw Exception(INTERNET_IS_NOT_AVAILABLE)
        }

        saveTaskInFirebase(taskEntity.id, task)

        taskDao.updateTaskOnServerStatus(taskEntity.id, true)

        firebaseFileUploader.uploadFiles(taskEntity.id)
    }

    override suspend fun saveTask(taskId: UUID, task: TaskModel) {

        updateTaskInDatabase(taskId, task)

        if (!networkStatusService.isInternetAvailable()) {
            throw Exception(INTERNET_IS_NOT_AVAILABLE)
        }

        saveTaskInFirebase(taskId, task)

        taskDao.updateTaskOnServerStatus(taskId, true)

        firebaseFileUploader.uploadFiles(taskId)
    }

    private suspend fun createTaskInDatabase(task: TaskModel): TaskEntity {
        val taskEntity = TaskEntity(
            name = task.name,
            description = task.description,
            isOnServer = false
        )

        taskDao.insertTask(taskEntity)

        task.taskFiles.forEach { taskFile ->
            taskDao.insertTaskFile(
                TaskFileEntity(
                    fileUri = taskFile.uri.toString(),
                    isOnServer = false,
                    taskId = taskEntity.id
                )
            )
        }

        return taskEntity
    }

    private suspend fun saveTaskInFirebase(taskId: UUID, task: TaskModel) {
        runCatching {
            fireStore
                .collection(TASK_COLLECTION)
                .document(taskId.toString())
                .set(
                    FirestoreTaskModel(
                        authRepository.getEmail(),
                        taskId,
                        task.name,
                        task.description
                    )
                )
                .await()
        }.getOrElse {
            throw Exception(getErrorMessageOrDefault(it))
        }
    }

    private suspend fun updateTaskInDatabase(taskId: UUID, task: TaskModel) {
        val taskEntity = taskDao.getTaskById(taskId)

        taskDao.updateTask(
            taskEntity.copy(
                name = task.name,
                description = task.description,
                isOnServer = false
            )
        )

        val taskFileEntities = taskDao.getTaskFilesByTaskId(taskId)
        val taskFiles = task.taskFiles

        val fileEntityMap = taskFileEntities.associateBy { it.fileUri }
        val fileMap = taskFiles.associateBy { it.uri.toString() }

        val deleteList = taskFileEntities.filterNot { fileMap.containsKey(it.fileUri) }
        val insertList = taskFiles.filterNot { fileEntityMap.containsKey(it.uri.toString()) }

        deleteList.forEach {
            taskDao.deleteTaskFile(it)
        }

        insertList.forEach {
            taskDao.insertTaskFile(
                TaskFileEntity(
                    fileUri = it.uri.toString(),
                    isOnServer = false,
                    taskId = taskId
                )
            )
        }
    }

    override suspend fun getTask(taskId: UUID) = taskDao.getTaskAndFileFlow(taskId)

    override suspend fun loadTasksFromServer() {
        if (!networkStatusService.isInternetAvailable()) {
            throw Exception(INTERNET_IS_NOT_AVAILABLE)
        }

        val resultTask = runCatching {
            fireStore
                .collection(TASK_COLLECTION)
                .whereEqualTo("email", authRepository.getEmail())
                .get()
                .await()
        }.getOrElse {
            throw Exception(getErrorMessageOrDefault(it))
        }

        resultTask
            .map {
                with(it.toObject(FirestoreTaskModel::class.java)) {
                    TaskEntity(taskId, name, description, true)
                }
            }
            .forEach {
                //TODO update if it is newer than in local
                taskDao.insertTask(it)
            }

        //TODO download task files
    }

    override fun getAllTasks(): Flow<List<TaskEntity>> {
        return taskDao.getTasksFlow()
    }

}