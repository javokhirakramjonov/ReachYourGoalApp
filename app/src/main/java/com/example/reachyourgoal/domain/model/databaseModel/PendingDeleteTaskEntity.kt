package com.example.reachyourgoal.domain.model.databaseModel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "pending_delete_tasks")
data class PendingDeleteTaskEntity(
    @PrimaryKey
    @ColumnInfo(name = "task_id")
    val taskId: UUID
)