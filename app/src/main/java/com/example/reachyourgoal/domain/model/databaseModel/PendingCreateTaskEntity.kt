package com.example.reachyourgoal.domain.model.databaseModel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "pending_create_tasks")
data class PendingCreateTaskEntity(
    @PrimaryKey
    @ColumnInfo(name = "task_id")
    val taskId: UUID
)