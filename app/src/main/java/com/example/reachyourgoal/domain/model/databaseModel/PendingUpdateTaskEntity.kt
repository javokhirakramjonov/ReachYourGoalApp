package com.example.reachyourgoal.domain.model.databaseModel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID


@Entity(tableName = "pending_update_tasks")
data class PendingUpdateTaskEntity(
    @PrimaryKey
    @ColumnInfo(name = "task_id")
    val taskId: UUID,
    @ColumnInfo(name = "updated_time")
    val updatedTime: String
)