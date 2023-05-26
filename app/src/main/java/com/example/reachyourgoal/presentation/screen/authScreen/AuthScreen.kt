package com.example.reachyourgoal.presentation.screen.authScreen

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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.reachyourgoal.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint

@Composable
fun ShowErrorMessage(message: String) {
    Snackbar {
        Text(message)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    navHostController: NavHostController,
    viewModel: AuthScreenViewModel = hiltViewModel()
) {

    ShowErrorMessage(message = "Hello")
    
    val uiState by viewModel.uiState.collectAsState()
    val uiEffect by viewModel.uiEffect.collectAsState(null)

    uiEffect?.let { effect ->
        when (effect) {
            is AuthScreenEffect.ErrorMessage -> ShowErrorMessage(effect.message)
            AuthScreenEffect.NavigateToMainScreen -> navHostController.navigate(Screen.MainScreen.route)
            AuthScreenEffect.NavigateToRegisterScreen -> Unit//TODO()
        }
    }

    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                value = uiState.email,
                onValueChange = { viewModel.onEvent(AuthScreenEvent.OnEmailChanged(it)) },
                label = {
                    Text("email")
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = "email")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                value = uiState.password,
                onValueChange = {
                    viewModel.onEvent(AuthScreenEvent.OnPasswordChanged(it))
                },
                label = {
                    Text("password")
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "email")
                },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                modifier = Modifier.fillMaxWidth(0.8f),
                onClick = { viewModel.onEvent(AuthScreenEvent.OnLoginBtnClicked) }
            ) {
                Text("Login")
            }
        }
    }
}
