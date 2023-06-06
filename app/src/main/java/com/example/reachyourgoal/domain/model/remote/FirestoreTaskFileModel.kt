package com.example.reachyourgoal.domain.model.remote

import java.util.UUID

data class FirestoreTaskFileModel(
    val taskId: UUID,
    val taskFileId: UUID,
    val url: String
)