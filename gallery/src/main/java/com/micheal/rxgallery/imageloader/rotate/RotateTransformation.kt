package com.micheal.rxgallery.imageloader.rotate

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation

class RotateTransformation(context: Context,private val rotateRotationAngle :Float)
    : BitmapTransformation(context) {

    override fun getId() = "rotate$rotateRotationAngle"

    override fun transform(
        pool: BitmapPool?,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap = Matrix().run {
        postRotate(rotateRotationAngle)
        Bitmap.createBitmap(toTransform,0,0,toTransform.width,toTransform.height,this,true)
    }

}