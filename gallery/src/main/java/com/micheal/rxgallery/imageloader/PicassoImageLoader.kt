package com.micheal.rxgallery.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.micheal.rxgallery.ui.widget.FixImageView
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import java.io.File

class PicassoImageLoader : AbsImageLoader{



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

        var creator = Picasso.with(context)
            .load(File(path))
            .placeholder(defaultDrawable)
            .error(defaultDrawable)
            .rotate(rotate.toFloat())
            .networkPolicy(NetworkPolicy.NO_STORE)
            .config(config)
            .tag(context)

        if (resize)
        {
            creator = creator.resize(width,height).centerCrop()
        }
        creator.into(imageView)

    }

}