package com.micheal.rxgallery.presenter

import com.micheal.rxgallery.view.MediaGridView

interface MediaGridPresenter {
    fun setMediaGridView(mediaGridView : MediaGridView)
    fun getMediaList(bucketId : String,pageSize : Int,currentOffset : Int)
    fun getBucketList()
}