package com.example.reachyourgoal.presentation.screen.registerScreen

import androidx.lifecycle.viewModelScope
import com.example.reachyourgoal.common.BaseViewModel
import com.example.reachyourgoal.common.Validators
import com.example.reachyourgoal.domain.model.UserModel
import com.example.reachyourgoal.domain.repository.AuthRepository
import com.example.reachyourgoal.util.EMPTY_STRING
import com.example.reachyourgoal.util.within
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel<RegisterScreenState, RegisterScreenEvent, RegisterScreenEffect>() {

    private val _uiState = MutableStateFlow(
        RegisterScreenState(
            false,
            EMPTY_STRING,
            EMPTY_STRING,
            EMPTY_STRING,
            EMPTY_STRING,
            EMPTY_STRING,
            null,
            null,
            null,
            null,
            null,
            null
        )
    )
    override val uiState: StateFlow<RegisterScreenState>
        get() = _uiState

    private val _uiEffect = MutableSharedFlow<RegisterScreenEffect>()
    override val uiEffect: SharedFlow<RegisterScreenEffect>
        get() = _uiEffect

    override fun onEvent(event: RegisterScreenEvent) {
        when (event) {
            is RegisterScreenEvent.OnFirstnameChanged -> onFirstnameChanged(event.firstname)
            is RegisterScreenEvent.OnLastnameChanged -> onLastnameChanged(event.lastname)
            is RegisterScreenEvent.OnUsernameChanged -> onUsernameChanged(event.username)
            is RegisterScreenEvent.OnEmailChanged -> onEmailChanged(event.email)
            is RegisterScreenEvent.OnPasswordChanged -> onPasswordChanged(event.password)
            is RegisterScreenEvent.OnImageUriChanged -> onImageUriChanged(event.imageUri)
            RegisterScreenEvent.RegisterBtnClicked -> onRegisterBtnClicked()
            RegisterScreenEvent.LoginBtnClicked -> onLoginBtnClicked()
        }
    }

    private fun onFirstnameChanged(firstname: String) {
        _uiState.update { state ->
            state.copy(firstname = firstname)
        }
    }

    private fun onLastnameChanged(lastname: String) {
        _uiState.update { state ->
            state.copy(lastname = lastname)
        }
    }

    private fun onUsernameChanged(username: String) {
        _uiState.update { state ->
            state.copy(username = username)
        }
    }

    private fun onEmailChanged(email: String) {
        _uiState.update { state ->
            state.copy(email = email)
        }
    }

    private fun onPasswordChanged(password: String) {
        _uiState.update { state ->
            state.copy(password = password)
        }
    }

    private fun onImageUriChanged(imageUri: String) {
        _uiState.update { state ->
            state.copy(imageUri = imageUri)
        }
    }

    private fun onRegisterBtnClicked() {
        _uiState.value.within {
            validateFirstname(firstname)
            validateLastname(lastname)
            validateUsername(username)
            validateEmail(email)
            validatePassword(password)
        }
        _uiState.value.within {
            if (listOfNotNull(
                    firstnameError,
                    lastnameError,
                    usernameError,
                    emailError,
                    passwordError
                ).isNotEmpty()
            ) {
                return
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            val firstname = _uiState.value.firstname
            val lastname = _uiState.value.lastname
            val username = _uiState.value.username
            val email = _uiState.value.email
            val password = _uiState.value.password
            val imageUri = _uiState.value.imageUri

            authRepository.register(
                UserModel(
                    firstname,
                    lastname,
                    username,
                    email,
                    password,
                    imageUri
                )
            )
                .onStart {
                    _uiState.update { state ->
                        state.copy(isLoading = true)
                    }
                }
                .onEach { data ->
                    data.fold(
                        {
                            _uiEffect.emit(RegisterScreenEffect.NavigateToMainScreen)
                        },
                        { error ->
                            error.message?.let {
                                _uiEffect.emit(RegisterScreenEffect.ShowErrorMessage(it))
                            }
                        }
                    )
                }
                .onCompletion { error ->
                    error?.message?.let { errorMessage ->
                        _uiEffect.emit(RegisterScreenEffect.ShowErrorMessage(errorMessage))
                    }
                    _uiState.update { state ->
                        state.copy(isLoading = false)
                    }
                }
        }
    }

    private fun validateFirstname(firstname: String) {
        _uiState.update { state ->
            state.copy(firstnameError = Validators.nameValidator(firstname))
        }
    }

    private fun validateLastname(lastname: String) {
        _uiState.update { state ->
            state.copy(lastnameError = Validators.nameValidator(lastname))
        }
    }

    private fun validateUsername(username: String) {
        _uiState.update { state ->
            state.copy(usernameError = Validators.nameValidator(username))
        }
    }

    private fun validateEmail(email: String) {
        _uiState.update { state ->
            state.copy(emailError = Validators.emailValidator(email))
        }
    }

    private fun validatePassword(password: String) {
        _uiState.update { state ->
            state.copy(passwordError = Validators.passwordValidator(password))
        }
    }

    private fun onLoginBtnClicked() {}
}
