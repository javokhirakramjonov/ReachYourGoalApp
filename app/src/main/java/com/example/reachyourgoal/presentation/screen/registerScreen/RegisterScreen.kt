package com.example.reachyourgoal.presentation.screen.registerScreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.reachyourgoal.navigation.Screen
import com.example.reachyourgoal.ui.common.CustomSnackBarHost
import com.example.reachyourgoal.ui.common.ErrorText
import com.example.reachyourgoal.ui.common.ShowLoading
import com.example.reachyourgoal.ui.common.SnackBarStyles
import com.example.reachyourgoal.ui.common.navigateWithPopUp
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navHostController: NavHostController,
    viewModel: RegisterScreenViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    val pickMedia =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
            viewModel.onEvent(RegisterScreenEvent.OnImageUriChanged(uri))
        }

    LaunchedEffect(true) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                RegisterScreenEffect.NavigateToLoginScreen -> {
                    navHostController.navigateWithPopUp(
                        Screen.LoginScreen.route,
                        Screen.RegisterScreen.route
                    )
                }

                RegisterScreenEffect.NavigateToMainScreen -> {
                    navHostController.navigateWithPopUp(
                        Screen.MainScreen.route,
                        Screen.RegisterScreen.route
                    )
                }

                is RegisterScreenEffect.ShowErrorMessage -> {
                    snackBarHostState.showSnackbar(
                        SnackBarStyles.ErrorSnackBar(effect.errorMessage)
                    )
                }

                RegisterScreenEffect.ShowPhotoPicker -> {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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

    val scrollState = rememberScrollState()

    Scaffold(snackbarHost = { CustomSnackBarHost(hostState = snackBarHostState) }) {
        Surface(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(200.dp)
                        .padding(20.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable { viewModel.onEvent(RegisterScreenEvent.OnPhotoPickerBtnClicked) },
                    model = uiState.imageUri,
                    contentDescription = "user image",
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = android.R.drawable.ic_menu_add),
                    placeholder = painterResource(id = android.R.drawable.ic_menu_add)
                )
                FirstnameInput(
                    modifier = modifierForTextFields,
                    viewModel = viewModel,
                    uiState = uiState
                )
                ErrorText(
                    modifier = modifierForErrorTexts,
                    message = uiState.firstnameError
                )
                Spacer(modifier = Modifier.height(16.dp))
                LastnameInput(
                    modifier = modifierForTextFields,
                    viewModel = viewModel,
                    uiState = uiState
                )
                ErrorText(
                    modifier = modifierForErrorTexts,
                    message = uiState.lastnameError
                )
                Spacer(modifier = Modifier.height(16.dp))
                UsernameInput(
                    modifier = modifierForTextFields,
                    viewModel = viewModel,
                    uiState = uiState
                )
                ErrorText(
                    modifier = modifierForErrorTexts,
                    message = uiState.usernameError
                )
                Spacer(modifier = Modifier.height(16.dp))
                EmailInput(
                    modifier = modifierForTextFields,
                    viewModel = viewModel,
                    uiState = uiState
                )
                ErrorText(
                    modifier = modifierForErrorTexts,
                    message = uiState.emailError
                )
                Spacer(modifier = Modifier.height(16.dp))
                Password(
                    modifier = modifierForTextFields,
                    viewModel = viewModel,
                    uiState = uiState
                )
                ErrorText(
                    modifier = modifierForErrorTexts,
                    message = uiState.passwordError
                )
                Spacer(modifier = Modifier.height(16.dp))
                PasswordRepeat(
                    modifier = modifierForTextFields,
                    viewModel = viewModel,
                    uiState = uiState
                )
                ErrorText(
                    modifier = modifierForErrorTexts,
                    message = uiState.passwordRepeatError
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    onClick = { viewModel.onEvent(RegisterScreenEvent.OnRegisterBtnClicked) },
                    enabled = uiState.isLoading.not()
                ) {
                    Text("Create Account")
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    onClick = { viewModel.onEvent(RegisterScreenEvent.OnLoginBtnClicked) },
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
private fun FirstnameInput(
    modifier: Modifier,
    viewModel: RegisterScreenViewModel,
    uiState: RegisterScreenState
) {
    OutlinedTextField(
        modifier = modifier,
        value = uiState.firstname,
        onValueChange = { newValue ->
            viewModel.onEvent(RegisterScreenEvent.OnFirstnameChanged(newValue))
        },
        label = {
            Text("firstname")
        },
        isError = uiState.firstnameError != null,
        leadingIcon = {
            Icon(imageVector = Icons.Default.AccountBox, contentDescription = "firstname")
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        enabled = uiState.isLoading.not()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LastnameInput(
    modifier: Modifier,
    viewModel: RegisterScreenViewModel,
    uiState: RegisterScreenState
) {
    OutlinedTextField(
        modifier = modifier,
        value = uiState.lastname,
        onValueChange = { newValue ->
            viewModel.onEvent(RegisterScreenEvent.OnLastnameChanged(newValue))
        },
        label = {
            Text("lastname")
        },
        isError = uiState.lastnameError != null,
        leadingIcon = {
            Icon(imageVector = Icons.Default.AccountBox, contentDescription = "lastname")
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        enabled = uiState.isLoading.not()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UsernameInput(
    modifier: Modifier,
    viewModel: RegisterScreenViewModel,
    uiState: RegisterScreenState
) {
    OutlinedTextField(
        modifier = modifier,
        value = uiState.username,
        onValueChange = { newValue ->
            viewModel.onEvent(RegisterScreenEvent.OnUsernameChanged(newValue))
        },
        label = {
            Text("username")
        },
        isError = uiState.usernameError != null,
        leadingIcon = {
            Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "username")
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        enabled = uiState.isLoading.not()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmailInput(
    modifier: Modifier,
    viewModel: RegisterScreenViewModel,
    uiState: RegisterScreenState
) {
    OutlinedTextField(
        modifier = modifier,
        value = uiState.email,
        onValueChange = { newValue ->
            viewModel.onEvent(RegisterScreenEvent.OnEmailChanged(newValue))
        },
        label = {
            Text("email")
        },
        isError = uiState.firstnameError != null,
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Email, contentDescription = "email")
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        enabled = uiState.isLoading.not()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Password(
    modifier: Modifier,
    viewModel: RegisterScreenViewModel,
    uiState: RegisterScreenState
) {
    OutlinedTextField(
        modifier = modifier,
        value = uiState.password,
        onValueChange = { newValue ->
            viewModel.onEvent(RegisterScreenEvent.OnPasswordChanged(newValue))
        },
        label = {
            Text("password")
        },
        isError = uiState.passwordError != null,
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Lock, contentDescription = "password")
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        enabled = uiState.isLoading.not()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordRepeat(
    modifier: Modifier,
    viewModel: RegisterScreenViewModel,
    uiState: RegisterScreenState
) {
    OutlinedTextField(
        modifier = modifier,
        value = uiState.passwordRepeat,
        onValueChange = { newValue ->
            viewModel.onEvent(RegisterScreenEvent.OnPasswordRepeatChanged(newValue))
        },
        label = {
            Text("repeat password")
        },
        isError = uiState.passwordRepeatError != null,
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Lock, contentDescription = "password repeat")
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        enabled = uiState.isLoading.not()
    )
}