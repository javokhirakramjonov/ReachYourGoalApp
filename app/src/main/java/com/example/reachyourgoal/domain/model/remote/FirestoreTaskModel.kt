package com.example.reachyourgoal.domain.model.remote

import java.util.UUID

data class FirestoreTaskModel(
    val taskId: UUID,
    val name: String,
    val description: String
)