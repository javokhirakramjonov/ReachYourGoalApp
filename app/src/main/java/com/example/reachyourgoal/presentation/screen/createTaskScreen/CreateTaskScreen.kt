package com.example.reachyourgoal.presentation.screen.createTaskScreen

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
import androidx.compose.material3.Checkbox
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.reachyourgoal.ui.common.CustomSnackBarHost
import com.example.reachyourgoal.ui.common.ErrorText
import com.example.reachyourgoal.ui.common.ShowLoading
import com.example.reachyourgoal.ui.common.SnackBarStyles
import com.example.reachyourgoal.util.within
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import kotlinx.coroutines.flow.collectLatest
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    navHostController: NavHostController, viewModel: CreateTaskScreenViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                CreateTaskScreenEffect.CloseScreen -> navHostController.popBackStack()
                CreateTaskScreenEffect.ShowFilePicker -> TODO()
                is CreateTaskScreenEffect.ShowErrorMessage -> snackBarHostState.showSnackbar(
                    SnackBarStyles.ErrorSnackBar(effect.message)
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
                RemainderTime(
                    modifier = modifierForColumnElements, viewModel = viewModel, uiState = uiState
                )
                Spacer(modifier = Modifier.height(20.dp))
                TaskTimeRange(
                    modifier = modifierForColumnElements, viewModel = viewModel, uiState = uiState
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
                    Button(modifier = Modifier.weight(0.4f),
                        onClick = { viewModel.onEvent(CreateTaskScreenEvent.OnCloseBtnClicked) }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.weight(0.2f))
                    Button(modifier = Modifier.weight(0.4f),
                        onClick = { viewModel.onEvent(CreateTaskScreenEvent.OnCreateTaskBtnClicked) }) {
                        Text("Create task")
                    }
                }
            }
            if (uiState.isLoading) {
                ShowLoading()
            }
            if (uiState.remainderTimeSelecting) {
                ShowClockDialog { time ->
                    viewModel.onEvent(CreateTaskScreenEvent.OnRemainderTimeChanged(time))
                }
            }
            if (uiState.startTimeSelecting) {
                ShowClockDialog { time ->
                    viewModel.onEvent(CreateTaskScreenEvent.OnTaskStartTimeChanged(time))
                }
            }
            if (uiState.endTimeSelecting) {
                ShowClockDialog { time ->
                    viewModel.onEvent(CreateTaskScreenEvent.OnTaskEndTimeChanged(time))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowClockDialog(
    onFinished: (Calendar) -> Unit
) {
    ClockDialog(
        state = rememberUseCaseState(
            visible = true
        ), selection = ClockSelection.HoursMinutes { hours, minutes ->
            val time = Calendar.getInstance()
            time.set(Calendar.HOUR_OF_DAY, hours)
            time.set(Calendar.MINUTE, minutes)
            onFinished(time)
        }, config = ClockConfig(is24HourFormat = true)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskNameInput(
    modifier: Modifier, viewModel: CreateTaskScreenViewModel, uiState: CreateTaskScreenState
) {
    OutlinedTextField(modifier = modifier,
        value = uiState.taskName,
        onValueChange = { newValue ->
            viewModel.onEvent(CreateTaskScreenEvent.OnTaskNameChanged(newValue))
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
    modifier: Modifier, viewModel: CreateTaskScreenViewModel, uiState: CreateTaskScreenState
) {
    OutlinedTextField(modifier = modifier,
        value = uiState.taskDescription,
        onValueChange = { newValue ->
            viewModel.onEvent(CreateTaskScreenEvent.OnTaskDescriptionChanged(newValue))
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
private fun RemainderTime(
    modifier: Modifier, viewModel: CreateTaskScreenViewModel, uiState: CreateTaskScreenState
) {
    Column(
        modifier = modifier
            .border(
                1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Remainder time")
            Checkbox(checked = uiState.remainderTime != null, onCheckedChange = { checked ->
                var time: Calendar? = null
                if (checked) {
                    time = Calendar.getInstance()
                    time.set(Calendar.HOUR_OF_DAY, 7)
                    time.set(Calendar.MINUTE, 0)
                }
                viewModel.onEvent(CreateTaskScreenEvent.OnRemainderTimeChanged(time))
            })
        }
        uiState.remainderTime?.within {
            Spacer(modifier = Modifier.height(16.dp))

            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val formattedTime = dateFormat.format(time)
            Text("Time: $formattedTime")

            Spacer(modifier = Modifier.height(16.dp))

            Button(modifier = Modifier,
                onClick = { viewModel.onEvent(CreateTaskScreenEvent.OnUpdateRemainderTimeBtnClicked) }) {
                Text("change time")
            }
        }
    }
}

@Composable
private fun TaskTimeRange(
    modifier: Modifier, viewModel: CreateTaskScreenViewModel, uiState: CreateTaskScreenState
) {
    Column(
        modifier = modifier
            .border(
                1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Task expected time range")
            Checkbox(checked = uiState.startTime != null && uiState.endTime != null,
                onCheckedChange = { checked ->
                    var time: Calendar? = null
                    if (checked) {
                        time = Calendar.getInstance()
                        time.set(Calendar.HOUR_OF_DAY, 7)
                        time.set(Calendar.MINUTE, 0)
                    }
                    viewModel.onEvent(CreateTaskScreenEvent.OnTaskStartTimeChanged(time))
                    viewModel.onEvent(CreateTaskScreenEvent.OnTaskEndTimeChanged(time))
                })
        }
        if (uiState.startTime != null && uiState.endTime != null) {
            val startTime: Calendar = uiState.startTime
            val endTime: Calendar = uiState.endTime

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround
            ) {
                val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                val formattedStartTime = dateFormat.format(startTime.time)
                Text("Start time: $formattedStartTime")

                val formattedEndTime = dateFormat.format(endTime.time)
                Text("End time: $formattedEndTime")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(modifier = Modifier,
                    onClick = { viewModel.onEvent(CreateTaskScreenEvent.OnUpdateTaskStartTimeBtnClicked) }) {
                    Text("change start time")
                }
                Button(modifier = Modifier,
                    onClick = { viewModel.onEvent(CreateTaskScreenEvent.OnUpdateTaskEndTimeBtnClicked) }) {
                    Text("change end time")
                }
            }
        }
    }
}

@Composable
private fun SelectedFiles(
    modifier: Modifier, viewModel: CreateTaskScreenViewModel, uiState: CreateTaskScreenState
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
        ShowFiles(uiState.files) { fileToDelete ->
            viewModel.onEvent(CreateTaskScreenEvent.OnFileDeleted(fileToDelete))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(modifier = Modifier.weight(0.4f),
                onClick = { viewModel.onEvent(CreateTaskScreenEvent.OnDeleteAllFilesBtnClicked) }) {
                Text("Delete All")
            }
            Spacer(modifier = Modifier.weight(0.2f))
            Button(modifier = Modifier.weight(0.4f),
                onClick = { viewModel.onEvent(CreateTaskScreenEvent.OnAddFileBtnClicked) }) {
                Text("Add file")
            }
        }
    }
}

@Composable
private fun ShowFiles(
    fileList: List<File>, onDelete: (File) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        items(fileList) {
            FileElement(it, onDelete)
        }
    }
}

@Composable
private fun FileElement(
    file: File, onDelete: (File) -> Unit
) {
    Box(
        modifier = Modifier
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
            Box(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxWidth(), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = file.path,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
private fun FileElementPreview() {
    FileElement(file = File("Hello"), onDelete = {})
}