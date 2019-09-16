package com.micheal.rxgallery.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class FixViewPager(context: Context,attrs: AttributeSet?) : ViewPager(context,attrs) {

    constructor(context:Context) : this(context,null)

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        try {
            return super.dispatchTouchEvent(ev)
        } catch (ignored: IllegalArgumentException) {
        }

        return false
    }
}