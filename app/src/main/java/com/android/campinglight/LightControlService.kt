package com.android.campinglight

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.BatteryManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.android.campinglight.App.Companion.P2PRO_PATH
import com.android.campinglight.App.Companion.T2_PATH
import com.android.campinglight.App.Companion.isP2Pro
import com.android.campinglight.App.Companion.isT2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


class LightControlService : Service() {
    private var batteryManager: BatteryManager? = null
    override fun onBind(p0: Intent?): IBinder? = null


    override fun onCreate() {
        super.onCreate()
        val notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val id = "gh0st_id"
        val mChannel = NotificationChannel(id, id, NotificationManager.IMPORTANCE_NONE)
        notificationManager.createNotificationChannel(mChannel)
        val notification = Notification.Builder(this, id).setOngoing(true)
            .setContentTitle(getString(R.string.resume_info))
            .setSmallIcon(R.mipmap.ic_launcher).setColor(Color.argb(0, 0, 0, 0))
            .setCategory(NotificationCompat.CATEGORY_SERVICE).build()
        startForeground(1, notification)
        batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        MainScope().launch(Dispatchers.IO) {
            while (true) {
                val batteryValue =
                    batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: 0
                changeStatus = "$batteryValue"
                if (batteryValue <= 15) {
                    if (isT2) File(T2_PATH).write("0")
                    if (isP2Pro) File(P2PRO_PATH).write("0")
                }
                delay(60000)
            }
        }
    }
}