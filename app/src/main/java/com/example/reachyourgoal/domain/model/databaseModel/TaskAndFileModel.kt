package com.example.reachyourgoal.domain.model.databaseModel

import androidx.room.Embedded
import androidx.room.Relation

data class TaskAndFileModel(
    @Embedded val task: TaskEntity,
    @Relation(
        parentColumn = "task_id",
        entityColumn = "task_id"
    )
    val files: List<TaskFileEntity>
)