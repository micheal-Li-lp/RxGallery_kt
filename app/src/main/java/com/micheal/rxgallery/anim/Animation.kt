package com.micheal.rxgallery.anim

import android.view.View

abstract class Animation {
    companion object{
        const val DIRECTION_DOWN = 4
        const val DURATION_DEFAULT =300

        const val DIRECTION_LEFT = 1
        const val DIRECTION_RIGHT = 2
        const val DIRECTION_UP = 3
        const val DURATION_LONG = 500L
    }

    lateinit var view :View

    abstract fun animate()

}