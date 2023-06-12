package com.example.reachyourgoal.common

import android.util.Patterns
import com.example.reachyourgoal.domain.model.local.Sex
import com.example.reachyourgoal.util.BULLET_POINT
import com.example.reachyourgoal.util.END_OF_LINE

object Validators {
    fun emailValidator(email: String): String? {
        return if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
            null
        else
            generateFormattedErrorMessage("Email is not valid.")
    }

    fun firstAndLastnameValidator(name: String): String? {
        return if ("^[a-zA-Z]{2,30}\$".toRegex().matches(name))
            null
        else
            generateFormattedErrorMessage(
                "It must be at least 2 characters long.",
                "It must be at most 30 characters long.",
                "It should contain only letters.",
            )
    }

    fun userNameValidator(username: String): String? {
        return if ("^[a-zA-Z][a-zA-Z0-9]{3,19}\$".toRegex().matches(username))
            null
        else
            generateFormattedErrorMessage(
                "It must be at least 4 characters long.",
                "It must be at most 20 characters long.",
                "It should start with a letter.",
                "It should not contain any spaces.",
            )
    }

    fun passwordValidator(password: String): String? {
        return if ("^.{6,20}\$".toRegex().matches(password))
            null
        else
            generateFormattedErrorMessage(
                "It must be at least 6 characters long.",
                "It must be at most 20 characters long.",
            )
    }

    fun passwordRepeatValidator(password: String, passwordRepeat: String): String? {
        return if (password == passwordRepeat)
            null
        else
            generateFormattedErrorMessage("Passwords must be same.")
    }

    fun taskNameValidator(taskName: String): String? {
        return if ("^[a-zA-Z0-9][a-zA-Z0-9\\s]{0,49}$".toRegex().matches(taskName))
            null
        else
            generateFormattedErrorMessage(
                "It must be at least 6 characters long.",
                "It must be at most 50 characters long.",
                "It should not start with space.",
                "It can contain letters, digits and spaces",
            )
    }

    fun taskDescriptionValidator(taskDescription: String): String? {
        return if ("^.{0,250}$".toRegex().matches(taskDescription))
            null
        else
            generateFormattedErrorMessage("It must be at most 250 characters long.")
    }

    fun sexValidator(sex: Sex): String? {
        return if (sex != Sex.PREFER_NOT_TO_SAY)
            null
        else
            generateFormattedErrorMessage("You should select one of them.")
    }

    private fun generateFormattedErrorMessage(vararg errorMessages: String): String? {
        if (errorMessages.isEmpty()) return null
        return errorMessages.joinToString(separator = END_OF_LINE) {
            "$BULLET_POINT $it"
        }
    }
}