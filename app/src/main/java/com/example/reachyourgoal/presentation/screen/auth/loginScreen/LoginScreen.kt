package com.example.reachyourgoal.presentation.screen.auth.loginScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.reachyourgoal.presentation.screen.destinations.LoginScreenDestination
import com.example.reachyourgoal.presentation.screen.destinations.MainScreenDestination
import com.example.reachyourgoal.presentation.screen.destinations.UserDetailsScreenDestination
import com.example.reachyourgoal.ui.common.CustomSnackBarHost
import com.example.reachyourgoal.ui.common.ErrorText
import com.example.reachyourgoal.ui.common.ShowLoading
import com.example.reachyourgoal.ui.common.SnackBarStyles
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest

@Destination
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navigator: DestinationsNavigator,
    viewModel: LoginScreenViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                is LoginScreenEffect.ShowErrorMessage -> {
                    snackBarHostState.showSnackbar(
                        SnackBarStyles.ErrorSnackBar(effect.errorMessage)
                    )
                }

                is LoginScreenEffect.ShowSuccessMessage -> {
                    snackBarHostState.showSnackbar(
                        SnackBarStyles.SuccessSnackBar(effect.successMessage)
                    )
                }

                LoginScreenEffect.NavigateToMainScreen -> {
                    navigator.navigate(MainScreenDestination()) {
                        popUpTo(LoginScreenDestination.route) {
                            inclusive = true
                        }
                    }
                }

                LoginScreenEffect.NavigateToUserDetailsScreen -> {
                    navigator.navigate(UserDetailsScreenDestination())
                }
            }
        }
    }

    val modifierForTextFields = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp)

    val modifierForErrorTexts = Modifier
        .fillMaxWidth()
        .padding(top = 4.dp, start = 14.dp, end = 14.dp)

    Scaffold(snackbarHost = { CustomSnackBarHost(hostState = snackBarHostState) }) { paddingValues ->
        Surface(modifier = Modifier.padding(paddingValues)) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EmailInput(
                    modifier = modifierForErrorTexts,
                    viewModel = viewModel,
                    uiState = uiState
                )
                ErrorText(
                    modifier = modifierForErrorTexts,
                    message = uiState.emailError
                )
                Spacer(modifier = Modifier.height(16.dp))
                PasswordInput(
                    modifier = modifierForTextFields,
                    viewModel = viewModel,
                    uiState = uiState
                )
                ErrorText(
                    modifier = modifierForErrorTexts,
                    message = uiState.passwordError
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    onClick = { viewModel.onEvent(LoginScreenEvent.OnLoginBtnClicked) },
                    enabled = uiState.isLoading.not()
                ) {
                    Text("Login")
                }
            }
            if (uiState.isLoading) {
                ShowLoading()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmailInput(
    modifier: Modifier,
    viewModel: LoginScreenViewModel,
    uiState: LoginScreenState
) {
    OutlinedTextField(
        modifier = modifier,
        value = uiState.email,
        onValueChange = { newValue ->
            viewModel.onEvent(LoginScreenEvent.OnEmailChanged(newValue))
        },
        label = {
            Text("email")
        },
        isError = uiState.emailError != null,
        leadingIcon = {
            Icon(imageVector = Icons.Default.Email, contentDescription = "email")
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        enabled = uiState.isLoading.not()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordInput(
    modifier: Modifier,
    viewModel: LoginScreenViewModel,
    uiState: LoginScreenState
) {
    OutlinedTextField(
        modifier = modifier,
        value = uiState.password,
        onValueChange = { newValue ->
            viewModel.onEvent(LoginScreenEvent.OnPasswordChanged(newValue))
        },
        label = {
            Text("password")
        },
        isError = uiState.passwordError != null,
        leadingIcon = {
            Icon(imageVector = Icons.Default.Lock, contentDescription = "password")
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        enabled = uiState.isLoading.not()
    )
}
