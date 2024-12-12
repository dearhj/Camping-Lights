package com.android.campinglight

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.android.campinglight.App.Companion.P2PRO_PATH
import com.android.campinglight.App.Companion.T2_PATH
import com.android.campinglight.App.Companion.isP2Pro
import com.android.campinglight.App.Companion.isT2
import com.android.campinglight.App.Companion.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File


class MainActivity : AppCompatActivity() {
    private var sosButton: ImageButton? = null
    private var superButton: ImageButton? = null
    private var fullButton: ImageButton? = null
    private var halfButton: ImageButton? = null
    private var quarterButton: ImageButton? = null
    private var helpButton: ImageButton? = null
    private var timeButton: ImageButton? = null
    private var flash: LinearLayout? = null
    private var status = ""
    private lateinit var alarmManager: AlarmManager
    private lateinit var intent: Intent
    private lateinit var pendingIntent: PendingIntent
    private var batteryValue = 0
    private var batteryLevel15 = false
    private var batteryManager: BatteryManager? = null

    @SuppressLint("ScheduleExactAlarm")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        setContentView(R.layout.activity_main)
        val level = batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: 0
        if (level <= 15) {
            batteryLevel15 = true
            MainScope().launch(Dispatchers.IO) {
                if (isT2) File(T2_PATH).write("0")
                if (isP2Pro) File(P2PRO_PATH).write("0")
            }
            status = "OFF"
            showDialog(this@MainActivity, getString(R.string.tips), getString(R.string.batteryInfo))
        }

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        intent = Intent(this, TimeReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        offFlag.observe(this) {
            if (it) {
                offFlag.value = false
                status = "OFF"
                updateUI(status)
            }
        }

        setSendChangeString {
            MainScope().launch(Dispatchers.Main) {
                batteryValue = it.toInt()
                if (batteryValue <= 15) {
                    batteryLevel15 = true
                    if (status != "OFF") {
                        status = "OFF"
                        updateUI(status)
                        showDialog(
                            this@MainActivity,
                            getString(R.string.tips),
                            getString(R.string.batteryInfo)
                        )
                    }
                } else batteryLevel15 = false
            }
        }
        startForegroundService(Intent(this, LightControlService::class.java))
        sosButton = findViewById(R.id.btn_flash_sos)
        superButton = findViewById(R.id.btn_flash_super)
        fullButton = findViewById(R.id.btn_constant_full)
        halfButton = findViewById(R.id.btn_constant_half)
        quarterButton = findViewById(R.id.btn_constant_quarter)
        helpButton = findViewById(R.id.btn_settings_help)
        timeButton = findViewById(R.id.btn_settings_time)
        flash = findViewById(R.id.flash)
        if (isT2) {
            flash?.visibility = View.GONE
            findViewById<View>(R.id.view).visibility = View.VISIBLE
            findViewById<View>(R.id.title_view).visibility = View.VISIBLE
            findViewById<View>(R.id.help_view).visibility = View.VISIBLE
            findViewById<View>(R.id.time_view).visibility = View.VISIBLE
        }
        sosButton?.setOnClickListener {
            if (batteryLevel15) toast(getString(R.string.batteryInfo), this)
            else {
                var result: Boolean
                result = File("/sys/devices/platform/gftk_camplight/camplight_mode").write("0")
                if (result) {
                    if (status == "SOS") status = "OFF"
                    else {
                        result =
                            File("/sys/devices/platform/gftk_camplight/camplight_mode").write("5")
                        if (result) status = "SOS"
                    }
                    updateUI(status)
                }
            }
        }
        superButton?.setOnClickListener {
            if (batteryLevel15) toast(getString(R.string.batteryInfo), this)
            else {
                var result: Boolean
                result = File("/sys/devices/platform/gftk_camplight/camplight_mode").write("0")
                if (result) {
                    if (status == "BLINK") status = "OFF"
                    else {
                        result =
                            File("/sys/devices/platform/gftk_camplight/camplight_mode").write("4")
                        if (result) status = "BLINK"
                    }
                    updateUI(status)
                }
            }
        }
        fullButton?.setOnClickListener {
            if (batteryLevel15) toast(getString(R.string.batteryInfo), this)
            else {
                var result = false
                if (isT2) result = File(T2_PATH).write("0")
                if (isP2Pro) result = File(P2PRO_PATH).write("0")
                if (result) {
                    if (status == "HIGH") status = "OFF"
                    else {
                        if (isT2) result = File(T2_PATH).write("100")
                        if (isP2Pro) result = File(P2PRO_PATH).write("3")
                        if (result) status = "HIGH"
                    }
                    updateUI(status)
                }
            }
        }
        halfButton?.setOnClickListener {
            if (batteryLevel15) toast(getString(R.string.batteryInfo), this)
            else {
                var result = false
                if (isT2) result = File(T2_PATH).write("0")
                if (isP2Pro) result = File(P2PRO_PATH).write("0")
                if (result) {
                    if (status == "NORMAL") status = "OFF"
                    else {
                        if (isT2) result = File(T2_PATH).write("50")
                        if (isP2Pro) result = File(P2PRO_PATH).write("2")
                        if (result) status = "NORMAL"
                    }
                    updateUI(status)
                }
            }
        }
        quarterButton?.setOnClickListener {
            if (batteryLevel15) toast(getString(R.string.batteryInfo), this)
            else {
                var result = false
                if (isT2) result = File(T2_PATH).write("0")
                if (isP2Pro) result = File(P2PRO_PATH).write("0")
                if (result) {
                    if (status == "LOW") status = "OFF"
                    else {
                        if (isT2) result = File(T2_PATH).write("25")
                        if (isP2Pro) result = File(P2PRO_PATH).write("1")
                        if (result) status = "LOW"
                    }
                    updateUI(status)
                }
            }
        }
        helpButton?.setOnClickListener {
            showTipDialog(this, getString(R.string.help), getString(R.string.help_info))
        }
        timeButton?.setOnClickListener {
            val time = sp?.getString("time", "")
            showTimeDialog(this, "${if (time == "") "0" else time}") {
                sp?.edit()?.putString("time", it)?.apply()
                alarmManager.cancel(pendingIntent)
                if (it != "0" && status != "" && status != "OFF") {
                    val triggerAtMillis = System.currentTimeMillis() + 60000 * it.toInt()
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initView()
        if (!batteryLevel15) showTipDialog(
            this,
            getString(R.string.app_name),
            getString(R.string.waring)
        )
    }


    private fun initView() {
        if (isT2) {
            val readResult = File(T2_PATH).readText().toInt()
            status = if (readResult > 75) "HIGH"
            else if (readResult in 26..75) "NORMAL"
            else if (readResult in 1..25) "LOW"
            else "OFF"
        }
        if (isP2Pro) status = File(P2PRO_PATH).readText()
        updateUI(status, true)
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun updateUI(uiStatus: String, isInitView: Boolean = false) {
        if (!isInitView) {
            if (uiStatus != "OFF") {
                alarmManager.cancel(pendingIntent)
                val time = sp?.getString("time", "")
                if (time != "" && time != "0") {
                    val triggerAtMillis = System.currentTimeMillis() + 60000 * (time?.toInt() ?: 0)
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                }
            } else alarmManager.cancel(pendingIntent)
        }
        when (uiStatus) {
            "LOW" -> {
                quarterButton?.setBackgroundResource(R.drawable.constant_light_quarter_pressed)
                halfButton?.setBackgroundResource(R.drawable.constant_light_half_default)
                fullButton?.setBackgroundResource(R.drawable.constant_light_full_default)
                superButton?.setBackgroundResource(R.drawable.flash_super_default)
                sosButton?.setBackgroundResource(R.drawable.flash_sos_default)
            }

            "NORMAL" -> {
                halfButton?.setBackgroundResource(R.drawable.constant_light_half_pressed)
                quarterButton?.setBackgroundResource(R.drawable.constant_light_quarter_default)
                fullButton?.setBackgroundResource(R.drawable.constant_light_full_default)
                superButton?.setBackgroundResource(R.drawable.flash_super_default)
                sosButton?.setBackgroundResource(R.drawable.flash_sos_default)
            }

            "HIGH" -> {
                fullButton?.setBackgroundResource(R.drawable.constant_light_full_pressed)
                quarterButton?.setBackgroundResource(R.drawable.constant_light_quarter_default)
                halfButton?.setBackgroundResource(R.drawable.constant_light_half_default)
                superButton?.setBackgroundResource(R.drawable.flash_super_default)
                sosButton?.setBackgroundResource(R.drawable.flash_sos_default)
            }

            "BLINK" -> {
                superButton?.setBackgroundResource(R.drawable.flash_super_pressed)
                quarterButton?.setBackgroundResource(R.drawable.constant_light_quarter_default)
                halfButton?.setBackgroundResource(R.drawable.constant_light_half_default)
                fullButton?.setBackgroundResource(R.drawable.constant_light_full_default)
                sosButton?.setBackgroundResource(R.drawable.flash_sos_default)
            }

            "SOS" -> {
                sosButton?.setBackgroundResource(R.drawable.flash_sos_pressed)
                quarterButton?.setBackgroundResource(R.drawable.constant_light_quarter_default)
                halfButton?.setBackgroundResource(R.drawable.constant_light_half_default)
                fullButton?.setBackgroundResource(R.drawable.constant_light_full_default)
                superButton?.setBackgroundResource(R.drawable.flash_super_default)
            }

            "OFF" -> {
                quarterButton?.setBackgroundResource(R.drawable.constant_light_quarter_default)
                halfButton?.setBackgroundResource(R.drawable.constant_light_half_default)
                fullButton?.setBackgroundResource(R.drawable.constant_light_full_default)
                superButton?.setBackgroundResource(R.drawable.flash_super_default)
                sosButton?.setBackgroundResource(R.drawable.flash_sos_default)
            }
        }
    }

}