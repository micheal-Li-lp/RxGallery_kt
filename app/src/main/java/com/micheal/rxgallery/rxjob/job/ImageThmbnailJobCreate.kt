package com.micheal.rxgallery.rxjob.job

import android.content.Context
import com.micheal.rxgallery.entity.MediaEntity
import com.micheal.rxgallery.rxjob.Job
import com.micheal.rxgallery.rxjob.JobCreator

class ImageThmbnailJobCreate(private val context: Context,private val mediaEntity: MediaEntity) : JobCreator {

    override fun create(): Job {
        val params = Job.Params(mediaEntity.originalPath, mediaEntity)
        return ImageThmbnailJob(context, params)
    }
}