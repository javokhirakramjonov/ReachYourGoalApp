package com.example.reachyourgoal.di.module

import android.content.Context
import com.example.reachyourgoal.data.dao.TaskDao
import com.example.reachyourgoal.data.database.ReachYourGoalDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @[Provides]
    fun provideDatabase(@ApplicationContext context: Context): ReachYourGoalDatabase {
        return ReachYourGoalDatabase.create(context)
    }

    @[Provides Singleton]
    fun provideTaskDao(database: ReachYourGoalDatabase): TaskDao = database.getTaskDao()

}