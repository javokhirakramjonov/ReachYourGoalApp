package com.example.reachyourgoal.domain.model.remote

import java.util.UUID

data class FirestoreTaskFileModel(
    val email: String,
    val taskId: UUID,
    val taskFileId: UUID,
    val url: String
)