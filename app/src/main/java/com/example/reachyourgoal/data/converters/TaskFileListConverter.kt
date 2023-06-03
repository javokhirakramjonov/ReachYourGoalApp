package com.example.reachyourgoal.data.converters

import androidx.room.TypeConverter
import com.example.reachyourgoal.domain.model.databaseModel.TaskFileEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TaskFileListConverter {
    @TypeConverter
    fun fromTaskFileList(files: List<TaskFileEntity>): String {
        val gson = Gson()
        return gson.toJson(files)
    }

    @TypeConverter
    fun toTaskFileList(filesString: String): List<TaskFileEntity> {
        val gson = Gson()
        val type = object : TypeToken<List<TaskFileEntity>>() {}.type
        return gson.fromJson(filesString, type)
    }
}