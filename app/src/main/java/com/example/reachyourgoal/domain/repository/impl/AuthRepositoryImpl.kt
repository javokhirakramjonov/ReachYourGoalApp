package com.example.reachyourgoal.domain.repository.impl

import com.example.reachyourgoal.domain.model.local.UserModel
import com.example.reachyourgoal.domain.repository.AuthRepository
import com.example.reachyourgoal.domain.repository.result.LoginResult
import com.example.reachyourgoal.service.NetworkStatusService
import com.example.reachyourgoal.util.INTERNET_IS_NOT_AVAILABLE
import com.example.reachyourgoal.util.getErrorMessageOrDefault
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val networkStatusService: NetworkStatusService
) : AuthRepository {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    private val firebaseStorage = Firebase.storage.reference

    companion object {
        const val COLLECTION_USER = "users"
    }

    override fun loginOrRegister(email: String, password: String) = flow {
        if (!networkStatusService.isInternetAvailable()) {
            throw Exception(INTERNET_IS_NOT_AVAILABLE)
        }

        val signInMethods = runCatching {
            auth
                .fetchSignInMethodsForEmail(email)
                .await()
        }.getOrElse { throwable ->
            throw Exception(getErrorMessageOrDefault(throwable))
        }.signInMethods

        runCatching {
            if (signInMethods.isNullOrEmpty())
                auth
                    .createUserWithEmailAndPassword(email, password)
                    .await()
            else
                auth
                    .signInWithEmailAndPassword(email, password)
                    .await()
        }.getOrElse { throwable ->
            throw Exception(getErrorMessageOrDefault(throwable))
        }

        val userDetailsResult = runCatching {
            firestore
                .collection(COLLECTION_USER)
                .document(email)
                .get()
                .await()
        }.getOrElse { throwable ->
            throw Exception(getErrorMessageOrDefault(throwable))
        }
        if (userDetailsResult.exists()) {
            emit(LoginResult.Success)
        } else {
            emit(LoginResult.UserDetailsRequired)
        }
    }

    override fun saveUserDetails(userModel: UserModel) = flow {
        if (!networkStatusService.isInternetAvailable()) {
            throw Exception(INTERNET_IS_NOT_AVAILABLE)
        }

        val imageUrl = userModel.imageUri?.runCatching {
            firebaseStorage
                .child(getUserId())
                .putFile(this)
                .await()
        }?.getOrElse { throwable ->
            throw Exception(getErrorMessageOrDefault(throwable))
        }?.runCatching {
            storage
                .downloadUrl
                .await()
        }?.getOrElse { throwable ->
            throw Exception(getErrorMessageOrDefault(throwable))
        }

        runCatching {
            firestore
                .collection(COLLECTION_USER)
                .document(getUserId())
                .set(userModel.copy(imageUri = imageUrl))
                .await()
        }.getOrElse { throwable ->
            throw Exception(getErrorMessageOrDefault(throwable))
        }
        emit(Result.success(Unit))
    }

    override fun getUserId() = auth.currentUser!!.uid
}