package com.example.reachyourgoal.service.firebaseFileUploader

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.reachyourgoal.data.dao.TaskDao
import com.example.reachyourgoal.domain.model.local.FileUploadModel
import com.example.reachyourgoal.domain.model.local.FileUploadState
import com.example.reachyourgoal.domain.model.remote.FirestoreTaskFileModel
import com.example.reachyourgoal.service.FirebaseFileUploadService
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class FirebaseFileUploaderImpl @Inject constructor(
    private val taskDao: TaskDao,
    @ApplicationContext private val context: Context,
) : FirebaseFileUploader {

    companion object {
        private const val FILE_DIR = "task_files"
        private const val TASK_FILE_COLLECTION = "task_files"
    }

    private val firestore = Firebase.firestore
    private val storage = Firebase.storage.reference

    private var notificationId = 1
    private val filesToUpload = hashMapOf<Int, FileUploadModel>()
    private val uploadTasks = hashMapOf<Int, UploadTask>()

    private fun startService(notificationId: Int) {
        val intent = Intent(context, FirebaseFileUploadService::class.java)
        intent.putExtra(FirebaseFileUploadService.STATE, FileUploadState.NOT_STARTED.name)
        intent.putExtra(FirebaseFileUploadService.NOTIFICATION_ID, notificationId)
        context.startService(intent)
    }

    override suspend fun uploadFiles(taskId: UUID) {
        taskDao
            .getTaskFilesByTaskId(taskId)
            .filterNot { it.isOnServer }
            .forEach { taskFileEntity ->
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

                        CoroutineScope(Dispatchers.IO).launch {
                            val taskFileId = filesToUpload[notificationId]!!.taskFileId
                            filesToUpload.remove(notificationId)

                            runCatching {
                                it.storage.downloadUrl.await()
                            }.getOrNull()?.let {
                                saveFileToFirestore(
                                    taskFileId,
                                    it.toString()
                                )
                            }
                        }
                    }
            }
        awaitClose()
    }

    private suspend fun saveFileToFirestore(taskFileId: UUID, taskFileUrl: String) {
        val taskFileEntity = taskDao.getTaskFileById(taskFileId)
        val result = runCatching {
            firestore
                .collection(TASK_FILE_COLLECTION)
                .document(taskFileId.toString())
                .set(FirestoreTaskFileModel(taskFileEntity.taskId, taskFileId, taskFileUrl))
                .await()
        }
        if (result.isSuccess) {
            taskDao.updateTaskUploadStatus(taskFileId, true)
        }
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