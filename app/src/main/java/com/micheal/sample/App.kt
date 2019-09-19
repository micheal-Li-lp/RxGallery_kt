package com.micheal.sample

import android.content.pm.PackageManager
import com.facebook.drawee.backends.pipeline.Fresco
import com.micheal.rxgallery.BaseApplication
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.QueueProcessingType

class App : BaseApplication(){

    override fun onCreate() {
        super.onCreate()

        Fresco.initialize(this)

        ImageLoaderConfiguration.Builder(this).run {
            threadPriority(Thread.NORM_PRIORITY - 2)
            denyCacheImageMultipleSizesInMemory()
            diskCacheFileNameGenerator(Md5FileNameGenerator())
            diskCacheSize(50 * 1024 * 1024) // 50 MiB
            tasksProcessingOrder(QueueProcessingType.LIFO)
            ImageLoader.getInstance().init(build())
        }

        try {
            packageManager.getPackageInfo("com.nostra13.universalimageloader",PackageManager.GET_ACTIVITIES)

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            println("没有添加依赖")
        }

    }
}