package com.example.reachyourgoal.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
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
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            db.execSQL(
                                """
                                    CREATE TRIGGER task_added AFTER INSERT ON tasks
                                    FOR EACH ROW
                                    BEGIN
                                        UPDATE tasks
                                        SET updated_time = datetime('now', 'localtime')
                                        WHERE task_id = NEW.task_id;
                                    END;
                                """.trimIndent()
                            )
                            db.execSQL(
                                """
                                    CREATE TRIGGER task_updated AFTER UPDATE ON tasks
                                    FOR EACH ROW
                                    BEGIN
                                        UPDATE tasks
                                        SET updated_time = datetime('now', 'localtime')
                                        WHERE task_id = NEW.task_id AND 
                                        (strftime('%s', datetime('now', 'localtime')) - strftime('%s', COALESCE(updated_time, strftime(datetime('now', 'localtime')) - 3)) > 1);
                                    END;
                                """.trimIndent()
                            )
                        }
                    })
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}