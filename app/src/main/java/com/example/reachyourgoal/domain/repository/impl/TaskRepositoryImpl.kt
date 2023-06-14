package com.example.reachyourgoal.domain.repository.impl

import com.example.reachyourgoal.data.dao.TaskDao
import com.example.reachyourgoal.domain.model.databaseModel.TaskEntity
import com.example.reachyourgoal.domain.model.databaseModel.TaskFileEntity
import com.example.reachyourgoal.domain.model.local.TaskModel
import com.example.reachyourgoal.domain.model.remote.FirestoreTaskModel
import com.example.reachyourgoal.domain.repository.AuthRepository
import com.example.reachyourgoal.domain.repository.TaskRepository
import com.example.reachyourgoal.domain.repository.result.SaveTaskResult
import com.example.reachyourgoal.service.NetworkStatusService
import com.example.reachyourgoal.service.firebaseFileUploader.FirebaseFileUploader
import com.example.reachyourgoal.util.INTERNET_IS_NOT_AVAILABLE
import com.example.reachyourgoal.util.getErrorMessageOrDefault
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val networkStatusService: NetworkStatusService,
    private val firebaseFileUploader: FirebaseFileUploader,
    private val taskDao: TaskDao
) : TaskRepository {

    init {
        //Temporary
        CoroutineScope(Dispatchers.IO).launch {
            Firebase.auth.signInWithEmailAndPassword(
                "javokhirakromjonov@gmail.com",
                "javokhirakromjonov@gmail.com"
            ).await()
        }
    }

    companion object {
        const val TASK_FILE_COLLECTION = "task_files"
    }

    override fun createTask(task: TaskModel) = flow {

        val taskEntity = createTaskInDatabase(task)

        emit(SaveTaskResult.TaskSavedOffline(taskEntity.id))

        if (!networkStatusService.isInternetAvailable()) {
            throw Exception(INTERNET_IS_NOT_AVAILABLE)
        }

        //saveTaskInFirebase(taskEntity.id, task)

        //taskDao.updateTaskOnServerStatus(taskEntity.id, true)

        //firebaseFileUploader.uploadFiles(taskEntity.id)
    }

    override suspend fun saveTask(taskId: UUID, task: TaskModel) {

        updateTaskInDatabase(taskId, task)

        if (!networkStatusService.isInternetAvailable()) {
            throw Exception(INTERNET_IS_NOT_AVAILABLE)
        }

        //saveTaskInFirebase(taskId, task)

        //taskDao.updateTaskOnServerStatus(taskId, true)

        //firebaseFileUploader.uploadFiles(taskId)
    }

    override suspend fun deleteTask(taskId: UUID) {
        taskDao.deleteTask(taskId)
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
        //TODO
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

    override suspend fun synchronizeWithServer() {
        //TODO
    }

    override fun getAllTasksAndFiles() = taskDao
        .getTasksFlow()
        .map { taskList -> taskList.map { task -> taskDao.getTaskAndFile(task.id) } }

}