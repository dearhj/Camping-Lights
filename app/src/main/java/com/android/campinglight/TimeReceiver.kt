package com.android.campinglight

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.android.campinglight.App.Companion.P2PRO_PATH
import com.android.campinglight.App.Companion.T2_PATH
import com.android.campinglight.App.Companion.isP2Pro
import com.android.campinglight.App.Companion.isT2
import java.io.File

class TimeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        var result = false
        if (isT2) result = File(T2_PATH).write("0")
        if (isP2Pro) result = File(P2PRO_PATH).write("0")
        if (result) offFlag.value = true
    }
}