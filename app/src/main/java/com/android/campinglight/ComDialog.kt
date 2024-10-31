package com.android.campinglight

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View


object ComDialog {
    private var mDialog: AlertDialog? = null

    fun dialogCreate(context: Context?, str: String?, str2: String?) {
        dialogClose()
        val create = AlertDialog.Builder(context).create()
        mDialog = create
        create.setTitle(str)
        if (str2 != null) {
            mDialog?.setMessage(str2)
        }
        val window = mDialog?.window
        window!!.setGravity(80)
        window.setWindowAnimations(R.style.DialogAnimation)
    }

    fun setPositiveButton(str: String?, onClickListener: DialogInterface.OnClickListener?) {
        val alertDialog = mDialog
        alertDialog?.setButton(-1, str, onClickListener)
    }

    fun setNegativeButton(str: String?, onClickListener: DialogInterface.OnClickListener?) {
        val alertDialog = mDialog
        alertDialog?.setButton(-2, str, onClickListener)
    }

    fun setDialogView(view: View?) {
        val alertDialog = mDialog
        alertDialog?.setView(view)
    }

    fun dialogShow() {
        val alertDialog = mDialog
        alertDialog?.show()
    }

    fun isShowing(): Boolean {
        return mDialog?.isShowing ?: false
    }

    private fun dialogClose() {
        try {
            val alertDialog = mDialog
            if (alertDialog != null) {
                alertDialog.dismiss()
                mDialog = null
            }
        } catch (_: Exception) {
        }
    }
}
