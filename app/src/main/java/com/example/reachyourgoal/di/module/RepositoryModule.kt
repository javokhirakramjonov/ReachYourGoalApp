package com.example.reachyourgoal.di.module

import com.example.reachyourgoal.domain.repository.AuthRepository
import com.example.reachyourgoal.domain.repository.impl.AuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @[Binds Singleton]
    fun bindAuthRepository(impl: AuthRepositoryImpl) : AuthRepository

}