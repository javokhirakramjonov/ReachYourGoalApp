package com.example.reachyourgoal.domain.repository.impl

import com.example.reachyourgoal.domain.model.UserModel
import com.example.reachyourgoal.domain.repository.AuthRepository
import com.example.reachyourgoal.service.NetworkStatusService
import com.example.reachyourgoal.util.INTERNET_IS_NOT_AVAILABLE
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val networkStatusService: NetworkStatusService
) : AuthRepository {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    override fun login(email: String, password: String) = flow<Result<FirebaseUser?>> {
        if (!networkStatusService.isInternetAvailable()) {
            emit(Result.failure(Error(INTERNET_IS_NOT_AVAILABLE)))
            return@flow
        }
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

    override fun register(user: UserModel) = flow<Result<FirebaseUser?>> {
        if (!networkStatusService.isInternetAvailable()) {
            emit(Result.failure(Error(INTERNET_IS_NOT_AVAILABLE)))
            return@flow
        }
        val resultEmailAndPassword = runCatching {
            auth.createUserWithEmailAndPassword(user.email, user.password).await()
        }
        resultEmailAndPassword.fold(
            {},
            {
                emit(Result.failure(it))
                return@flow
            }
        )
        val resultUserData = runCatching {
            firestore.collection("users").document(user.email).set(user).await()
        }
        resultUserData.fold(
            {
                emit(Result.success(auth.currentUser))
            },
            {
                emit(Result.failure(it))
            }
        )
    }
}