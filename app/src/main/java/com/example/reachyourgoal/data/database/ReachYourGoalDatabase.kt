package com.example.reachyourgoal.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.reachyourgoal.data.converters.TaskFileListConverter
import com.example.reachyourgoal.data.dao.TaskDao
import com.example.reachyourgoal.domain.model.databaseModel.TaskEntity
import com.example.reachyourgoal.domain.model.databaseModel.TaskFileEntity

@Database(entities = [TaskEntity::class, TaskFileEntity::class], version = 1)
@TypeConverters(TaskFileListConverter::class)
abstract class ReachYourGoalDatabase : RoomDatabase() {

    abstract fun getTaskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: ReachYourGoalDatabase? = null

        fun create(context: Context): ReachYourGoalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    ReachYourGoalDatabase::class.java,
                    "reach_your_goal_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}