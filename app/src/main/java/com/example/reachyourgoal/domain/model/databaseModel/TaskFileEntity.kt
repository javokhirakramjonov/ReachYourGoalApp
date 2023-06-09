package com.example.reachyourgoal.domain.model.databaseModel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "task_files",
    foreignKeys = [ForeignKey(
        entity = TaskEntity::class,
        parentColumns = ["task_id"],
        childColumns = ["task_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class TaskFileEntity(
    @PrimaryKey
    @ColumnInfo(name = "task_file_id")
    val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "file_uri")
    val fileUri: String,
    @ColumnInfo(name = "task_id")
    val taskId: UUID
)
