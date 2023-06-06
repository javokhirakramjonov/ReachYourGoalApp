package com.example.reachyourgoal.data.dao

import androidx.room.Dao
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(task: TaskEntity): TaskEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTaskFile(taskFile: TaskFileEntity): TaskFileEntity

    @Update
    suspend fun updateTask(task: TaskEntity): TaskEntity

    @Update
    suspend fun updateTaskFile(taskFile: TaskFileEntity): TaskFileEntity

    @Query("UPDATE task_files SET is_on_server = :isUploaded WHERE task_id = :taskFileId")
    suspend fun updateTaskUploadStatus(taskFileId: UUID, isUploaded: Boolean): TaskFileEntity

    @Query("SELECT * FROM tasks")
    suspend fun getTasks(): Flow<List<TaskEntity>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE task_id = :taskId")
    suspend fun getTaskAndFile(taskId: UUID): Flow<TaskAndFileModel>

    @Query("SELECT * FROM task_files WHERE task_file_id = :taskFileId")
    suspend fun getTaskFileById(taskFileId: UUID): TaskFileEntity
}