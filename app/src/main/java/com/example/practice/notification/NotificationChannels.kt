package com.example.practice.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.practice.R

object NotificationChannels {
    const val FAVORITE_PAIR_ID = "favorite_pair_channel"

    fun ensureFavoritePairChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        val channelName = context.getString(R.string.favorite_pair_notification_channel_name)
        val description = context.getString(R.string.favorite_pair_notification_channel_description)
        val channel = NotificationChannel(
            FAVORITE_PAIR_ID,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            this.description = description
            enableVibration(true)
        }
        manager.createNotificationChannel(channel)
    }
}


