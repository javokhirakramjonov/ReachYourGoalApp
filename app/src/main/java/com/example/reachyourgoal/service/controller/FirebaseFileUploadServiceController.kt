package com.example.reachyourgoal.service.controller

import com.example.reachyourgoal.domain.model.local.FileUploadModel
import com.example.reachyourgoal.service.firebaseFileUploader.FirebaseFileUploader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class FirebaseFileUploadServiceController @Inject constructor(
    private val firebaseFileUploader: FirebaseFileUploader
) {

    private var job: Job? = null

    private val _uploadState = MutableStateFlow<FileUploadModel?>(null)
    val uploadState = _uploadState.asStateFlow()

    fun startFileUpload(notificationId: Int) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            firebaseFileUploader.startUploadFile(notificationId).collect { fileUploadModel ->
                _uploadState.emit(fileUploadModel)
            }
        }
    }

    fun pauseFileUpload(notificationId: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            _uploadState.emit(firebaseFileUploader.pauseUploadFile(notificationId))
        }
    }

    fun resumeFileUpload(notificationId: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            _uploadState.emit(firebaseFileUploader.resumeUploadFile(notificationId))
        }
    }

    fun cancelFileUpload(notificationId: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            _uploadState.emit(firebaseFileUploader.cancelUploadFile(notificationId))
        }
    }

    fun restartFileUpload(notificationId: Int) {
        firebaseFileUploader.restartUploadFile(notificationId)
    }

    fun stop() {
        job?.cancel()
    }

}