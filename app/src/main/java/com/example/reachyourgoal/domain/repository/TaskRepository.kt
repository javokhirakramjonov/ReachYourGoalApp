package com.example.reachyourgoal.domain.repository

import com.example.reachyourgoal.domain.model.databaseModel.TaskAndFileEntity
import com.example.reachyourgoal.domain.model.local.TaskModel
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface TaskRepository {

    suspend fun createTask(task: TaskModel): UUID

    suspend fun saveTask(taskId: UUID, task: TaskModel)

    suspend fun deleteTask(taskId: UUID)

    suspend fun getTask(taskId: UUID): TaskAndFileEntity

    fun getAllTasksAndFiles(): Flow<List<TaskAndFileEntity>>
}