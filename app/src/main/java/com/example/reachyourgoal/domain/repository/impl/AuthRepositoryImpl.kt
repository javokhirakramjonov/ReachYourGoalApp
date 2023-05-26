package com.example.reachyourgoal.domain.repository.impl

import com.example.reachyourgoal.domain.model.UserModel
import com.example.reachyourgoal.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(): AuthRepository {
    private val auth = Firebase.auth

    override fun login(email: String, password: String) = flow<Result<FirebaseUser?>> {
        val result = runCatching {
            auth.signInWithEmailAndPassword(email, password).await()
        }
        result.fold(
            {
                emit(Result.success(auth.currentUser))
            },
            { error ->
                emit(Result.failure(error))
            }
        )
    }

    override fun register(user: UserModel) = flow {
        //TODO
        emit(Result.success(Unit))
    }
}