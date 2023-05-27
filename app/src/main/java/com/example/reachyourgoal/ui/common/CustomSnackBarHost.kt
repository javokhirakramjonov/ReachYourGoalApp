package com.example.reachyourgoal.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.reachyourgoal.util.EMPTY_STRING

@Composable
fun CustomSnackBarHost(
    hostState: SnackbarHostState,
    onActionBtnClick: (() -> Unit)? = null
) {
    SnackbarHost(hostState = hostState) { data ->
        val buttonColor = when (data.visuals) {
            is SnackBarStyles.ErrorSnackBar -> {
                ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error
                )
            }

            else -> {
                ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.inversePrimary
                )
            }
        }

        Snackbar(
            modifier = Modifier
                .border(2.dp, Color.Transparent)
                .padding(12.dp),
            action = {
                TextButton(
                    onClick = { onActionBtnClick?.invoke() ?: data.dismiss() },
                    colors = buttonColor
                ) { Text(data.visuals.actionLabel ?: EMPTY_STRING) }
            }
        ) {
            Text(data.visuals.message)
        }
    }
}