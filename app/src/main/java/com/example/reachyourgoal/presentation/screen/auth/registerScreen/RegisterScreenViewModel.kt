package com.example.reachyourgoal.presentation.screen.auth.registerScreen

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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
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
            is RegisterScreenEvent.OnEmailChanged -> onEmailChanged(event.email)
            is RegisterScreenEvent.OnPasswordChanged -> onPasswordChanged(event.password)
            is RegisterScreenEvent.OnPasswordRepeatChanged -> onPasswordRepeatChanged(event.password)
            RegisterScreenEvent.OnRegisterBtnClicked -> onRegisterBtnClicked()
            RegisterScreenEvent.OnLoginBtnClicked -> onLoginBtnClicked()
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

    private fun onRegisterBtnClicked() {
        val (email, password) = with(_uiState.value) {
            val email = validateEmail(email)
            val password = validatePassword(password)
            Pair(email, password)
        }
        _uiState.value.within {
            validatePasswordRepeat(password, passwordRepeat)

            if (listOfNotNull(
                    emailError,
                    passwordError,
                    passwordRepeatError
                ).isNotEmpty()
            ) {
                return
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            authRepository
                .register(email, password)
                .onStart {
                    _uiState.update { state ->
                        state.copy(isLoading = true)
                    }
                }
                .onEach {
                    _uiEffect.emit(RegisterScreenEffect.NavigateToUserDetailsScreen)
                }
                .catch { error ->
                    error.message?.let { errorMessage ->
                        _uiEffect.emit(RegisterScreenEffect.ShowErrorMessage(errorMessage))
                    }
                }
                .onCompletion {
                    _uiState.update { state ->
                        state.copy(isLoading = false)
                    }
                }
                .collect()
        }
    }

    private fun validateEmail(email: String): String {
        _uiState.update { state ->
            state.copy(emailError = Validators.emailValidator(email))
        }
        return email
    }

    private fun validatePassword(password: String): String {
        _uiState.update { state ->
            state.copy(passwordError = Validators.passwordValidator(password))
        }
        return password
    }

    private fun validatePasswordRepeat(
        password: String,
        passwordRepeat: String
    ): String {
        _uiState.update { state ->
            state.copy(
                passwordRepeatError = Validators.passwordRepeatValidator(
                    password,
                    passwordRepeat
                )
            )
        }
        return passwordRepeat
    }

    private fun onLoginBtnClicked() {
        viewModelScope.launch {
            _uiEffect.emit(RegisterScreenEffect.NavigateToLoginScreen)
        }
    }
}
