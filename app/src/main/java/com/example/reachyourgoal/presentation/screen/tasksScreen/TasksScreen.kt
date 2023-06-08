package com.example.reachyourgoal.presentation.screen.tasksScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.reachyourgoal.navigation.Screen
import com.example.reachyourgoal.ui.common.CustomSnackBarHost
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    navHostController: NavHostController,
    viewModel: TasksScreenViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.onEvent(TasksScreenEvent.OnLoadTasks)

        viewModel.uiEffect.collectLatest {
            when (it) {
                is TasksScreenEffect.OpenTask -> {
                    val argument = it.taskId?.let { id -> "/taskId=$id" } ?: ""
                    navHostController.navigate("${Screen.TaskScreen.route}$argument")
                }
                is TasksScreenEffect.ShowDeleteTaskConfirmDialog -> TODO()
                is TasksScreenEffect.ShowErrorMessage -> TODO()
                is TasksScreenEffect.ShowSuccessMessage -> TODO()
            }
        }

    }

    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(snackbarHost = { CustomSnackBarHost(hostState = snackBarHostState) }) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            LazyColumn {
                items(uiState.tasks) {
                    Button(onClick = { viewModel.onEvent(TasksScreenEvent.OnOpenTask(it.id)) }) {
                        Text(it.name)
                    }
                }
            }
        }
    }
}