package com.android.campinglight

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.android.campinglight.App.Companion.P2PRO_PATH
import com.android.campinglight.App.Companion.T2_PATH
import com.android.campinglight.App.Companion.alarmManager
import com.android.campinglight.App.Companion.isT2
import com.android.campinglight.App.Companion.pendingIntentHighBrightness
import com.android.campinglight.App.Companion.pendingIntentOtherBrightness
import com.android.campinglight.App.Companion.sp
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import kotlin.properties.Delegates


var offFlag = MutableLiveData(false)

var dialog: AlertDialog? = null
fun showDialog(
    context: Context,
    title: String,
    content: String,
) {
    if (dialog?.isShowing == true) dialog?.dismiss()
    dialog = AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(content)
        .create()
    dialog?.show()
}

fun showTipDialog(
    context: Context,
    tipTitleMsg: String,
    tipInfoMsg: String,
) {
    val v =
        LayoutInflater.from(context).inflate(R.layout.dialog_resume, null as ViewGroup?)
    val tipTitle = v.findViewById<TextView>(R.id.textTitle)
    val tipInfo = v.findViewById<TextView>(R.id.textInfo)
    tipTitle.text = tipTitleMsg
    tipInfo.text = tipInfoMsg
    ComDialog.dialogCreate(context, "", null)
    ComDialog.setDialogView(v)
    ComDialog.setPositiveButton(context.getString(R.string.sure)) { _, _ -> }
    ComDialog.dialogShow()
}


@SuppressLint("CutPasteId")
fun showTimeDialog(context: Context, choose: String, result: (String) -> Unit) {
    var time = choose
    val v =
        LayoutInflater.from(context).inflate(R.layout.dialog_time_settings, null as ViewGroup?)
    val radioGroup: RadioGroup = v.findViewById(R.id.camping_lamp_time_settings_menu)
    if (isT2) {
        v.findViewById<RadioButton>(R.id.minute_0_id).visibility = View.GONE
        v.findViewById<RadioButton>(R.id.minute_60_id).visibility = View.GONE
    }
    when (choose) {
        "0" -> v.findViewById<RadioButton>(R.id.minute_0_id).isChecked = true
        "5" -> v.findViewById<RadioButton>(R.id.minute_5_id).isChecked = true
        "10" -> v.findViewById<RadioButton>(R.id.minute_10_id).isChecked = true
        "15" -> v.findViewById<RadioButton>(R.id.minute_15_id).isChecked = true
        "30" -> v.findViewById<RadioButton>(R.id.minute_30_id).isChecked = true
        "60" -> v.findViewById<RadioButton>(R.id.minute_60_id).isChecked = true
    }
    radioGroup.setOnCheckedChangeListener { _, i ->
        when (i) {
            R.id.minute_0_id -> time = "0"
            R.id.minute_5_id -> time = "5"
            R.id.minute_10_id -> time = "10"
            R.id.minute_15_id -> time = "15"
            R.id.minute_30_id -> time = "30"
            R.id.minute_60_id -> time = "60"
        }
    }
    ComDialog.dialogCreate(context, "", null)
    ComDialog.setDialogView(v)
    ComDialog.setPositiveButton(context.getString(R.string.sure)) { _, _ -> result(time) }
    ComDialog.setNegativeButton(context.getString(R.string.cancel)) { _, _ -> }
    ComDialog.dialogShow()
}

var change: Change? = null

interface Change {
    fun change(string: String)
}

var changeStatus: String by Delegates.observable("") { _, _, new ->
    change?.change(new)
}

fun setSendChangeString(ch: (str: String) -> Unit) {
    change = object : Change {
        override fun change(str: String) {
            ch(str)
        }
    }
}

var dialogChange: Change? = null

var dialogChangeStatus: String by Delegates.observable("") { _, _, new ->
    dialogChange?.change(new)
}

fun setDialogChangeString(ch: () -> Unit) {
    dialogChange = object : Change {
        override fun change(string: String) {
            ch()
        }
    }
}

var toast: Toast? = null
fun toast(msg: String, context: Context) {
    if (toast != null) toast?.cancel()
    toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
    toast?.show()
}

fun writeStatus(content: String): Boolean {
    return try {
        val file = if (isT2) File(T2_PATH) else File(P2PRO_PATH)
        val writer = BufferedWriter(FileWriter(file, false))
        writer.write(content)
        writer.flush()
        writer.close()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun readStatus(): String {
    return try {
        val file = if (isT2) File(T2_PATH) else File(P2PRO_PATH)
        val reader = BufferedReader(FileReader(file))
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

@SuppressLint("ScheduleExactAlarm")
fun t2Timer(status: String) {
    try {
        if (status == "HIGH") {
            val nowHighTime = System.currentTimeMillis()
            alarmManager.cancel(pendingIntentOtherBrightness)  //取消低亮定时器
            //获取上一次取消高亮定时器的时间
            val closeHighLightTime = sp?.getLong("stopHighTimer", 0L) ?: 0L
            val triggerAtMillis: Long
            //判断两次高亮之间的时长是否超过了1分钟
            if (nowHighTime - closeHighLightTime <= 1 * 60 * 1000L) {
                //获取高亮已亮时长
                val allHighTime = sp?.getLong("alreadyHighTime", 0L) ?: 0L
                //15分钟减去已亮时长等于剩余的高亮时长
                val remainingTime = 15 * 60 * 1000 - allHighTime
                triggerAtMillis = nowHighTime + remainingTime
            } else triggerAtMillis = nowHighTime + 1000 * 15 * 60  //15m
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntentHighBrightness
            )
            //记录高亮开始时间
            sp?.edit()?.putLong("startHighTimer", nowHighTime)?.apply()
            sp?.edit()?.putBoolean("highTimerStarted", true)?.apply()   //高亮定时器标志置为true
        } else {
            //点击高亮以外的其他亮度或关灯时，判断高亮定时器是否正在运行，如果正在运行则取消高亮定时器，并记录当前时间，并且启动一个低亮计时器。
            if (sp?.getBoolean("highTimerStarted", false) == true) {
                alarmManager.cancel(pendingIntentHighBrightness)  //取消高亮定时器
                sp?.edit()?.putBoolean("highTimerStarted", false)?.apply() //高亮定时器标志置为false
                //记录高亮结束时间
                val nowTime = System.currentTimeMillis()
                sp?.edit()?.putLong("stopHighTimer", nowTime)?.apply()
                //获取高亮已亮时长
                val alreadyHighLightTime = sp?.getLong("alreadyHighTime", 0L) ?: 0L
                //获取本轮高亮时间 （现在时间-高亮开启时间）
                val thisRoundHighTime =
                    nowTime - (sp?.getLong("startHighTimer", 0L) ?: 0L)
                //存储本轮过后的高亮时间
                sp?.edit()
                    ?.putLong("alreadyHighTime", alreadyHighLightTime + thisRoundHighTime)
                    ?.apply()
                //启动低亮计时器 1分钟
                val triggerAtMillis = nowTime + 1000 * 60 * 1
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntentOtherBrightness
                )
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
