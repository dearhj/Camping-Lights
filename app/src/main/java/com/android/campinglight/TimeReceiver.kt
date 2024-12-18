package com.android.campinglight

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val result = writeStatus("0")
        if (result) offFlag.value = true
    }
}