package com.example.reachyourgoal.domain.repository.impl

import com.example.reachyourgoal.domain.model.local.UserModel
import com.example.reachyourgoal.domain.repository.AuthRepository
import com.example.reachyourgoal.service.NetworkStatusService
import com.example.reachyourgoal.util.INTERNET_IS_NOT_AVAILABLE
import com.google.firebase.auth.AuthResult
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

    companion object {
        const val COLLECTION_USER = "users"
    }

    override fun login(email: String, password: String) = flow<Result<AuthResult>> {
        if (!networkStatusService.isInternetAvailable()) {
            throw Exception(INTERNET_IS_NOT_AVAILABLE)
        }
        val result = runCatching {
            auth.signInWithEmailAndPassword(email, password).await()
        }
        emit(result)
    }

    override fun register(user: UserModel) = flow<Result<AuthResult>> {
        if (!networkStatusService.isInternetAvailable()) {
            throw Exception(INTERNET_IS_NOT_AVAILABLE)
        }
        val resultEmailAndPassword = runCatching {
            auth.createUserWithEmailAndPassword(user.email, user.password).await()
        }
        val resultUserData = runCatching {
            firestore.collection(COLLECTION_USER).document(user.email).set(user).await()
        }
        emit(resultEmailAndPassword)
    }
}