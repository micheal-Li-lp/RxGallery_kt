package com.micheal.rxgallery.utils

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionCheckUtils {

    @JvmStatic
    fun checkPermission(activity:Activity,permission:String,permissionDesc :String,requestCode :Int):Boolean{
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    permission
                ) !== PackageManager.PERMISSION_GRANTED
            ) {
                Logger.i(
                    "ContextCompat.checkSelfPermission(activity, permission):" + ContextCompat.checkSelfPermission(
                        activity,
                        permission
                    )
                )
                Logger.i("PackageManager.PERMISSION_GRANTED:" + PackageManager.PERMISSION_GRANTED)
                Logger.i("permission:$permission")
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    val alertBuilder = AlertDialog.Builder(activity)
                    alertBuilder.setCancelable(false)
                    alertBuilder.setTitle("授权对话框")
                    alertBuilder.setMessage(permissionDesc)
                    alertBuilder.setPositiveButton(
                        android.R.string.yes
                    ) { _, _ ->
                        ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(permission),
                            requestCode
                        )
                    }
                    val alert = alertBuilder.create()
                    alert.show()
                } else {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(permission),
                        requestCode
                    )
                }
                return false
            } else {
                return true
            }
        } else {
            return true
        }
    }

    /**
     * 检查是否对sd卡读取授权
     */
    @JvmStatic
    @TargetApi(16)
    fun checkReadExternalPermission(activity: Activity,permissionDesc: String,requestCode :Int):Boolean{
        return checkPermission(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            permissionDesc,
            requestCode
        )
    }

    /**
     * 检查是否对sd卡读取授权
     */
    @JvmStatic
    @TargetApi(16)
    fun checkWriteExternalPermission(
        activity: Activity,
        permissionDesc: String,
        requestCode: Int
    ): Boolean {
        return checkPermission(
            activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            permissionDesc,
            requestCode
        )
    }

    /**
     * 检查是否对相机读取授权
     */
    @JvmStatic
    @TargetApi(16)
    fun checkCameraPermission(
        activity: Activity,
        permissionDesc: String,
        requestCode: Int
    ): Boolean {
        return checkPermission(activity, Manifest.permission.CAMERA, permissionDesc, requestCode)
    }

}