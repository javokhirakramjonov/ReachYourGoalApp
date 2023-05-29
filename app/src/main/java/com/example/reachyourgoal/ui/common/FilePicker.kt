package com.example.reachyourgoal.ui.common

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect

@Composable
fun FilePicker(
    fileTypes: String = "*/*",
    onSelected: (List<Uri>?) -> Unit
) {
    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris: List<Uri>? ->
        onSelected(uris)
    }

    SideEffect {
        filePicker.launch(arrayOf(fileTypes))
    }
}