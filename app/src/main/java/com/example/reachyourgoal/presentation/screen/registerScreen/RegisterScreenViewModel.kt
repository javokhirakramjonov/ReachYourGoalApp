package com.example.reachyourgoal.presentation.screen.registerScreen

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.reachyourgoal.common.BaseViewModel
import com.example.reachyourgoal.common.Validators
import com.example.reachyourgoal.domain.model.local.UserModel
import com.example.reachyourgoal.domain.repository.AuthRepository
import com.example.reachyourgoal.util.EMPTY_STRING
import com.example.reachyourgoal.util.within
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
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
            EMPTY_STRING,
            null,
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
            is RegisterScreenEvent.OnPasswordRepeatChanged -> onPasswordRepeatChanged(event.password)
            is RegisterScreenEvent.OnImageUriChanged -> onImageUriChanged(event.imageUri)
            RegisterScreenEvent.OnRegisterBtnClicked -> onRegisterBtnClicked()
            RegisterScreenEvent.OnLoginBtnClicked -> onLoginBtnClicked()
            RegisterScreenEvent.OnPhotoPickerBtnClicked -> onPhotoPickerBtnClicked()
        }
    }

    private fun onFirstnameChanged(firstname: String) {
        _uiState.update { state ->
            state.copy(firstname = firstname, firstnameError = null)
        }
    }

    private fun onLastnameChanged(lastname: String) {
        _uiState.update { state ->
            state.copy(lastname = lastname, lastnameError = null)
        }
    }

    private fun onUsernameChanged(username: String) {
        _uiState.update { state ->
            state.copy(username = username, usernameError = null)
        }
    }

    private fun onEmailChanged(email: String) {
        _uiState.update { state ->
            state.copy(email = email, emailError = null)
        }
    }

    private fun onPasswordChanged(password: String) {
        _uiState.update { state ->
            state.copy(password = password, passwordError = null)
        }
    }

    private fun onPasswordRepeatChanged(password: String) {
        _uiState.update { state ->
            state.copy(passwordRepeat = password, passwordRepeatError = null)
        }
    }

    private fun onImageUriChanged(imageUri: Uri?) {
        _uiState.update { state ->
            state.copy(imageUri = imageUri)
        }
    }

    private fun onRegisterBtnClicked() {
        validateFirstname()
        validateLastname()
        validateUsername()
        validateEmail()
        validatePassword()
        validatePasswordRepeat()
        _uiState.value.within {
            if (listOfNotNull(
                    firstnameError,
                    lastnameError,
                    usernameError,
                    emailError,
                    passwordError,
                    passwordRepeatError
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
                .launchIn(this)
        }
    }

    private fun validateFirstname() {
        _uiState.update { state ->
            state.copy(firstnameError = Validators.firstAndLastnameValidator(state.firstname))
        }
    }

    private fun validateLastname() {
        _uiState.update { state ->
            state.copy(lastnameError = Validators.firstAndLastnameValidator(state.lastname))
        }
    }

    private fun validateUsername() {
        _uiState.update { state ->
            state.copy(usernameError = Validators.userNameValidator(state.username))
        }
    }

    private fun validateEmail() {
        _uiState.update { state ->
            state.copy(emailError = Validators.emailValidator(state.email))
        }
    }

    private fun validatePassword() {
        _uiState.update { state ->
            state.copy(passwordError = Validators.passwordValidator(state.password))
        }
    }

    private fun validatePasswordRepeat() {
        _uiState.update { state ->
            state.copy(
                passwordRepeatError = Validators.passwordRepeatValidator(
                    state.password,
                    state.passwordRepeat
                )
            )
        }
    }

    private fun onLoginBtnClicked() {
        viewModelScope.launch {
            _uiEffect.emit(RegisterScreenEffect.NavigateToLoginScreen)
        }
    }

    private fun onPhotoPickerBtnClicked() {
        viewModelScope.launch {
            _uiEffect.emit(RegisterScreenEffect.ShowPhotoPicker)
        }
    }
}
