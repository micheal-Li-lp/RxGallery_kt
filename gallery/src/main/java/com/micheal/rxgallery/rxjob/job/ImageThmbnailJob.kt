package com.micheal.rxgallery.rxjob.job

import android.content.Context
import com.micheal.rxgallery.entity.MediaEntity
import com.micheal.rxgallery.rxjob.Job
import com.micheal.rxgallery.utils.BitmapUtils
import com.micheal.rxgallery.utils.MediaUtils

class ImageThmbnailJob(private val context: Context, params: Job.Params) :Job{
    private val mediaEntity = params.requestData as MediaEntity

    override fun onRunJob(): Job.Result {

        val originalPath = mediaEntity.originalPath
        val bigThumFile = MediaUtils.createThumbnailBigFileName(context, originalPath)
        val smallThumFile = MediaUtils.createThumbnailSmallFileName(context, originalPath)
        if (!bigThumFile.exists()) {
            BitmapUtils.createThumbnailBig(bigThumFile, originalPath)
        }
        if (!smallThumFile.exists()) {
            BitmapUtils.createThumbnailSmall(smallThumFile, originalPath)
        }
        val result = Job.Result.SUCCESS
        result.data = mediaEntity
        return result
    }
}