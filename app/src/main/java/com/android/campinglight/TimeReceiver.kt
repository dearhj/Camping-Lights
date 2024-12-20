package com.android.campinglight

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.android.campinglight.App.Companion.hasLight15Min
import com.android.campinglight.App.Companion.isT2
import com.android.campinglight.App.Companion.sp

class TimeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "com.light.LIGHTING_DURATION" -> {
                if (readStatus() != "0") {
                    val result = writeStatus("0")
                    if (result) offFlag.value = true
                    if (isT2) t2Timer("OFF")
                }
            }

            "com.light.HIGH_BRIGHTNESS" -> {
                //高亮已经亮了15分钟
                if(readStatus() != "0") {
                    val result = writeStatus("0")
                    if (result) offFlag.value = true
                    if (isT2) t2Timer("OFF")
                    //高亮超过十五分钟，用户再次点亮会再次计时15分钟
                    sp?.edit()?.putLong("alreadyHighTime", 0L)?.apply()
                    sp?.edit()?.putBoolean("highTimerStarted", false)?.apply()
                    hasLight15Min = true   //这个标志位的作用是，当亮15分钟后，再次点亮，如果间隔较短。则弹出提示
                }
            }

            "com.light.OTHER_BRIGHTNESS" -> {
                //低亮度或者关闭时长已经超过1分钟
                sp?.edit()?.putLong("alreadyHighTime", 0L)?.apply()
                hasLight15Min = false
            }
        }
    }
}