package com.example.reachyourgoal.util

inline fun <T> T.within(action: T.() -> Unit) {
    this.action()
}