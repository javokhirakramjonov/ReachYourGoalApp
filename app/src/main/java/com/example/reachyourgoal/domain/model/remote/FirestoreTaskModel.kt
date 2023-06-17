package com.example.reachyourgoal.domain.model.remote

data class FirestoreTaskModel(
    val id: String,
    val name: String,
    val description: String,
    val userId: String
)