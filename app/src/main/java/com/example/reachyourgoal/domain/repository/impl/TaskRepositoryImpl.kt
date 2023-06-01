package com.example.reachyourgoal.domain.repository.impl

import android.content.Context
import android.content.Intent
import com.example.reachyourgoal.domain.model.FileUploadModel
import com.example.reachyourgoal.domain.model.FileUploadState
import com.example.reachyourgoal.domain.model.TaskModel
import com.example.reachyourgoal.domain.repository.TaskRepository
import com.example.reachyourgoal.service.FirebaseFileUploadService
import com.example.reachyourgoal.service.FirebaseFileUploadService.Companion.NOTIFICATION_ID
import com.example.reachyourgoal.service.FirebaseFileUploadService.Companion.STATE
import com.example.reachyourgoal.service.NetworkStatusService
import com.example.reachyourgoal.util.INTERNET_IS_NOT_AVAILABLE
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkStatusService: NetworkStatusService
) : TaskRepository {

    private var notificationId = 1
    private val filesToUpload = hashMapOf<Int, FileUploadModel>()
    private val uploadTasks = hashMapOf<Int, UploadTask>()

    private val storage = Firebase.storage.reference

    companion object {
        private const val FILE_DIR = "task_files"
    }

    override fun createTask(task: TaskModel): Flow<Result<TaskModel>> = flow {
        if (!networkStatusService.isInternetAvailable()) {
            emit(Result.failure(Error(INTERNET_IS_NOT_AVAILABLE)))
            return@flow
        }
        val x = kotlin.runCatching {
            Firebase.auth.signInWithEmailAndPassword(
                "javokhirakromjonov@gmail.com",
                "javokhirakromjonov@gmail.com"
            ).await()
        }

        task.fileUris?.let { uris ->
            uris.forEach { uri ->
                val fileUploadModel = FileUploadModel(
                    uri,
                    0,
                    notificationId++,
                    FileUploadState.NOT_STARTED
                )
                filesToUpload[fileUploadModel.notificationId] = fileUploadModel
                startService(fileUploadModel.notificationId)
            }
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
                        filesToUpload[notificationId] = filesToUpload[notificationId]!!.copy(
                            progress = 100,
                            state = FileUploadState.IN_PROGRESS
                        )
                        trySend(filesToUpload[notificationId]!!)

                        filesToUpload[notificationId] =
                            filesToUpload[notificationId]!!.copy(state = FileUploadState.FINISHED)
                        trySend(filesToUpload[notificationId]!!)

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

}