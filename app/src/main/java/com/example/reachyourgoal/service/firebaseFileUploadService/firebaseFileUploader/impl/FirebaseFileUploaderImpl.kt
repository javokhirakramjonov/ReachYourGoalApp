package com.example.reachyourgoal.service.firebaseFileUploadService.firebaseFileUploader.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.reachyourgoal.data.converters.toFirestoreTaskFile
import com.example.reachyourgoal.data.dao.TaskDao
import com.example.reachyourgoal.domain.model.local.FileUploadModel
import com.example.reachyourgoal.domain.model.local.FileUploadState
import com.example.reachyourgoal.domain.repository.AuthRepository
import com.example.reachyourgoal.service.firebaseFileUploadService.FirebaseFileUploadService
import com.example.reachyourgoal.service.firebaseFileUploadService.firebaseFileUploader.FirebaseFileUploader
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
    private val authRepository: AuthRepository,
    private val taskDao: TaskDao,
    @ApplicationContext private val context: Context,
) : FirebaseFileUploader {


    companion object {
        private const val FILE_DIR = "task_files"
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
        val taskFiles = taskDao.getTaskFilesByTaskId(taskId)

        taskFiles.forEach { taskFile ->
            val fileUploadModel = FileUploadModel(
                taskId,
                Uri.parse(taskFile.fileUri),
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
                    .addOnProgressListener { taskSnapshot ->
                        filesToUpload[notificationId] = filesToUpload[notificationId]!!.copy(
                            progress = ((taskSnapshot.bytesTransferred * 100) / taskSnapshot.totalByteCount).toInt(),
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
                    .addOnSuccessListener { taskSnapshot ->
                        filesToUpload[notificationId] =
                            filesToUpload[notificationId]!!.copy(state = FileUploadState.FINISHED)
                        trySend(filesToUpload[notificationId]!!)

                        CoroutineScope(Dispatchers.IO).launch {
                            val taskFileId = filesToUpload[notificationId]!!.taskFileId
                            filesToUpload.remove(notificationId)

                            runCatching {
                                taskSnapshot.storage.downloadUrl.await()
                            }.getOrNull()?.let {
                                saveFileToFirestore(
                                    taskFileId,
                                    taskSnapshot.toString()
                                )
                            }
                        }
                    }
            }
        awaitClose()
    }

    private suspend fun saveFileToFirestore(taskFileId: UUID, taskFileUrl: String) {
        val taskFile = taskDao.getTaskFileById(taskFileId)
        firestore
            .collection(FILE_DIR)
            .document(taskFileId.toString())
            .set(
                taskFile.toFirestoreTaskFile(
                    context.contentResolver,
                    authRepository.getUserId(),
                    taskFileUrl
                )
            )
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