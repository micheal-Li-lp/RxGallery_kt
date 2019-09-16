package com.micheal.rxgallery.utils

object FileUtils {
    fun existImageDir(dir :String) = dir.trim { it <= ' ' }.lastIndexOf(".")
}