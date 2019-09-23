package com.micheal.rxgallery

import android.app.Application

abstract class BaseApplication :Application(){
    companion object{
        var INSTANCE : Application? = null
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

}