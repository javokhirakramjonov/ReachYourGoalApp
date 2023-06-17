package com.example.reachyourgoal.domain.model.remote

data class FirestoreTaskFileModel(
    val id: String,
    val name: String,
    val url: String,
    val taskId: String,
    val userId: String
)