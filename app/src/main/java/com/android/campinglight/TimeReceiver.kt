package com.android.campinglight

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.io.File

class TimeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val result = File("/sys/devices/platform/gftk_camplight/camplight_mode").write("0")
        if (result) offFlag.value = true
    }
}