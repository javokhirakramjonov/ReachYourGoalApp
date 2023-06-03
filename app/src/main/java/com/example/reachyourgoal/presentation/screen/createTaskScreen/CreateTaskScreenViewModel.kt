package com.example.reachyourgoal.presentation.screen.createTaskScreen

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.reachyourgoal.common.BaseViewModel
import com.example.reachyourgoal.common.Validators
import com.example.reachyourgoal.domain.model.local.TaskModel
import com.example.reachyourgoal.domain.repository.TaskRepository
import com.example.reachyourgoal.util.EMPTY_STRING
import com.example.reachyourgoal.util.within
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateTaskScreenViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) :
    BaseViewModel<CreateTaskScreenState, CreateTaskScreenEvent, CreateTaskScreenEffect>() {

    private val _uiState = MutableStateFlow(
        CreateTaskScreenState(
            false,
            EMPTY_STRING,
            EMPTY_STRING,
            emptyList(),
            null,
            null,
            false
        )
    )
    override val uiState: StateFlow<CreateTaskScreenState>
        get() = _uiState

    private val _uiEffect = MutableSharedFlow<CreateTaskScreenEffect>()
    override val uiEffect: SharedFlow<CreateTaskScreenEffect>
        get() = _uiEffect

    override fun onEvent(event: CreateTaskScreenEvent) {
        when (event) {
            is CreateTaskScreenEvent.OnTaskNameChanged -> onTaskNameChanged(event.taskName)
            is CreateTaskScreenEvent.OnTaskDescriptionChanged -> onTaskDescriptionChanged(event.taskDescription)
            is CreateTaskScreenEvent.OnFilesAdded -> onFilesAdded(event.fileUris)
            is CreateTaskScreenEvent.OnFileDeleted -> onFileDeleted(event.fileUri)
            CreateTaskScreenEvent.OnCloseBtnClicked -> onCloseBtnClicked()
            CreateTaskScreenEvent.OnCreateTaskBtnClicked -> onCreateTaskBtnClicked()
            CreateTaskScreenEvent.OnDeleteAllFilesBtnClicked -> onDeleteAllFilesBtnClicked()
            CreateTaskScreenEvent.OnAddFileBtnClicked -> onAddFileBtnClicked()
        }
    }

    private fun onTaskNameChanged(taskName: String) {
        _uiState.update { state ->
            state.copy(taskName = taskName, taskNameError = null)
        }
    }

    private fun onTaskDescriptionChanged(taskDescription: String) {
        _uiState.update { state ->
            state.copy(taskDescription = taskDescription)
        }
    }

    private fun onFilesAdded(selectedFileUris: List<Uri>?) {
        _uiState.update { state ->
            val fileUris = state.fileUris.toMutableList()
            selectedFileUris?.let {
                fileUris.addAll(0, it)
            }
            state.copy(fileUris = fileUris, isFilesBeingSelected = false)
        }
    }

    private fun onFileDeleted(fileUri: Uri) {
        _uiState.update { state ->
            val fileUris = state.fileUris.toMutableList()
            fileUris.remove(fileUri)
            state.copy(fileUris = fileUris)
        }
    }

    private fun onCloseBtnClicked() {
        viewModelScope.launch {
            _uiEffect.emit(CreateTaskScreenEffect.CloseScreen)
        }
    }

    private fun onCreateTaskBtnClicked() {
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
                _uiState.value.fileUris
            )
            taskRepository
                .createTask(task)
                .onStart {
                    _uiState.update { state ->
                        state.copy(isLoading = true)
                    }
                }
                .onCompletion { error ->
                    _uiState.update { state ->
                        state.copy(isLoading = false)
                    }
                    if (error != null) {
                        _uiEffect.emit(CreateTaskScreenEffect.ShowErrorMessage(error.message.toString()))
                    }
                }
                .collect()
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
            state.copy(fileUris = emptyList())
        }
    }

    private fun onAddFileBtnClicked() {
        _uiState.update { state ->
            state.copy(isFilesBeingSelected = true)
        }
    }
}