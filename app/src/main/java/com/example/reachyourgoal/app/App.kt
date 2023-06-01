package com.example.reachyourgoal.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.reachyourgoal.service.FirebaseFileUploadService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                FirebaseFileUploadService.CHANNEL_ID,
                FirebaseFileUploadService.CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = FirebaseFileUploadService.CHANNEL_DESCRIPTION
            val service = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(channel)
        }
    }
}