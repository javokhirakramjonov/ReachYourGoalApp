package com.example.reachyourgoal.di.module

import com.example.reachyourgoal.service.firebaseFileUploadService.firebaseFileUploader.FirebaseFileUploader
import com.example.reachyourgoal.service.firebaseFileUploadService.firebaseFileUploader.impl.FirebaseFileUploaderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ServiceModule {

    @[Binds Singleton]
    fun bindsFirebaseUploader(impl: FirebaseFileUploaderImpl): FirebaseFileUploader

}