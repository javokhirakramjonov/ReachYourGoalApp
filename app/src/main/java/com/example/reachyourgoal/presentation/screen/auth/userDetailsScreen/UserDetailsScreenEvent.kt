package com.example.reachyourgoal.presentation.screen.auth.userDetailsScreen

import android.net.Uri
import com.example.reachyourgoal.domain.model.local.Sex

sealed class UserDetailsScreenEvent {
    data class OnFirstnameChanged(val firstname: String) : UserDetailsScreenEvent()
    data class OnLastnameChanged(val lastname: String) : UserDetailsScreenEvent()
    data class OnUsernameChanged(val username: String) : UserDetailsScreenEvent()
    data class OnSexChanged(val sex: Sex) : UserDetailsScreenEvent()
    data class OnImageUriChanged(val imageUri: Uri?) : UserDetailsScreenEvent()
    object OnPhotoPickerBtnClicked : UserDetailsScreenEvent()
    object OnSaveBtnClicked : UserDetailsScreenEvent()
    object OnBackBtnClicked : UserDetailsScreenEvent()
}