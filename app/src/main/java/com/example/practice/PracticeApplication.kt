package com.example.practice

import android.app.Application
import com.example.practice.notification.NotificationChannels
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PracticeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationChannels.ensureFavoritePairChannel(this)
    }
}
