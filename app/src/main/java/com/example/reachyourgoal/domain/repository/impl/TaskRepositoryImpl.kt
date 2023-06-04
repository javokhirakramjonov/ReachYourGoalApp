package com.example.reachyourgoal.domain.repository.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.reachyourgoal.data.dao.TaskDao
import com.example.reachyourgoal.domain.model.databaseModel.TaskEntity
import com.example.reachyourgoal.domain.model.databaseModel.TaskFileEntity
import com.example.reachyourgoal.domain.model.local.FileUploadModel
import com.example.reachyourgoal.domain.model.local.FileUploadState
import com.example.reachyourgoal.domain.model.local.TaskModel
import com.example.reachyourgoal.domain.repository.TaskRepository
import com.example.reachyourgoal.service.FirebaseFileUploadService
import com.example.reachyourgoal.service.FirebaseFileUploadService.Companion.NOTIFICATION_ID
import com.example.reachyourgoal.service.FirebaseFileUploadService.Companion.STATE
import com.example.reachyourgoal.service.NetworkStatusService
import com.example.reachyourgoal.util.INTERNET_IS_NOT_AVAILABLE
import com.example.reachyourgoal.util.getErrorMessageOrDefault
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkStatusService: NetworkStatusService,
    private val taskDao: TaskDao
) : TaskRepository {

    private var notificationId = 1
    private val filesToUpload = hashMapOf<Int, FileUploadModel>()
    private val uploadTasks = hashMapOf<Int, UploadTask>()

    private val auth = Firebase.auth
    private val fireStore = Firebase.firestore
    private val storage = Firebase.storage.reference

    companion object {
        private const val COLLECTION_TASK = "tasks"
        private const val FILE_DIR = "task_files"
    }

    override fun createTask(task: TaskModel) = flow {

        var taskEntity = taskDao.addTask(
            TaskEntity(
                name = task.name,
                description = task.description,
                isUploadedToServer = false
            )
        )

        val taskFileEntities = task.fileUris.map { uri ->
            taskDao.addTaskFile(
                TaskFileEntity(
                    fileUri = uri.toString(),
                    isOnServer = false,
                    taskId = taskEntity.id
                )
            )
        }

        emit(Unit)

        if (!networkStatusService.isInternetAvailable()) {
            throw Exception(INTERNET_IS_NOT_AVAILABLE)
        }

        runCatching {
            fireStore
                .collection(COLLECTION_TASK)
                .document(getEmail())
                .collection(COLLECTION_TASK)
                .document(taskEntity.id.toString())
                .set(
                    hashMapOf(
                        "name" to task.name,
                        "description" to task.description
                    )
                )
        }.getOrElse {
            throw Throwable(getErrorMessageOrDefault(it))
        }

        taskEntity = taskDao.updateTask(taskEntity.copy(isUploadedToServer = true))

        taskFileEntities.forEach { taskFileEntity ->
            val fileUploadModel = FileUploadModel(
                taskFileEntity.id,
                Uri.parse(taskFileEntity.fileUri),
                0,
                notificationId++,
                FileUploadState.NOT_STARTED
            )
            filesToUpload[fileUploadModel.notificationId] = fileUploadModel
            startService(fileUploadModel.notificationId)
        }
    }

    private fun startService(notificationId: Int) {
        val intent = Intent(context, FirebaseFileUploadService::class.java)
        intent.putExtra(STATE, FileUploadState.NOT_STARTED.name)
        intent.putExtra(NOTIFICATION_ID, notificationId)
        context.startService(intent)
    }

    override fun startUploadFile(notificationId: Int) = callbackFlow {
        val file = filesToUpload[notificationId]!!
        trySend(file.copy(state = FileUploadState.STARTED))
        uploadTasks[notificationId] = storage
            .child(FILE_DIR)
            .child(file.uri.lastPathSegment.toString())
            .putFile(file.uri)
            .also { task ->
                task
                    .addOnProgressListener {
                        filesToUpload[notificationId] = filesToUpload[notificationId]!!.copy(
                            progress = ((it.bytesTransferred * 100) / it.totalByteCount).toInt(),
                            state = FileUploadState.IN_PROGRESS
                        )
                        trySend(filesToUpload[notificationId]!!)
                    }
                    .addOnFailureListener {
                        filesToUpload[notificationId] =
                            filesToUpload[notificationId]!!.copy(state = FileUploadState.FAILED)
                        trySend(filesToUpload[notificationId]!!)
                        filesToUpload.remove(notificationId)
                    }
                    .addOnSuccessListener {
                        filesToUpload[notificationId] =
                            filesToUpload[notificationId]!!.copy(state = FileUploadState.FINISHED)
                        trySend(filesToUpload[notificationId]!!)

                        launch {

                            taskDao.updateTaskUploadStatus(
                                filesToUpload[notificationId]!!.taskFileId,
                                true
                            )

                            //TODO NOTIFY FIRESTORE
                        }

                        filesToUpload.remove(notificationId)
                    }
            }
        awaitClose()
    }

    override fun pauseUploadFile(notificationId: Int): FileUploadModel {
        uploadTasks[notificationId]?.pause()
        filesToUpload[notificationId] =
            filesToUpload[notificationId]!!.copy(state = FileUploadState.PAUSED)
        return filesToUpload[notificationId]!!
    }

    override fun resumeUploadFile(notificationId: Int): FileUploadModel {
        uploadTasks[notificationId]?.resume()
        filesToUpload[notificationId] =
            filesToUpload[notificationId]!!.copy(state = FileUploadState.IN_PROGRESS)
        return filesToUpload[notificationId]!!
    }

    override fun cancelUploadFile(notificationId: Int): FileUploadModel {
        uploadTasks[notificationId]?.cancel()
        filesToUpload[notificationId] =
            filesToUpload[notificationId]!!.copy(state = FileUploadState.CANCELED)
        return filesToUpload[notificationId]!!
    }

    override fun restartUploadFile(notificationId: Int) {
        uploadTasks.remove(notificationId)
        startService(notificationId)
    }

    private fun getEmail() = auth.currentUser!!.email!!

}