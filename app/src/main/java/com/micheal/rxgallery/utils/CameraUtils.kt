package com.micheal.rxgallery.utils

import android.content.Context
import android.content.pm.PackageManager

object CameraUtils {

    @JvmStatic
    fun hasCamera(context: Context):Boolean{
        val packageManager = context.packageManager
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }
}