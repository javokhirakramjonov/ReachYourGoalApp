package com.example.reachyourgoal.presentation.screen.taskScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.reachyourgoal.domain.model.local.AvailableStatus
import com.example.reachyourgoal.domain.model.local.TaskFileModel
import com.example.reachyourgoal.navigation.TaskScreenMode
import com.example.reachyourgoal.ui.common.CustomSnackBarHost
import com.example.reachyourgoal.ui.common.ErrorText
import com.example.reachyourgoal.ui.common.FilePicker
import com.example.reachyourgoal.ui.common.ShowLoading
import com.example.reachyourgoal.ui.common.SnackBarStyles
import kotlinx.coroutines.flow.collectLatest
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    navHostController: NavHostController,
    mode: TaskScreenMode = TaskScreenMode.CREATE,
    viewModel: TaskScreenViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                TaskScreenEffect.CloseScreen -> navHostController.popBackStack()
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

    val scrollState = rememberScrollState()

    Scaffold(snackbarHost = { CustomSnackBarHost(hostState = snackBarHostState) }) {
        Surface(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    modifier = modifierForColumnElements,
                    text = "Create task",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(uiState.availableStatus.name)
                Spacer(modifier = Modifier.height(16.dp))
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
                    modifier = modifierForColumnElements, viewModel = viewModel, uiState = uiState
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = modifierForColumnElements,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(modifier = Modifier.weight(1f),
                        onClick = { viewModel.onEvent(TaskScreenEvent.OnCloseBtnClicked) }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Button(modifier = Modifier.weight(1f),
                        onClick = { viewModel.onEvent(TaskScreenEvent.OnSaveBtnClicked) }) {
                        Text("Save task")
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
            if (uiState.isLoading) {
                ShowLoading()
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
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
        enabled = uiState.isLoading.not()
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
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
        enabled = uiState.isLoading.not()
    )
}

@Composable
private fun SelectedFiles(
    modifier: Modifier, viewModel: TaskScreenViewModel, uiState: TaskScreenState
) {
    Column(
        modifier = modifier
            .border(
                1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp)
            )
            .padding(12.dp)
    ) {
        Text("Necessary files")
        Spacer(modifier = Modifier.height(12.dp))
        ShowFiles(uiState.taskFiles) { fileToDelete ->
            viewModel.onEvent(TaskScreenEvent.OnFileDeleted(fileToDelete))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(modifier = Modifier.weight(1f),
                onClick = { viewModel.onEvent(TaskScreenEvent.OnDeleteAllFilesBtnClicked) }) {
                Text("Delete All")
            }
            Spacer(modifier = Modifier.width(20.dp))
            Button(modifier = Modifier.weight(1f),
                onClick = { viewModel.onEvent(TaskScreenEvent.OnAddFileBtnClicked) }) {
                Text("Add file")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ShowFiles(
    files: List<TaskFileModel>, onDelete: (TaskFileModel) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        items(files) {
            FileElement(
                Modifier.animateItemPlacement(), it, onDelete
            )
        }
    }
}

@Composable
private fun FileElement(
    modifier: Modifier = Modifier, file: TaskFileModel, onDelete: (TaskFileModel) -> Unit
) {
    Box(
        modifier = modifier
            .size(width = 100.dp, height = 120.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
    ) {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .size(20.dp)
                .border(1.dp, MaterialTheme.colorScheme.error, CircleShape)
                .padding(2.dp)
                .align(Alignment.TopEnd), contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = {
                onDelete(file)
            }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "close",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.weight(0.7f), contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier.padding(20.dp),
                    imageVector = Icons.Default.FileUpload,
                    contentDescription = "file"
                )
            }
            Column(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = file.uri.path ?: "Unknown",
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = file.availableStatus.name
                )
            }
        }
    }
}

@Preview
@Composable
private fun FileElementPreview() {
    FileElement(file = TaskFileModel(File("Hello").toUri(), AvailableStatus.NOT_AVAILABLE),
        onDelete = {})
}