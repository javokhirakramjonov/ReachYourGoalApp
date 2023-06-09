package com.example.reachyourgoal.presentation.screen.taskScreen

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.reachyourgoal.common.BaseViewModel
import com.example.reachyourgoal.common.Validators
import com.example.reachyourgoal.domain.model.local.AvailableStatus
import com.example.reachyourgoal.domain.model.local.TaskFileModel
import com.example.reachyourgoal.domain.model.local.TaskModel
import com.example.reachyourgoal.domain.repository.TaskRepository
import com.example.reachyourgoal.domain.repository.result.SaveTaskResult
import com.example.reachyourgoal.util.EMPTY_STRING
import com.example.reachyourgoal.util.within
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TaskScreenViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) :
    BaseViewModel<TaskScreenState, TaskScreenEvent, TaskScreenEffect>() {

    private var loadTaskJob: Job? = null
    private var taskId: UUID? = null

    private val _uiState = MutableStateFlow(
        TaskScreenState(
            AvailableStatus.EDITING,
            false,
            EMPTY_STRING,
            EMPTY_STRING,
            emptyList(),
            null,
            null,
            false
        )
    )

    override val uiState: StateFlow<TaskScreenState>
        get() = _uiState

    private val _uiEffect = MutableSharedFlow<TaskScreenEffect>()
    override val uiEffect: SharedFlow<TaskScreenEffect>
        get() = _uiEffect

    override fun onEvent(event: TaskScreenEvent) {
        when (event) {
            is TaskScreenEvent.OnTaskNameChanged -> onTaskNameChanged(event.taskName)
            is TaskScreenEvent.OnTaskDescriptionChanged -> onTaskDescriptionChanged(event.taskDescription)
            is TaskScreenEvent.OnFilesAdded -> onFilesAdded(event.fileUris)
            is TaskScreenEvent.OnFileDeleted -> onFileDeleted(event.file)
            is TaskScreenEvent.OnTaskEditing -> setTaskId(event.taskId)
            TaskScreenEvent.OnCloseBtnClicked -> onCloseBtnClicked()
            TaskScreenEvent.OnSaveBtnClicked -> onSaveBtnClicked()
            TaskScreenEvent.OnDeleteAllFilesBtnClicked -> onDeleteAllFilesBtnClicked()
            TaskScreenEvent.OnAddFileBtnClicked -> onAddFileBtnClicked()
        }
    }

    private fun setTaskId(taskId: UUID) {
        this.taskId = taskId
        loadTask()
    }

    private fun onTaskNameChanged(taskName: String) {
        _uiState.update { state ->
            state.copy(
                taskName = taskName,
                taskNameError = null,
                availableStatus = AvailableStatus.EDITING
            )
        }
    }

    private fun onTaskDescriptionChanged(taskDescription: String) {
        _uiState.update { state ->
            state.copy(
                taskDescription = taskDescription,
                taskDescriptionError = null,
                availableStatus = AvailableStatus.EDITING
            )
        }
    }

    private fun onFilesAdded(selectedFileUris: List<Uri>?) {
        _uiState.update { state ->
            val fileUrisAndOnServerStatuses = state.taskFiles.toMutableList()
            selectedFileUris?.let { uris ->
                fileUrisAndOnServerStatuses.addAll(
                    0,
                    uris.map { uri -> TaskFileModel(uri, AvailableStatus.EDITING) })
            }
            state.copy(
                taskFiles = fileUrisAndOnServerStatuses,
                isFilesBeingSelected = false,
                availableStatus = AvailableStatus.EDITING
            )
        }
    }

    private fun onFileDeleted(file: TaskFileModel) {
        _uiState.update { state ->
            val fileUrisAndOnServerStatuses = state.taskFiles.toMutableList()
            fileUrisAndOnServerStatuses.remove(file)
            state.copy(
                taskFiles = fileUrisAndOnServerStatuses,
                availableStatus = AvailableStatus.EDITING
            )
        }
    }

    private fun onCloseBtnClicked() {
        viewModelScope.launch {
            _uiEffect.emit(TaskScreenEffect.CloseScreen)
        }
    }

    private fun onSaveBtnClicked() {
        validateTaskName()
        validateTaskDescription()
        _uiState.value.within {
            if (
                listOfNotNull(
                    taskNameError,
                    taskDescriptionError
                ).isNotEmpty()
            ) {
                return
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            val task = TaskModel(
                _uiState.value.taskName,
                _uiState.value.taskDescription,
                _uiState.value.availableStatus,
                _uiState.value.taskFiles
            )

            taskId?.let {
                runCatching {
                    taskRepository.saveTask(it, task)
                }.getOrElse { error ->
                    error.message?.let { errorMessage ->
                        _uiEffect.emit(TaskScreenEffect.ShowErrorMessage(errorMessage))
                    }
                }
            } ?: taskRepository.createTask(task)
                .onStart {
                    _uiState.update { state ->
                        state.copy(isLoading = true)
                    }
                }
                .onEach {
                    when (it) {
                        is SaveTaskResult.TaskSavedOffline -> {
                            setTaskId(it.taskId)
                        }
                    }
                }
                .catch { error ->
                    error.message?.let { errorMessage ->
                        _uiEffect.emit(TaskScreenEffect.ShowErrorMessage(errorMessage))
                    }
                }
                .onCompletion { error ->
                    _uiState.update { state ->
                        state.copy(isLoading = false)
                    }
                    error?.message?.let { errorMessage ->
                        _uiEffect.emit(TaskScreenEffect.ShowErrorMessage(errorMessage))
                    }
                }
                .collect()
        }
    }

    private fun loadTask() {
        loadTaskJob?.cancel()
        taskId?.let {
            loadTaskJob = CoroutineScope(Dispatchers.IO).launch {
                taskRepository
                    .getTask(it)
                    .onEach { taskAndFileModel ->
                        _uiState.update { state ->
                            state.copy(
                                taskName = taskAndFileModel.task.name,
                                taskDescription = taskAndFileModel.task.description,
                                availableStatus = if (taskAndFileModel.task.isOnServer)
                                    AvailableStatus.OFFLINE_AND_ONLINE
                                else
                                    AvailableStatus.OFFLINE,
                                taskFiles = taskAndFileModel.files.map {
                                    TaskFileModel(
                                        Uri.parse(it.fileUri),
                                        if (it.isOnServer)
                                            AvailableStatus.OFFLINE_AND_ONLINE
                                        else
                                            AvailableStatus.OFFLINE
                                    )
                                }
                            )
                        }
                    }
                    .collect()
            }
        }
    }

    private fun validateTaskName() {
        _uiState.update { state ->
            state.copy(taskNameError = Validators.taskNameValidator(state.taskName))
        }
    }

    private fun validateTaskDescription() {
        _uiState.update { state ->
            state.copy(taskDescriptionError = Validators.taskDescriptionValidator(state.taskDescription))
        }
    }

    private fun onDeleteAllFilesBtnClicked() {
        _uiState.update { state ->
            state.copy(taskFiles = emptyList())
        }
    }

    private fun onAddFileBtnClicked() {
        _uiState.update { state ->
            state.copy(isFilesBeingSelected = true)
        }
    }

    override fun onCleared() {
        loadTaskJob?.cancel()
        loadTaskJob = null
        super.onCleared()
    }
}