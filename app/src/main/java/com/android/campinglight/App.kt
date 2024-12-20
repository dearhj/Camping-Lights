package com.android.campinglight

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import java.io.File

class App : Application() {
    companion object {
        var sp: SharedPreferences? = null
        const val P2PRO_PATH = "/sys/devices/platform/gftk_camplight/camplight_mode"
        const val T2_PATH = "/sys/devices/platform/camp_led/camp_led"
        val isT2 = File(T2_PATH).exists()
        val isP2Pro = File(P2PRO_PATH).exists()
        lateinit var alarmManager: AlarmManager
        lateinit var intentAll: Intent
        lateinit var intentHighBrightness: Intent
        lateinit var intentOtherBrightness: Intent
        lateinit var pendingIntentAll: PendingIntent
        lateinit var pendingIntentHighBrightness: PendingIntent
        lateinit var pendingIntentOtherBrightness: PendingIntent
        var hasLight15Min = false
    }

    override fun onCreate() {
        super.onCreate()
        sp = PreferenceManager.getDefaultSharedPreferences(this)
        if (sp?.getString("time", "") == "") {
            if (isT2) sp?.edit()?.putString("time", "5")?.apply()
            if (isP2Pro) sp?.edit()?.putString("time", "0")?.apply()
        }
        hasLight15Min = false
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        intentAll = Intent(this, TimeReceiver::class.java).apply {
            action = "com.light.LIGHTING_DURATION"
        }
        intentHighBrightness = Intent(this, TimeReceiver::class.java).apply {
            action = "com.light.HIGH_BRIGHTNESS"
        }
        intentOtherBrightness = Intent(this, TimeReceiver::class.java).apply {
            action = "com.light.OTHER_BRIGHTNESS"
        }
        pendingIntentAll =
            PendingIntent.getBroadcast(this, 1000, intentAll, PendingIntent.FLAG_IMMUTABLE)
        pendingIntentHighBrightness =
            PendingIntent.getBroadcast(
                this,
                1001,
                intentHighBrightness,
                PendingIntent.FLAG_IMMUTABLE
            )
        pendingIntentOtherBrightness =
            PendingIntent.getBroadcast(
                this,
                1002,
                intentOtherBrightness,
                PendingIntent.FLAG_IMMUTABLE
            )
    }
}