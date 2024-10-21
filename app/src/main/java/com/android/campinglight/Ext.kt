package com.android.campinglight

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView


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
