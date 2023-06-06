package com.example.reachyourgoal.util

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap

fun getFileExtensionFromUri(contentResolver: ContentResolver, uri: Uri): String? {
    val mimeTypeMap = MimeTypeMap.getSingleton()
    return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))?.lowercase()
}

fun getFileNameFromUri(contentResolver: ContentResolver, uri: Uri): String? {
    var fileName: String? = null
    val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val displayNameColumnIndex: Int = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (displayNameColumnIndex != -1) {
                fileName = it.getString(displayNameColumnIndex)
            }
        }
    }
    return fileName
}