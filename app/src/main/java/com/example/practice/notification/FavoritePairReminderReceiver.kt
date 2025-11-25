package com.example.practice.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.practice.MainActivity
import com.example.practice.R

class FavoritePairReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive called")
        val fullName = intent.getStringExtra(EXTRA_FULL_NAME).orEmpty()
        val favoritePairTime = intent.getStringExtra(EXTRA_FAVORITE_PAIR_TIME).orEmpty()
        val notificationManager = NotificationManagerCompat.from(context)
        
        // Проверяем разрешение на показ уведомлений
        if (!notificationManager.areNotificationsEnabled()) {
            Log.w(TAG, "Notifications are disabled")
            return
        }

        val contentText = if (fullName.isBlank()) {
            context.getString(R.string.favorite_pair_notification_body_generic)
        } else {
            context.getString(R.string.favorite_pair_notification_body_named, fullName)
        }

        val mainActivityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            mainActivityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(context, NotificationChannels.FAVORITE_PAIR_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.favorite_pair_notification_title))
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        try {
            notificationManager.notify(NOTIFICATION_ID, notification)
            Log.d(TAG, "Notification sent successfully")
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to show notification: ${e.message}", e)
        }

        if (favoritePairTime.isNotBlank()) {
            FavoritePairReminderScheduler.schedule(
                context = context,
                fullName = fullName,
                favoritePairTime = favoritePairTime,
                showPermissionUi = false
            )
        }
    }

    companion object {
        private const val TAG = "FavoritePairReminder"
        const val EXTRA_FULL_NAME = "extra_full_name"
        const val EXTRA_FAVORITE_PAIR_TIME = "extra_favorite_pair_time"
        const val NOTIFICATION_ID = 1011
    }
}


