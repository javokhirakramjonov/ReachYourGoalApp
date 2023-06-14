package com.example.reachyourgoal.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.reachyourgoal.domain.model.databaseModel.PendingCreateTaskEntity
import com.example.reachyourgoal.domain.model.databaseModel.PendingCreateTaskFileEntity
import com.example.reachyourgoal.domain.model.databaseModel.PendingDeleteTaskEntity
import com.example.reachyourgoal.domain.model.databaseModel.PendingDeleteTaskFileEntity
import com.example.reachyourgoal.domain.model.databaseModel.PendingUpdateTaskEntity
import com.example.reachyourgoal.domain.model.databaseModel.TaskAndFileEntity
import com.example.reachyourgoal.domain.model.databaseModel.TaskEntity
import com.example.reachyourgoal.domain.model.databaseModel.TaskFileEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface TaskDao {

    //INSERT

    @Upsert
    suspend fun upsertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskFile(taskFile: TaskFileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingCreateTask(pendingCreateTaskEntity: PendingCreateTaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingUpdateTask(pendingUpdateTaskEntity: PendingUpdateTaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingDeleteTask(pendingDeleteTaskEntity: PendingDeleteTaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingCreateTaskFile(pendingCreateTaskFileEntity: PendingCreateTaskFileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingDeleteTaskFile(pendingDeleteTaskFileEntity: PendingDeleteTaskFileEntity)

    //UPDATE

    //GET

    @Query("SELECT * FROM tasks")
    fun getTasksFlow(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE task_id = :taskId")
    suspend fun getTaskById(taskId: UUID): TaskEntity

    @Transaction
    @Query("SELECT * FROM tasks WHERE task_id = :taskId")
    fun getTaskAndFileFlowByTaskId(taskId: UUID): Flow<TaskAndFileEntity>

    @Transaction
    @Query("SELECT * FROM tasks WHERE task_id = :taskId")
    suspend fun getTaskAndFile(taskId: UUID): TaskAndFileEntity

    @Query("SELECT * FROM task_files WHERE task_id = :taskId")
    suspend fun getTaskFilesByTaskId(taskId: UUID): List<TaskFileEntity>

    @Query("SELECT * FROM task_files WHERE task_file_id = :taskFileId")
    suspend fun getTaskFileById(taskFileId: UUID): TaskFileEntity

    @Query("SELECT * FROM pending_create_tasks")
    fun getPendingCreateTaskFlow(): Flow<List<PendingCreateTaskEntity>>

    @Query("SELECT * FROM pending_update_tasks")
    fun getPendingUpdateTaskFlow(): Flow<List<PendingUpdateTaskEntity>>

    @Query("SELECT * FROM pending_delete_tasks")
    fun getPendingDeleteTaskFlow(): Flow<List<PendingDeleteTaskEntity>>

    @Query("SELECT * FROM pending_create_task_files")
    fun getPendingCreateTaskFilesFlow(): Flow<List<PendingCreateTaskFileEntity>>

    @Query("SELECT * FROM pending_delete_task_files")
    fun getPendingDeleteTaskFilesFlow(): Flow<List<PendingDeleteTaskFileEntity>>

    //DELETE

    @Delete
    suspend fun deleteTaskFile(taskFileEntity: TaskFileEntity)

    @Query("DELETE FROM tasks WHERE task_id = :taskId")
    suspend fun deleteTaskByTaskId(taskId: UUID)

    @Delete
    suspend fun deletePendingCreateTask(pendingCreateTaskEntity: PendingCreateTaskEntity)

    @Delete
    suspend fun deletePendingUpdateTask(pendingUpdateTaskEntity: PendingUpdateTaskEntity)

    @Delete
    suspend fun deletePendingDeleteTask(pendingDeleteTaskEntity: PendingDeleteTaskEntity)

    @Delete
    suspend fun deletePendingCreateTaskFile(pendingCreateTaskFileEntity: PendingCreateTaskFileEntity)

    @Delete
    suspend fun deletePendingDeleteTaskFile(pendingDeleteTaskFileEntity: PendingDeleteTaskFileEntity)
}