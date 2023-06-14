package com.example.reachyourgoal.presentation.screen.tasksScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.reachyourgoal.domain.model.databaseModel.TaskAndFileEntity
import com.example.reachyourgoal.domain.model.databaseModel.TaskEntity
import com.example.reachyourgoal.domain.model.databaseModel.TaskFileEntity
import com.example.reachyourgoal.presentation.screen.destinations.TaskScreenDestination
import com.example.reachyourgoal.ui.common.CustomSnackBarHost
import com.example.reachyourgoal.ui.common.SnackBarStyles
import com.example.reachyourgoal.ui.common.dpToPx
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import java.util.UUID
import kotlin.math.max
import kotlin.math.min

@Destination
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    navigator: DestinationsNavigator, viewModel: TasksScreenViewModel = hiltViewModel()
) {

    val snackBarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.onEvent(TasksScreenEvent.OnLoadTasks)

        viewModel.uiEffect.collectLatest {
            when (it) {
                is TasksScreenEffect.OpenTask -> {
                    navigator.navigate(TaskScreenDestination(it.taskId))
                }

                is TasksScreenEffect.ShowErrorMessage -> {
                    snackBarHostState.showSnackbar(SnackBarStyles.ErrorSnackBar(it.errorMessage))
                }

                is TasksScreenEffect.ShowSuccessMessage -> {
                    snackBarHostState.showSnackbar(SnackBarStyles.SuccessSnackBar(it.successMessage))
                }
            }
        }

    }

    val listState = rememberLazyListState()
    var listY by remember { mutableFloatStateOf(0f) }
    var listHeight by remember { mutableIntStateOf(0) }

    Scaffold(
        snackbarHost = { CustomSnackBarHost(hostState = snackBarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onEvent(TasksScreenEvent.OnCreateTaskBtnClicked)
            }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coordinate ->
                        listY = coordinate.localToRoot(Offset.Zero).y
                        listHeight = coordinate.size.height
                    },
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(uiState.tasks, key = { taskItem -> taskItem.task.id }) { item ->
                    TaskItem(
                        parentY = listY,
                        parentHeight = listHeight,
                        taskItem = item,
                        onSelect = { viewModel.onEvent(TasksScreenEvent.OnOpenTask(item.task.id)) },
                        onDelete = { viewModel.onEvent(TasksScreenEvent.OnDeleteTask(item.task.id)) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskItem(
    parentY: Float,
    parentHeight: Int,
    taskItem: TaskAndFileEntity,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {

    val colors = listOf(
        Color(0xFF304352), Color(0xFFFF5722)
    )

    var brush by remember { mutableStateOf(Brush.verticalGradient(colors, 0f, 0f)) }

    val itemHeight = 70.dp
    val itemHeightPx = itemHeight.dpToPx()

    Card(
        modifier = Modifier
            .padding(10.dp)
            .onGloballyPositioned { coordinate ->

                val startY = coordinate.localToRoot(Offset.Zero).y

                val startPercent = ((startY - parentY) / parentHeight)

                val startP = min(max(startPercent, 0f), 1f)

                val endY = startY + itemHeightPx

                val endPercent = ((endY - parentY) / parentHeight)

                val endP = min(max(endPercent, 0f), 1f)

                val myColors = listOf(
                    lerp(colors[0], colors[1], startP), lerp(colors[0], colors[1], endP)
                )

                brush = Brush.verticalGradient(colors = myColors)
            }
            .fillMaxWidth()
            .height(itemHeight)
            .combinedClickable(
                onClick = onSelect,
                onDoubleClick = onDelete
            ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RectangleShape
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(5.dp)
                    .background(brush)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = taskItem.task.name,
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.labelMedium.copy(brush = brush),
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${taskItem.files.size} file(s)",
                    fontSize = 12.sp,
                    style = MaterialTheme.typography.labelSmall.copy(brush = brush)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskItemPreview() {
    TaskItem(parentY = 0f, parentHeight = 100, taskItem = TaskAndFileEntity(
        TaskEntity(
            UUID.randomUUID(),
            "Hello world",
            ""
        ),
        listOf(
            TaskFileEntity(
                UUID.randomUUID(),
                "",
                UUID.randomUUID()
            )
        )
    ), onSelect = { }, onDelete = { })
}