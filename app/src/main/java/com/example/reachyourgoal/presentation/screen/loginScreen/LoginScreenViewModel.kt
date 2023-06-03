package com.example.reachyourgoal.presentation.screen.loginScreen

import androidx.lifecycle.viewModelScope
import com.example.reachyourgoal.common.BaseViewModel
import com.example.reachyourgoal.common.Validators
import com.example.reachyourgoal.domain.repository.AuthRepository
import com.example.reachyourgoal.util.EMPTY_STRING
import com.example.reachyourgoal.util.within
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel<LoginScreenState, LoginScreenEvent, LoginScreenEffect>() {

    private val _uiState = MutableStateFlow(
        LoginScreenState(
            false,
            EMPTY_STRING,
            EMPTY_STRING,
            null,
            null
        )
    )
    override val uiState: StateFlow<LoginScreenState>
        get() = _uiState

    private val _uiEffect = MutableSharedFlow<LoginScreenEffect>()
    override val uiEffect: SharedFlow<LoginScreenEffect>
        get() = _uiEffect

    override fun onEvent(event: LoginScreenEvent) {
        when (event) {
            is LoginScreenEvent.OnEmailChanged -> onEmailChanged(event.email)
            is LoginScreenEvent.OnPasswordChanged -> onPasswordChanged(event.password)
            LoginScreenEvent.OnLoginBtnClicked -> onLogin()
            LoginScreenEvent.OnRegisterBtnClicked -> onCreateAccount()
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

    private fun onLogin() {
        _uiState.value.within {
            validateEmail()
            validatePassword()
        }
        _uiState.value.within {
            if (listOfNotNull(
                    emailError,
                    passwordError
                ).isNotEmpty()
            ) {
                return
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            val email = _uiState.value.email
            val password = _uiState.value.password
            authRepository
                .login(email, password)
                .onStart {
                    _uiState.update { state ->
                        state.copy(isLoading = true)
                    }
                }
                .onEach { data ->
                    data.fold(
                        {
                            _uiEffect.emit(LoginScreenEffect.NavigateToMainScreen)
                        },
                        { error ->
                            error.message?.let {
                                _uiEffect.emit(LoginScreenEffect.ShowErrorMessage(it))
                            }
                        }
                    )
                }
                .onCompletion { error ->
                    error?.message?.let { errorMessage ->
                        _uiEffect.emit(LoginScreenEffect.ShowErrorMessage(errorMessage))
                    }
                    _uiState.update { state ->
                        state.copy(isLoading = false)
                    }
                }
                .collect()
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

    private fun onCreateAccount() {
        viewModelScope.launch {
            _uiEffect.emit(LoginScreenEffect.NavigateToRegisterScreen)
        }
    }
}