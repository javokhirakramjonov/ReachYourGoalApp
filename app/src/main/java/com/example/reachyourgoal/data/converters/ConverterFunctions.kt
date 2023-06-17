package com.example.reachyourgoal.data.converters

import android.content.ContentResolver
import android.net.Uri
import com.example.reachyourgoal.domain.model.databaseModel.TaskEntity
import com.example.reachyourgoal.domain.model.databaseModel.TaskFileEntity
import com.example.reachyourgoal.domain.model.remote.FirestoreTaskFileModel
import com.example.reachyourgoal.domain.model.remote.FirestoreTaskModel
import com.example.reachyourgoal.util.getFileNameFromUri

fun TaskEntity.toFirestoreTask(userId: String): FirestoreTaskModel = FirestoreTaskModel(
    this.id.toString(),
    this.name,
    this.description,
    userId
)

fun TaskFileEntity.toFirestoreTaskFile(
    contentResolver: ContentResolver,
    userId: String,
    url: String
): FirestoreTaskFileModel = FirestoreTaskFileModel(
    this.id.toString(),
    getFileNameFromUri(contentResolver, Uri.parse(this.fileUri)) ?: "Unknown",//TODO
    url,
    this.taskId.toString(),
    userId
)