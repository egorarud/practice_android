package com.example.practice.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.example.practice.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object FavoritePairReminderScheduler {
    private const val TAG = "FavoritePairScheduler"

    fun schedule(
        context: Context,
        fullName: String,
        favoritePairTime: String,
        showPermissionUi: Boolean = true
    ): Boolean {
        val trimmed = favoritePairTime.trim()
        if (trimmed.isEmpty()) {
            Log.w(TAG, "Empty favorite pair time")
            return false
        }
        val parts = trimmed.split(":")
        if (parts.size != 2) {
            Log.w(TAG, "Invalid time format: $trimmed")
            return false
        }
        val hour = parts[0].toIntOrNull() ?: return false
        val minute = parts[1].toIntOrNull() ?: return false

        val triggerTimeMillis = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            // Устанавливаем секунды и миллисекунды в 0, чтобы уведомление приходило точно в начале указанной минуты
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // Если указанное время уже прошло сегодня, планируем на завтра
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }.timeInMillis
        
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        Log.d(TAG, "Scheduling notification for: ${dateFormat.format(triggerTimeMillis)}")

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        if (alarmManager == null) {
            Log.e(TAG, "AlarmManager is null")
            return false
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Log.w(TAG, "Cannot schedule exact alarms - permission not granted")
            if (showPermissionUi) {
                Toast.makeText(
                    context,
                    R.string.favorite_pair_exact_alarm_permission_rationale,
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = "package:${context.packageName}".toUri()
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                runCatching { context.startActivity(intent) }
            }
            return false
        }

        NotificationChannels.ensureFavoritePairChannel(context)

        val intent = Intent(context, FavoritePairReminderReceiver::class.java).apply {
            putExtra(FavoritePairReminderReceiver.EXTRA_FULL_NAME, fullName)
            putExtra(FavoritePairReminderReceiver.EXTRA_FAVORITE_PAIR_TIME, favoritePairTime)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            FavoritePairReminderReceiver.NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
            Log.d(TAG, "Alarm scheduled successfully")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to schedule alarm: ${e.message}", e)
            return false
        }
    }
}

