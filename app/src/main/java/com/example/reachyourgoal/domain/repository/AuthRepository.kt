package com.example.reachyourgoal.domain.repository

import com.example.reachyourgoal.domain.model.UserModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(email: String, password: String) : Flow<Result<FirebaseUser?>>
    fun register(user: UserModel) : Flow<Result<Unit>>
}