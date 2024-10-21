package com.android.campinglight

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class MainActivity : AppCompatActivity() {
    private var sosButton: ImageButton? = null
    private var superButton: ImageButton? = null
    private var fullButton: ImageButton? = null
    private var halfButton: ImageButton? = null
    private var quarterButton: ImageButton? = null
    private var helpButton: ImageButton? = null
    private var timeButton: ImageButton? = null
    private var status = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        sosButton = findViewById(R.id.btn_flash_sos)
        superButton = findViewById(R.id.btn_flash_super)
        fullButton = findViewById(R.id.btn_constant_full)
        halfButton = findViewById(R.id.btn_constant_half)
        quarterButton = findViewById(R.id.btn_constant_quarter)
        helpButton = findViewById(R.id.btn_settings_help)
        timeButton = findViewById(R.id.btn_settings_time)
        sosButton?.setOnClickListener {
            val result: Boolean
            if (status == "SOS") {
                result = File("/sys/devices/platform/gftk_camplight/camplight_mode").write("0")
                if (result) status = "OFF"
            } else {
                result = File("/sys/devices/platform/gftk_camplight/camplight_mode").write("5")
                if (result) status = "SOS"
            }
            if (result) updateUI(status)
        }
        superButton?.setOnClickListener {
            val result: Boolean
            if (status == "BLINK") {
                result = File("/sys/devices/platform/gftk_camplight/camplight_mode").write("0")
                if (result) status = "OFF"
            } else {
                result = File("/sys/devices/platform/gftk_camplight/camplight_mode").write("4")
                if (result) status = "BLINK"
            }
            if (result) updateUI(status)
        }
        fullButton?.setOnClickListener {
            val result: Boolean
            if (status == "HIGH") {
                result = File("/sys/devices/platform/gftk_camplight/camplight_mode").write("0")
                if (result) status = "OFF"
            } else {
                result = File("/sys/devices/platform/gftk_camplight/camplight_mode").write("3")
                if (result) status = "HIGH"
            }
            if (result) updateUI(status)
        }
        halfButton?.setOnClickListener {
            val result: Boolean
            if (status == "NORMAL") {
                result = File("/sys/devices/platform/gftk_camplight/camplight_mode").write("0")
                if (result) status = "OFF"
            } else {
                result = File("/sys/devices/platform/gftk_camplight/camplight_mode").write("2")
                if (result) status = "NORMAL"
            }
            if (result) updateUI(status)
        }
        quarterButton?.setOnClickListener {
            val result: Boolean
            if (status == "LOW") {
                result = File("/sys/devices/platform/gftk_camplight/camplight_mode").write("0")
                if (result) status = "OFF"
            } else {
                result = File("/sys/devices/platform/gftk_camplight/camplight_mode").write("1")
                if (result) status = "LOW"
            }
            if (result) updateUI(status)
        }
        helpButton?.setOnClickListener {
            showTipDialog(this, getString(R.string.help), getString(R.string.help_info))
        }
        timeButton?.setOnClickListener {
            showTimeDialog(this, "15") {
                println("这里选中的数是   $it")
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
        updateUI(status)
    }

    private fun updateUI(uiStatus: String) {
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


    private fun File.write(content: String): Boolean {
        return try {
            val writer = BufferedWriter(FileWriter(this, false))
            writer.write(content)
            writer.flush()
            writer.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun File.readText(): String {
        return try {
            val reader = BufferedReader(FileReader(this))
            val stringBuilder = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line).append("\n")
            }
            reader.close()
            if (stringBuilder.isNotEmpty() && stringBuilder.last() == '\n') {
                stringBuilder.deleteCharAt(stringBuilder.lastIndex)
            }
            stringBuilder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

}