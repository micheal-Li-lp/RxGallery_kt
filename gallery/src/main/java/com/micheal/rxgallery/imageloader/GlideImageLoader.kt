package com.micheal.rxgallery.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.micheal.rxgallery.imageloader.rotate.RotateTransformation
import com.micheal.rxgallery.ui.widget.FixImageView

class GlideImageLoader :AbsImageLoader{

    override fun displayImage(
        context: Context,
        path: String,
        imageView: FixImageView,
        defaultDrawable: Drawable?,
        config: Bitmap.Config,
        resize: Boolean,
        isGif: Boolean,
        width: Int,
        height: Int,
        rotate: Int
    ) {

        if (isGif) {
            Glide
                .with(context)
                .load(path)
                .placeholder(defaultDrawable)
                .error(defaultDrawable)
                .override(width, height)
                .crossFade()
                .transform(RotateTransformation(context, rotate.toFloat()))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView)
        } else {
            Glide
                .with(context)
                .load(path)
                .asBitmap()
                .placeholder(defaultDrawable)
                .error(defaultDrawable)
                .override(width, height)
                .transform(RotateTransformation(context, rotate.toFloat()))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView)
        }

    }

}