package com.example.reachyourgoal.domain.model.local

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