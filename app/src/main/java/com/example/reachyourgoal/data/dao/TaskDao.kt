package com.example.reachyourgoal.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.reachyourgoal.domain.model.databaseModel.TaskAndFileModel
import com.example.reachyourgoal.domain.model.databaseModel.TaskEntity
import com.example.reachyourgoal.domain.model.databaseModel.TaskFileEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface TaskDao {

    //INSERT

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskFile(taskFile: TaskFileEntity)

    //UPDATE

    @Update
    suspend fun updateTask(taskEntity: TaskEntity)

    @Query("UPDATE tasks SET is_on_server = :isUploaded WHERE task_id = :taskId")
    suspend fun updateTaskOnServerStatus(taskId: UUID, isUploaded: Boolean)

    @Query("UPDATE task_files SET is_on_server = :isUploaded WHERE task_file_id = :taskFileId")
    suspend fun updateTaskUploadStatus(taskFileId: UUID, isUploaded: Boolean)

    //GET

    @Query("SELECT * FROM tasks")
    fun getTasksFlow(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE task_id = :taskId")
    suspend fun getTaskById(taskId: UUID): TaskEntity

    @Transaction
    @Query("SELECT * FROM tasks WHERE task_id = :taskId")
    fun getTaskAndFileFlow(taskId: UUID): Flow<TaskAndFileModel>

    @Transaction
    @Query("SELECT * FROM tasks WHERE task_id = :taskId")
    suspend fun getTaskAndFile(taskId: UUID): TaskAndFileModel

    @Query("SELECT * FROM task_files WHERE task_id = :taskId")
    suspend fun getTaskFilesByTaskId(taskId: UUID): List<TaskFileEntity>

    @Query("SELECT * FROM task_files WHERE task_file_id = :taskFileId")
    suspend fun getTaskFileById(taskFileId: UUID): TaskFileEntity

    //DELETE

    @Delete
    suspend fun deleteTaskFile(taskFileEntity: TaskFileEntity)

    @Query("DELETE FROM tasks WHERE task_id = :taskId")
    suspend fun deleteTask(taskId: UUID)

    //Exists

    @Query("SELECT EXISTS (SELECT 1 FROM task_files WHERE task_file_id = :taskFileId)")
    suspend fun fileExists(taskFileId: UUID): Boolean
    @Query("SELECT EXISTS (SELECT 1 FROM tasks WHERE task_id = :taskId)")
    suspend fun taskExists(taskId: UUID): Boolean
}