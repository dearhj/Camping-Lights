package com.android.campinglight

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager

class App : Application() {
    companion object {
        var sp: SharedPreferences? = null
    }

    override fun onCreate() {
        super.onCreate()
        sp = PreferenceManager.getDefaultSharedPreferences(this)
    }
}