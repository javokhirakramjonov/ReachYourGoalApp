package com.example.reachyourgoal.presentation.screen.authScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reachyourgoal.domain.repository.AuthRepository
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthScreenState())
    val uiState: StateFlow<AuthScreenState>
        get() = _uiState

    private val _uiEffect = MutableSharedFlow<AuthScreenEffect>()
    val uiEffect: SharedFlow<AuthScreenEffect>
        get() = _uiEffect

    fun onEvent(event: AuthScreenEvent) {
        when (event) {
            is AuthScreenEvent.OnEmailChanged -> _uiState.value =
                _uiState.value.copy(email = event.email)

            is AuthScreenEvent.OnPasswordChanged -> _uiState.value =
                _uiState.value.copy(password = event.password)

            AuthScreenEvent.OnLoginBtnClicked -> onLogin()
        }
    }

    private fun onLogin() {
        viewModelScope.launch(Dispatchers.IO) {
            val email = _uiState.value.email
            val password = _uiState.value.password
            authRepository
                .login(email, password)
                .onStart {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
                .onEach { data ->
                    data.fold(
                        {
                            _uiEffect.emit(AuthScreenEffect.NavigateToMainScreen)
                        },
                        { error ->
                            error.message?.let {
                                _uiEffect.emit(AuthScreenEffect.ErrorMessage(it))
                            }
                        }
                    )
                }
                .onCompletion { error ->
                    error?.message?.let {
                        _uiEffect.emit(AuthScreenEffect.ErrorMessage(it))
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .launchIn(this)
        }
    }
}