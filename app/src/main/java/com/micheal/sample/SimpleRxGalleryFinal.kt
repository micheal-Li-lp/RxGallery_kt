package com.micheal.sample

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.FileNotFoundException

object SimpleRxGalleryFinal {
    private const val IMAGE_TYPE = "image/jpeg"
    private const val TYPE_CAMERA = 1111
    private lateinit var imagePath: Uri

    private  var listener: RxGalleryFinalCropListener?=null

    @JvmStatic
    fun init(listener: RxGalleryFinalCropListener): SimpleRxGalleryFinal {
        this.listener = listener
        return this
    }


    fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        imagePath = Uri.fromFile(getDiskCacheDir())
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imagePath)
        } else {
            val contentValues = ContentValues(1)
            contentValues.put(MediaStore.Images.Media.DATA, imagePath.path)
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, IMAGE_TYPE)
            val uri = listener!!.simpleActivity.contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }
        listener!!.simpleActivity
            .startActivityForResult(intent, TYPE_CAMERA)
    }

    @JvmStatic
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_CANCELED -> listener?.onCropCancel()
            UCrop.RESULT_ERROR -> if (data != null) {
                val cropError = UCrop.getError(data)
                if (cropError?.message != null) {
                    listener?.onCropError(cropError.message!!)
                } else {
                    listener?.onCropError("裁剪出现未知错误")
                }
            } else {
                listener?.onCropError("获取相册图片出现错误")
            }

            Activity.RESULT_OK -> when (requestCode) {
                TYPE_CAMERA -> {
                    notifyImageToCamera(listener!!.simpleActivity, imagePath)
                    val of = UCrop.of(imagePath, Uri.fromFile(getDiskCacheDir()))
                    of.start(listener!!.simpleActivity)
                }
                UCrop.REQUEST_CROP -> listener?.onCropSuccess(UCrop.getOutput(data!!))
            }
        }
    }

    private fun getDiskCacheDir(): File {
        val cachePath =
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
                || !Environment.isExternalStorageRemovable())
            {
                val externalCacheDir = listener!!.simpleActivity.externalCacheDir
                if (externalCacheDir != null) {
                    externalCacheDir.path
                } else {
                    listener!!.simpleActivity.cacheDir.path
                }
            } else {
                listener!!.simpleActivity.cacheDir.path
            }
        return File(cachePath, imageName())
    }

    private fun notifyImageToCamera(context: Context, uri: Uri) {
        try {
            val file = File(uri.path!!)
            MediaStore.Images.Media.insertImage(
                context.contentResolver,
                file.absolutePath,
                file.name,
                null
            )
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
    }

    private fun imageName() =  "${System.currentTimeMillis()}.jpg"

    interface RxGalleryFinalCropListener {

        val simpleActivity: Activity


        /**
         * 裁剪被取消
         */
        fun onCropCancel()

        /**
         * 裁剪成功
         *
         * @param uri 裁剪的 Uri , 有可能会为Null
         */
        fun onCropSuccess(uri: Uri?)


        /**
         * 裁剪失败
         *
         * @param errorMessage 错误信息
         */
        fun onCropError(errorMessage: String)

    }
}