package com.example.reachyourgoal.domain.repository

import com.example.reachyourgoal.domain.model.databaseModel.TaskAndFileModel
import com.example.reachyourgoal.domain.model.databaseModel.TaskEntity
import com.example.reachyourgoal.domain.model.local.TaskModel
import com.example.reachyourgoal.domain.repository.result.SaveTaskResult
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface TaskRepository {

    fun createTask(task: TaskModel): Flow<SaveTaskResult>

    suspend fun saveTask(taskId: UUID, task: TaskModel)

    suspend fun getTask(taskId: UUID): Flow<TaskAndFileModel>

    suspend fun loadTasksFromServer()

    fun getAllTasks() : Flow<List<TaskEntity>>
}