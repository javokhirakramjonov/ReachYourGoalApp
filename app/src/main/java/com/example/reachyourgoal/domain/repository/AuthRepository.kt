package com.example.reachyourgoal.domain.repository

import com.example.reachyourgoal.domain.model.local.UserModel
import com.example.reachyourgoal.domain.repository.result.LoginResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun loginOrRegister(email: String, password: String): Flow<LoginResult>
    fun saveUserDetails(userModel: UserModel): Flow<Result<Unit>>
}