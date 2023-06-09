package com.example.reachyourgoal.presentation.screen.auth.userDetailsScreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import coil.compose.AsyncImage
import com.example.reachyourgoal.domain.model.local.Sex
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
fun UserDetailsScreen(
    navigator: DestinationsNavigator,
    viewModel: UserDetailsScreenViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    val pickMedia =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
            viewModel.onEvent(UserDetailsScreenEvent.OnImageUriChanged(uri))
        }

    LaunchedEffect(true) {
        viewModel.uiEffect.collectLatest { effect ->
            when (effect) {
                UserDetailsScreenEffect.NavigateBack -> {
                    navigator.popBackStack()
                }

                UserDetailsScreenEffect.NavigateToMainScreen -> {
                    navigator.navigate(MainScreenDestination()) {
                        popUpTo(UserDetailsScreenDestination.route) {
                            inclusive = true
                        }
                    }
                }

                is UserDetailsScreenEffect.ShowErrorMessage -> {
                    snackBarHostState.showSnackbar(
                        SnackBarStyles.ErrorSnackBar(effect.errorMessage)
                    )
                }

                UserDetailsScreenEffect.ShowPhotoPicker -> {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }

                is UserDetailsScreenEffect.ShowSuccessMessage -> {
                    snackBarHostState.showSnackbar(
                        SnackBarStyles.SuccessSnackBar(effect.successMessage)
                    )
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
                        .clickable { viewModel.onEvent(UserDetailsScreenEvent.OnPhotoPickerBtnClicked) },
                    model = uiState.imageUri,
                    contentDescription = "user image",
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = android.R.drawable.ic_menu_add),
                    placeholder = painterResource(id = android.R.drawable.ic_menu_add)
                )
                FirstnameInput(
                    modifier = modifierForTextFields, viewModel = viewModel, uiState = uiState
                )
                ErrorText(
                    modifier = modifierForErrorTexts, message = uiState.firstnameError
                )
                Spacer(modifier = Modifier.height(16.dp))
                LastnameInput(
                    modifier = modifierForTextFields, viewModel = viewModel, uiState = uiState
                )
                ErrorText(
                    modifier = modifierForErrorTexts, message = uiState.lastnameError
                )
                Spacer(modifier = Modifier.height(16.dp))
                UsernameInput(
                    modifier = modifierForTextFields, viewModel = viewModel, uiState = uiState
                )
                ErrorText(
                    modifier = modifierForErrorTexts, message = uiState.usernameError
                )
                Spacer(modifier = Modifier.height(16.dp))
                ShowSelectSex(
                    modifier = modifierForTextFields,
                    viewModel = viewModel,
                    uiState = uiState
                )
                ErrorText(
                    modifier = modifierForErrorTexts, message = uiState.sexError
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    onClick = { viewModel.onEvent(UserDetailsScreenEvent.OnSaveBtnClicked) },
                    enabled = uiState.isLoading.not()
                ) {
                    Text("Save")
                }
                Spacer(modifier = Modifier.height(10.dp))
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
    modifier: Modifier, viewModel: UserDetailsScreenViewModel, uiState: UserDetailsScreenState
) {
    OutlinedTextField(
        modifier = modifier,
        value = uiState.firstname,
        onValueChange = { newValue ->
            viewModel.onEvent(UserDetailsScreenEvent.OnFirstnameChanged(newValue))
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
    modifier: Modifier, viewModel: UserDetailsScreenViewModel, uiState: UserDetailsScreenState
) {
    OutlinedTextField(
        modifier = modifier,
        value = uiState.lastname,
        onValueChange = { newValue ->
            viewModel.onEvent(UserDetailsScreenEvent.OnLastnameChanged(newValue))
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
    modifier: Modifier, viewModel: UserDetailsScreenViewModel, uiState: UserDetailsScreenState
) {
    OutlinedTextField(
        modifier = modifier,
        value = uiState.username,
        onValueChange = { newValue ->
            viewModel.onEvent(UserDetailsScreenEvent.OnUsernameChanged(newValue))
        },
        label = {
            Text("username")
        },
        isError = uiState.usernameError != null,
        leadingIcon = {
            Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "username")
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        enabled = uiState.isLoading.not()
    )
}

@Composable
private fun ShowSelectSex(
    modifier: Modifier,
    viewModel: UserDetailsScreenViewModel,
    uiState: UserDetailsScreenState
) {
    Column(modifier = modifier) {
        Text("Select your gender:")
        Row(modifier = Modifier.fillMaxWidth()) {
            RadioButtonOption(
                text = "Male",
                selected = uiState.sex == Sex.MALE,
                uiState.isLoading.not()
            ) {
                viewModel.onEvent(UserDetailsScreenEvent.OnSexChanged(Sex.MALE))
            }
            Spacer(modifier = Modifier.width(8.dp))
            RadioButtonOption(
                text = "Female",
                selected = uiState.sex == Sex.FEMALE,
                uiState.isLoading.not()
            ) {
                viewModel.onEvent(UserDetailsScreenEvent.OnSexChanged(Sex.FEMALE))
            }
        }
    }
}

@Composable
fun RadioButtonOption(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Row {
        RadioButton(
            selected = selected,
            onClick = onClick,
            enabled = enabled
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(text)
    }
}


