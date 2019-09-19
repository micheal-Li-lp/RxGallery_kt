package com.micheal.rxgallery.utils

import android.util.Log
import com.micheal.rxgallery.BuildConfig

object ModelUtils {

    private const val TAG = "Test"

    @JvmStatic
    fun logDebug() {
        Log.w(TAG, "BuildConfig.DEBUG:--" + BuildConfig.DEBUG + "--")
        if (BuildConfig.DEBUG)
            Logger.w("is debug mode")
        else
            Logger.w("not debug mode")
    }

    /**
     * 多层依赖时DEBUGCONFIG会出错，所以提供了内部接口更改
     *
     * @param f 是否打开
     */
    @JvmStatic
    fun setDebugModel(f: Boolean) {
        Logger.DEBUG = f
    }

}