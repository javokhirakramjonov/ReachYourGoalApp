package com.example.reachyourgoal.presentation.screen.taskScreen

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.reachyourgoal.common.BaseViewModel
import com.example.reachyourgoal.common.Validators
import com.example.reachyourgoal.domain.model.local.TaskModel
import com.example.reachyourgoal.domain.repository.TaskRepository
import com.example.reachyourgoal.util.EMPTY_STRING
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TaskScreenViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) :
    BaseViewModel<TaskScreenState, TaskScreenEvent, TaskScreenEffect>() {

    private var taskId: UUID? = null

    private val _uiState = MutableStateFlow(
        TaskScreenState(
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
            is TaskScreenEvent.OnTaskOpened -> onTaskOpened(event.taskId)
            TaskScreenEvent.OnBackBtnClicked -> onCloseBtnClicked()
            TaskScreenEvent.OnSaveBtnClicked -> onSaveBtnClicked()
            TaskScreenEvent.OnDeleteAllFilesBtnClicked -> onDeleteAllFilesBtnClicked()
            TaskScreenEvent.OnAddFileBtnClicked -> onAddFileBtnClicked()
        }
    }

    private fun onTaskNameChanged(taskName: String) {
        _uiState.update { state ->
            state.copy(
                taskName = taskName,
                taskNameError = null
            )
        }
    }

    private fun onTaskDescriptionChanged(taskDescription: String) {
        _uiState.update { state ->
            state.copy(
                taskDescription = taskDescription,
                taskDescriptionError = null
            )
        }
    }

    private fun onFilesAdded(selectedFileUris: List<Uri>?) {
        _uiState.update { state ->
            val files = state.taskFiles.toMutableList()
            files.addAll(0, selectedFileUris ?: emptyList())
            state.copy(
                taskFiles = files,
                isFilesBeingSelected = false
            )
        }
    }

    private fun onFileDeleted(file: Uri) {
        _uiState.update { state ->
            val files = state.taskFiles.toMutableList()
            files.remove(file)
            state.copy(taskFiles = files)
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
        val errors = _uiState.value.run {
            listOfNotNull(
                taskNameError,
                taskDescriptionError
            )
        }
        if (errors.isNotEmpty()) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val task = TaskModel(
                _uiState.value.taskName,
                _uiState.value.taskDescription,
                _uiState.value.taskFiles
            )

            taskId?.let { id ->
                taskRepository.saveTask(id, task)
                _uiEffect.emit(TaskScreenEffect.ShowSuccessMessage("Task updated successfully."))
            } ?: run {
                taskId = taskRepository.createTask(task)
                _uiEffect.emit(TaskScreenEffect.ShowSuccessMessage("Task created successfully."))
            }
        }
    }

    private fun onTaskOpened(taskId: UUID) {
        this.taskId = taskId
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository
                .getTask(taskId)
                .let { taskAndFileModel ->
                    _uiState.update { state ->
                        state.copy(
                            taskName = taskAndFileModel.task.name,
                            taskDescription = taskAndFileModel.task.description,
                            taskFiles = taskAndFileModel.files.map { taskFile -> Uri.parse(taskFile.fileUri) }
                        )
                    }
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
}