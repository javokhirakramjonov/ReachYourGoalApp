package com.example.reachyourgoal.domain.model.databaseModel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    @ColumnInfo(name = "task_id")
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String,
    @ColumnInfo(name = "is_on_server")
    val isOnServer: Boolean,
    @ColumnInfo(name = "updated_time")
    val updatedTime: String = ""
)