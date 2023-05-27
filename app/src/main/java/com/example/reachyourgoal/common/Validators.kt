package com.example.reachyourgoal.common

import android.util.Patterns

object Validators {
    fun emailValidator(email: String): String? {
        return if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
            null
        else
            "Email is not valid"
    }

    fun nameValidator(firstname: String): String? {
        return if ("^[a-zA-Z]{2,}\$".toRegex().matches(firstname))
            null
        else
            "First name must be at least 2 characters long and should only contain letters"
    }

    fun passwordValidator(password: String): String? {
        return if ("^.{4,}\$".toRegex().matches(password))
            null
        else
            "Password must be at least 4 characters long and should not be blank."
    }
}