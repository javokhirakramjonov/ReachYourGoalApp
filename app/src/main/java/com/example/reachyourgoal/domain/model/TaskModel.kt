package com.example.reachyourgoal.domain.model

import java.io.File
import java.util.Calendar

data class TaskModel(
    val name: String,
    val description: String?,
    val files: List<File>?,
    val remainderTime: Calendar?,
    val taskTimeRange: List<Calendar>?
)