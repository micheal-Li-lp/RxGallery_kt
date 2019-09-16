package com.micheal.rxgallery.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * 时间工具类
 * Created by KARL-dujinyang on 2017-04-13.
 */
object SimpleDateUtils {

    @JvmStatic
    fun getNowTime():String{
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA)
        return dateFormat.format(Date())
    }

}