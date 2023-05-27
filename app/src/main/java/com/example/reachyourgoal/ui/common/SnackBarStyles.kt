package com.example.reachyourgoal.ui.common

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals

sealed class SnackBarStyles {
    class ErrorSnackBar(
        errorMessage: String,
    ) : SnackbarVisuals {
        override val actionLabel = "Ok"
        override val duration = SnackbarDuration.Indefinite
        override val message = errorMessage
        override val withDismissAction = true
    }
}