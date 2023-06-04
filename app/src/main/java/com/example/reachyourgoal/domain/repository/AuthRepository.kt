package com.example.reachyourgoal.domain.repository

import com.example.reachyourgoal.domain.model.local.UserModel
import com.example.reachyourgoal.domain.repository.result.LoginResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(email: String, password: String): Flow<LoginResult>
    fun register(email: String, password: String): Flow<Result<Unit>>
    fun saveUserDetails(userModel: UserModel): Flow<Result<Unit>>
}