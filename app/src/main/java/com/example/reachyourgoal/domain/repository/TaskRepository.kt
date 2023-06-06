package com.example.reachyourgoal.domain.repository

import com.example.reachyourgoal.domain.model.databaseModel.TaskAndFileModel
import com.example.reachyourgoal.domain.model.local.TaskModel
import com.example.reachyourgoal.domain.repository.result.SaveTaskResult
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface TaskRepository {

    fun saveTask(task: TaskModel): Flow<SaveTaskResult>

    suspend fun getTask(taskId: UUID): Flow<TaskAndFileModel>

}