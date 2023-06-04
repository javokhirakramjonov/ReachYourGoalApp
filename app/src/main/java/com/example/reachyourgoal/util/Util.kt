package com.example.reachyourgoal.util

inline fun <T> T.within(action: T.() -> Unit) {
    this.action()
}

fun getErrorMessageOrDefault(throwable: Throwable?): String {
    return throwable?.message ?: SOMETHING_WENT_WRONG
}