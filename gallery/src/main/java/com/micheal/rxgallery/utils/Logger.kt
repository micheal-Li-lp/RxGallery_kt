package com.micheal.rxgallery.utils

import android.util.Log
import com.micheal.rxgallery.BuildConfig

object Logger {

    private const val TAG = "RxGalleryFinal"
    @JvmField
    var DEBUG = BuildConfig.DEBUG

    @JvmStatic
    fun d(value: String) {
        if (DEBUG) {
            Log.d(TAG, value)
        }
    }

    @JvmStatic
    fun e(value: String?) {
        if (DEBUG) {
            Log.e(TAG, "$value")
        }
    }

    @JvmStatic
    fun e(value: Exception?) {
        if (DEBUG && value != null) {
            Log.e(TAG, "${value.message}")
        }
    }

    @JvmStatic
    fun i(value: String?) {
        if (DEBUG) {
            Log.i(TAG, "$value")
        }
    }

    @JvmStatic
    fun w(value: String?) {
        if (DEBUG) {
            Log.w(TAG, "$value")
        }
    }

}