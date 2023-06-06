package com.example.reachyourgoal.domain.model.local

import android.net.Uri

data class TaskFileModel(
    val uri: Uri,
    val availableStatus: AvailableStatus
)