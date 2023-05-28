package com.example.reachyourgoal.common

import android.util.Patterns
import com.example.reachyourgoal.util.BULLET_POINT
import com.example.reachyourgoal.util.END_OF_LINE

object Validators {
    fun emailValidator(email: String): String? {
        return if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
            null
        else
            listOf(
                "Email is not valid."
            ).joinToString(separator = END_OF_LINE) {
                "$BULLET_POINT $it"
            }
    }

    fun firstAndLastnameValidator(name: String): String? {
        return if ("^[a-zA-Z]{2,30}\$".toRegex().matches(name))
            null
        else
            listOf(
                "It must be at least 2 characters long.",
                "It must be at most 30 characters long.",
                "It should contain only letters.",
            ).joinToString(separator = END_OF_LINE) {
                "$BULLET_POINT $it"
            }
    }

    fun userNameValidator(username: String): String? {
        return if ("^[a-zA-Z][a-zA-Z0-9]{3,19}\$".toRegex().matches(username))
            null
        else
            listOf(
                "It must be at least 4 characters long.",
                "It must be at most 20 characters long.",
                "It should start with a letter.",
                "It should not contain any spaces.",
            ).joinToString(separator = END_OF_LINE) {
                "$BULLET_POINT $it"
            }
    }

    fun passwordValidator(password: String): String? {
        return if ("^.{4,20}\$".toRegex().matches(password))
            null
        else
            listOf(
                "It must be at least 4 characters long.",
                "It must be at most 20 characters long.",
            ).joinToString(separator = END_OF_LINE) {
                "$BULLET_POINT $it"
            }
    }

    fun passwordRepeatValidator(password: String, passwordRepeat: String): String? {
        return if (password == passwordRepeat)
            null
        else
            listOf(
                "Passwords must be same."
            ).joinToString(separator = END_OF_LINE) {
                "$BULLET_POINT $it"
            }
    }

    fun taskNameValidator(taskName: String) : String? {
        return if("^[a-zA-Z0-9][a-zA-Z0-9\\s]{3,49}$".toRegex().matches(taskName))
            null
        else
            listOf(
                "It must be at least 4 characters long.",
                "It must be at most 50 characters long.",
                "It should not start with space.",
                "It can contain letters, digits and spaces",
            ).joinToString(separator = END_OF_LINE) {
                "$BULLET_POINT $it"
            }
    }

    fun taskDescriptionValidator(taskDescription: String): String? {
        return if("^.{0,250}$".toRegex().matches(taskDescription))
            null
        else
            listOf(
                "It must be at most 250 characters long.",
            ).joinToString(separator = END_OF_LINE) {
                "$BULLET_POINT $it"
            }
    }
}