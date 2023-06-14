package com.example.reachyourgoal.domain.model.databaseModel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "pending_delete_task_files")
data class PendingDeleteTaskFileEntity(
    @PrimaryKey
    @ColumnInfo(name = "task_file_id")
    val taskFileId: UUID
)