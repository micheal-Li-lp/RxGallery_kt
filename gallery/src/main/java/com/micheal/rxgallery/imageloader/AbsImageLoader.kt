package com.micheal.rxgallery.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.micheal.rxgallery.ui.widget.FixImageView

interface AbsImageLoader {

    fun displayImage(context : Context,
                     path : String,
                     imageView : FixImageView,
                     defaultDrawable : Drawable?,
                     config : Bitmap.Config,
                     resize : Boolean,
                     isGif : Boolean,
                     width : Int,
                     height : Int,
                     rotate : Int)

}
