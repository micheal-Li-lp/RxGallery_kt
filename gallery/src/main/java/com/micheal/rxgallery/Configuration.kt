package com.micheal.rxgallery

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.IntRange
import com.micheal.rxgallery.entity.MediaEntity
import com.micheal.rxgallery.imageloader.*
import com.micheal.rxgallery.utils.BaseUtils
import com.yalantis.ucrop.model.AspectRatio
import com.yalantis.ucrop.view.CropImageView
import com.yalantis.ucrop.view.OverlayView

class Configuration() :Parcelable{


    var image = true
    var context: Context? = null
    var selectedList: List<MediaEntity>? = null
    var radio: Boolean = false
    var crop: Boolean = false
    var maxSize = 1
    var imageLoaderType: Int = 0
    @JvmField
    var imageConfig: Int = 0
    var hideCamera: Boolean = false
    var isPlayGif: Boolean = false
    var hidePreview: Boolean = false
    var isVideoPreview: Boolean = false

    //==========UCrop START==========
    //是否隐藏裁剪页面底部控制栏,默认显示
    var hideBottomControls: Boolean = false
    //图片压缩质量,默认不压缩
    var compressionQuality = 90
    //手势方式,默认all
    var gestures: IntArray? = null
    //设置图片最大值,默认根据屏幕得出
    var maxBitmapSize = CropImageView.DEFAULT_MAX_BITMAP_SIZE
    //设置最大缩放值,默认10.f
    var maxScaleMultiplier = CropImageView.DEFAULT_MAX_SCALE_MULTIPLIER
    //宽高比
    var aspectRatioX: Float = 0.toFloat()
    var aspectRatioY: Float = 0.toFloat()
    //等比缩放默认值索引,默认原图比例
    var selectedByDefault: Int = 0
    //等比缩放值表,默认1:1,3:4,原图比例,3:2,16:9
    var aspectRatio: Array<AspectRatio>? = null
    //是否允许改变裁剪大小
    var freestyleCropEnabled = OverlayView.DEFAULT_FREESTYLE_CROP_ENABLED
    //是否显示裁剪框半透明椭圆浮层
    var ovalDimmedLayer = OverlayView.DEFAULT_CIRCLE_DIMMED_LAYER//DEFAULT_OVAL_DIMMED_LAYER
    var maxResultWidth: Int = 0
    var maxResultHeight: Int = 0

    constructor(parcel: Parcel) : this() {
        image = parcel.readByte().toInt() != 0
        selectedList = parcel.createTypedArrayList(MediaEntity.CREATOR)
        radio = parcel.readByte().toInt() != 0
        crop = parcel.readByte().toInt() != 0
        maxSize = parcel.readInt()
        hideBottomControls = parcel.readByte().toInt() != 0
        compressionQuality = parcel.readInt()
        gestures = parcel.createIntArray()
        maxBitmapSize = parcel.readInt()
        maxScaleMultiplier = parcel.readFloat()
        aspectRatioX = parcel.readFloat()
        aspectRatioY = parcel.readFloat()
        selectedByDefault = parcel.readInt()
        aspectRatio = parcel.createTypedArray(AspectRatio.CREATOR)
        freestyleCropEnabled = parcel.readByte().toInt() != 0
        ovalDimmedLayer = parcel.readByte().toInt() != 0
        maxResultWidth = parcel.readInt()
        maxResultHeight = parcel.readInt()
        imageLoaderType = parcel.readInt()
        imageConfig = parcel.readInt()
        hideCamera = parcel.readByte().toInt() != 0
        isPlayGif = parcel.readByte().toInt() != 0
        hidePreview = parcel.readByte().toInt() != 0
        isVideoPreview = parcel.readByte().toInt() != 0

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte((if (image) 1 else 0).toByte())
        parcel.writeTypedList(selectedList)
        parcel.writeByte((if (radio) 1 else 0).toByte())
        parcel.writeByte((if (crop) 1 else 0).toByte())
        parcel.writeInt(maxSize)
        parcel.writeByte((if (hideBottomControls) 1 else 0).toByte())
        parcel.writeInt(compressionQuality)
        parcel.writeIntArray(gestures)
        parcel.writeInt(maxBitmapSize)
        parcel.writeFloat(maxScaleMultiplier)
        parcel.writeFloat(aspectRatioX)
        parcel.writeFloat(aspectRatioY)
        parcel.writeInt(selectedByDefault)
        parcel.writeTypedArray(aspectRatio, flags)
        parcel.writeByte((if (freestyleCropEnabled) 1 else 0).toByte())
        parcel.writeByte((if (ovalDimmedLayer) 1 else 0).toByte())
        parcel.writeInt(maxResultWidth)
        parcel.writeInt(maxResultHeight)
        parcel.writeInt(imageLoaderType)
        parcel.writeInt(imageConfig)
        parcel.writeByte((if (hideCamera) 1 else 0).toByte())
        parcel.writeByte((if (isPlayGif) 1 else 0).toByte())
        parcel.writeByte((if (hidePreview) 1 else 0).toByte())
        parcel.writeByte((if (isVideoPreview) 1 else 0).toByte())
    }

    override fun describeContents() = 0

    fun setMaxResultSize(@IntRange(from=100) width: Int,@IntRange(from=100) height: Int) {
        this.maxResultWidth = width
        this.maxResultHeight = height
    }

    fun getImageLoader() : AbsImageLoader = when (imageLoaderType) {
        1 -> PicassoImageLoader()
        2 -> GlideImageLoader()
        3 -> FrescoImageLoader()
        4 -> UniversalImageLoader()
        else -> getImageLoaderByPkg()
    }

    private fun getImageLoaderByPkg() :AbsImageLoader{
       val context =  BaseApplication.INSTANCE!!.applicationContext
        return when {
            BaseUtils.isPkgExists(context,"com.squareup.picasso") -> PicassoImageLoader()
            BaseUtils.isPkgExists(context,"com.bumptech.glide") -> GlideImageLoader()
            BaseUtils.isPkgExists(context,"com.facebook.drawee.backends.pipeline.Fresco") -> FrescoImageLoader()
            BaseUtils.isPkgExists(context,"com.nostra13.universalimageloader") -> UniversalImageLoader()
            else -> PicassoImageLoader()
        }

    }


    fun getImageConfig() :Bitmap.Config = when (imageConfig) {
        1 -> Bitmap.Config.ALPHA_8
        2 -> Bitmap.Config.ARGB_4444
        3 -> Bitmap.Config.ARGB_8888
        4 -> Bitmap.Config.RGB_565
        else -> Bitmap.Config.ARGB_8888
    }


    companion object CREATOR : Parcelable.Creator<Configuration> {
        override fun createFromParcel(parcel: Parcel): Configuration {
            return Configuration(parcel)
        }

        override fun newArray(size: Int): Array<Configuration?> {
            return arrayOfNulls(size)
        }
    }

}