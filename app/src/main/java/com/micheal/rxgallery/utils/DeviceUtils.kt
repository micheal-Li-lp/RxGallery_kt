package com.micheal.rxgallery.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

object DeviceUtils {

    @JvmStatic
    fun getScreenSize(context: Context): DisplayMetrics {
        val displaysMetrics = DisplayMetrics()
        context.resources.displayMetrics
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(displaysMetrics)
        return displaysMetrics
    }

}