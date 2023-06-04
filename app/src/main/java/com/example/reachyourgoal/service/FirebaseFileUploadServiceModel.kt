package com.example.reachyourgoal.service

import com.example.reachyourgoal.domain.model.local.FileUploadModel
import com.example.reachyourgoal.domain.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class FirebaseFileUploadServiceModel @Inject constructor(
    private val repository: TaskRepository
) {

    private var job: Job? = null

    private val _uploadState = MutableStateFlow<FileUploadModel?>(null)
    val uploadState = _uploadState.asStateFlow()

    fun startFileUpload(notificationId: Int) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            repository.startUploadFile(notificationId).collect {
                _uploadState.emit(it)
            }
        }
    }

    fun pauseFileUpload(notificationId: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            _uploadState.emit(repository.pauseUploadFile(notificationId))
        }
    }

    fun resumeFileUpload(notificationId: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            _uploadState.emit(repository.resumeUploadFile(notificationId))
        }
    }

    fun cancelFileUpload(notificationId: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            _uploadState.emit(repository.cancelUploadFile(notificationId))
        }
    }

    fun restartFileUpload(notificationId: Int) {
        repository.restartUploadFile(notificationId)
    }

    fun stop() {
        job?.cancel()
    }

}