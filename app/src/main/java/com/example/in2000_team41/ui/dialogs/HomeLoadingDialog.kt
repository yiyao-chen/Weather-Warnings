package com.example.in2000_team41.ui.dialogs

import android.app.Activity
import android.app.AlertDialog
import com.example.in2000_team41.R

class HomeLoadingDialog(val mActivity: Activity) {

    lateinit var isDialog: AlertDialog

    fun startLoading(){
        val inflater = mActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.loading_homescreen,  null)
        val builder = AlertDialog.Builder(mActivity, R.style.Theme_Design_Light_NoActionBar)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.show()
    }

    fun dismiss(){
        isDialog.dismiss()
    }
}
