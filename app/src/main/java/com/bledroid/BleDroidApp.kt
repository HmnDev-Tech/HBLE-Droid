package com.bledroid

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

class BleDroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "BleDroid Spam Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Persistent notification while BLE spam is active"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "bledroid_spam_channel"
    }
}
