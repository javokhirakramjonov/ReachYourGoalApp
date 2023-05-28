package com.example.reachyourgoal.presentation.screen.registerScreen

import android.net.Uri

sealed class RegisterScreenEvent {
    data class OnFirstnameChanged(val firstname: String) : RegisterScreenEvent()
    data class OnLastnameChanged(val lastname: String) : RegisterScreenEvent()
    data class OnUsernameChanged(val username: String) : RegisterScreenEvent()
    data class OnEmailChanged(val email: String) : RegisterScreenEvent()
    data class OnPasswordChanged(val password: String) : RegisterScreenEvent()
    data class OnPasswordRepeatChanged(val password: String) : RegisterScreenEvent()
    data class OnImageUriChanged(val imageUri: Uri?) : RegisterScreenEvent()
    object OnPhotoPickerBtnClicked: RegisterScreenEvent()
    object OnRegisterBtnClicked : RegisterScreenEvent()
    object OnLoginBtnClicked : RegisterScreenEvent()
}