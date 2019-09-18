package com.micheal.rxgallery.utils

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View

object OsCompat {
    @JvmStatic
    fun setBackgroundDrawableCompat(view:View,drawable: Drawable){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.background = drawable
        } else {

            view.setBackgroundDrawable(drawable)
        }
    }
}