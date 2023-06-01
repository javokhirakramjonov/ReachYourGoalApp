package com.example.reachyourgoal.domain.model

import android.net.Uri
import java.util.Calendar

data class TaskModel(
    val name: String,
    val description: String?,
    val fileUris: List<Uri>?,
    val remainderTime: Calendar?,
    val taskStartTime: Calendar?,
    val taskEndTime: Calendar?
)