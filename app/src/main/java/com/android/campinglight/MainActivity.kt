package com.android.campinglight

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.android.campinglight.App.Companion.sp
import java.io.File


class MainActivity : AppCompatActivity() {
    private var sosButton: ImageButton? = null
    private var superButton: ImageButton? = null
    private var fullButton: ImageButton? = null
    private var halfButton: ImageButton? = null
    private var quarterButton: ImageButton? = null
    private var helpButton: ImageButton? = null
    private var timeButton: ImageButton? = null
    private var status = ""
    private lateinit var alarmManager: AlarmManager
    private lateinit var intent: Intent
    private lateinit var pendingIntent: PendingIntent

    @SuppressLint("ScheduleExactAlarm")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
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
        sosButton = findViewById(R.id.btn_flash_sos)
        superButton = findViewById(R.id.btn_flash_super)
        fullButton = findViewById(R.id.btn_constant_full)
        halfButton = findViewById(R.id.btn_constant_half)
        quarterButton = findViewById(R.id.btn_constant_quarter)
        helpButton = findViewById(R.id.btn_settings_help)
        timeButton = findViewById(R.id.btn_settings_time)
        sosButton?.setOnClickListener {
            var result: Boolean
            result = File("/sys/devices/platform/gftk_camplight/camplight_mode").write("0")
            if (result) {
                if (status == "SOS") status = "OFF"
                else {
                    result = File("/sys/devices/platform/gftk_camplight/camplight_mode").write("5")
                    if (result) status = "SOS"
                }
                updateUI(status)
            }
        }
        superButton?.setOnClickListener {
            var result: Boolean
            result = File("/sys/devices/platform/gftk_camplight/camplight_mode").write("0")
            if (result) {
                if (status == "BLINK") status = "OFF"
                else {
                    result = File("/sys/devices/platform/gftk_camplight/camplight_mode").write("4")
                    if (result) status = "BLINK"
                }
                updateUI(status)
            }
        }
        fullButton?.setOnClickListener {
            var result: Boolean
            result = File("/sys/devices/platform/gftk_camplight/camplight_mode").write("0")
            if (result) {
                if (status == "HIGH") status = "OFF"
                else {
                    result = File("/sys/devices/platform/gftk_camplight/camplight_mode").write("3")
                    if (result) status = "HIGH"
                }
                updateUI(status)
            }
        }
        halfButton?.setOnClickListener {
            var result: Boolean
            result = File("/sys/devices/platform/gftk_camplight/camplight_mode").write("0")
            if (result) {
                if (status == "NORMAL") status = "OFF"
                else {
                    result = File("/sys/devices/platform/gftk_camplight/camplight_mode").write("2")
                    if (result) status = "NORMAL"
                }
                updateUI(status)
            }
        }
        quarterButton?.setOnClickListener {
            var result: Boolean
            result = File("/sys/devices/platform/gftk_camplight/camplight_mode").write("0")
            if (result) {
                if (status == "LOW") status = "OFF"
                else {
                    result = File("/sys/devices/platform/gftk_camplight/camplight_mode").write("1")
                    if (result) status = "LOW"
                }
                updateUI(status)
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
        showTipDialog(this, getString(R.string.app_name), getString(R.string.resume_info))
    }


    private fun initView() {
        status = File("/sys/devices/platform/gftk_camplight/camplight_mode").readText()
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