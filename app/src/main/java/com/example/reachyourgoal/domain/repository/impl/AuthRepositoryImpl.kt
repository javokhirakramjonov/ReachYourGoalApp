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
            auth.fetchSignInMethodsForEmail(email).await()
        }.getOrElse {
            throw Exception(getErrorMessageOrDefault(it))
        }.signInMethods

        runCatching {
            if (signInMethods.isNullOrEmpty())
                auth.createUserWithEmailAndPassword(email, password).await()
            else
                auth.signInWithEmailAndPassword(email, password).await()
        }.getOrElse {
            throw Exception(getErrorMessageOrDefault(it))
        }

        val userDetailsResult = runCatching {
            firestore.collection(COLLECTION_USER).document(email).get().await()
        }.getOrElse {
            throw Exception(getErrorMessageOrDefault(it))
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
        //Always coming here after sign in
        val email = auth.currentUser!!.email!!

        val imageUrl = userModel.imageUri?.runCatching {
            firebaseStorage.child(email).putFile(this).await()
        }?.getOrElse {
            throw Exception(getErrorMessageOrDefault(it))
        }?.runCatching {
            storage.downloadUrl.await()
        }?.getOrElse {
            throw Exception(getErrorMessageOrDefault(it))
        }

        runCatching {
            firestore.collection(COLLECTION_USER).document(email)
                .set(userModel.copy(imageUri = imageUrl)).await()
        }.getOrElse {
            throw Exception(getErrorMessageOrDefault(it))
        }
        emit(Result.success(Unit))
    }

    override fun getEmail() = auth.currentUser!!.email!!
}