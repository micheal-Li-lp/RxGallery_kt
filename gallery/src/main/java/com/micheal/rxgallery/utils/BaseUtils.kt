package com.micheal.rxgallery.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import java.io.File

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

    @JvmStatic
    fun getExternalDirectory(): File =  Environment.getExternalStorageDirectory()



}