package com.example.reachyourgoal.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.reachyourgoal.R
import com.example.reachyourgoal.domain.model.FileUploadModel
import com.example.reachyourgoal.domain.model.FileUploadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class FirebaseFileUploadService : Service() {

    @Inject
    lateinit var serviceModelProvider: Provider<FirebaseFileUploadServiceModel>
    private lateinit var serviceModel: FirebaseFileUploadServiceModel

    companion object {
        const val CHANNEL_ID = "FILE_UPLOAD"
        const val CHANNEL_DESCRIPTION = "Used for upload files notifications"
        const val STATE = "STATE"
        const val NOTIFICATION_ID = "NOTIFICATION_ID"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        serviceModel = serviceModelProvider.get()

        val state =
            intent.getStringExtra(STATE) ?: return super.onStartCommand(intent, flags, startId)

        val notificationId =
            intent.getIntExtra(NOTIFICATION_ID, -1)

        CoroutineScope(Dispatchers.Default).launch {
            serviceModel.uploadState.filterNotNull().collect { file ->
                when (file.state) {
                    FileUploadState.CANCELED, FileUploadState.FINISHED -> hideNotification(file.notificationId)
                    else -> showNotification(file)
                }
            }
        }

        when (state) {
            FileUploadState.NOT_STARTED.name -> serviceModel.startFileUpload(notificationId)
            FileUploadState.RESUMED.name -> serviceModel.resumeFileUpload(notificationId)
            FileUploadState.PAUSED.name -> serviceModel.pauseFileUpload(notificationId)
            FileUploadState.CANCELED.name -> serviceModel.cancelFileUpload(notificationId)
            FileUploadState.RESTARTED.name -> serviceModel.restartFileUpload(notificationId)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun showNotification(fileToUpload: FileUploadModel) {
        val notification = NotificationCompat
            .Builder(
                this,
                CHANNEL_ID
            )
            .setSmallIcon(R.drawable.ic_cup)
            .setSound(null)
            .setVibrate(null)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(createRemoteView(fileToUpload))
            .build()
        if (fileToUpload.state == FileUploadState.STARTED) {
            startForeground(fileToUpload.notificationId, notification)
        } else {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(fileToUpload.notificationId, notification)
        }
    }

    private fun createRemoteView(fileToUpload: FileUploadModel): RemoteViews {
        val view = RemoteViews(packageName, R.layout.view_file_upload_notification)
        view.setTextViewText(R.id.file_name_tv, fileToUpload.uri.lastPathSegment)
        view.setTextViewText(R.id.file_upload_progress, fileToUpload.progress.toString())
        view.setProgressBar(R.id.file_upload_progress_bar, 100, fileToUpload.progress, false)
        when (fileToUpload.state) {
            FileUploadState.IN_PROGRESS, FileUploadState.RESUMED -> {
                view.setOnClickPendingIntent(
                    R.id.pause_resume_btn,
                    createPendingIntent(fileToUpload.notificationId, FileUploadState.PAUSED)
                )
                view.setImageViewResource(R.id.pause_resume_btn, R.drawable.ic_pause)
            }

            FileUploadState.PAUSED -> {
                view.setOnClickPendingIntent(
                    R.id.pause_resume_btn,
                    createPendingIntent(fileToUpload.notificationId, FileUploadState.RESUMED)
                )
                view.setImageViewResource(R.id.pause_resume_btn, R.drawable.ic_play)
            }

            FileUploadState.FAILED -> {
                view.setOnClickPendingIntent(
                    R.id.pause_resume_btn,
                    createPendingIntent(fileToUpload.notificationId, FileUploadState.RESTARTED)
                )
                view.setImageViewResource(R.id.pause_resume_btn, R.drawable.ic_replay)
            }

            else -> Unit
        }
        view.setOnClickPendingIntent(
            R.id.cancel_btn,
            createPendingIntent(fileToUpload.notificationId, FileUploadState.CANCELED)
        )
        return view
    }

    private fun createPendingIntent(notificationId: Int, state: FileUploadState): PendingIntent {
        val intent = Intent(this, FirebaseFileUploadService::class.java)
        intent.putExtra(STATE, state.name)
        intent.putExtra(NOTIFICATION_ID, notificationId)
        return PendingIntent.getService(
            this,
            notificationId * FileUploadState.values().size - state.ordinal,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun hideNotification(notificationId: Int) {
        NotificationManagerCompat.from(this).cancel(notificationId)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        serviceModel.stop()
        super.onDestroy()
    }
}