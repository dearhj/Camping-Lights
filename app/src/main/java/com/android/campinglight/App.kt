package com.android.campinglight

import android.app.Application
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
    }

    override fun onCreate() {
        super.onCreate()
        sp = PreferenceManager.getDefaultSharedPreferences(this)
    }
}