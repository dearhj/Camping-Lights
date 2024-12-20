package com.android.campinglight

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.BatteryManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.android.campinglight.App.Companion.alarmManager
import com.android.campinglight.App.Companion.hasLight15Min
import com.android.campinglight.App.Companion.isP2Pro
import com.android.campinglight.App.Companion.isT2
import com.android.campinglight.App.Companion.pendingIntentAll
import com.android.campinglight.App.Companion.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private var sosButton: ImageButton? = null
    private var sosButtonLand: ImageButton? = null
    private var superButton: ImageButton? = null
    private var superButtonLand: ImageButton? = null
    private var fullButton: ImageButton? = null
    private var fullButtonLand: ImageButton? = null
    private var halfButton: ImageButton? = null
    private var halfButtonLand: ImageButton? = null
    private var quarterButton: ImageButton? = null
    private var quarterButtonLand: ImageButton? = null
    private var helpButton: ImageButton? = null
    private var timeButton: ImageButton? = null
    private var buttonLand: LinearLayout? = null
    private var buttonPortrait: LinearLayout? = null
    private var status = ""
    private var batteryValue = 0
    private var batteryLevel15 = false
    private var batteryManager: BatteryManager? = null

    @SuppressLint("ScheduleExactAlarm", "SourceLockedOrientationActivity", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (isP2Pro) requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.navigationBarColor = Color.parseColor("#282E31")
        batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        setContentView(R.layout.activity_main)
        val level = batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: 0
        if (level <= 15) {
            batteryLevel15 = true
            MainScope().launch(Dispatchers.IO) { writeStatus("0") }
            status = "OFF"
            showDialog(this@MainActivity, getString(R.string.tips), getString(R.string.batteryInfo))
        }
        offFlag.observe(this) {
            if (it) {
                offFlag.value = false
                status = "OFF"
                updateUI(status, isAutoClose = true)
            }
        }

        setSendChangeString {
            MainScope().launch(Dispatchers.Main) {
                batteryValue = it.toInt()
                if (batteryValue <= 15) {
                    batteryLevel15 = true
                    if (status != "OFF") {
                        status = "OFF"
                        updateUI(status, isAutoClose = true)
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
        sosButtonLand = findViewById(R.id.btn_flash_sos_land)
        superButton = findViewById(R.id.btn_flash_super)
        superButtonLand = findViewById(R.id.btn_flash_super_land)
        fullButton = findViewById(R.id.btn_constant_full)
        fullButtonLand = findViewById(R.id.btn_constant_full_land)
        halfButton = findViewById(R.id.btn_constant_half)
        halfButtonLand = findViewById(R.id.btn_constant_half_land)
        quarterButton = findViewById(R.id.btn_constant_quarter)
        quarterButtonLand = findViewById(R.id.btn_constant_quarter_land)
        helpButton = findViewById(R.id.btn_settings_help)
        timeButton = findViewById(R.id.btn_settings_time)
        buttonPortrait = findViewById(R.id.camping_lamp_menu_box)
        buttonLand = findViewById(R.id.land_box)
        if (isT2) {
            findViewById<View>(R.id.title_view).visibility = View.VISIBLE
            findViewById<View>(R.id.help_view).visibility = View.VISIBLE
            findViewById<View>(R.id.time_view).visibility = View.VISIBLE
        }
        sosButton?.setOnClickListener { openLight("SOS") }
        sosButtonLand?.setOnClickListener { openLight("SOS") }
        superButton?.setOnClickListener { openLight("BLINK") }
        superButtonLand?.setOnClickListener { openLight("BLINK") }
        fullButton?.setOnClickListener { openLight("HIGH") }
        fullButtonLand?.setOnClickListener { openLight("HIGH") }
        halfButton?.setOnClickListener { openLight("NORMAL") }
        halfButtonLand?.setOnClickListener { openLight("NORMAL") }
        quarterButton?.setOnClickListener { openLight("LOW") }
        quarterButtonLand?.setOnClickListener { openLight("LOW") }
        helpButton?.setOnClickListener {
            showTipDialog(this, getString(R.string.help), getString(R.string.help_info))
        }
        timeButton?.setOnClickListener {
            val time = sp?.getString("time", "") ?: "0"
            showTimeDialog(this, time) {
                sp?.edit()?.putString("time", it)?.apply()
                alarmManager.cancel(pendingIntentAll)
                if (it != "0" && status != "" && status != "OFF") {
                    val triggerAtMillis = System.currentTimeMillis() + 60000 * it.toInt()
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntentAll
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initView()
        setScreenOrientationVisibility()
        if (!batteryLevel15) showTipDialog(
            this,
            getString(R.string.app_name),
            getString(R.string.waring)
        )
    }

    private fun openLight(lightType: String) {
        if (batteryLevel15) toast(getString(R.string.batteryInfo), this)
        else {
            var result = writeStatus("0")
            if (result) {
                if (lightType == status) status = "OFF"
                else {
                    when (lightType) {
                        "LOW" -> {
                            result = writeStatus("1")
                            if (result) status = "LOW"
                        }

                        "NORMAL" -> {
                            result = writeStatus("2")
                            if (result) status = "NORMAL"
                        }

                        "HIGH" -> {
                            if (isT2) {
                                if (hasLight15Min) {
                                    toast(
                                        getString(R.string.more_15_tips_open_again),
                                        this
                                    )
                                }
                                result = writeStatus("3")
                                if (result) status = "HIGH"
                            } else {
                                result = writeStatus("3")
                                if (result) status = "HIGH"
                            }
                        }

                        "BLINK" -> {
                            result = writeStatus("4")
                            if (result) status = "BLINK"
                        }

                        "SOS" -> {
                            result = writeStatus("5")
                            if (result) status = "SOS"
                        }
                    }
                }
                updateUI(status)
            }
        }
    }

    private fun initView() {
        val mode = Settings.Secure.getInt(contentResolver, "navigation_mode")
        if (mode == 0) findViewById<View>(R.id.tip_view).visibility = View.VISIBLE
        else findViewById<View>(R.id.tip_view).visibility = View.GONE
        status = readStatus()
        updateUI(status, true)
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun updateUI(
        uiStatus: String,
        isInitView: Boolean = false,
        isAutoClose: Boolean = false
    ) {
        if (!isInitView) {
            if (uiStatus != "OFF") {
                alarmManager.cancel(pendingIntentAll)
                val time = sp?.getString("time", "")
                if (time != "" && time != "0") {
                    val triggerAtMillis = System.currentTimeMillis() + 60000 * (time?.toInt() ?: 0)
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntentAll
                    )
                }
            } else alarmManager.cancel(pendingIntentAll)
        }
        quarterButton?.setBackgroundResource(R.drawable.constant_light_quarter_default)
        halfButton?.setBackgroundResource(R.drawable.constant_light_half_default)
        fullButton?.setBackgroundResource(R.drawable.constant_light_full_default)
        superButton?.setBackgroundResource(R.drawable.flash_super_default)
        sosButton?.setBackgroundResource(R.drawable.flash_sos_default)
        quarterButtonLand?.setBackgroundResource(R.drawable.constant_light_quarter_default)
        halfButtonLand?.setBackgroundResource(R.drawable.constant_light_half_default)
        fullButtonLand?.setBackgroundResource(R.drawable.constant_light_full_default)
        superButtonLand?.setBackgroundResource(R.drawable.flash_super_default)
        sosButtonLand?.setBackgroundResource(R.drawable.flash_sos_default)
        when (uiStatus) {
            "LOW" -> {
                quarterButton?.setBackgroundResource(R.drawable.constant_light_quarter_pressed)
                quarterButtonLand?.setBackgroundResource(R.drawable.constant_light_quarter_pressed)
            }

            "NORMAL" -> {
                halfButton?.setBackgroundResource(R.drawable.constant_light_half_pressed)
                halfButtonLand?.setBackgroundResource(R.drawable.constant_light_half_pressed)
            }

            "HIGH" -> {
                fullButton?.setBackgroundResource(R.drawable.constant_light_full_pressed)
                fullButtonLand?.setBackgroundResource(R.drawable.constant_light_full_pressed)
            }

            "BLINK" -> {
                superButton?.setBackgroundResource(R.drawable.flash_super_pressed)
                superButtonLand?.setBackgroundResource(R.drawable.flash_super_pressed)
            }

            "SOS" -> {
                sosButton?.setBackgroundResource(R.drawable.flash_sos_pressed)
                sosButtonLand?.setBackgroundResource(R.drawable.flash_sos_pressed)
            }
        }
        if (isT2 && !isInitView && !isAutoClose)  t2Timer(uiStatus)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setScreenOrientationVisibility()
    }

    private fun setScreenOrientationVisibility() {
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            buttonLand?.visibility = View.VISIBLE
            buttonPortrait?.visibility = View.GONE
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            buttonLand?.visibility = View.GONE
            buttonPortrait?.visibility = View.VISIBLE
        }
    }
}