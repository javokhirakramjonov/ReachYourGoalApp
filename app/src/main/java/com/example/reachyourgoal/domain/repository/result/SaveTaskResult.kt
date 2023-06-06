package com.example.reachyourgoal.domain.repository.result

import java.util.UUID

sealed class SaveTaskResult {
    data class TaskSavedOffline(val taskId: UUID) : SaveTaskResult()
}