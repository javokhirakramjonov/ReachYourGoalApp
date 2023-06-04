package com.example.reachyourgoal.presentation.screen.auth.userDetailsScreen

sealed class UserDetailsScreenEffect {
    object NavigateBack : UserDetailsScreenEffect()
    object NavigateToMainScreen : UserDetailsScreenEffect()
    object ShowPhotoPicker : UserDetailsScreenEffect()
    data class ShowErrorMessage(val errorMessage: String) : UserDetailsScreenEffect()
    data class ShowSuccessMessage(val successMessage: String) : UserDetailsScreenEffect()
}