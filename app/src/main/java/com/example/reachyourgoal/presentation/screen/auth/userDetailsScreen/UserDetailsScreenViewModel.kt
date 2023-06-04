package com.example.reachyourgoal.presentation.screen.auth.userDetailsScreen

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.reachyourgoal.common.BaseViewModel
import com.example.reachyourgoal.common.Validators
import com.example.reachyourgoal.domain.model.local.Sex
import com.example.reachyourgoal.domain.model.local.UserModel
import com.example.reachyourgoal.domain.repository.AuthRepository
import com.example.reachyourgoal.presentation.screen.auth.registerScreen.RegisterScreenEffect
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
class UserDetailsScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository
) :
    BaseViewModel<UserDetailsScreenState, UserDetailsScreenEvent, UserDetailsScreenEffect>() {

    private val _uiState = MutableStateFlow(
        UserDetailsScreenState(
            false,
            EMPTY_STRING,
            EMPTY_STRING,
            EMPTY_STRING,
            Sex.PREFER_NOT_TO_SAY,
            null,
            null,
            null,
            null,
            null,
        )
    )
    override val uiState: StateFlow<UserDetailsScreenState>
        get() = _uiState

    private val _uiEffect = MutableSharedFlow<UserDetailsScreenEffect>()
    override val uiEffect: SharedFlow<UserDetailsScreenEffect>
        get() = _uiEffect

    override fun onEvent(event: UserDetailsScreenEvent) {
        when (event) {
            is UserDetailsScreenEvent.OnFirstnameChanged -> onFirstnameChanged(event.firstname)
            is UserDetailsScreenEvent.OnLastnameChanged -> onLastnameChanged(event.lastname)
            is UserDetailsScreenEvent.OnUsernameChanged -> onUsernameChanged(event.username)
            is UserDetailsScreenEvent.OnImageUriChanged -> onImageUriChanged(event.imageUri)
            is UserDetailsScreenEvent.OnSexChanged -> onSexChanged(event.sex)
            UserDetailsScreenEvent.OnPhotoPickerBtnClicked -> onPhotoPickerBtnClicked()
            UserDetailsScreenEvent.OnBackBtnClicked -> onBackBtnClicked()
            UserDetailsScreenEvent.OnSaveBtnClicked -> onSaveBtnClicked()
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

    private fun onImageUriChanged(imageUri: Uri?) {
        _uiState.update { state ->
            state.copy(imageUri = imageUri)
        }
    }

    private fun onSexChanged(sex: Sex) {
        _uiState.update { state ->
            state.copy(sex = sex)
        }
    }

    private fun onSaveBtnClicked() {
        val userModel = with(_uiState.value) {
            val firstname = validateFirstname(firstname)
            val lastname = validateLastname(lastname)
            val username = validateUsername(username)
            val imageUri = imageUri
            val sex = validateSex(sex)
            UserModel(
                firstname,
                lastname,
                username,
                imageUri,
                sex
            )
        }
        _uiState.value.within {
            if (listOfNotNull(
                    firstnameError,
                    lastnameError,
                    usernameError,
                    sexError
                ).isNotEmpty()
            ) {
                return
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            authRepository
                .saveUserDetails(userModel)
                .onStart {
                    _uiState.update { state ->
                        state.copy(isLoading = true)
                    }
                }
                .onEach {
                    _uiEffect.emit(UserDetailsScreenEffect.NavigateToMainScreen)
                }
                .catch { error ->
                    error.message?.let { errorMessage ->
                        _uiEffect.emit(UserDetailsScreenEffect.ShowErrorMessage(errorMessage))
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

    private fun validateFirstname(firstname: String): String {
        _uiState.update { state ->
            state.copy(firstnameError = Validators.firstAndLastnameValidator(firstname))
        }
        return firstname
    }

    private fun validateLastname(lastname: String): String {
        _uiState.update { state ->
            state.copy(lastnameError = Validators.firstAndLastnameValidator(state.lastname))
        }
        return lastname
    }

    private fun validateUsername(username: String): String {
        _uiState.update { state ->
            state.copy(usernameError = Validators.userNameValidator(username))
        }
        return username
    }

    private fun validateSex(sex: Sex): Sex {
        _uiState.update { state ->
            state.copy(sexError = Validators.sexValidator(sex))
        }
        return sex
    }

    private fun onBackBtnClicked() {
        viewModelScope.launch {
            _uiEffect.emit(UserDetailsScreenEffect.NavigateBack)
        }
    }

    private fun onPhotoPickerBtnClicked() {
        viewModelScope.launch {
            _uiEffect.emit(UserDetailsScreenEffect.ShowPhotoPicker)
        }
    }
}