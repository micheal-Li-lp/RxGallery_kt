package com.micheal.rxgallery.utils

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager

object BaseUtils {

    @JvmStatic
    fun isPkgExists(context: Context ,pkgName:String) :Boolean{
        return try {
            context.packageManager.getPackageInfo(pkgName,PackageManager.GET_ACTIVITIES)
            true
        }catch (e :PackageManager.NameNotFoundException){
            false
        }

    }

}