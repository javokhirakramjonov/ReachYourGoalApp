package com.example.reachyourgoal.presentation.screen.taskScreen

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.reachyourgoal.ui.common.CustomSnackBarHost
import com.example.reachyourgoal.ui.common.ErrorText
import com.example.reachyourgoal.ui.common.FilePicker
import com.example.reachyourgoal.ui.common.SnackBarStyles
import com.example.reachyourgoal.ui.common.getFileIcon
import com.example.reachyourgoal.util.getFileExtensionFromUri
import com.example.reachyourgoal.util.getFileNameFromUri
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import java.io.File
import java.util.UUID

@Destination
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    navigator: DestinationsNavigator,
    taskId: UUID?,
    viewModel: TaskScreenViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        taskId?.let {
            viewModel.onEvent(TaskScreenEvent.OnTaskOpened(it))
        }

        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                TaskScreenEffect.CloseScreen -> {
                    navigator.popBackStack()
                }

                is TaskScreenEffect.ShowErrorMessage -> snackBarHostState.showSnackbar(
                    SnackBarStyles.ErrorSnackBar(effect.errorMessage)
                )

                is TaskScreenEffect.ShowSuccessMessage -> snackBarHostState.showSnackbar(
                    SnackBarStyles.SuccessSnackBar(effect.successMessage)
                )
            }
        }
    }

    val modifierForColumnElements = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp)

    val modifierForErrorTexts = Modifier
        .fillMaxWidth()
        .padding(top = 4.dp, start = 14.dp, end = 14.dp)

    Scaffold(snackbarHost = { CustomSnackBarHost(hostState = snackBarHostState) }) {
        Surface(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    modifier = modifierForColumnElements,
                    text = "Task",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(20.dp))
                TaskNameInput(
                    modifier = modifierForColumnElements, viewModel = viewModel, uiState = uiState
                )
                ErrorText(
                    modifier = modifierForErrorTexts, message = uiState.taskNameError
                )
                Spacer(modifier = Modifier.height(16.dp))
                TaskDescriptionInput(
                    modifier = modifierForColumnElements, viewModel = viewModel, uiState = uiState
                )
                ErrorText(
                    modifier = modifierForErrorTexts, message = uiState.taskDescriptionError
                )
                Spacer(modifier = Modifier.height(20.dp))
                SelectedFiles(
                    modifier = modifierForColumnElements.weight(1f, false),
                    viewModel = viewModel,
                    uiState = uiState
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = modifierForColumnElements,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(modifier = Modifier.weight(1f),
                        onClick = { viewModel.onEvent(TaskScreenEvent.OnBackBtnClicked) }) {
                        Text("Back")
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.onEvent(TaskScreenEvent.OnSaveBtnClicked) }) {
                        Text("Save task")
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            if (uiState.isFilesBeingSelected) {
                FilePicker { fileUris ->
                    viewModel.onEvent(TaskScreenEvent.OnFilesAdded(fileUris))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskNameInput(
    modifier: Modifier, viewModel: TaskScreenViewModel, uiState: TaskScreenState
) {
    OutlinedTextField(
        modifier = modifier,
        value = uiState.taskName,
        onValueChange = { newValue ->
            viewModel.onEvent(TaskScreenEvent.OnTaskNameChanged(newValue))
        },
        label = {
            Text("taskName")
        },
        isError = uiState.taskNameError != null,
        maxLines = 3,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDescriptionInput(
    modifier: Modifier, viewModel: TaskScreenViewModel, uiState: TaskScreenState
) {
    OutlinedTextField(
        modifier = modifier,
        value = uiState.taskDescription,
        onValueChange = { newValue ->
            viewModel.onEvent(TaskScreenEvent.OnTaskDescriptionChanged(newValue))
        },
        label = {
            Text("description")
        },
        isError = uiState.taskDescriptionError != null,
        maxLines = 50,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SelectedFiles(
    modifier: Modifier,
    viewModel: TaskScreenViewModel,
    uiState: TaskScreenState
) {
    Column(
        modifier = modifier
            .border(
                1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Filled.AttachFile, contentDescription = null)
            Text("Attached files")
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { viewModel.onEvent(TaskScreenEvent.OnAddFileBtnClicked) }
            ) {
                Text("Add files")
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp)
        ) {
            items(uiState.taskFiles) {
                FileElement(
                    modifier = Modifier.animateItemPlacement(),
                    fileUri = it
                ) { deletedFile ->
                    viewModel.onEvent(TaskScreenEvent.OnFileDeleted(deletedFile))
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FileElement(
    modifier: Modifier = Modifier,
    fileUri: Uri,
    onDelete: (Uri) -> Unit
) {

    val contentResolver = LocalContext.current.contentResolver
    val fileName = remember(fileUri) { getFileNameFromUri(contentResolver, fileUri) }
    val extension = remember(fileUri) { getFileExtensionFromUri(contentResolver, fileUri) }

    Row(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.weight(0.1f),
            imageVector = getFileIcon(extension = extension),
            contentDescription = null
        )
        Text(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .basicMarquee(),
            text = "$fileName"
        )
        Box(modifier = Modifier
            .padding(2.dp)
            .border(1.dp, MaterialTheme.colorScheme.error, CircleShape)
            .clickable { onDelete(fileUri) }
            .padding(4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview
@Composable
private fun FileElementPreview() {
    FileElement(
        fileUri = File("Hello").toUri(),
        onDelete = {}
    )
}