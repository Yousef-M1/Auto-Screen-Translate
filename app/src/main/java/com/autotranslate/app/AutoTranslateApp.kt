package com.autotranslate.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.autotranslate.app.data.PrefsManager
import com.google.android.gms.ads.MobileAds

class AutoTranslateApp : Application() {

    companion object {
        const val CHANNEL_ID = "translate_service_channel"
    }

    override fun onCreate() {
        super.onCreate()
        PrefsManager.init(this)
        createNotificationChannel()
        MobileAds.initialize(this)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Translation Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows when Auto Translate is active"
            setShowBadge(false)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}
