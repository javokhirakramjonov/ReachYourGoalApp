package com.example.reachyourgoal.domain.repository

import com.example.reachyourgoal.domain.model.local.UserModel
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(email: String, password: String): Flow<Result<AuthResult>>
    fun register(user: UserModel): Flow<Result<AuthResult>>
}