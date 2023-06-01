package com.example.reachyourgoal.domain.model

enum class FileUploadState {
    NOT_STARTED,
    STARTED,
    IN_PROGRESS,
    PAUSED,
    RESUMED,
    FAILED,
    CANCELED,
    FINISHED,
    RESTARTED
}