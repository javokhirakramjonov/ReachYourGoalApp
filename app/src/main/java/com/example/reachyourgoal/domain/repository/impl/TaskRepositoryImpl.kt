package com.example.reachyourgoal.domain.repository.impl

import com.example.reachyourgoal.data.dao.TaskDao
import com.example.reachyourgoal.domain.model.databaseModel.PendingCreateTaskEntity
import com.example.reachyourgoal.domain.model.databaseModel.PendingDeleteTaskEntity
import com.example.reachyourgoal.domain.model.databaseModel.PendingUpdateTaskEntity
import com.example.reachyourgoal.domain.model.databaseModel.TaskEntity
import com.example.reachyourgoal.domain.model.databaseModel.TaskFileEntity
import com.example.reachyourgoal.domain.model.local.TaskModel
import com.example.reachyourgoal.domain.repository.TaskRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    init {
        //Temporary
        CoroutineScope(Dispatchers.IO).launch {
            Firebase.auth.signInWithEmailAndPassword(
                "javokhirakromjonov@gmail.com", "javokhirakromjonov@gmail.com"
            ).await()
        }
    }

    override suspend fun createTask(task: TaskModel): UUID {

        //Local
        val taskEntity = createTaskInDatabase(task)

        //Server
        taskDao.insertPendingCreateTask(PendingCreateTaskEntity(taskEntity.id))

        return taskEntity.id
    }

    override suspend fun saveTask(taskId: UUID, task: TaskModel) {

        //Local
        updateTaskInDatabase(taskId, task)

        //Server
        val time = Calendar.getInstance().time.toString()
        taskDao.insertPendingUpdateTask(PendingUpdateTaskEntity(taskId, time))
    }

    override suspend fun deleteTask(taskId: UUID) {
        //Local
        taskDao.deleteTaskByTaskId(taskId)

        //Server
        taskDao.insertPendingDeleteTask(PendingDeleteTaskEntity(taskId))
    }

    private suspend fun createTaskInDatabase(task: TaskModel): TaskEntity {
        val taskEntity = TaskEntity(
            name = task.name, description = task.description
        )

        taskDao.upsertTask(taskEntity)

        task.taskFiles.forEach { taskFile ->
            taskDao.insertTaskFile(
                TaskFileEntity(
                    fileUri = taskFile.toString(),
                    taskId = taskEntity.id
                )
            )
        }

        return taskEntity
    }

    private suspend fun updateTaskInDatabase(taskId: UUID, task: TaskModel) {

        taskDao.upsertTask(
            TaskEntity(
                taskId, task.name, task.description
            )
        )

        val taskFileEntities = taskDao.getTaskFilesByTaskId(taskId)
        val taskFiles = task.taskFiles

        val fileEntityMap = taskFileEntities.associateBy { taskFile -> taskFile.fileUri }
        val fileMap = taskFiles.associateBy { taskFile -> taskFile.toString() }

        val deleteList =
            taskFileEntities.filterNot { taskFile -> fileMap.containsKey(taskFile.fileUri) }
        val insertList =
            taskFiles.filterNot { taskFile -> fileEntityMap.containsKey(taskFile.toString()) }

        deleteList.forEach { taskFile ->
            taskDao.deleteTaskFile(taskFile)
        }

        insertList.forEach { taskFileUri ->
            taskDao.insertTaskFile(
                TaskFileEntity(
                    fileUri = taskFileUri.toString(),
                    taskId = taskId
                )
            )
        }
    }

    override suspend fun getTask(taskId: UUID) = taskDao.getTaskAndFile(taskId)

    override fun getAllTasksAndFiles() = taskDao.getTasksFlow()
        .map { taskList -> taskList.map { task -> taskDao.getTaskAndFile(task.id) } }

    override fun synchronizeWithServer(): Flow<Unit> = flow<Unit> {
        //TODO
    }

}