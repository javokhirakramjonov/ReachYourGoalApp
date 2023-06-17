package com.example.reachyourgoal.presentation.screen.tasksScreen

import androidx.lifecycle.viewModelScope
import com.example.reachyourgoal.common.BaseViewModel
import com.example.reachyourgoal.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksScreenViewModel @Inject constructor(
    private val tasksRepository: TaskRepository
) : BaseViewModel<TasksScreenState, TasksScreenEvent, TasksScreenEffect>() {

    private val _uiState = MutableStateFlow(
        TasksScreenState(
            false,
            emptyList()
        )
    )

    override val uiState: StateFlow<TasksScreenState>
        get() = _uiState

    private val _uiEffect = MutableSharedFlow<TasksScreenEffect>()
    override val uiEffect: SharedFlow<TasksScreenEffect>
        get() = _uiEffect

    override fun onEvent(event: TasksScreenEvent) {
        when (event) {
            is TasksScreenEvent.OnDeleteTask -> {
                viewModelScope.launch {
                    tasksRepository.deleteTask(event.taskId)
                }
            }

            is TasksScreenEvent.OnOpenTask -> {
                viewModelScope.launch {
                    _uiEffect.emit(TasksScreenEffect.OpenTask(event.taskId))
                }
            }

            TasksScreenEvent.OnLoadTasks -> onLoadTasks()
            TasksScreenEvent.OnCreateTaskBtnClicked -> {
                viewModelScope.launch {
                    _uiEffect.emit(TasksScreenEffect.OpenTask(null))
                }
            }
        }
    }

    private fun onLoadTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            tasksRepository.synchronizeWithServer()

            tasksRepository
                .getAllTasksAndFiles()
                .onEach { taskAndFileModel ->
                    _uiState.update { state ->
                        state.copy(
                            tasks = taskAndFileModel
                        )
                    }
                }
                .collect()
        }
    }
}