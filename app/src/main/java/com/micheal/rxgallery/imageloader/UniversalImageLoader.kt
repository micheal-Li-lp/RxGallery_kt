package com.micheal.rxgallery.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.micheal.rxgallery.ui.widget.FixImageView
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.ImageSize
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware

class UniversalImageLoader : AbsImageLoader{

    private var displayImageOptions: DisplayImageOptions? = null

    override fun displayImage(
        context: Context,
        path: String,
        imageView: FixImageView,
        defaultDrawable: Drawable,
        config: Bitmap.Config,
        resize: Boolean,
        isGif: Boolean,
        width: Int,
        height: Int,
        rotate: Int
    ) {
        if (displayImageOptions == null) {
            displayImageOptions = DisplayImageOptions.Builder()
                .cacheOnDisk(false)
                .cacheInMemory(true)
                .bitmapConfig(config)
                .showImageOnFail(defaultDrawable)
                .showImageOnLoading(defaultDrawable)
                .showImageForEmptyUri(defaultDrawable)
                .build()
        }
        val imageSize = if (resize){
            ImageSize(width,height)
        }else null

        ImageLoader.getInstance().displayImage(
            "file://$path",
            ImageViewAware(imageView),
            displayImageOptions,
            imageSize,
            null,
            null
        )
    }
}