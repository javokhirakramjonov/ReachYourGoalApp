package com.example.reachyourgoal.ui.common

import androidx.navigation.NavHostController

fun NavHostController.navigateWithPopUp(
    toRoute: String,
    fromRoute: String,
    isInclusive: Boolean = true
) {
    this.navigate(toRoute) {
        popUpTo(fromRoute) {
            inclusive = isInclusive
        }
    }
}