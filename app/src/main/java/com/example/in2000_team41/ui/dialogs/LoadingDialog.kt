package com.example.in2000_team41.ui.dialogs

import android.app.Activity
import android.app.AlertDialog
import com.example.in2000_team41.R

class LoadingDialog(val mActivity: Activity) {

    lateinit var isDialog: AlertDialog

    fun startLoading(){
        val inflater = mActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.loading_item,  null)
        val builder = AlertDialog.Builder(mActivity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.show()
    }

    fun dismiss(){
        isDialog.dismiss()
    }
}