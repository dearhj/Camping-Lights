package com.android.campinglight

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.android.campinglight.App.Companion.P2PRO_PATH
import com.android.campinglight.App.Companion.T2_PATH
import com.android.campinglight.App.Companion.isT2
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


fun showTimeDialog(context: Context, choose: String, result: (String) -> Unit) {
    var time = choose
    val v =
        LayoutInflater.from(context).inflate(R.layout.dialog_time_settings, null as ViewGroup?)
    val radioGroup: RadioGroup = v.findViewById(R.id.camping_lamp_time_settings_menu)
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
