package com.example.reachyourgoal.domain.model.remote

import java.util.UUID

data class FirestoreTaskModel(
    val userId: String,
    val taskId: UUID,
    val name: String,
    val description: String
)