package com.micheal.rxgallery.presenter.impl

import android.content.Context
import com.micheal.rxgallery.entity.BucketEntity
import com.micheal.rxgallery.entity.MediaEntity
import com.micheal.rxgallery.interactor.MediaBucketFactoryInteractor
import com.micheal.rxgallery.interactor.MediaSrcFactoryInteractor
import com.micheal.rxgallery.interactor.impl.MediaBucketFactoryInteractorImpl
import com.micheal.rxgallery.interactor.impl.MediaSrcFactoryInteractorImpl
import com.micheal.rxgallery.presenter.MediaGridPresenter
import com.micheal.rxgallery.view.MediaGridView

class MediaGridPresenterImpl(private val context: Context,private val isImage:Boolean) :MediaGridPresenter,
    MediaSrcFactoryInteractor.OnGenerateMediaListener,
    MediaBucketFactoryInteractor.OnGenerateBucketListener{

    private val mediaSrcFactoryInteractor : MediaSrcFactoryInteractor =
        MediaSrcFactoryInteractorImpl(context, this,isImage)
    private val mediaBucketFactoryInteractor : MediaBucketFactoryInteractor =
        MediaBucketFactoryInteractorImpl(context, isImage, this)
    private var mediaGridView : MediaGridView?=null

    override fun setMediaGridView(mediaGridView: MediaGridView) {
        this.mediaGridView=mediaGridView
    }

    override fun getMediaList(bucketId: String, pageSize: Int, currentOffset: Int) {
        mediaSrcFactoryInteractor.generateMedias(bucketId, pageSize, currentOffset)
    }


    override fun getBucketList() {
        mediaBucketFactoryInteractor.generateBuckets()
    }

    override fun onFinished(
        bucketId: String?,
        pageSize: Int,
        currentOffset: Int,
        list: List<MediaEntity>?
    ) {
        mediaGridView?.onRequestMediaCallback(list)
    }

    override fun onFinished(list: List<BucketEntity>?) {
        mediaGridView?.onRequestBucketCallback(list)
    }
}