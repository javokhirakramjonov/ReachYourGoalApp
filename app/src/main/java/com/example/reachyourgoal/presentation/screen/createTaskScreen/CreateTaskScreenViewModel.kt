package com.example.reachyourgoal.presentation.screen.createTaskScreen

import androidx.lifecycle.viewModelScope
import com.example.reachyourgoal.common.BaseViewModel
import com.example.reachyourgoal.common.Validators
import com.example.reachyourgoal.util.EMPTY_STRING
import com.example.reachyourgoal.util.within
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CreateTaskScreenViewModel @Inject constructor() :
    BaseViewModel<CreateTaskScreenState, CreateTaskScreenEvent, CreateTaskScreenEffect>() {

    private val _uiState = MutableStateFlow(
        CreateTaskScreenState(
            false,
            EMPTY_STRING,
            EMPTY_STRING,
            listOf(
                File("Hello 1"),
                File("Hello 2"),
                File("Hello 3"),
                File("Hello 4"),
                File("Hello 5"),
                File("Hello 6"),
            ),
            null,
            null,
            null,
            null,
            null,
            false,
            false,
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
            is CreateTaskScreenEvent.OnFileAdded -> onFileAdded(event.file)
            is CreateTaskScreenEvent.OnFileDeleted -> onFileDeleted(event.file)
            is CreateTaskScreenEvent.OnRemainderTimeChanged -> onRemainderTimeChanged(event.remainderTime)
            is CreateTaskScreenEvent.OnTaskEndTimeChanged -> onTaskEndTimeChanged(event.taskEndTime)
            is CreateTaskScreenEvent.OnTaskStartTimeChanged -> onTaskStartTimeChanged(event.taskStartTime)
            CreateTaskScreenEvent.OnCloseBtnClicked -> onCloseBtnClicked()
            CreateTaskScreenEvent.OnCreateTaskBtnClicked -> onCreateTaskBtnClicked()
            CreateTaskScreenEvent.OnUpdateRemainderTimeBtnClicked -> onUpdateRemainderTimeBtnClicked()
            CreateTaskScreenEvent.OnUpdateTaskEndTimeBtnClicked -> onUpdateTaskEndTimeBtnClicked()
            CreateTaskScreenEvent.OnUpdateTaskStartTimeBtnClicked -> onUpdateTaskStartTimeBtnClicked()
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

    private fun onFileAdded(file: File) {
        _uiState.update { state ->
            val files = state.files.toMutableList()
            files.add(file)
            state.copy(files = files)
        }
    }

    private fun onFileDeleted(file: File) {
        _uiState.update { state ->
            val files = state.files.toMutableList()
            files.remove(file)
            state.copy(files = files)
        }
    }

    private fun onRemainderTimeChanged(remainderTime: Calendar?) {
        _uiState.update { state ->
            state.copy(remainderTime = remainderTime, remainderTimeSelecting = false)
        }
    }

    private fun onTaskEndTimeChanged(taskEndTime: Calendar?) {
        _uiState.update { state ->
            state.copy(endTime = taskEndTime, endTimeSelecting = false)
        }
    }

    private fun onTaskStartTimeChanged(taskStartTime: Calendar?) {
        _uiState.update { state ->
            state.copy(startTime = taskStartTime, startTimeSelecting = false)
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
        //TODO()
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

    private fun onUpdateRemainderTimeBtnClicked() {
        _uiState.update { state ->
            state.copy(remainderTimeSelecting = true)
        }
    }

    private fun onUpdateTaskStartTimeBtnClicked() {
        _uiState.update { state ->
            state.copy(startTimeSelecting = true)
        }
    }

    private fun onUpdateTaskEndTimeBtnClicked() {
        _uiState.update { state ->
            state.copy(endTimeSelecting = true)
        }
    }

    private fun onDeleteAllFilesBtnClicked() {
        _uiState.update { state ->
            state.copy(files = emptyList())
        }
    }

    private fun onAddFileBtnClicked() {
        viewModelScope.launch {
            _uiEffect.emit(CreateTaskScreenEffect.ShowFilePicker)
        }
    }
}